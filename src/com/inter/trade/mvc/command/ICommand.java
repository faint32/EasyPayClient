package com.inter.trade.mvc.command;

import com.inter.trade.mvc.common.IResponseListener;
import com.inter.trade.mvc.common.Request;
import com.inter.trade.mvc.common.Response;


/**
 * @author hzx 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public interface ICommand
{
	Request getRequest();

	void setRequest(Request request);

	Response getResponse();

	void setResponse(Response response);

	void execute();

	IResponseListener getResponseListener();

	void setResponseListener(IResponseListener listener);

	void setTerminated(boolean terminated);

	boolean isTerminated();
}
