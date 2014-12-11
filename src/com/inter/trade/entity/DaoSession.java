package com.inter.trade.entity;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import com.inter.trade.orm.dao.AbstractDao;
import com.inter.trade.orm.dao.AbstractDaoSession;
import com.inter.trade.orm.dao.internal.DaoConfig;
import com.inter.trade.orm.dao.scope.IdentityScopeType;

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession
{

	public DaoSession(SQLiteDatabase db, IdentityScopeType type,
			Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap)
	{
		super(db);

	}

	public void clear()
	{
	}
}