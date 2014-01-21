package name.tanglei.www;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
	public final static String ACTION_TAG = "airmode_action";
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(FlightModeSwitcher.TAG, "the alarm time is up");
		
		int startState = intent.getIntExtra("startState", -1);
		int endState = intent.getIntExtra("endState", -1);
		
		Log.d(FlightModeSwitcher.TAG, "start state:" + startState);
		Log.d(FlightModeSwitcher.TAG, "end state:" + endState);
		
		boolean isEnabled = AirplaneModeService.isAirplaneModeOn(context);
		
		
		if (startState == 1)
		{
			if (isEnabled == true)
				return;
			
			Intent i = new Intent(context, ReceivedAction.class); 
		    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    i.putExtra(ACTION_TAG, true);
		    context.startActivity(i);
			
		}
		if (endState == 1)
		{
			if(isEnabled == false)
				return;
			
			Intent i = new Intent(context, ReceivedAction.class); 
		    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    i.putExtra(ACTION_TAG, false);
		    context.startActivity(i);
		}
	}
	
	
}
