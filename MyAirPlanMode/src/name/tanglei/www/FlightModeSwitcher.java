package name.tanglei.www;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class FlightModeSwitcher extends Activity implements OnTimeChangedListener
{
	static final String TAG = FlightModeSwitcher.class.getName();
	
	private static final String ALARM_ACTION_START = "name.tanglei.ALARM_ACTION_START";
	private static final String ALARM_ACTION_END = "name.tanglei.ALARM_ACTION_END";
	
	private static final String PreferenceKey = FlightModeSwitcher.class.getName();
	
	private Calendar calendar;
	
	private RadioButton startBtn = null;
	private RadioButton stopBtn = null;
	private TimePicker startTimePicker = null;
	private TimePicker stopTimePicker = null;

	private int startHour = 0;
	private int startMinute = 0;
	private int stopHour = 0;
	private int stopMinute = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		calendar = Calendar.getInstance();
		startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
		stopTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
		
		startTimePicker.setIs24HourView(true);
		stopTimePicker.setIs24HourView(true);
		
		startTimePicker.setOnTimeChangedListener(this);
		stopTimePicker.setOnTimeChangedListener(this);
		
		// 根据之前的设置，恢复开始时间和结束时间
		SharedPreferences preferences = getSharedPreferences(
				"TimerAirPlaneMode", MODE_PRIVATE);
		startHour = preferences.getInt("startHour", 0);
		startMinute = preferences.getInt("startMinute", 30);
		stopHour = preferences.getInt("endHour", 7);
		stopMinute = preferences.getInt("endMinute", 0);
		
		startTimePicker.setCurrentHour(startHour);
		startTimePicker.setCurrentMinute(startMinute);
		stopTimePicker.setCurrentHour(stopHour);
		stopTimePicker.setCurrentMinute(stopMinute);

		startBtn = (RadioButton) findViewById(R.id.start);
		startBtn.setOnClickListener(new ControlButtonClickListener(true));
		stopBtn = (RadioButton) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new ControlButtonClickListener(false));
	}

	class ControlButtonClickListener implements RadioButton.OnClickListener
	{
		private boolean flag;

		public ControlButtonClickListener(boolean flag)
		{
			this.flag = flag;
		}

		@Override
		public void onClick(View arg0)
		{
			// 已经选择了
			if (startBtn.isChecked())
			{
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
			}
			if (stopBtn.isChecked())
			{
				stopBtn.setEnabled(false);
				startBtn.setEnabled(true);
			}
			if (flag)
			{
				Log.d(TAG, "start the auto fly mode");
				
				SharedPreferences preferences = getSharedPreferences(
						PreferenceKey, MODE_PRIVATE);

				// 设置日历的时间，主要是让日历的年月日和当前同步
				calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
				// 设置日历的小时和分钟
				calendar.set(Calendar.HOUR_OF_DAY, startHour);
				calendar.set(Calendar.MINUTE, startMinute);
				
				
				Intent startIntent = new Intent(ALARM_ACTION_START);
				startIntent.putExtra("startState", 1);
				PendingIntent startPendingIntent = PendingIntent.getBroadcast(
						FlightModeSwitcher.this, 0, startIntent, 0);

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), 24 * 60 * 60 * 1000,
						startPendingIntent);

				Intent endIntent = new Intent(ALARM_ACTION_END);
				endIntent.putExtra("endState", 1);
				PendingIntent endPendingIntent = PendingIntent.getBroadcast(
						FlightModeSwitcher.this, 1, endIntent, 0);
				calendar.set(Calendar.HOUR_OF_DAY, stopHour);
				calendar.set(Calendar.MINUTE, stopMinute);
				
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), 24 * 60 * 60 * 1000,
						endPendingIntent);
				
				Toast.makeText(FlightModeSwitcher.this, "设置已经启用",
						Toast.LENGTH_LONG).show();
			} else
			{
				Log.d(TAG, "shutdown the auto flight mode");
				Intent intent = new Intent(ALARM_ACTION_START);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						FlightModeSwitcher.this, 0, intent, 0);

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				alarmManager.cancel(pendingIntent);

				Intent intent2 = new Intent(ALARM_ACTION_END);
				PendingIntent pendingIntent2 = PendingIntent.getBroadcast(
						FlightModeSwitcher.this, 1, intent2, 0);
				alarmManager.cancel(pendingIntent2);
				
				System.putString(getContentResolver(), System.AIRPLANE_MODE_ON,
						"0");
				
				Intent flyIntent = new Intent(
						Intent.ACTION_AIRPLANE_MODE_CHANGED);
				flyIntent.putExtra("state", false);
				sendBroadcast(flyIntent);
				
				Toast.makeText(FlightModeSwitcher.this, "设置已经取消！",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	public void setTimeToPreference()
	{
		SharedPreferences preferences = getSharedPreferences(
				PreferenceKey, 0);
		SharedPreferences.Editor editor = preferences
				.edit();
		editor.putInt("startHour", startHour);
		editor.putInt("startMinute", startMinute);
		editor.putInt("endHour", stopHour);
		editor.putInt("endMinute", stopMinute);
		
		editor.commit();
	}

	/**
	 * 格式化时间为00:00
	 * 
	 * @param h
	 * @param m
	 * @return
	 */
	public String formatTime(int h, int m)
	{
		StringBuffer buf = new StringBuffer();
		if (h < 10)
		{
			buf.append("0" + h);
		} else
		{
			buf.append(h);
		}
		buf.append(" : ");

		if (m < 10)
		{
			buf.append("0" + m);
		} else
		{
			buf.append(m);
		}
		return buf.toString();
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
	{
		
		if(view == this.startTimePicker)
		{
			Log.i(TAG, "start time change to" + (hourOfDay) + ":" + minute);
			startHour = hourOfDay;
			startMinute = minute;
			
		}else
		{
			Log.i(TAG, "end time change to" + (hourOfDay) + ":" + minute);
			stopHour = hourOfDay;
			stopMinute = minute;
		}
		setTimeToPreference();
	}
}