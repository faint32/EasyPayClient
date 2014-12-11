package com.inter.trade.orm.dao;

import java.util.HashMap;
import java.util.Map;

import com.inter.trade.orm.dao.internal.DaoConfig;
import com.inter.trade.orm.dao.scope.IdentityScopeType;

import android.database.sqlite.SQLiteDatabase;


public abstract class AbstractDaoMaster
{
	protected final SQLiteDatabase db;
	protected final int schemaVersion;
	protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;

	public AbstractDaoMaster(SQLiteDatabase db, int schemaVersion)
	{
		this.db = db;
		this.schemaVersion = schemaVersion;

		daoConfigMap = new HashMap<Class<? extends AbstractDao<?, ?>>, DaoConfig>();
	}

	protected void registerDaoClass(Class<? extends AbstractDao<?, ?>> daoClass)
	{
		DaoConfig daoConfig = new DaoConfig(db, daoClass);
		daoConfigMap.put(daoClass, daoConfig);
	}

	public int getSchemaVersion()
	{
		return schemaVersion;
	}

	public SQLiteDatabase getDatabase()
	{
		return db;
	}

	public abstract AbstractDaoSession newSession();

	public abstract AbstractDaoSession newSession(IdentityScopeType type);
}
