package name.tanglei.www;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{

	private Context context;

	@Override
	public void onReceive(Context arg0, Intent data)
	{
		context = arg0;
		Log.d(FlightModeSwitcher.TAG, "the alrm time is up");

		int startState = data.getIntExtra("startState", -1);
		int endState = data.getIntExtra("endState", -1);
		Log.d(FlightModeSwitcher.TAG, "start state:" + startState);
		Log.d(FlightModeSwitcher.TAG, "end state:" + endState);
		if (startState == 1)
		{
			Log.d(FlightModeSwitcher.TAG, "set the fly mode true");
			setAirPlaneState(true);
		}
		if (endState == 1)
		{
			Log.d(FlightModeSwitcher.TAG, "set the fly mode false");
			setAirPlaneState(false);
		}
	}

	public void setAirPlaneState(boolean state)
	{
		try
		{
			AirplaneModeService.setAirplane(context, state);
			String tip = "";
			if(state)
				tip = context.getString(R.string.airplanemode_on_tip);
			else 
				tip = context.getString(R.string.airplanemode_off_tip);
			
			Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
		} catch (Exception e)
		{
			Toast.makeText(context, context.getString(R.string.airplanemode_error), Toast.LENGTH_SHORT).show();
		}
	}
}
