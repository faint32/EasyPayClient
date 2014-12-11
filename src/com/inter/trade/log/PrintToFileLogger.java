package com.inter.trade.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.inter.trade.EasyPayApplication;
import com.inter.trade.util.AndroidVersionCheckUtils;
import com.inter.trade.util.ExternalOverFroyoUtils;
import com.inter.trade.util.ExternalUnderFroyoUtils;

import android.annotation.SuppressLint;



/**
 * @author hzx 
 * 2014Äê4ÔÂ21ÈÕ
 * @version V1.0
 */
public class PrintToFileLogger implements ILogger
{

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;
	private String mPath;
	private Writer mWriter;

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");
	private String basePath = "";
	private static String LOG_DIR = "log";
	private static String BASE_FILENAME = "ta.log";
	private File logDir;

	public PrintToFileLogger()
	{

	}

	@Override
	public void open()
	{
		if (AndroidVersionCheckUtils.hasFroyo())
		{
			logDir = ExternalOverFroyoUtils.getDiskCacheDir(EasyPayApplication
					.getInstance().getApplicationContext(), LOG_DIR);
		}
		else
		{
			logDir = ExternalUnderFroyoUtils.getDiskCacheDir(EasyPayApplication
					.getInstance().getApplicationContext(), LOG_DIR);
		}
		if (!logDir.exists())
		{
			logDir.mkdirs();
			try
			{
				new File(logDir, ".nomedia").createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		basePath = logDir.getAbsolutePath() + "/" + BASE_FILENAME;
		try
		{
			File file = new File(basePath + "-" + getCurrentTimeString());
			mPath = file.getAbsolutePath();
			mWriter = new BufferedWriter(new FileWriter(mPath), 2048);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentTimeString()
	{
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(now);
	}

	public String getPath()
	{
		return mPath;
	}

	@Override
	public void d(String tag, String message)
	{
		println(DEBUG, tag, message);
	}

	@Override
	public void e(String tag, String message)
	{
		println(ERROR, tag, message);
	}

	@Override
	public void i(String tag, String message)
	{
		println(INFO, tag, message);
	}

	@Override
	public void v(String tag, String message)
	{
		println(VERBOSE, tag, message);
	}

	@Override
	public void w(String tag, String message)
	{
		println(WARN, tag, message);
	}

	@Override
	public void println(int priority, String tag, String message)
	{
		String printMessage = "";
		switch (priority)
		{
		case VERBOSE:
			printMessage = "[V]|"
					+ tag
					+ "|"
					+ EasyPayApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case DEBUG:
			printMessage = "[D]|"
					+ tag
					+ "|"
					+ EasyPayApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case INFO:
			printMessage = "[I]|"
					+ tag
					+ "|"
					+ EasyPayApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case WARN:
			printMessage = "[W]|"
					+ tag
					+ "|"
					+ EasyPayApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case ERROR:
			printMessage = "[E]|"
					+ tag
					+ "|"
					+ EasyPayApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		default:

			break;
		}
		println(printMessage);

	}

	public void println(String message)
	{
		try
		{
			mWriter.write(TIMESTAMP_FMT.format(new Date()));
			mWriter.write(message);
			mWriter.write('\n');
			mWriter.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void close()
	{
		try
		{
			mWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
