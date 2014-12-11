package com.inter.trade.ui;

import com.inter.trade.EasyPayApplication;
import com.inter.trade.log.Logger;
import com.inter.trade.mvc.command.IdentityCommand;
import com.inter.trade.mvc.common.IResponseListener;
import com.inter.trade.mvc.common.Request;
import com.inter.trade.mvc.common.Response;
import com.inter.trade.util.NetWorkUtil.netType;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
@SuppressLint("DefaultLocale")
public class BaseActivity extends FragmentActivity
{
	/** 模块的名字 */
	private String moduleName = "";
	/** 布局文件的名字 */
	private String layouName = "";
	private static final int DIALOG_ID_PROGRESS_DEFAULT = 0x174980;
	private static final String TAIDENTITYCOMMAND = "taidentitycommand";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		notifiyApplicationActivityCreating();
		onPreOnCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		getBaseApplication().getAppManager().addActivity(this);
		initActivity();
		onAfterOnCreate(savedInstanceState);
		notifiyApplicationActivityCreated();
	}

	public EasyPayApplication getBaseApplication()
	{
		return (EasyPayApplication) getApplication();
	}

	private void notifiyApplicationActivityCreating()
	{
		getBaseApplication().onActivityCreating(this);
	}

	private void notifiyApplicationActivityCreated()
	{
		getBaseApplication().onActivityCreated(this);
	}

	protected void onPreOnCreate(Bundle savedInstanceState)
	{
		getBaseApplication().registerCommand(TAIDENTITYCOMMAND,
				IdentityCommand.class);
	}

	private void initActivity()
	{
		// 初始化模块名
		getModuleName();
		// 初始化布局名
		getLayouName();
		// 加载类注入器
		initInjector();
		// 自动加载默认布局
		loadDefautLayout();
	}

	protected void onAfterOnCreate(Bundle savedInstanceState)
	{
	}

	/**
	 * 初始化注入器
	 */
	private void initInjector()
	{
		getBaseApplication().getInjector().injectResource(this);
		getBaseApplication().getInjector().inject(this);
	}

	/**
	 * 自动加载默认布局
	 */
	private void loadDefautLayout()
	{
		try
		{
			int layoutResID = getBaseApplication().getLayoutLoader()
					.getLayoutID(layouName);
			setContentView(layoutResID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		// 由于view必须在视图记载之后添加注入
		getBaseApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	@Override
	public void setContentView(View view, LayoutParams params)
	{
		super.setContentView(view, params);
		// 由于view必须在视图记载之后添加注入
		getBaseApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	@Override
	public void setContentView(View view)
	{
		super.setContentView(view);
		// 由于view必须在视图记载之后添加注入
		getBaseApplication().getInjector().injectView(this);
		onAfterSetContentView();
	}

	protected void onAfterSetContentView()
	{

	}

	/**
	 * 获取模块的名字
	 */
	public String getModuleName()
	{
		String moduleName = this.moduleName;
		if (moduleName == null || moduleName.equalsIgnoreCase(""))
		{
			moduleName = getClass().getName().substring(0,
					getClass().getName().length() - 8);
			String arrays[] = moduleName.split("\\.");
			this.moduleName = moduleName = "activity_"
					+ arrays[arrays.length - 1].toLowerCase();
		}
		return moduleName;
	}

	/**
	 * 设置模块的名字
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * 获取布局文件名
	 * 
	 * @return布局文件名
	 */
	public String getLayouName()
	{
		String layouName = this.layouName;
		if (layouName == null || layouName.equalsIgnoreCase(""))
		{
			this.layouName = this.moduleName;
		}
		return layouName;
	}

	/**
	 * 设置布局文件名
	 */
	protected void setLayouName(String layouName)
	{
		this.layouName = layouName;
	}

	public void preProcessData(Response response)
	{

	}

	public void processData(Response response)
	{

	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case DIALOG_ID_PROGRESS_DEFAULT:
			ProgressDialog dlg = new ProgressDialog(this);
			dlg.setMessage("正在加载...");
			dlg.setCancelable(true);
			return dlg;
		default:
			return super.onCreateDialog(id);

		}
	}

	public final void doCommand(int resId, Request request)
	{
		String commandKey = getString(resId);
		doCommand(commandKey, request, null, true);
	}

	public final void doCommand(String commandKey, Request request)
	{
		doCommand(commandKey, request, null, true);
	}

	public final void doCommand(int resId, Request request,
			IResponseListener listener)
	{
		String commandKey = getString(resId);
		doCommand(commandKey, request, listener, true);
	}

	public final void doCommand(String commandKey, Request request,
			IResponseListener listener)
	{
		doCommand(commandKey, request, listener, true);
	}

	public final void doCommand(int resId, Request request,
			IResponseListener listener, boolean showProgress)
	{
		String commandKey = getString(resId);
		Logger.i(BaseActivity.this, "go with cmdid=" + commandKey
				+ ", request: " + request);
		doCommand(commandKey, request, listener, showProgress, true);
	}

	public final void doCommand(String commandKey, Request request,
			IResponseListener listener, boolean showProgress)
	{
		Logger.i(BaseActivity.this, "go with cmdid=" + commandKey
				+ ", request: " + request);
		doCommand(commandKey, request, listener, showProgress, true);
	}

	public final void doCommand(int resId, Request request,
			IResponseListener listener, boolean showProgress, boolean record)
	{
		String commandKey = getString(resId);
		Logger.i(BaseActivity.this, "go with cmdid=" + commandKey
				+ ", record: " + record + ", request: " + request);
		doCommand(commandKey, request, listener, showProgress, record, false);
	}

	public final void doCommand(String commandKey, Request request,
			IResponseListener listener, boolean showProgress, boolean record)
	{
		Logger.i(BaseActivity.this, "go with cmdid=" + commandKey
				+ ", record: " + record + ", request: " + request);
		doCommand(commandKey, request, listener, showProgress, record, false);
	}

	public final void doCommand(int resId, Request request,
			IResponseListener listener, boolean showProgress, boolean record,
			boolean resetStack)
	{
		String commandKey = getString(resId);
		doCommand(commandKey, request, listener, showProgress, record,
				resetStack);
	}

	public final void doCommand(String commandKey, Request request,
			IResponseListener listener, boolean showProgress, boolean record,
			boolean resetStack)
	{
		if (showProgress)
		{
			showProgress();
		}
		getBaseApplication().doCommand(commandKey, request, listener, record,
				resetStack);
	}

	/**
	 * 返回
	 */
	public final void back()
	{
		getBaseApplication().back();
	}

	/**
	 * 需要自定义进度条，请重写
	 */
	protected void showProgress()
	{
		showDialog(DIALOG_ID_PROGRESS_DEFAULT);
	}

	/**
	 * 隐藏进度跳，需要重写的请重写
	 */
	protected void hideProgress()
	{
		try
		{
			removeDialog(DIALOG_ID_PROGRESS_DEFAULT);
		}
		catch (IllegalArgumentException iae)
		{
		}
	}

	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(netType type)
	{

	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect()
	{

	}

	@Override
	public void finish()
	{
		getBaseApplication().getAppManager().removeActivity(this);
		super.finish();
	}

	/**
	 * 退出应用程序
	 * 
	 * @param isBackground
	 *            是否开开启后台运行,如果为true则为后台运行
	 */
	public void exitApp(Boolean isBackground)
	{
		getBaseApplication().exitApp(isBackground);
	}

	/**
	 * 退出应用程序
	 * 
	 */
	public void exitApp()
	{
		getBaseApplication().exitApp(false);
	}

	/**
	 * 退出应用程序，且在后台运行
	 * 
	 */
	public void exitAppToBackground()
	{
		getBaseApplication().exitApp(true);
	}

	/**
	 * 运行activity
	 * 
	 * @param activityResID
	 */
	public final void doActivity(int activityResID)
	{
		doActivity(activityResID, null);
	}

	public final void doActivity(int activityResID, Bundle bundle)
	{
		Request request = new Request();
		request.setData(bundle);
		request.setActivityKeyResID(activityResID);
		// 启动activity
		doCommand(TAIDENTITYCOMMAND, request);
	}

	@Override
	public void onBackPressed()
	{
		back();
	}
}
