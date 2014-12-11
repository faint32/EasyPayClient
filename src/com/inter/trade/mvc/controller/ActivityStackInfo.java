package com.inter.trade.mvc.controller;

import com.inter.trade.mvc.common.Request;
import com.inter.trade.mvc.common.Response;
import com.inter.trade.ui.BaseActivity;


/**
 * @author hzx 
 * 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class ActivityStackInfo
{
	private Class<? extends BaseActivity> activityClass;
	private String commandKey;
	private Request request;
	private boolean record;
	private boolean resetStack;
	private Response response;

	public ActivityStackInfo()
	{
	}

	public ActivityStackInfo(String commandKey, Request request,
			boolean record, boolean resetStack)
	{
		this.commandKey = commandKey;
		this.request = request;
		this.record = record;
		this.resetStack = resetStack;
	}

	public ActivityStackInfo(Class<? extends BaseActivity> activityClass,
			String commandKey, Request request)
	{
		this.activityClass = activityClass;
		this.commandKey = commandKey;
		this.request = request;
	}

	public ActivityStackInfo(Class<? extends BaseActivity> activityClass,
			String commandKey, Request request, boolean record,
			boolean resetStack)
	{
		this.activityClass = activityClass;
		this.commandKey = commandKey;
		this.request = request;
		this.record = record;
		this.resetStack = resetStack;
	}

	public Class<? extends BaseActivity> getActivityClass()
	{
		return activityClass;
	}

	public void setActivityClass(Class<? extends BaseActivity> activityClass)
	{
		this.activityClass = activityClass;
	}

	public String getCommandKey()
	{
		return commandKey;
	}

	public void setCommandKey(String commandKey)
	{
		this.commandKey = commandKey;
	}

	public Request getRequest()
	{
		return request;
	}

	public void setRequest(Request request)
	{
		this.request = request;
	}

	public boolean isRecord()
	{
		return record;
	}

	public void setRecord(boolean record)
	{
		this.record = record;
	}

	public Response getResponse()
	{
		return response;
	}

	public void setResponse(Response response)
	{
		this.response = response;
	}

	public boolean isResetStack()
	{
		return resetStack;
	}

	public void setResetStack(boolean resetStack)
	{
		this.resetStack = resetStack;
	}
}
