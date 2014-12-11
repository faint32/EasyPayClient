package com.inter.trade.net;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppManager
{
	private static Stack<Activity> mActivityStack;
	private static AppManager mInstance;

	private AppManager()
	{

	}

	/**
	 * 获取应用管理器
	 * 
	 * @author hzx 2014年7月8日
	 * @return
	 */
	public static AppManager getAppManager()
	{
		if (null == mInstance)
		{
			mInstance = new AppManager();
		}
		return mInstance;
	}

	/**
	 * 添加Activity到堆栈中
	 * 
	 * @author hzx 2014年7月8日
	 * @param activity
	 */
	public void addActivity(Activity activity)
	{
		if (null == mActivityStack)
		{
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	/**
	 * 获取栈顶的Activity
	 * 
	 * @author hzx 2014年7月8日
	 * @return
	 */
	public Activity getCurrActivity()
	{
		return mActivityStack.lastElement();
	}

	/**
	 * 结束
	 * 
	 * @author hzx 2014年7月8日
	 */
	public void finishActivity()
	{
		Activity activity = mActivityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity)
	{
		if (null != activity)
		{
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 移除指定的Activity
	 */
	public void removeActivity(Activity activity)
	{
		if (activity != null)
		{
			mActivityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls)
	{
		for (Activity activity : mActivityStack)
		{
			if (activity.getClass().equals(cls))
			{
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity()
	{
		for (int i = 0, size = mActivityStack.size(); i < size; i++)
		{
			if (null != mActivityStack.get(i))
			{
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 * 
	 * @param context
	 *            上下文
	 * @param isBackground
	 *            是否开开启后台运行
	 */
	public void AppExit(Context context, Boolean isBackground)
	{
		try
		{
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
		}
		catch (Exception e)
		{

		}
		finally
		{
			// 注意，如果您有后台程序运行，请不要支持此句子
			if (!isBackground)
			{
				System.exit(0);
			}
		}
	}
}
