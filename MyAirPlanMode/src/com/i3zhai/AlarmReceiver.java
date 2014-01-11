package com.i3zhai;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.System;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{

	private Context context;

	@Override
	public void onReceive(Context arg0, Intent data)
	{
		context = arg0;
		Log.d(AirPlaneModeActivity.TAG, "the alrm time is up");

		int startState = data.getIntExtra("startState", -1);
		int endState = data.getIntExtra("endState", -1);
		Log.d(AirPlaneModeActivity.TAG, "start state:" + startState);
		Log.d(AirPlaneModeActivity.TAG, "end state:" + endState);
		if (startState == 1)
		{
			Log.d(AirPlaneModeActivity.TAG, "set the fly mode true");
			setAirPlaneState(true);
		}
		if (endState == 1)
		{
			Log.d(AirPlaneModeActivity.TAG, "set the fly mode false");
			setAirPlaneState(false);
		}
	}

	public void setAirPlaneState(boolean state)
	{
		try
		{
			ContentResolver cr = context.getContentResolver();
			if (state
					&& System.getString(cr, System.AIRPLANE_MODE_ON)
							.equals("0"))
			{
				Toast.makeText(context, "正在转飞行模式", Toast.LENGTH_LONG).show();
				// 在receiver中10S没有处理 就会异常。
				/* 本来想切换前，给用户点提示，可以取消啥滴。不然用户正在用，直接给切换了不爽啊。
				 * try { Log.e("tl3shi", "befor sleep"); Thread.sleep(5*1000);
				 * Log.e("tl3shi", "end sleep "); } catch (Exception e) {
				 * Log.e("tl3shi",e.toString()); e.printStackTrace(); }
				 */
				System.putString(cr, System.AIRPLANE_MODE_ON, "1");
				Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				intent.putExtra("state", true);
				context.sendBroadcast(intent);
			} else if (!state
					&& System.getString(cr, System.AIRPLANE_MODE_ON)
							.equals("1"))
			{
				System.putString(cr, System.AIRPLANE_MODE_ON, "0");
				Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				intent.putExtra("state", false);
				context.sendBroadcast(intent);
			}
		} catch (Exception e)
		{
			Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
		}
	}
}
