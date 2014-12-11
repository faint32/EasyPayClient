package com.inter.trade.orm.dao;

import java.lang.reflect.Constructor;

import com.inter.trade.orm.dao.internal.DaoConfig;
import com.inter.trade.orm.dao.scope.IdentityScope;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class InternalUnitTestDaoAccess<T, K>
{
	private final AbstractDao<T, K> dao;

	public InternalUnitTestDaoAccess(SQLiteDatabase db,
			Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope)
			throws Exception
	{
		DaoConfig daoConfig = new DaoConfig(db, daoClass);
		daoConfig.setIdentityScope(identityScope);
		Constructor<AbstractDao<T, K>> constructor = daoClass
				.getConstructor(DaoConfig.class);
		dao = constructor.newInstance(daoConfig);
	}

	public K getKey(T entity)
	{
		return dao.getKey(entity);
	}

	public Property[] getProperties()
	{
		return dao.getProperties();
	}

	public boolean isEntityUpdateable()
	{
		return dao.isEntityUpdateable();
	}

	public T readEntity(Cursor cursor, int offset)
	{
		return dao.readEntity(cursor, offset);
	}

	public K readKey(Cursor cursor, int offset)
	{
		return dao.readKey(cursor, offset);
	}

	public AbstractDao<T, K> getDao()
	{
		return dao;
	}

}
