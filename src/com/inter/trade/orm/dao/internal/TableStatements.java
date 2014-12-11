package com.inter.trade.orm.dao.internal;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class TableStatements
{
	private final SQLiteDatabase db;
	private final String tablename;
	private final String[] allColumns;
	private final String[] pkColumns;

	private SQLiteStatement insertStatement;
	private SQLiteStatement insertOrReplaceStatement;
	private SQLiteStatement updateStatement;
	private SQLiteStatement deleteStatement;

	private volatile String selectAll;
	private volatile String selectByKey;
	private volatile String selectByRowId;
	private volatile String selectKeys;

	public TableStatements(SQLiteDatabase db, String tablename,
			String[] allColumns, String[] pkColumns)
	{
		this.db = db;
		this.tablename = tablename;
		this.allColumns = allColumns;
		this.pkColumns = pkColumns;
	}

	public SQLiteStatement getInsertStatement()
	{
		if (insertStatement == null)
		{
			String sql = SqlUtils.createSqlInsert("INSERT INTO ", tablename,
					allColumns);
			insertStatement = db.compileStatement(sql);
		}
		return insertStatement;
	}

	public SQLiteStatement getInsertOrReplaceStatement()
	{
		if (insertOrReplaceStatement == null)
		{
			String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ",
					tablename, allColumns);
			insertOrReplaceStatement = db.compileStatement(sql);
		}
		return insertOrReplaceStatement;
	}

	public SQLiteStatement getDeleteStatement()
	{
		if (deleteStatement == null)
		{
			String sql = SqlUtils.createSqlDelete(tablename, pkColumns);
			deleteStatement = db.compileStatement(sql);
		}
		return deleteStatement;
	}

	public SQLiteStatement getUpdateStatement()
	{
		if (updateStatement == null)
		{
			String sql = SqlUtils.createSqlUpdate(tablename, allColumns,
					pkColumns);
			updateStatement = db.compileStatement(sql);
		}
		return updateStatement;
	}

	public String getSelectAll()
	{
		if (selectAll == null)
		{
			selectAll = SqlUtils.createSqlSelect(tablename, "T", allColumns);
		}
		return selectAll;
	}

	public String getSelectKeys()
	{
		if (selectKeys == null)
		{
			selectKeys = SqlUtils.createSqlSelect(tablename, "T", pkColumns);
		}
		return selectKeys;
	}

	public String getSelectByKey()
	{
		if (selectByKey == null)
		{
			StringBuilder builder = new StringBuilder(getSelectAll());
			builder.append("WHERE ");
			SqlUtils.appendColumnsEqValue(builder, "T", pkColumns);
			selectByKey = builder.toString();
		}
		return selectByKey;
	}

	public String getSelectByRowId()
	{
		if (selectByRowId == null)
		{
			selectByRowId = getSelectAll() + "WHERE ROWID=?";
		}
		return selectByRowId;
	}

}
