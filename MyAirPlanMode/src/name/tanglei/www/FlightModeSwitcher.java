package name.tanglei.www;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class FlightModeSwitcher extends Activity implements OnTimeChangedListener
{
	static final String TAG = FlightModeSwitcher.class.getName();
	
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
	private boolean currentState = false;
	
	private Intent startIntent = null;
	private Intent endIntent = null;
	private PendingIntent startPendingIntent;
	private PendingIntent endPendingIntent;
	
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
		
		SharedPreferences preferences = getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		startHour = preferences.getInt("startHour", 0);
		startMinute = preferences.getInt("startMinute", 30);
		stopHour = preferences.getInt("endHour", 7);
		stopMinute = preferences.getInt("endMinute", 0);
		boolean firsttime = false;
		if(!preferences.contains("currentState"))
			firsttime = true;
		currentState = preferences.getBoolean("currentState", true);
		
		Log.i(TAG, "load time data from preference:" + startHour + ":" + startMinute);
		startTimePicker.setCurrentHour(startHour); //this should put before the set on timechangelisten
		startTimePicker.setCurrentMinute(startMinute);//for it will invoke the timechange
		stopTimePicker.setCurrentHour(stopHour);
		stopTimePicker.setCurrentMinute(stopMinute);

		startBtn = (RadioButton) findViewById(R.id.start);
		startBtn.setOnClickListener(controlBtnClickListener);
		stopBtn = (RadioButton) findViewById(R.id.stop);
		stopBtn.setOnClickListener(controlBtnClickListener);
		if(firsttime)
		{
			startBtn.setChecked(false);
			stopBtn.setChecked(false);
		}else
		{
			startBtn.setChecked(currentState);
			stopBtn.setChecked(!currentState);
		}
		
		startTimePicker.setOnTimeChangedListener(this);
		stopTimePicker.setOnTimeChangedListener(this);
	}
	void test()
	{
		Log.i(TAG, "testxxxxx");
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);  
		int requestCode = 0;  
		PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),  
		        requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		long triggerAtTime = SystemClock.elapsedRealtime()  + 5 * 1000;  
		alarmMgr.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendIntent);  
	}

	public void onResume()
	{
		super.onResume();
	}
	
	//when activity is not visible
	public void onStop()
	{
		startSchedule(false);
		super.onStop();
	}
	
	private RadioButton.OnClickListener controlBtnClickListener = new RadioButton.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			/*if (startBtn.isChecked())
			{
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
			}
			if (stopBtn.isChecked())
			{
				stopBtn.setEnabled(false);
				startBtn.setEnabled(true);
			}*/
			
			startSchedule(true);
		}
	};

	
	
	public void startSchedule(boolean showTip)
	{
		if(!(startBtn.isChecked() || stopBtn.isChecked()))
		{	
			showAlertDialog(getString(R.string.noCheckTitle), getString(R.string.noCheckContent));
			return;
		}
		Log.d(TAG, " auto flight mode start ? " + startBtn.isChecked());
		if(startBtn.isChecked())
		{
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, startHour);
			calendar.set(Calendar.MINUTE, startMinute);
			Log.i(TAG, "current:" + System.currentTimeMillis());
			startIntent = new Intent(this, AlarmReceiver.class);
			startIntent.putExtra("startState", 1);
			startPendingIntent = PendingIntent.getBroadcast(
					FlightModeSwitcher.this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					
			long nextStarttime = calendar.getTimeInMillis();
			if(nextStarttime < System.currentTimeMillis())
				nextStarttime += 24 * 60 * 60 * 1000;
			
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					nextStarttime - 55*1000, 24 * 60 * 60 * 1000, startPendingIntent);
					
			endIntent = new Intent(this, AlarmReceiver.class);
			endIntent.putExtra("endState", 1);
			endPendingIntent = PendingIntent.getBroadcast(
					FlightModeSwitcher.this, 1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			calendar.set(Calendar.HOUR_OF_DAY, stopHour);
			calendar.set(Calendar.MINUTE, stopMinute);
			
			long nextEndtime = calendar.getTimeInMillis();
			if(nextEndtime < System.currentTimeMillis())
				nextEndtime += 24 * 60 * 60 * 1000;
			
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					nextEndtime - 55*1000, 24 * 60 * 60 * 1000,
					endPendingIntent);
			
			Log.i(TAG, "next start:" + nextStarttime);
			Log.i(TAG, "next end:" + nextEndtime);
			
			if(showTip)
			{
				Toast.makeText(FlightModeSwitcher.this, getString(R.string.setup_on),
					Toast.LENGTH_SHORT).show();
				Toast.makeText(this, getString(R.string.nextStartTipPref) + this.formatTime(nextStarttime), 
						Toast.LENGTH_LONG).show();
			}
		} else
		{
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			alarmManager.cancel(startPendingIntent);
			alarmManager.cancel(endPendingIntent);
			
			AirplaneModeService.setAirplane(FlightModeSwitcher.this, false);
			if(showTip)
				Toast.makeText(FlightModeSwitcher.this, getString(R.string.setup_off),
						Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setTimeToPreference()
	{
		SharedPreferences preferences = getSharedPreferences(
				PreferenceKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences
				.edit();
		editor.putInt("startHour", startHour);
		editor.putInt("startMinute", startMinute);
		editor.putInt("endHour", stopHour);
		editor.putInt("endMinute", stopMinute);
		editor.putBoolean("currentState", currentState);
		
		Log.i(TAG, "save time to preference:" + startHour + ":" + startMinute);
		editor.commit();
	}

	

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
	{
		
		if(view == this.startTimePicker)
		{
			Log.i(TAG, "start time change to  " + (hourOfDay) + ":" + minute);
			startHour = hourOfDay;
			startMinute = minute;
			
		}else
		{
			Log.i(TAG, "end time change to  " + (hourOfDay) + ":" + minute);
			stopHour = hourOfDay;
			stopMinute = minute;
		}
		
		setTimeToPreference();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, Menu.FIRST, 1, getString(R.string.menuHelp));
		menu.add(Menu.NONE, Menu.FIRST+1, 2, getString(R.string.menuAbout));
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case Menu.FIRST:
				showAlertDialog(getString(R.string.helpTitle), getString(R.string.helpContent));
				break;
			case Menu.FIRST + 1:
				showAlertDialog(getString(R.string.aboutTitle), getString(R.string.aboutContent));
				break;
		}
		return true;
	}
	
	public void showAlertDialog(String title, String content) {
        showAlertDialog(title, content, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void showAlertDialog(String title, String content,
            DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
        		this.getString(android.R.string.ok), listener);
        alertDialogBuilder.show();
    }
    
    public String formatTime(long mini)
    {
    	Date date = new Date(mini);
    	SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault());  
        return formatter.format(date); 
    }
}