package com.inter.trade.ioc.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 主键配置
 * 不配置的时候默认找类的id或_id字段作为主键，column不配置的是默认为字段名，如果不设置主键，自动生成ID
 * @author hzx 
 * 2014年4月21日
 * @version V1.0
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Field
{
	/**
	 * 设置配置的名称
	 * 2014年4月21日
	 * @return
	 */
	public String name() default "";
	
	/**
	 * 设置配置的默认值
	 * 2014年4月21日
	 * @return
	 */
	public String defaultValue() default "";
}
