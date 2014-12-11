package com.inter.trade.util;

import java.lang.reflect.Field;
import java.util.Date;

import com.inter.trade.ioc.annotation.Transparent;

/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public class ReflectUtils
{
	/**
	 * 检测实体属性是否已经被标注为 不被识别 2014年4月21日
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isTransient(Field field)
	{
		return field.getAnnotation(Transparent.class) != null;
	}

	/**
	 * 是否为基本的数据类型 2014年4月21日
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isBaseDateType(Field field)
	{
		Class<?> clazz = field.getType();
		return clazz.equals(String.class) || clazz.equals(Integer.class)
				|| clazz.equals(Byte.class) || clazz.equals(Long.class)
				|| clazz.equals(Double.class) || clazz.equals(Float.class)
				|| clazz.equals(Character.class) || clazz.equals(Short.class)
				|| clazz.equals(Boolean.class) || clazz.equals(Date.class)
				|| clazz.equals(java.util.Date.class)
				|| clazz.equals(java.sql.Date.class) || clazz.isPrimitive();
	}

	/**
	 * 获得配置名 2014年4月21日
	 * 
	 * @param field
	 * @return
	 */
	public static String getFieldName(Field field)
	{
		com.inter.trade.ioc.annotation.Field column = field
				.getAnnotation(com.inter.trade.ioc.annotation.Field.class);
		if (column != null && column.name().trim().length() != 0) { return column
				.name(); }
		return field.getName();
	}
}
