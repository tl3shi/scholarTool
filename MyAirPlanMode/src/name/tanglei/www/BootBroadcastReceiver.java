package name.tanglei.www;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	public static final String TAG = BootBroadcastReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction().toString();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED))
		{
			Utils.getStoredPreference(context);
			Utils.startSchedule(context, Utils.getStoredPreference(context), false);
			return;
		}
	}
}
