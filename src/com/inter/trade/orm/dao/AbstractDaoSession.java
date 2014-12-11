package com.inter.trade.orm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.inter.trade.orm.dao.async.AsyncSession;
import com.inter.trade.orm.dao.query.QueryBuilder;

import android.database.sqlite.SQLiteDatabase;

public class AbstractDaoSession
{
	private final SQLiteDatabase db;
	private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

	public AbstractDaoSession(SQLiteDatabase db)
	{
		this.db = db;
		this.entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();
	}

	protected <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao)
	{
		entityToDao.put(entityClass, dao);
	}

	public <T> long insert(T entity)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
		return dao.insert(entity);
	}

	public <T> long insertOrReplace(T entity)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
		return dao.insertOrReplace(entity);
	}

	public <T> void refresh(T entity)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
		dao.refresh(entity);
	}

	public <T> void update(T entity)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
		dao.update(entity);
	}

	public <T> void delete(T entity)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
		dao.delete(entity);
	}

	public <T> void deleteAll(Class<T> entityClass)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entityClass);
		dao.deleteAll();
	}

	public <T, K> T load(Class<T> entityClass, K key)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
		return dao.load(key);
	}

	public <T, K> List<T> loadAll(Class<T> entityClass)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
		return dao.loadAll();
	}

	public <T, K> List<T> queryRaw(Class<T> entityClass, String where,
			String... selectionArgs)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
		return dao.queryRaw(where, selectionArgs);
	}

	public <T> QueryBuilder<T> queryBuilder(Class<T> entityClass)
	{
		@SuppressWarnings("unchecked")
		AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entityClass);
		return dao.queryBuilder();
	}

	public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass)
	{
		AbstractDao<?, ?> dao = entityToDao.get(entityClass);
		if (dao == null) { throw new DaoException("No DAO registered for "
				+ entityClass); }
		return dao;
	}

	public void runInTx(Runnable runnable)
	{
		db.beginTransaction();
		try
		{
			runnable.run();
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	public <V> V callInTx(Callable<V> callable) throws Exception
	{
		db.beginTransaction();
		try
		{
			V result = callable.call();
			db.setTransactionSuccessful();
			return result;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public <V> V callInTxNoException(Callable<V> callable)
	{
		db.beginTransaction();
		try
		{
			V result;
			try
			{
				result = callable.call();
			}
			catch (Exception e)
			{
				throw new DaoException("Callable failed", e);
			}
			db.setTransactionSuccessful();
			return result;
		}
		finally
		{
			db.endTransaction();
		}
	}

	public SQLiteDatabase getDatabase()
	{
		return db;
	}

	public AsyncSession startAsyncSession()
	{
		return new AsyncSession(this);
	}

}
