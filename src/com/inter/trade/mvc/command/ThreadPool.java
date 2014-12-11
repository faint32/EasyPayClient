package com.inter.trade.mvc.command;

import com.inter.trade.log.Logger;


/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public class ThreadPool
{
	// 线程的最大数量
	private static final int MAX_THREADS_COUNT = 2;
	private CommandThread threads[] = null;
	private boolean started = false;
	private static ThreadPool instance;

	private ThreadPool()
	{

	}

	public static ThreadPool getInstance()
	{
		if (instance == null)
		{
			instance = new ThreadPool();
		}
		return instance;
	}

	public void start()
	{
		if (!started)
		{
			Logger.i(ThreadPool.this, "线程池开始运行！");
			int threadCount = MAX_THREADS_COUNT;

			threads = new CommandThread[threadCount];
			for (int threadId = 0; threadId < threadCount; threadId++)
			{
				threads[threadId] = new CommandThread(threadId);
				threads[threadId].start();
			}
			started = true;
			Logger.i(ThreadPool.this, "线程池运行完成！");
		}
	}

	public void shutdown()
	{
		Logger.i(ThreadPool.this, "关闭所有线程！");
		if (started)
		{
			for (CommandThread thread : threads)
			{
				thread.stop();
			}
			threads = null;
			started = false;
		}
		Logger.i(ThreadPool.this, "关闭完所有线程！");
	}
}
