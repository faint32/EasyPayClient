package com.inter.trade.mvc.command;

import com.inter.trade.log.Logger;


/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public final class CommandQueueManager
{
	private static CommandQueueManager instance;
	private boolean initialized = false;
	private ThreadPool pool;
	private CommandQueue queue;

	private CommandQueueManager()
	{
	}

	public static CommandQueueManager getInstance()
	{
		if (instance == null)
		{
			instance = new CommandQueueManager();
		}
		return instance;
	}

	public void initialize()
	{
		Logger.i(CommandQueueManager.this, "准备初始化！");
		if (!initialized)
		{
			Logger.i(CommandQueueManager.this, "正在初始化！");
			queue = new CommandQueue();
			pool = ThreadPool.getInstance();
			Logger.i(CommandQueueManager.this, "完成初始化！");

			pool.start();
			initialized = true;
		}
		Logger.i(CommandQueueManager.this, "初始化完成！");
	}

	/**
	 * 从队列中获取Command
	 * 
	 * @return TAICommand
	 */
	public ICommand getNextCommand()
	{
		Logger.i(CommandQueueManager.this, "获取Command！");
		ICommand cmd = queue.getNextCommand();
		Logger.i(CommandQueueManager.this, "获取Command" + cmd + "完成！");
		return cmd;
	}

	/**
	 * 添加Command到队列中
	 */
	public void enqueue(ICommand cmd)
	{
		Logger.i(CommandQueueManager.this, "添加" + cmd + "开始");
		queue.enqueue(cmd);
		Logger.i(CommandQueueManager.this, "添加" + cmd + "完成");
	}

	/**
	 * 清除队列
	 */
	public void clear()
	{
		queue.clear();
	}

	/**
	 * 关闭队列
	 */
	public void shutdown()
	{
		if (initialized)
		{
			queue.clear();
			pool.shutdown();
			initialized = false;
		}
	}
}
