package ${YYAndroidPackageName};

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import ${YYAndroidPackageName}.RunnerActivity;
import ${YYAndroidPackageName}.PushLocalAlarmReceiver;

import com.yoyogames.runner.RunnerJNILib;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class GcmPush
{
	//push class, or something
	public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_MESSAGE_COUNT="message_count";
    private static final String PROPERTY_MESSAGE_N="message_";
    private static final String PROPERTY_MESSAGE_TYPE_N="message_type_";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    public static final int PUSH_EVENT_REGISTER = 0;
    public static final int PUSH_EVENT_REMOTE = 1;
    public static final int PUSH_EVENT_LOCAL = 2;
    
    //keys for local push intent
    public static final String KEY_NTF_TITLE="ntf_title";
    public static final String KEY_NTF_MESSAGE="ntf_message";
    public static final String KEY_NTF_DATA="ntf_data";
    
    //keys for STORING details of pending notification alarms...what a pain
    private static final String PROP_ALARM_COUNT = "ntf_alarm_count";
    private static final String PROP_ALARM_DATA_N = "ntf_alarmdata_";
    
    
    
    
	private Context mContext;
	private String 	mSenderId;
	private GoogleCloudMessaging mGcm;
	private String mRegId;
	
	public  GcmPush( Context context )
	{
		mContext = context;
	}
	
	public boolean Init( String SenderID )
	{
		boolean bOK=true;
		mSenderId = SenderID;

		Log.i("yoyo","Initialising GCM Push with SenderID " + SenderID);
		
		if( mSenderId.length() > 0 )
		{
			// Check device for Play Services APK. If check succeeds, proceed with GCM registration.
			if (checkPlayServices()) 
	        {
	            mGcm = GoogleCloudMessaging.getInstance(mContext);
	            mRegId = getRegistrationId(mContext.getApplicationContext() );
	
	            if (mRegId.length()==0) 
	            {
	                registerInBackground();
	            }
	            else
	            {
	            	//send existing reg id ->async event?
	            	RunnerJNILib.GCMPushResult(mRegId, PUSH_EVENT_REGISTER, true);
	            }
	        } else {
	            Log.i("yoyo", "No valid Google Play Services APK found.");
	            bOK = false;
	        }
		}
        return bOK;
	}
	
	private boolean checkPlayServices() 
	{
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) 
        {
            //if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) 
            //{
            //    GooglePlayServicesUtil.getErrorDialog(resultCode, mContext, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            //} else {
            //    Log.i(TAG, "This device is not supported.");
            //}
        	Log.i("yoyo", "!Google play not found on device");
            return false;
        }
        return true;
    }
	
	/**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) 
    {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length()==0) 
        {
            Log.i("yoyo", "GCM: no stored registration id found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) 
        {
            Log.i("yoyo", "GCM: App version changed.");
            return "";
        }
        Log.i("yoyo", "GCM - FOUND stored registration id:" + registrationId);
        return registrationId;
    }
	
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) 
    {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGcmPreferences(Context context) 
    {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(RunnerActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }
	
    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) 
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("yoyo", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    public static void storeMessage( Context context, int type, String data )
    {
    	final SharedPreferences prefs = getGcmPreferences( context );
    	int messageCount = prefs.getInt(PROPERTY_MESSAGE_COUNT, 0);
    	++messageCount;
    	Log.i("yoyo", "GCM: Store message " + messageCount);// + " data=" + data);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt(PROPERTY_MESSAGE_COUNT, messageCount);
    	String keyData = PROPERTY_MESSAGE_N + Integer.toString(messageCount);
    	String keyType = PROPERTY_MESSAGE_TYPE_N + Integer.toString(messageCount);
    	editor.putString(keyData, data);
    	editor.putInt(keyType, type);
    	editor.commit();
    }
    
    //
    public void deliverStoredMessages()
    {
    	final SharedPreferences prefs = getGcmPreferences(mContext);
    	int messageCount = prefs.getInt(PROPERTY_MESSAGE_COUNT, 0);
    	Log.i("yoyo", "GCM: sending " + messageCount + " stored messages");
    	
    	if( messageCount > 0 )
    	{
    		for( int i = 1; i <= messageCount; ++i)
    		{
    			String keyData = PROPERTY_MESSAGE_N + Integer.toString(i);
    			String keyType = PROPERTY_MESSAGE_TYPE_N + Integer.toString(i);
    			String data = prefs.getString(keyData, "");
    			int msgType = prefs.getInt(keyType, PUSH_EVENT_REMOTE);
    			RunnerJNILib.GCMPushResult( data, msgType, true );
    		}
    	}
    	
    	//remove the messages
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt(PROPERTY_MESSAGE_COUNT, 0);
    	for( int i = 0; i < messageCount; ++i)
    	{
    		String key = PROPERTY_MESSAGE_N + Integer.toString(i);
    		editor.remove(key);
    	}
    	editor.commit();
    	
    	//we can remove notifications now, since we have delivered all the data to the app
    	NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.cancelAll();
    }
    
    //schedule notification in fireTimeOffsetS seconds
    public void pushLocalNotification( float fireTimeOffsetS, String title, String message, String data )
    {
    	//Log.i("yoyo", "local notifcation scheduled in " + fireTimeOffsetS + " seconds");
    	Context appContext = mContext.getApplicationContext();
    	Intent intent = new Intent( appContext, PushLocalAlarmReceiver.class);
    	intent.putExtra( KEY_NTF_TITLE, title );
    	intent.putExtra( KEY_NTF_MESSAGE, message );
    	intent.putExtra( KEY_NTF_DATA, data);
    	
    	//problems stacking multiple alarms...try this??
    	long timeMilli = System.currentTimeMillis() + (long)(fireTimeOffsetS*1000);
    	int uniqueID = (int)timeMilli;
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, uniqueID, intent, PendingIntent.FLAG_ONE_SHOT);
    	AlarmManager am = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
    	
    	am.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent);
    	
    	//store details of alarm - so it can be cancelled
    	storePendingLocalAlarm( title, message, data, timeMilli );
    	
    }
    
    //------- local notifications - retrieve & cancel pending notifications ----------------------
    //return data for scheduled local notification
    private boolean pushGetLocalNotificationByIndex( int iIndex, int dsMap )
    {
    	//Log.i("yoyo", "pushGetLocalNotification:" + iIndex + " map=" + dsMap );
    	//i)find item N in stored pending local notifications
    	String alarmData = getStoredPendingLocalAlarm( mContext, iIndex );
    	if( alarmData != null )
    	{
    		//ii)fill return map
    		if( dsMap < 0 )
    		{
    			return true;	//no map supplied- return true value to indicate ntf index exists
    		}
    		
    		try
    		{
	    		JSONObject jobj = new JSONObject(alarmData);
	    		String title = jobj.getString("title");
	    		String message = jobj.getString("message");
	    		String data = jobj.getString("data");
	    		int id = jobj.getInt("time");
	    		
	    		RunnerJNILib.dsMapAddString(dsMap, "title", title );
	    		RunnerJNILib.dsMapAddString(dsMap, "message", message );
	    		RunnerJNILib.dsMapAddString(dsMap, "data", data );
    		
	    		return true;
    		}
    		catch(Exception ex )
    		{
    			Log.i("yoyo", ex.toString());
    		}
    	}
    	
    	return false;
    }
    
    public int pushGetLocalNotification( int iIndex, int dsMap)
    {
    	if( pushGetLocalNotificationByIndex( iIndex, dsMap ))
    	{
    		return iIndex;
    	}
    	return -1;
    }
    	
    public int pushCancelLocalNotification( int iIndex )
    {
    	//i)find item N in stored pending local notifications
    	//Log.i("yoyo", "GCM: cancel alarm " + iIndex );
    	String alarmData = getStoredPendingLocalAlarm( mContext, iIndex );
    	if( alarmData != null )
    	{
    		//ii)extract id
    		try 
    		{
	    		JSONObject jobj = new JSONObject(alarmData);
	    		//String title = jobj.getString("title");
	    		//String message = jobj.getString("message");
	    		//String data = jobj.getString("data");
	    		long time= jobj.getInt("time");
	    		int uniqueID = (int)time;
	    		
	    		//iii)create a matching pending intent and cancel it
	    		Context appContext = mContext.getApplicationContext();
	        	Intent intent = new Intent( appContext, PushLocalAlarmReceiver.class);
	        	PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, uniqueID, intent, PendingIntent.FLAG_ONE_SHOT);
	    		
	        	pendingIntent.cancel();
	        	AlarmManager am = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
	        	am.cancel( pendingIntent);
	    		
	    		//iv)remove from the stored pending list
	    		if( removeStoredLocalAlarm( mContext, iIndex ) )
	    		{
	    			//return success
	    			return 1;
	    		}
    		}
    		catch(Exception ex)
    		{
    			Log.i("yoyo", ex.toString());
    		}
	    		
    	}
    	
    	//return fail status
    	return 0;
    }
    
    //because there is no way to retrieve pending alarms set on alarm manager,
    //we have to store the details to allow cancelling of a local notification 
    public void storePendingLocalAlarm(String title, String message, String data, long timeMilli)
    {
    	final SharedPreferences prefs = getGcmPreferences( mContext );
    	int alarmCount = prefs.getInt(PROP_ALARM_COUNT, 0);
    	//Log.i("yoyo", "GCM: Store alarm " + alarmCount + " time=" + timeMilli);
    	
    	SharedPreferences.Editor editor = prefs.edit();
    	String json = null;
    	try 
    	{
	    	JSONObject jobj = new JSONObject();
	    	jobj.put( "title", title);
	    	jobj.put( "message", message);
	    	jobj.put( "data", data );
	    	jobj.put( "time", timeMilli );
	    	json = jobj.toString();
	    	
	    	String keyData = PROP_ALARM_DATA_N + Integer.toString(alarmCount);
	    	editor.putString(keyData, json);
	    	//increment count
	    	++alarmCount;
	    	editor.putInt(PROP_ALARM_COUNT, alarmCount);
	    	
	    	editor.commit();
    	}
    	catch(Exception ex)
    	{
    		Log.i("yoyo", ex.toString());
    	}
    }
    
    //+ability to remove stored alarm details from storage
    public static boolean removeStoredLocalAlarm( Context context, int index )
    {
    	//Log.i("yoyo", "GCM: Removing stored alarm " + index );
    	final SharedPreferences prefs = getGcmPreferences( context );
    	int alarmCount = prefs.getInt(PROP_ALARM_COUNT, 0);
    	if( index >= 0 && index < alarmCount )
    	{
	    	SharedPreferences.Editor editor = prefs.edit();
	    	
	    	//shuffle data from last entry into delete position (unless removing last entry )
	    	if( index < alarmCount-1)
	    	{
		    	String alarmData = getStoredPendingLocalAlarm( context, alarmCount-1 );
		    	String keyData = PROP_ALARM_DATA_N + Integer.toString(index);
		    	editor.putString( keyData, alarmData );
	    	}
	    	
	    	//remove data for last alarm
	    	String keyLast = PROP_ALARM_DATA_N + Integer.toString(alarmCount-1);
	    	editor.remove(keyLast);
	    	
	    	//decrement alarm count
	    	--alarmCount;
	    	editor.putInt(PROP_ALARM_COUNT, alarmCount);
	    	editor.commit();
	    	
	    	//Log.i("yoyo", "GCM: Removed alarm; stored count=" + alarmCount );
	    	return true;
    	}
    	Log.i("yoyo", "GCM: Failed to remove alarm; stored count=" + alarmCount );
    	return false;
    }
    
    //remove fired alarms from stored list
    public static void removeOldStoredAlarms( Context context, long currTime )
    {
    	final SharedPreferences prefs = getGcmPreferences( context );
    	int alarmCount = prefs.getInt(PROP_ALARM_COUNT, 0);
    	//Log.i("yoyo", "removeOldStoredAlarms: alarmCount=" + alarmCount + " currTime=" + currTime );
    	
    	for( int i=0; i < alarmCount; ++i )
    	{
    		String data = getStoredPendingLocalAlarm( context, i );
    		if( data != null )
    		{
    			try
    			{
    				JSONObject jobj = new JSONObject(data);
    				long time = jobj.getLong("time");
    				//Log.i("yoyo", "found alarm " + i +" time=" + time );
	    			if( currTime >= time )
	    			{
	    				removeStoredLocalAlarm(context, i);
	    				--i;	//because we shuffled up - new item at current index
	    			}
    			} 
    			catch( JSONException ex)
    			{
    				Log.i("yoyo", ex.toString());
    			}
    			
    		}
    	}
    }
    
    //+retrieve stored alarm details for given 0-based index
    public static String getStoredPendingLocalAlarm( Context context, int index )
    {
    	final SharedPreferences prefs = getGcmPreferences( context );
    	String keyData = PROP_ALARM_DATA_N + Integer.toString(index);
    	String alarmData = prefs.getString(keyData, null );
    	return alarmData;
    }
    
    //----------------------------------------------------------------
    
    public static void sendNotification(Context context, String title, String msg) 
    {
 	   int uniqueID = (int)System.currentTimeMillis();	//unique id for each notification - otherwise previous ntf gets replaced
 	   Log.i("yoyo", "GCM notification: id=" + uniqueID + " title=" + title + " msg=" + msg);
 	   NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

         PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, RunnerActivity.class), 0);

         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
         .setSmallIcon(R.drawable.icon)
         .setContentTitle(title)
         .setStyle(new NotificationCompat.BigTextStyle()
         .bigText(msg))
         .setContentText(msg)
         //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
         .setDefaults(Notification.DEFAULT_ALL )
         .setAutoCancel(true)
         //.setPriority(NotificationCompat.PRIORITY_MAX)
         .setTicker(msg);
         
         mBuilder.setContentIntent(contentIntent);
         mNotificationManager.notify(uniqueID, mBuilder.build());
     }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() 
    {
		
		Log.i("yoyo","Starting async task to register in background");
		
        new AsyncTask<Void, Void, String>() 
        {
            @Override
            protected String doInBackground(Void... params) 
            {
                String msg = "";
                try {
                    Log.i("yoyo","About to call mGcm.register with "+mSenderId);
                    mRegId = mGcm.register( mSenderId);
                    msg = "GCM: Device registered, registration ID=" + mRegId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mContext, mRegId);
                } 
                catch (IOException ex) 
                {
                    msg = "GCM Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) 
            {
                //mDisplay.append(msg + "\n");
            	Log.i("yoyo", msg);
            	if( mRegId.length()==0)
            	{
            		//post error result
            		RunnerJNILib.GCMPushResult(msg, PUSH_EVENT_REGISTER, false);
            	}
            	else
            	{
            		//post success result 
            		RunnerJNILib.GCMPushResult(mRegId, PUSH_EVENT_REGISTER, true);
            	}
            }
        }.execute(null, null, null);
    }
}
