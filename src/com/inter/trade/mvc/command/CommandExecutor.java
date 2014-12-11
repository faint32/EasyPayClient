
package com.inter.trade.mvc.command;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.inter.trade.exception.NoSuchCommandException;
import com.inter.trade.log.Logger;
import com.inter.trade.mvc.common.IResponseListener;
import com.inter.trade.mvc.common.Request;




/**
 * @author hzx
 * 2014年4月21日
 * @version V1.0
 */
public class CommandExecutor
{
	private final HashMap<String, Class<? extends ICommand>> commands = new HashMap<String, Class<? extends ICommand>>();

	private static final CommandExecutor instance = new CommandExecutor();
	private boolean initialized = false;

	public CommandExecutor()
	{
		ensureInitialized();
	}

	public static CommandExecutor getInstance()
	{
		return instance;
	}

	public void ensureInitialized()
	{
		if (!initialized)
		{
			initialized = true;
			Logger.i(CommandExecutor.this, "CommandExecutor初始化");
			CommandQueueManager.getInstance().initialize();
			Logger.i(CommandExecutor.this, "CommandExecutor初始化");
		}
	}

	/**
	 * 所有命令终止或标记为结束
	 */
	public void terminateAll()
	{

	}

	/**
	 * 命令入列
	 * 
	 * @param commandKey
	 *            命令ID
	 * @param request
	 *            提交的参数
	 * @param listener
	 *            响应监听器
	 * @throws TANoSuchCommandException
	 */
	public void enqueueCommand(String commandKey, Request request,
			IResponseListener listener) throws NoSuchCommandException
	{
		final ICommand cmd = getCommand(commandKey);
		enqueueCommand(cmd, request, listener);
	}

	public void enqueueCommand(ICommand command, Request request,
			IResponseListener listener) throws NoSuchCommandException
	{
		if (command != null)
		{
			command.setRequest(request);
			command.setResponseListener(listener);
			CommandQueueManager.getInstance().enqueue(command);
		}
	}

	public void enqueueCommand(ICommand command, Request request)
			throws NoSuchCommandException
	{
		enqueueCommand(command, null, null);
	}

	public void enqueueCommand(ICommand command)
			throws NoSuchCommandException
	{
		enqueueCommand(command, null);
	}

	private ICommand getCommand(String commandKey)
			throws NoSuchCommandException
	{
		ICommand rv = null;

		if (commands.containsKey(commandKey))
		{
			Class<? extends ICommand> cmd = commands.get(commandKey);
			if (cmd != null)
			{
				int modifiers = cmd.getModifiers();
				if ((modifiers & Modifier.ABSTRACT) == 0
						&& (modifiers & Modifier.INTERFACE) == 0)
				{
					try
					{
						rv = cmd.newInstance();
					} catch (Exception e)
					{
						throw new NoSuchCommandException("没发现" + commandKey
								+ "命令");
					}
				} else
				{
					throw new NoSuchCommandException("没发现" + commandKey
							+ "命令");
				}
			}
		}

		return rv;
	}

	public void registerCommand(String commandKey,
			Class<? extends ICommand> command)
	{
		if (command != null)
		{
			commands.put(commandKey, command);
		}
	}

	public void unregisterCommand(String commandKey)
	{
		commands.remove(commandKey);
	}
}
