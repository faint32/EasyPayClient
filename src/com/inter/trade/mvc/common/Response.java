package com.inter.trade.mvc.common;

import com.inter.trade.entity.BaseEntity;


/**
 * @author hzx 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class Response extends BaseEntity
{
	/**
	 * @author hzx
	 */
	private static final long serialVersionUID = -3851462574243050495L;
	private Object tag;
	private Object data;
	private String activityKey;
	private int activityKeyResID;

	public Response()
	{

	}

	public Response(Object tag, Object data)
	{
		this.tag = tag;
		this.data = data;
	}

	public Object getTag()
	{
		return tag;
	}

	public void setTag(Object tag)
	{
		this.tag = tag;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public int getActivityKeyResID()
	{
		return activityKeyResID;
	}

	public void setActivityKeyResID(int activityKeyResID)
	{
		this.activityKeyResID = activityKeyResID;
	}

	public String getActivityKey()
	{
		return activityKey;
	}

	public void setActivityKey(String activityKey)
	{
		this.activityKey = activityKey;
	}

}
