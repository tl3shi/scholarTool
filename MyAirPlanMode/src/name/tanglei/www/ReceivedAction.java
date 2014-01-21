package name.tanglei.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ReceivedAction extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    Intent intent = this.getIntent();
        boolean action = intent.getBooleanExtra(AlarmReceiver.ACTION_TAG, false);
        
        if(action)
        {
	        String title = this.getString(R.string.confirmAirmodeTitle);
			String content = this.getString(R.string.confirmAirmodeContent);
			
			showAlertDialog(this, title, content, 
							R.string.confirmAirmodeButtonOK, 
							R.string.confirmAirmodeButtonCancel, 
							oKListener, cancelListener, 3);
        }else
        {
        	setAirPlaneState(false);
        }
        
	}
	
	private DialogInterface.OnClickListener oKListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			//showProcessdialog
			Log.d(FlightModeSwitcher.TAG, "set the fly mode true");
			setAirPlaneState(true);
			ReceivedAction.this.finish();
		}
	};
	
	private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			ReceivedAction.this.finish();
			Log.d(FlightModeSwitcher.TAG, "cancel the fly mode true");
			dialog.dismiss();
			Toast.makeText(ReceivedAction.this, ReceivedAction.this.getString(R.string.cancelAirmodeToast), Toast.LENGTH_SHORT).show();
		}
	};
	
	public  void showAlertDialog(Context context, String title,
			String content, int positive_id, int negative_id, DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancelListener, int delay_seconds)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(content);
		alertDialogBuilder.setPositiveButton(
				context.getString(positive_id), okListener);
		alertDialogBuilder.setNegativeButton(
				context.getString(negative_id), cancelListener);
		final AlertDialog ad = alertDialogBuilder.create();
		ad.show();
		
		android.os.Handler hander= new android.os.Handler();
	    hander.postDelayed(new Runnable() 
	    {     
	     @Override
	     public void run() 
	     {
	    	 if(ad.isShowing())//the user does not take action.
	    	 {
	    		 ad.dismiss();
	    		 setAirPlaneState(true);
	    		 ReceivedAction.this.finish();
	    	 }
	     }
	    }, delay_seconds * 1000);
	}
	
	public void setAirPlaneState(boolean state)
	{
		try
		{
			AirplaneModeService.setAirplane(this, state);
			String tip = "";
			if(state)
				tip = this.getString(R.string.airplanemode_on_tip);
			else 
				tip = this.getString(R.string.airplanemode_off_tip);
			
			Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
		} catch (Exception e)
		{
			Log.e(FlightModeSwitcher.TAG, e.getMessage());
			Toast.makeText(this, this.getString(R.string.airplanemode_error), Toast.LENGTH_SHORT).show();
		}
	}
}
