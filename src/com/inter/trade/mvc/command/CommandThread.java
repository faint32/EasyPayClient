package com.inter.trade.mvc.command;

import com.inter.trade.log.Logger;


/**
 * @author hzx 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class CommandThread implements Runnable
{

	private int threadId;
	private Thread thread = null;
	private boolean running = false;
	private boolean stop = false;

	public CommandThread(int threadId)
	{
		Logger.i(CommandThread.this, "CommandThread::ctor");
		this.threadId = threadId;
		thread = new Thread(this);
	}

	@Override
	public void run()
	{
		Logger.i(CommandThread.this, "CommandThread::run-enter");
		while (!stop)
		{
			Logger.i(CommandThread.this, "CommandThread::get-next-command");
			ICommand cmd = CommandQueueManager.getInstance().getNextCommand();
			Logger.i(CommandThread.this, "CommandThread::to-execute");
			cmd.execute();
			Logger.i(CommandThread.this, "CommandThread::executed");
		}
		Logger.i(CommandThread.this, "CommandThread::run-exit");
	}

	public void start()
	{
		thread.start();
		running = true;
	}

	public void stop()
	{
		stop = true;
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}

	public int getThreadId()
	{
		return threadId;
	}

}
