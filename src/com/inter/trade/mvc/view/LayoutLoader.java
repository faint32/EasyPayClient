package com.inter.trade.mvc.view;

import java.lang.reflect.Field;

import com.inter.trade.exception.NoSuchNameLayoutException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;


/**
 * @author hzx 2014年4月21日
 * @version V1.0
 */
public class LayoutLoader implements ILayoutLoader
{

	private static LayoutLoader instance;
	private Context mContext;

	private LayoutLoader(Context context)
	{
		this.mContext = context;
	}

	/**
	 * 获得系统资源类
	 * 
	 * @return TASystemResLoader对象
	 */

	public static LayoutLoader getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new LayoutLoader(context);
		}
		return instance;
	}

	/**
	 * 获得布局ID
	 * 
	 * @param context
	 * @param cls
	 * @param resIDName
	 * @return
	 * @throws NameNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws TAResIDNotFoundException
	 */
	@Override
	public int getLayoutID(String resIDName) throws NameNotFoundException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, NoSuchNameLayoutException
	{
		int resID = readResID("layout", resIDName);
		if (resID == 0) { throw new NoSuchNameLayoutException(); }
		return resID;
	}

	/**
	 * @param context
	 * @param resIDName
	 *            资源id名字
	 * @return
	 * @throws NameNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public int readResID(String type, String resIDName)
			throws NameNotFoundException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException
	{
		String packageName;
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		packageName = pi.packageName;
		if (packageName == null || packageName.equalsIgnoreCase("")) { throw new NameNotFoundException(
				"没有获取到系统包名！"); }
		packageName = packageName + ".R";
		Class<?> clazz = Class.forName(packageName);
		Class<?> cls = readResClass(clazz, packageName + "$" + type);
		if (cls == null) { throw new NameNotFoundException("没发现资源包名！"); }
		return readResID(cls, resIDName);

	}

	/**
	 * 返回资源在R文件中生成的类
	 * 
	 * @param cls
	 *            资源类名
	 * @param respackageName
	 *            资源的包名
	 * @return 返回资源在R文件中生成的类
	 */
	public Class<?> readResClass(Class<?> cls, String respackageName)
	{
		Class<?>[] classes = cls.getDeclaredClasses();
		for (int i = 0; i < classes.length; i++)
		{
			Class<?> tempClass = classes[i];
			Log.v("TAReadSystemRes", tempClass.getName());
			if (tempClass.getName().equalsIgnoreCase(respackageName)) { return tempClass; }
		}
		return null;
	}

	/**
	 * 读取R资源文件
	 * 
	 * @param cls
	 * @param string
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public int readResID(Class<?> cls, String resIDName)
			throws IllegalArgumentException, IllegalAccessException
	{

		Field[] fields = cls.getDeclaredFields();
		for (int j = 0; j < fields.length; j++)
		{
			if (fields[j].getName().equalsIgnoreCase(resIDName)) { return fields[j]
					.getInt(cls); }
		}
		return 0;
	}

}
