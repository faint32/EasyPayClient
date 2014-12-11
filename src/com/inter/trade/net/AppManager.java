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
	 * ��ȡӦ�ù�����
	 * 
	 * @author hzx 2014��7��8��
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
	 * ���Activity����ջ��
	 * 
	 * @author hzx 2014��7��8��
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
	 * ��ȡջ����Activity
	 * 
	 * @author hzx 2014��7��8��
	 * @return
	 */
	public Activity getCurrActivity()
	{
		return mActivityStack.lastElement();
	}

	/**
	 * ����
	 * 
	 * @author hzx 2014��7��8��
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
	 * �Ƴ�ָ����Activity
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
	 * ����ָ��������Activity
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
	 * ��������Activity
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
	 * �˳�Ӧ�ó���
	 * 
	 * @param context
	 *            ������
	 * @param isBackground
	 *            �Ƿ񿪿�����̨����
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
			// ע�⣬������к�̨�������У��벻Ҫ֧�ִ˾���
			if (!isBackground)
			{
				System.exit(0);
			}
		}
	}
}
