package com.inter.trade.orm.dao.query;

import java.util.List;

import com.inter.trade.orm.dao.AbstractDao;
import com.inter.trade.orm.dao.DaoException;

import android.database.Cursor;


public class Query<T> extends AbstractQuery<T>
{
	private final static class QueryData<T2> extends
			AbstractQueryData<T2, Query<T2>>
	{
		private final int limitPosition;
		private final int offsetPosition;

		QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues,
				int limitPosition, int offsetPosition)
		{
			super(dao, sql, initialValues);
			this.limitPosition = limitPosition;
			this.offsetPosition = offsetPosition;
		}

		@Override
		protected Query<T2> createQuery()
		{
			return new Query<T2>(this, dao, sql, initialValues.clone(),
					limitPosition, offsetPosition);
		}

	}

	public static <T2> Query<T2> internalCreate(AbstractDao<T2, ?> dao,
			String sql, Object[] initialValues)
	{
		return create(dao, sql, initialValues, -1, -1);
	}

	static <T2> Query<T2> create(AbstractDao<T2, ?> dao, String sql,
			Object[] initialValues, int limitPosition, int offsetPosition)
	{
		QueryData<T2> queryData = new QueryData<T2>(dao, sql,
				toStringArray(initialValues), limitPosition, offsetPosition);
		return queryData.forCurrentThread();
	}

	private final int limitPosition;
	private final int offsetPosition;
	private final QueryData<T> queryData;

	private Query(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql,
			String[] initialValues, int limitPosition, int offsetPosition)
	{
		super(dao, sql, initialValues);
		this.queryData = queryData;
		this.limitPosition = limitPosition;
		this.offsetPosition = offsetPosition;
	}

	public Query<T> forCurrentThread()
	{
		return queryData.forCurrentThread(this);
	}

	@Override
	public void setParameter(int index, Object parameter)
	{
		if (index >= 0 && (index == limitPosition || index == offsetPosition))
		{
			throw new IllegalArgumentException("Illegal parameter index: "
					+ index);
		}
		super.setParameter(index, parameter);
	}

	public void setLimit(int limit)
	{
		checkThread();
		if (limitPosition == -1)
		{
			throw new IllegalStateException(
					"Limit must be set with QueryBuilder before it can be used here");
		}
		parameters[limitPosition] = Integer.toString(limit);
	}

	public void setOffset(int offset)
	{
		checkThread();
		if (offsetPosition == -1)
		{
			throw new IllegalStateException(
					"Offset must be set with QueryBuilder before it can be used here");
		}
		parameters[offsetPosition] = Integer.toString(offset);
	}

	public List<T> list()
	{
		checkThread();
		Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
		return daoAccess.loadAllAndCloseCursor(cursor);
	}

	public LazyList<T> listLazy()
	{
		checkThread();
		Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
		return new LazyList<T>(daoAccess, cursor, true);
	}

	public LazyList<T> listLazyUncached()
	{
		checkThread();
		Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
		return new LazyList<T>(daoAccess, cursor, false);
	}

	public CloseableListIterator<T> listIterator()
	{
		return listLazyUncached().listIteratorAutoClose();
	}

	public T unique()
	{
		checkThread();
		Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
		return daoAccess.loadUniqueAndCloseCursor(cursor);
	}

	public T uniqueOrThrow()
	{
		T entity = unique();
		if (entity == null)
		{
			throw new DaoException("No entity found for query");
		}
		return entity;
	}

}
