
package com.inter.trade.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author hzx
 * 2014年4月21日
 * @version V1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface InjectView
{
	/**
	 * View的ID
	 * 2014年4月21日
	 * @return
	 */
	public int id() default -1;
	
	/**
	 * View的点击事件
	 * 2014年4月21日
	 * @return
	 */
	public String click() default "";
	/**
	 * View的长按键事件
	 * 2014年4月21日
	 * @return
	 */
	public String longClick() default "";
	/**
	 * View的焦点改变事件
	 * 2014年4月21日
	 * @return
	 */
	public String focusChange() default "";
	/**
	 * View的键盘事件
	 * 2014年4月21日
	 * @return
	 */
	public String key() default "";
	/**
	 * View的触摸事件
	 * 2014年4月21日
	 * @return
	 */
	public String touch() default "";
	
	
}
