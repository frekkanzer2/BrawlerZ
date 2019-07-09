package ${YYAndroidPackageName};

import ${YYAndroidPackageName}.RunnerActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushLocalAlarmReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//This method is always called within the main thread of its process - jolly good!
		boolean bStoreMsg = false;
		Log.i("yoyo", "Local notification alarm received...");
		
		//grab data from intent extras
		Bundle extras = intent.getExtras();
		String msgData = extras.getString(GcmPush.KEY_NTF_DATA);//, "");
		boolean bMsgHasData = (msgData !=null && msgData.length() > 0 );
		
        if( RunnerActivity.CurrentActivity != null )
        {
        	//the app is running 
        	Log.i("yoyo", "Runner Current Activity is set");
        	if( RunnerActivity.CurrentActivity.mbAppSuspended)
        	{
        		//in background - store the message (& send notification?)
        		bStoreMsg = true;
        	}
        	else
        	{
        		//in foreground - send directly to running foreground app, no need for notification
        		Log.i("yoyo", "Sending Local push message to running app...");
        		//RunnerJNILib.GCMPushResult(msgData, GCMPush.PUSH_EVENT_REMOTE, true );
        		//don't bother if there is no data attached 
        		if( bMsgHasData) {
        			GooglePlayServicesExtension.QueuePushNotification(msgData, GcmPush.PUSH_EVENT_LOCAL, true );
        		}
        	}
        }
        else
        {
        	Log.i("yoyo", "App is not running");
        	bStoreMsg = true;
        }
        
        if( bStoreMsg )
        {
        	if( bMsgHasData )	//don't bother if there is no data attached 
        	{	
        		GcmPush.storeMessage( context.getApplicationContext(), GcmPush.PUSH_EVENT_LOCAL, msgData);
        	}
        	
			String notMessage = extras.getString(GcmPush.KEY_NTF_MESSAGE);//, "");
			if( notMessage == null ) notMessage="";
        	String notTitle = extras.getString(GcmPush.KEY_NTF_TITLE);//, "");
        	if( notTitle== null ) notTitle="";
        	
        	GcmPush.sendNotification( context.getApplicationContext(), notTitle, notMessage );
        }
        
        //remove stored alarm details when alarm fires
        long timeMilli = System.currentTimeMillis();
        GcmPush.removeOldStoredAlarms( context.getApplicationContext(), timeMilli );
    }
	
}