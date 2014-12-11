package com.inter.trade.Broadcast;

import com.gdseed.mobilereader.MobileReader.ReaderStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SwapCallServiceReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		byte rawData[] = new byte[1024];
		int trackCount[] = new int[1];
		trackCount[0] = 0;
		int defaultValue = ReaderStatus.DEVICE_PLUGOUT.ordinal();
		ReaderStatus state = ReaderStatus.values()[intent.getIntExtra(
				"ReaderStatus", defaultValue)];
	}
}
