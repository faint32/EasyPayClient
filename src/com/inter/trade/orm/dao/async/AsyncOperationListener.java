package com.inter.trade.orm.dao.async;

public interface AsyncOperationListener
{
	void onAsyncOperationCompleted(AsyncOperation operation);
}
