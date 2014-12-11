package com.inter.trade.orm.dao.query;

import com.inter.trade.orm.dao.AbstractDao;

import android.database.sqlite.SQLiteDatabase;


public class DeleteQuery<T> extends AbstractQuery<T>
{
	private final static class QueryData<T2> extends
			AbstractQueryData<T2, DeleteQuery<T2>>
	{

		private QueryData(AbstractDao<T2, ?> dao, String sql,
				String[] initialValues)
		{
			super(dao, sql, initialValues);
		}

		@Override
		protected DeleteQuery<T2> createQuery()
		{
			return new DeleteQuery<T2>(this, dao, sql, initialValues.clone());
		}
	}

	static <T2> DeleteQuery<T2> create(AbstractDao<T2, ?> dao, String sql,
			Object[] initialValues)
	{
		QueryData<T2> queryData = new QueryData<T2>(dao, sql,
				toStringArray(initialValues));
		return queryData.forCurrentThread();
	}

	private final QueryData<T> queryData;

	private DeleteQuery(QueryData<T> queryData, AbstractDao<T, ?> dao,
			String sql, String[] initialValues)
	{
		super(dao, sql, initialValues);
		this.queryData = queryData;
	}

	public DeleteQuery<T> forCurrentThread()
	{
		return queryData.forCurrentThread(this);
	}

	public void executeDeleteWithoutDetachingEntities()
	{
		checkThread();
		SQLiteDatabase db = dao.getDatabase();
		if (db.isDbLockedByCurrentThread())
		{
			dao.getDatabase().execSQL(sql, parameters);
		}
		else
		{
			db.beginTransaction();
			try
			{
				dao.getDatabase().execSQL(sql, parameters);
				db.setTransactionSuccessful();
			}
			finally
			{
				db.endTransaction();
			}
		}
	}

}
