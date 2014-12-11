package com.inter.trade.orm.dao.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.inter.trade.orm.dao.AbstractDao;
import com.inter.trade.orm.dao.DaoException;
import com.inter.trade.orm.dao.Property;
import com.inter.trade.orm.dao.scope.IdentityScope;
import com.inter.trade.orm.dao.scope.IdentityScopeLong;
import com.inter.trade.orm.dao.scope.IdentityScopeObject;
import com.inter.trade.orm.dao.scope.IdentityScopeType;

import android.database.sqlite.SQLiteDatabase;


public final class DaoConfig implements Cloneable
{

	public final SQLiteDatabase db;
	public final String tablename;
	public final Property[] properties;

	public final String[] allColumns;
	public final String[] pkColumns;
	public final String[] nonPkColumns;

	public final Property pkProperty;
	public final boolean keyIsNumeric;
	public final TableStatements statements;

	private IdentityScope<?, ?> identityScope;

	public DaoConfig(SQLiteDatabase db,
			Class<? extends AbstractDao<?, ?>> daoClass)
	{
		this.db = db;
		try
		{
			this.tablename = (String) daoClass.getDeclaredField("TABLENAME").get(null);
			Property[] properties = reflectProperties(daoClass);
			this.properties = properties;

			allColumns = new String[properties.length];

			List<String> pkColumnList = new ArrayList<String>();
			List<String> nonPkColumnList = new ArrayList<String>();
			Property lastPkProperty = null;
			for (int i = 0; i < properties.length; i++)
			{
				Property property = properties[i];
				String name = property.columnName;
				allColumns[i] = name;
				if (property.primaryKey)
				{
					pkColumnList.add(name);
					lastPkProperty = property;
				}
				else
				{
					nonPkColumnList.add(name);
				}
			}
			String[] nonPkColumnsArray = new String[nonPkColumnList.size()];
			nonPkColumns = nonPkColumnList.toArray(nonPkColumnsArray);
			String[] pkColumnsArray = new String[pkColumnList.size()];
			pkColumns = pkColumnList.toArray(pkColumnsArray);

			pkProperty = pkColumns.length == 1 ? lastPkProperty : null;
			statements = new TableStatements(db, tablename, allColumns,
					pkColumns);

			if (pkProperty != null)
			{
				Class<?> type = pkProperty.type;
				keyIsNumeric = type.equals(long.class)
						|| type.equals(Long.class) || type.equals(int.class)
						|| type.equals(Integer.class)
						|| type.equals(short.class) || type.equals(Short.class)
						|| type.equals(byte.class) || type.equals(Byte.class);
			}
			else
			{
				keyIsNumeric = false;
			}

		}
		catch (Exception e)
		{
			throw new DaoException("Could not init DAOConfig", e);
		}
	}

	private static Property[] reflectProperties(
			Class<? extends AbstractDao<?, ?>> daoClass)
			throws ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException
	{
		Class<?> propertiesClass = Class.forName(daoClass.getName()
				+ "$Properties");
		Field[] fields = propertiesClass.getDeclaredFields();

		ArrayList<Property> propertyList = new ArrayList<Property>();
		final int modifierMask = Modifier.STATIC | Modifier.PUBLIC;
		for (Field field : fields)
		{
			if ((field.getModifiers() & modifierMask) == modifierMask)
			{
				Object fieldValue = field.get(null);
				if (fieldValue instanceof Property)
				{
					propertyList.add((Property) fieldValue);
				}
			}
		}

		Property[] properties = new Property[propertyList.size()];
		for (Property property : propertyList)
		{
			if (properties[property.ordinal] != null)
			{
				throw new DaoException("Duplicate property ordinals");
			}
			properties[property.ordinal] = property;
		}
		return properties;
	}

	public DaoConfig(DaoConfig source)
	{
		db = source.db;
		tablename = source.tablename;
		properties = source.properties;
		allColumns = source.allColumns;
		pkColumns = source.pkColumns;
		nonPkColumns = source.nonPkColumns;
		pkProperty = source.pkProperty;
		statements = source.statements;
		keyIsNumeric = source.keyIsNumeric;
	}

	@Override
	public DaoConfig clone()
	{
		return new DaoConfig(this);
	}

	public IdentityScope<?, ?> getIdentityScope()
	{
		return identityScope;
	}

	public void setIdentityScope(IdentityScope<?, ?> identityScope)
	{
		this.identityScope = identityScope;
	}

	@SuppressWarnings("rawtypes")
	public void initIdentityScope(IdentityScopeType type)
	{
		if (type == IdentityScopeType.None)
		{
			identityScope = null;
		}
		else if (type == IdentityScopeType.Session)
		{
			if (keyIsNumeric)
			{
				identityScope = new IdentityScopeLong();
			}
			else
			{
				identityScope = new IdentityScopeObject();
			}
		}
		else
		{
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}

}
