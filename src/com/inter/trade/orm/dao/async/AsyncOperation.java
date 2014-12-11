package com.inter.trade.orm.dao.async;

import com.inter.trade.orm.dao.AbstractDao;
import com.inter.trade.orm.dao.DaoException;

import android.database.sqlite.SQLiteDatabase;


public class AsyncOperation
{
	public static enum OperationType
	{
		Insert, InsertInTxIterable, InsertInTxArray, //
		InsertOrReplace, InsertOrReplaceInTxIterable, InsertOrReplaceInTxArray, //
		Update, UpdateInTxIterable, UpdateInTxArray, //
		Delete, DeleteInTxIterable, DeleteInTxArray, //
		DeleteByKey, DeleteAll, //
		TransactionRunnable, TransactionCallable, //
		QueryList, QueryUnique, //
		Load, LoadAll, //
		Count, Refresh
	}

	public static final int FLAG_MERGE_TX = 1;

	public static final int FLAG_STOP_QUEUE_ON_EXCEPTION = 1 << 1;

	final OperationType type;
	final AbstractDao<Object, Object> dao;
	private final SQLiteDatabase database;
	final Object parameter;
	final int flags;

	volatile long timeStarted;
	volatile long timeCompleted;
	private volatile boolean completed;
	volatile Throwable throwable;
	volatile Object result;
	volatile int mergedOperationsCount;

	int sequenceNumber;

	@SuppressWarnings("unchecked")
	AsyncOperation(OperationType type, AbstractDao<?, ?> dao, Object parameter,
			int flags)
	{
		this.type = type;
		this.flags = flags;
		this.dao = (AbstractDao<Object, Object>) dao;
		this.database = null;
		this.parameter = parameter;
	}

	AsyncOperation(OperationType type, SQLiteDatabase database,
			Object parameter, int flags)
	{
		this.type = type;
		this.database = database;
		this.flags = flags;
		this.dao = null;
		this.parameter = parameter;
	}

	public Throwable getThrowable()
	{
		return throwable;
	}

	public void setThrowable(Throwable throwable)
	{
		this.throwable = throwable;
	}

	public OperationType getType()
	{
		return type;
	}

	public Object getParameter()
	{
		return parameter;
	}

	public synchronized Object getResult()
	{
		if (!completed)
		{
			waitForCompletion();
		}
		if (throwable != null)
		{
			throw new AsyncDaoException(this, throwable);
		}
		return result;
	}

	public boolean isMergeTx()
	{
		return (flags & FLAG_MERGE_TX) != 0;
	}

	SQLiteDatabase getDatabase()
	{
		return database != null ? database : dao.getDatabase();
	}

	boolean isMergeableWith(AsyncOperation other)
	{
		return other != null && isMergeTx() && other.isMergeTx()
				&& getDatabase() == other.getDatabase();
	}

	public long getTimeStarted()
	{
		return timeStarted;
	}

	public long getTimeCompleted()
	{
		return timeCompleted;
	}

	public long getDuration()
	{
		if (timeCompleted == 0)
		{
			throw new DaoException("This operation did not yet complete");
		}
		else
		{
			return timeCompleted - timeStarted;
		}
	}

	public boolean isFailed()
	{
		return throwable != null;
	}

	public boolean isCompleted()
	{
		return completed;
	}

	public synchronized Object waitForCompletion()
	{
		while (!completed)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				throw new DaoException(
						"Interrupted while waiting for operation to complete",
						e);
			}
		}
		return result;
	}

	public synchronized boolean waitForCompletion(int maxMillis)
	{
		if (!completed)
		{
			try
			{
				wait(maxMillis);
			}
			catch (InterruptedException e)
			{
				throw new DaoException(
						"Interrupted while waiting for operation to complete",
						e);
			}
		}
		return completed;
	}

	synchronized void setCompleted()
	{
		completed = true;
		notifyAll();
	}

	public boolean isCompletedSucessfully()
	{
		return completed && throwable == null;
	}

	public int getMergedOperationsCount()
	{
		return mergedOperationsCount;
	}

	public int getSequenceNumber()
	{
		return sequenceNumber;
	}

	void reset()
	{
		timeStarted = 0;
		timeCompleted = 0;
		completed = false;
		throwable = null;
		result = null;
		mergedOperationsCount = 0;
	}

}
