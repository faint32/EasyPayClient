package com.inter.trade.orm.dao.query;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.inter.trade.orm.dao.AbstractDao;
import com.inter.trade.orm.dao.DaoException;
import com.inter.trade.orm.dao.DaoLog;
import com.inter.trade.orm.dao.InternalQueryDaoAccess;
import com.inter.trade.orm.dao.Property;
import com.inter.trade.orm.dao.internal.SqlUtils;
import com.inter.trade.orm.dao.query.WhereCondition.PropertyCondition;



public class QueryBuilder<T>
{

	public static boolean LOG_SQL;

	public static boolean LOG_VALUES;

	private StringBuilder orderBuilder;
	private StringBuilder joinBuilder;

	private final List<WhereCondition> whereConditions;

	private final List<Object> values;
	private final AbstractDao<T, ?> dao;
	private final String tablePrefix;

	private Integer limit;

	private Integer offset;

	public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2, ?> dao)
	{
		return new QueryBuilder<T2>(dao);
	}

	protected QueryBuilder(AbstractDao<T, ?> dao)
	{
		this(dao, "T");
	}

	protected QueryBuilder(AbstractDao<T, ?> dao, String tablePrefix)
	{
		this.dao = dao;
		this.tablePrefix = tablePrefix;
		values = new ArrayList<Object>();
		whereConditions = new ArrayList<WhereCondition>();
	}

	private void checkOrderBuilder()
	{
		if (orderBuilder == null)
		{
			orderBuilder = new StringBuilder();
		}
		else if (orderBuilder.length() > 0)
		{
			orderBuilder.append(",");
		}
	}

	public QueryBuilder<T> where(WhereCondition cond,
			WhereCondition... condMore)
	{
		whereConditions.add(cond);
		for (WhereCondition whereCondition : condMore)
		{
			checkCondition(whereCondition);
			whereConditions.add(whereCondition);
		}
		return this;
	}

	public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2,
			WhereCondition... condMore)
	{
		whereConditions.add(or(cond1, cond2, condMore));
		return this;
	}

	public WhereCondition or(WhereCondition cond1, WhereCondition cond2,
			WhereCondition... condMore)
	{
		return combineWhereConditions(" OR ", cond1, cond2, condMore);
	}

	public WhereCondition and(WhereCondition cond1, WhereCondition cond2,
			WhereCondition... condMore)
	{
		return combineWhereConditions(" AND ", cond1, cond2, condMore);
	}

	protected WhereCondition combineWhereConditions(String combineOp,
			WhereCondition cond1, WhereCondition cond2,
			WhereCondition... condMore)
	{
		StringBuilder builder = new StringBuilder("(");
		List<Object> combinedValues = new ArrayList<Object>();

		addCondition(builder, combinedValues, cond1);
		builder.append(combineOp);
		addCondition(builder, combinedValues, cond2);

		for (WhereCondition cond : condMore)
		{
			builder.append(combineOp);
			addCondition(builder, combinedValues, cond);
		}
		builder.append(')');
		return new WhereCondition.StringCondition(builder.toString(),
				combinedValues.toArray());
	}

	protected void addCondition(StringBuilder builder, List<Object> values,
			WhereCondition condition)
	{
		checkCondition(condition);
		condition.appendTo(builder, tablePrefix);
		condition.appendValuesTo(values);
	}

	protected void checkCondition(WhereCondition whereCondition)
	{
		if (whereCondition instanceof PropertyCondition)
		{
			checkProperty(((PropertyCondition) whereCondition).property);
		}
	}

	public <J> QueryBuilder<J> join(Class<J> entityClass, Property toOneProperty)
	{
		throw new UnsupportedOperationException();
	}

	public <J> QueryBuilder<J> joinToMany(Class<J> entityClass,
			Property toManyProperty)
	{
		throw new UnsupportedOperationException();

	}

	public QueryBuilder<T> orderAsc(Property... properties)
	{
		orderAscOrDesc(" ASC", properties);
		return this;
	}

	public QueryBuilder<T> orderDesc(Property... properties)
	{
		orderAscOrDesc(" DESC", properties);
		return this;
	}

	private void orderAscOrDesc(String ascOrDescWithLeadingSpace,
			Property... properties)
	{
		for (Property property : properties)
		{
			checkOrderBuilder();
			append(orderBuilder, property);
			if (String.class.equals(property.type))
			{
				orderBuilder.append(" COLLATE LOCALIZED");
			}
			orderBuilder.append(ascOrDescWithLeadingSpace);
		}
	}

	public QueryBuilder<T> orderCustom(Property property,
			String customOrderForProperty)
	{
		checkOrderBuilder();
		append(orderBuilder, property).append(' ');
		orderBuilder.append(customOrderForProperty);
		return this;
	}

	public QueryBuilder<T> orderRaw(String rawOrder)
	{
		checkOrderBuilder();
		orderBuilder.append(rawOrder);
		return this;
	}

	protected StringBuilder append(StringBuilder builder, Property property)
	{
		checkProperty(property);
		builder.append(tablePrefix).append('.').append('\'')
				.append(property.columnName).append('\'');
		return builder;
	}

	protected void checkProperty(Property property)
	{
		if (dao != null)
		{
			Property[] properties = dao.getProperties();
			boolean found = false;
			for (Property property2 : properties)
			{
				if (property == property2)
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				throw new DaoException("Property '" + property.name
						+ "' is not part of " + dao);
			}
		}
	}

	public QueryBuilder<T> limit(int limit)
	{
		this.limit = limit;
		return this;
	}

	public QueryBuilder<T> offset(int offset)
	{
		this.offset = offset;
		return this;
	}

	public Query<T> build()
	{
		String select;
		if (joinBuilder == null || joinBuilder.length() == 0)
		{
			select = InternalQueryDaoAccess.getStatements(dao).getSelectAll();
		}
		else
		{
			select = SqlUtils.createSqlSelect(dao.getTablename(), tablePrefix,
					dao.getAllColumns());
		}
		StringBuilder builder = new StringBuilder(select);

		appendWhereClause(builder, tablePrefix);

		if (orderBuilder != null && orderBuilder.length() > 0)
		{
			builder.append(" ORDER BY ").append(orderBuilder);
		}

		int limitPosition = -1;
		if (limit != null)
		{
			builder.append(" LIMIT ?");
			values.add(limit);
			limitPosition = values.size() - 1;
		}

		int offsetPosition = -1;
		if (offset != null)
		{
			if (limit == null)
			{
				throw new IllegalStateException(
						"Offset cannot be set without limit");
			}
			builder.append(" OFFSET ?");
			values.add(offset);
			offsetPosition = values.size() - 1;
		}

		String sql = builder.toString();
		if (LOG_SQL)
		{
			DaoLog.d("Built SQL for query: " + sql);
		}

		if (LOG_VALUES)
		{
			DaoLog.d("Values for query: " + values);
		}

		return Query.create(dao, sql, values.toArray(), limitPosition,
				offsetPosition);
	}

	public DeleteQuery<T> buildDelete()
	{
		String tablename = dao.getTablename();
		String baseSql = SqlUtils.createSqlDelete(tablename, null);
		StringBuilder builder = new StringBuilder(baseSql);

		appendWhereClause(builder, tablePrefix);

		String sql = builder.toString();

		sql = sql.replace(tablePrefix + ".'", tablename + ".'");

		if (LOG_SQL)
		{
			DaoLog.d("Built SQL for delete query: " + sql);
		}
		if (LOG_VALUES)
		{
			DaoLog.d("Values for delete query: " + values);
		}

		return DeleteQuery.create(dao, sql, values.toArray());
	}

	public CountQuery<T> buildCount()
	{
		String tablename = dao.getTablename();
		String baseSql = SqlUtils.createSqlSelectCountStar(tablename,
				tablePrefix);
		StringBuilder builder = new StringBuilder(baseSql);
		appendWhereClause(builder, tablePrefix);
		String sql = builder.toString();

		if (LOG_SQL)
		{
			DaoLog.d("Built SQL for count query: " + sql);
		}
		if (LOG_VALUES)
		{
			DaoLog.d("Values for count query: " + values);
		}

		return CountQuery.create(dao, sql, values.toArray());
	}

	private void appendWhereClause(StringBuilder builder,
			String tablePrefixOrNull)
	{
		values.clear();
		if (!whereConditions.isEmpty())
		{
			builder.append(" WHERE ");
			ListIterator<WhereCondition> iter = whereConditions.listIterator();
			while (iter.hasNext())
			{
				if (iter.hasPrevious())
				{
					builder.append(" AND ");
				}
				WhereCondition condition = iter.next();
				condition.appendTo(builder, tablePrefixOrNull);
				condition.appendValuesTo(values);
			}
		}
	}

	public List<T> list()
	{
		return build().list();
	}

	public LazyList<T> listLazy()
	{
		return build().listLazy();
	}

	public LazyList<T> listLazyUncached()
	{
		return build().listLazyUncached();
	}

	public CloseableListIterator<T> listIterator()
	{
		return build().listIterator();
	}

	public T unique()
	{
		return build().unique();
	}

	public T uniqueOrThrow()
	{
		return build().uniqueOrThrow();
	}

	public long count()
	{
		return buildCount().count();
	}

}
