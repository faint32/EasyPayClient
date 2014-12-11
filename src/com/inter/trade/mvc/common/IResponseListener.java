package com.inter.trade.mvc.common;

/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public interface IResponseListener
{
	/**
	 * 开始加载数据 2013年12月12日
	 */
	void onStart();

	/**
	 * 数据加载成功 2013年12月12日
	 * 
	 * @param response
	 */
	void onSuccess(Response response);

	/**
	 * 正在加载中 2013年12月12日
	 * 
	 * @param response
	 */
	void onRuning(Response response);

	/**
	 * 数据加载失败 2013年12月12日
	 * 
	 * @param response
	 */
	void onFailure(Response response);

	/**
	 * 完成数据请求 2013年12月12日
	 */
	void onFinish();
}
