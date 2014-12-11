package com.inter.trade.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public class SplashActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				
			}
		}, 2 * 1000);
	}

}
