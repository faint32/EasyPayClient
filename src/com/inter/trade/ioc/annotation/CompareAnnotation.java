package com.inter.trade.ioc.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author hzx 
 * 2014年4月21日
 * @version V1.0
 */

@Retention(RUNTIME)
@Target(
{ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface CompareAnnotation
{
	/**
	 * 选择进行排序属性标识 
	 * 2014年4月21日
	 * @return 返回标识符
	 */
	public int sortFlag() default 0;
}
