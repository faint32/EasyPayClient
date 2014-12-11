package com.inter.trade.mvc.command;

import com.inter.trade.mvc.common.IResponseListener;
import com.inter.trade.mvc.common.Request;
import com.inter.trade.mvc.common.Response;


/**
 * @author hzx 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class BaseCommand implements ICommand
{
	private Request request;
	private Response response;
	private IResponseListener responseListener;
	private boolean terminated;

	@Override
	public void execute()
	{

	}

	@Override
	public void setTerminated(boolean terminated)
	{
		this.terminated = terminated;
	}

	@Override
	public boolean isTerminated()
	{
		return terminated;
	}

	@Override
	public Request getRequest()
	{
		return request;
	}

	@Override
	public void setRequest(Request request)
	{
		this.request = request;
	}

	@Override
	public Response getResponse()
	{
		return response;
	}

	@Override
	public void setResponse(Response response)
	{
		this.response = response;
	}

	@Override
	public IResponseListener getResponseListener()
	{
		return responseListener;
	}

	@Override
	public void setResponseListener(IResponseListener responseListener)
	{
		this.responseListener = responseListener;
	}

}
