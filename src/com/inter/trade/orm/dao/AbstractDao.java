package com.inter.trade.orm.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.inter.trade.orm.dao.internal.DaoConfig;
import com.inter.trade.orm.dao.internal.FastCursor;
import com.inter.trade.orm.dao.internal.TableStatements;
import com.inter.trade.orm.dao.query.Query;
import com.inter.trade.orm.dao.query.QueryBuilder;
import com.inter.trade.orm.dao.scope.IdentityScope;
import com.inter.trade.orm.dao.scope.IdentityScopeLong;

import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public abstract class AbstractDao<T, K>
{
	protected final SQLiteDatabase db;
	protected final DaoConfig config;
	protected IdentityScope<K, T> identityScope;
	protected IdentityScopeLong<T> identityScopeLong;
	protected TableStatements statements;

	protected final AbstractDaoSession session;
	protected final int pkOrdinal;

	public AbstractDao(DaoConfig config)
	{
		this(config, null);
	}

	@SuppressWarnings("unchecked")
	public AbstractDao(DaoConfig config, AbstractDaoSession daoSession)
	{
		this.config = config;
		this.session = daoSession;
		db = config.db;
		identityScope = (IdentityScope<K, T>) config.getIdentityScope();
		if (identityScope instanceof IdentityScopeLong)
		{
			identityScopeLong = (IdentityScopeLong<T>) identityScope;
		}
		statements = config.statements;
		pkOrdinal = config.pkProperty != null ? config.pkProperty.ordinal : -1;
	}

	public AbstractDaoSession getSession()
	{
		return session;
	}

	TableStatements getStatements()
	{
		return config.statements;
	}

	public String getTablename()
	{
		return config.tablename;
	}

	public Property[] getProperties()
	{
		return config.properties;
	}

	public Property getPkProperty()
	{
		return config.pkProperty;
	}

	public String[] getAllColumns()
	{
		return config.allColumns;
	}

	public String[] getPkColumns()
	{
		return config.pkColumns;
	}

	public String[] getNonPkColumns()
	{
		return config.nonPkColumns;
	}

	public T load(K key)
	{
		assertSinglePk();
		if (key == null)
		{
			return null;
		}
		if (identityScope != null)
		{
			T entity = identityScope.get(key);
			if (entity != null)
			{
				return entity;
			}
		}
		String sql = statements.getSelectByKey();
		String[] keyArray = new String[]
		{ key.toString() };
		Cursor cursor = db.rawQuery(sql, keyArray);
		return loadUniqueAndCloseCursor(cursor);
	}

	public T loadByRowId(long rowId)
	{
		String[] idArray = new String[]
		{ Long.toString(rowId) };
		Cursor cursor = db.rawQuery(statements.getSelectByRowId(), idArray);
		return loadUniqueAndCloseCursor(cursor);
	}

	protected T loadUniqueAndCloseCursor(Cursor cursor)
	{
		try
		{
			return loadUnique(cursor);
		}
		finally
		{
			cursor.close();
		}
	}

	protected T loadUnique(Cursor cursor)
	{
		boolean available = cursor.moveToFirst();
		if (!available)
		{
			return null;
		}
		else if (!cursor.isLast())
		{
			throw new DaoException("Expected unique result, but count was "
					+ cursor.getCount());
		}
		return loadCurrent(cursor, 0, true);
	}

	public List<T> loadAll()
	{
		Cursor cursor = db.rawQuery(statements.getSelectAll(), null);
		return loadAllAndCloseCursor(cursor);
	}

	public boolean detach(T entity)
	{
		if (identityScope != null)
		{
			K key = getKeyVerified(entity);
			return identityScope.detach(key, entity);
		}
		else
		{
			return false;
		}
	}

	protected List<T> loadAllAndCloseCursor(Cursor cursor)
	{
		try
		{
			return loadAllFromCursor(cursor);
		}
		finally
		{
			cursor.close();
		}
	}

	public void insertInTx(Iterable<T> entities)
	{
		insertInTx(entities, isEntityUpdateable());
	}

	public void insertInTx(T... entities)
	{
		insertInTx(Arrays.asList(entities), isEntityUpdateable());
	}

	public void insertInTx(Iterable<T> entities, boolean setPrimaryKey)
	{
		SQLiteStatement stmt = statements.getInsertStatement();
		executeInsertInTx(stmt, entities, setPrimaryKey);
	}

	public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey)
	{
		SQLiteStatement stmt = statements.getInsertOrReplaceStatement();
		executeInsertInTx(stmt, entities, setPrimaryKey);
	}

	public void insertOrReplaceInTx(Iterable<T> entities)
	{
		insertOrReplaceInTx(entities, isEntityUpdateable());
	}

	public void insertOrReplaceInTx(T... entities)
	{
		insertOrReplaceInTx(Arrays.asList(entities), isEntityUpdateable());
	}

	private void executeInsertInTx(SQLiteStatement stmt, Iterable<T> entities,
			boolean setPrimaryKey)
	{
		db.beginTransaction();
		try
		{
			synchronized (stmt)
			{
				if (identityScope != null)
				{
					identityScope.lock();
				}
				try
				{
					for (T entity : entities)
					{
						bindValues(stmt, entity);
						if (setPrimaryKey)
						{
							long rowId = stmt.executeInsert();
							updateKeyAfterInsertAndAttach(entity, rowId, false);
						}
						else
						{
							stmt.execute();
						}
					}
				}
				finally
				{
					if (identityScope != null)
					{
						identityScope.unlock();
					}
				}
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	public long insert(T entity)
	{
		return executeInsert(entity, statements.getInsertStatement());
	}

	public long insertWithoutSettingPk(T entity)
	{
		SQLiteStatement stmt = statements.getInsertStatement();
		long rowId;
		if (db.isDbLockedByCurrentThread())
		{
			synchronized (stmt)
			{
				bindValues(stmt, entity);
				rowId = stmt.executeInsert();
			}
		}
		else
		{

			db.beginTransaction();
			try
			{
				synchronized (stmt)
				{
					bindValues(stmt, entity);
					rowId = stmt.executeInsert();
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
		}
		return rowId;
	}

	public long insertOrReplace(T entity)
	{
		return executeInsert(entity, statements.getInsertOrReplaceStatement());
	}

	private long executeInsert(T entity, SQLiteStatement stmt)
	{
		long rowId;
		if (db.isDbLockedByCurrentThread())
		{
			synchronized (stmt)
			{
				bindValues(stmt, entity);
				rowId = stmt.executeInsert();
			}
		}
		else
		{

			db.beginTransaction();
			try
			{
				synchronized (stmt)
				{
					bindValues(stmt, entity);
					rowId = stmt.executeInsert();
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
		}
		updateKeyAfterInsertAndAttach(entity, rowId, true);
		return rowId;
	}

	protected void updateKeyAfterInsertAndAttach(T entity, long rowId,
			boolean lock)
	{
		if (rowId != -1)
		{
			K key = updateKeyAfterInsert(entity, rowId);
			attachEntity(key, entity, lock);
		}
		else
		{
			DaoLog.w("Could not insert row (executeInsert returned -1)");
		}
	}

	protected List<T> loadAllFromCursor(Cursor cursor)
	{
		int count = cursor.getCount();
		List<T> list = new ArrayList<T>(count);
		if (cursor instanceof CrossProcessCursor)
		{
			CursorWindow window = ((CrossProcessCursor) cursor).getWindow();
			if (window != null)
			{
				if (window.getNumRows() == count)
				{
					cursor = new FastCursor(window);
				}
				else
				{
					DaoLog.d("Window vs. result size: " + window.getNumRows()
							+ "/" + count);
				}
			}
		}

		if (cursor.moveToFirst())
		{
			if (identityScope != null)
			{
				identityScope.lock();
				identityScope.reserveRoom(count);
			}
			try
			{
				do
				{
					list.add(loadCurrent(cursor, 0, false));
				}
				while (cursor.moveToNext());
			}
			finally
			{
				if (identityScope != null)
				{
					identityScope.unlock();
				}
			}
		}
		return list;
	}

	final protected T loadCurrent(Cursor cursor, int offset, boolean lock)
	{
		if (identityScopeLong != null)
		{
			if (offset != 0)
			{
				if (cursor.isNull(pkOrdinal + offset))
				{
					return null;
				}
			}

			long key = cursor.getLong(pkOrdinal + offset);
			T entity = lock ? identityScopeLong.get2(key) : identityScopeLong
					.get2NoLock(key);
			if (entity != null)
			{
				return entity;
			}
			else
			{
				entity = readEntity(cursor, offset);
				attachEntity(entity);
				if (lock)
				{
					identityScopeLong.put2(key, entity);
				}
				else
				{
					identityScopeLong.put2NoLock(key, entity);
				}
				return entity;
			}
		}
		else if (identityScope != null)
		{
			K key = readKey(cursor, offset);
			if (offset != 0 && key == null)
			{
				return null;
			}
			T entity = lock ? identityScope.get(key) : identityScope
					.getNoLock(key);
			if (entity != null)
			{
				return entity;
			}
			else
			{
				entity = readEntity(cursor, offset);
				attachEntity(key, entity, lock);
				return entity;
			}
		}
		else
		{
			if (offset != 0)
			{
				K key = readKey(cursor, offset);
				if (key == null)
				{
					return null;
				}
			}
			T entity = readEntity(cursor, offset);
			attachEntity(entity);
			return entity;
		}
	}

	final protected <O> O loadCurrentOther(AbstractDao<O, ?> dao,
			Cursor cursor, int offset)
	{
		return dao.loadCurrent(cursor, offset, true);
	}

	public List<T> queryRaw(String where, String... selectionArg)
	{
		Cursor cursor = db.rawQuery(statements.getSelectAll() + where,
				selectionArg);
		return loadAllAndCloseCursor(cursor);
	}

	public Query<T> queryRawCreate(String where, Object... selectionArg)
	{
		List<Object> argList = Arrays.asList(selectionArg);
		return queryRawCreateListArgs(where, argList);
	}

	public Query<T> queryRawCreateListArgs(String where,
			Collection<Object> selectionArg)
	{
		return Query.internalCreate(this, statements.getSelectAll() + where,
				selectionArg.toArray());
	}

	public void deleteAll()
	{

		db.execSQL("DELETE FROM '" + config.tablename + "'");
		if (identityScope != null)
		{
			identityScope.clear();
		}
	}

	public void delete(T entity)
	{
		assertSinglePk();
		K key = getKeyVerified(entity);
		deleteByKey(key);
	}

	public void deleteByKey(K key)
	{
		assertSinglePk();
		SQLiteStatement stmt = statements.getDeleteStatement();
		if (db.isDbLockedByCurrentThread())
		{
			synchronized (stmt)
			{
				deleteByKeyInsideSynchronized(key, stmt);
			}
		}
		else
		{

			db.beginTransaction();
			try
			{
				synchronized (stmt)
				{
					deleteByKeyInsideSynchronized(key, stmt);
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
		}
		if (identityScope != null)
		{
			identityScope.remove(key);
		}
	}

	private void deleteByKeyInsideSynchronized(K key, SQLiteStatement stmt)
	{
		if (key instanceof Long)
		{
			stmt.bindLong(1, (Long) key);
		}
		else if (key == null)
		{
			throw new DaoException("Cannot delete entity, key is null");
		}
		else
		{
			stmt.bindString(1, key.toString());
		}
		stmt.execute();
	}

	private void deleteInTxInternal(Iterable<T> entities, Iterable<K> keys)
	{
		assertSinglePk();
		SQLiteStatement stmt = statements.getDeleteStatement();
		List<K> keysToRemoveFromIdentityScope = null;
		db.beginTransaction();
		try
		{
			synchronized (stmt)
			{
				if (identityScope != null)
				{
					identityScope.lock();
					keysToRemoveFromIdentityScope = new ArrayList<K>();
				}
				try
				{
					if (entities != null)
					{
						for (T entity : entities)
						{
							K key = getKeyVerified(entity);
							deleteByKeyInsideSynchronized(key, stmt);
							if (keysToRemoveFromIdentityScope != null)
							{
								keysToRemoveFromIdentityScope.add(key);
							}
						}
					}
					if (keys != null)
					{
						for (K key : keys)
						{
							deleteByKeyInsideSynchronized(key, stmt);
							if (keysToRemoveFromIdentityScope != null)
							{
								keysToRemoveFromIdentityScope.add(key);
							}
						}
					}
				}
				finally
				{
					if (identityScope != null)
					{
						identityScope.unlock();
					}
				}
			}
			db.setTransactionSuccessful();
			if (keysToRemoveFromIdentityScope != null && identityScope != null)
			{
				identityScope.remove(keysToRemoveFromIdentityScope);
			}
		}
		finally
		{
			db.endTransaction();
		}
	}

	public void deleteInTx(Iterable<T> entities)
	{
		deleteInTxInternal(entities, null);
	}

	public void deleteInTx(T... entities)
	{
		deleteInTxInternal(Arrays.asList(entities), null);
	}

	public void deleteByKeyInTx(Iterable<K> keys)
	{
		deleteInTxInternal(null, keys);
	}

	public void deleteByKeyInTx(K... keys)
	{
		deleteInTxInternal(null, Arrays.asList(keys));
	}

	public void refresh(T entity)
	{
		assertSinglePk();
		K key = getKeyVerified(entity);
		String sql = statements.getSelectByKey();
		String[] keyArray = new String[]
		{ key.toString() };
		Cursor cursor = db.rawQuery(sql, keyArray);
		try
		{
			boolean available = cursor.moveToFirst();
			if (!available)
			{
				throw new DaoException(
						"Entity does not exist in the database anymore: "
								+ entity.getClass() + " with key " + key);
			}
			else if (!cursor.isLast())
			{
				throw new DaoException("Expected unique result, but count was "
						+ cursor.getCount());
			}
			readEntity(cursor, entity, 0);
			attachEntity(key, entity, true);
		}
		finally
		{
			cursor.close();
		}
	}

	public void update(T entity)
	{
		assertSinglePk();
		SQLiteStatement stmt = statements.getUpdateStatement();
		if (db.isDbLockedByCurrentThread())
		{
			synchronized (stmt)
			{
				updateInsideSynchronized(entity, stmt, true);
			}
		}
		else
		{

			db.beginTransaction();
			try
			{
				synchronized (stmt)
				{
					updateInsideSynchronized(entity, stmt, true);
				}
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
		}
	}

	public QueryBuilder<T> queryBuilder()
	{
		return QueryBuilder.internalCreate(this);
	}

	protected void updateInsideSynchronized(T entity, SQLiteStatement stmt,
			boolean lock)
	{
		bindValues(stmt, entity);
		int index = config.allColumns.length + 1;
		K key = getKey(entity);
		if (key instanceof Long)
		{
			stmt.bindLong(index, (Long) key);
		}
		else if (key == null)
		{
			throw new DaoException(
					"Cannot update entity without key - was it inserted before?");
		}
		else
		{
			stmt.bindString(index, key.toString());
		}
		stmt.execute();
		attachEntity(key, entity, lock);
	}

	protected final void attachEntity(K key, T entity, boolean lock)
	{
		attachEntity(entity);
		if (identityScope != null && key != null)
		{
			if (lock)
			{
				identityScope.put(key, entity);
			}
			else
			{
				identityScope.putNoLock(key, entity);
			}
		}
	}

	protected void attachEntity(T entity)
	{
	}

	public void updateInTx(Iterable<T> entities)
	{
		SQLiteStatement stmt = statements.getUpdateStatement();
		db.beginTransaction();
		try
		{
			synchronized (stmt)
			{
				if (identityScope != null)
				{
					identityScope.lock();
				}
				try
				{
					for (T entity : entities)
					{
						updateInsideSynchronized(entity, stmt, false);
					}
				}
				finally
				{
					if (identityScope != null)
					{
						identityScope.unlock();
					}
				}
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	public void updateInTx(T... entities)
	{
		updateInTx(Arrays.asList(entities));
	}

	protected void assertSinglePk()
	{
		if (config.pkColumns.length != 1)
		{
			throw new DaoException(this + " (" + config.tablename
					+ ") does not have a single-column primary key");
		}
	}

	public long count()
	{
		return DatabaseUtils
				.queryNumEntries(db, '\'' + config.tablename + '\'');
	}

	protected K getKeyVerified(T entity)
	{
		K key = getKey(entity);
		if (key == null)
		{
			if (entity == null)
			{
				throw new NullPointerException("Entity may not be null");
			}
			else
			{
				throw new DaoException("Entity has no key");
			}
		}
		else
		{
			return key;
		}
	}

	public SQLiteDatabase getDatabase()
	{
		return db;
	}

	abstract protected T readEntity(Cursor cursor, int offset);

	abstract protected K readKey(Cursor cursor, int offset);

	abstract protected void readEntity(Cursor cursor, T entity, int offset);

	abstract protected void bindValues(SQLiteStatement stmt, T entity);

	abstract protected K updateKeyAfterInsert(T entity, long rowId);

	abstract protected K getKey(T entity);

	abstract protected boolean isEntityUpdateable();

}
