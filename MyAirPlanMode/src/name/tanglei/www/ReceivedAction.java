package name.tanglei.www;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
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

	private AlertDialog dialog;
	private Button positiveButton;
	
	public  void showAlertDialog(Context context, String title,
			String content, int positive_id, int negative_id, DialogInterface.OnClickListener okListener, 
			DialogInterface.OnClickListener cancelListener, int delay_seconds)
	{
		Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(content);
		alertDialogBuilder.setPositiveButton(
				context.getString(positive_id), okListener);
		alertDialogBuilder.setNegativeButton(
				context.getString(negative_id), cancelListener);
		dialog = alertDialogBuilder.create();
		dialog.show();
		positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		countDownTimeHander.sendEmptyMessageDelayed(0, 100);
	}
	
	//http://ifoggy.iteye.com/blog/1874499
	//or else This Handler class should be static or leaks might occur
	static class MsgHandler extends Handler 
	{  
	    private WeakReference<ReceivedAction> mActivity;  
	  
	    MsgHandler(ReceivedAction activity) {  
	        mActivity = new WeakReference<ReceivedAction>(activity);  
	    }  
	  
	    @Override  
	    public void handleMessage(Message msg) {  
	    	ReceivedAction activity = mActivity.get();  
	        if (activity != null) {  
	            activity.handleMessage(msg);  
	        }  
	    }  
	}  
	  
	private Handler countDownTimeHander = new MsgHandler(this); 
	
	public void handleMessage(Message msg)
	{
		if(delaycount >= 0)
		{
			if(dialog != null)
			{
				String txt = this.getString(R.string.confirmAirmodeButtonOK) + "(" + delaycount + " s )";
				positiveButton.setText(txt);
				countDownTimeHander.sendEmptyMessageDelayed(0, 1000); 
			}
			delaycount --;
		}else
		{
			
			if(dialog.isShowing())//user does not action
			{
				ReceivedAction.this.finish();
				this.setAirPlaneState(true);
				dialog.dismiss();
			}
		}
	}
	
	private int delaycount = 5;
	
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
