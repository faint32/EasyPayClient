package com.inter.trade.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author LiGuohui
 * @since 2013-1-28 下午10:33:30
 * @version 1.0.0
 */
public class VersionUtil
{
	/**
	 * 获取当前版本号
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		// 获取packagemanager的实例

		return "1.0";
	}

	/**
	 * 获取程序的versionCode
	 * 
	 * @param context
	 * @return
	 * @throw
	 * @return String
	 */
	public static String getVersionCode(Context context)
	{
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try
		{
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int versionCode = packInfo.versionCode;
			return versionCode + "";
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return "1.0";
	}

}
