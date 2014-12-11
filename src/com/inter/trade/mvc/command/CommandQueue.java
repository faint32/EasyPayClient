package com.inter.trade.mvc.command;

import java.util.concurrent.LinkedBlockingQueue;

import com.inter.trade.log.Logger;


/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public class CommandQueue
{
	private LinkedBlockingQueue<ICommand> theQueue = new LinkedBlockingQueue<ICommand>();

	public CommandQueue()
	{
		Logger.i(CommandQueue.this, "初始化Command队列");
	}

	public void enqueue(ICommand cmd)
	{
		Logger.i(CommandQueue.this, "添加Command到队列");
		theQueue.add(cmd);
	}

	public synchronized ICommand getNextCommand()
	{
		Logger.i(CommandQueue.this, "获取Command");
		ICommand cmd = null;
		try
		{
			Logger.i(CommandQueue.this, "CommandQueue::to-take");
			cmd = theQueue.take();
			Logger.i(CommandQueue.this, "CommandQueue::taken");
		}
		catch (InterruptedException e)
		{
			Logger.i(CommandQueue.this, "没有获取到Command");
			e.printStackTrace();
		}
		Logger.i(CommandQueue.this, "返回Command" + cmd);
		return cmd;
	}

	public synchronized void clear()
	{
		Logger.i(CommandQueue.this, "清空所有Command");
		theQueue.clear();
	}
}
