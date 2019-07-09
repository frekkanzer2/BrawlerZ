package ${YYAndroidPackageName};

import ${YYAndroidPackageName}.RunnerActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService 
{
    public static int NOTIFICATION_ID = 1;
    private static final String TAG = "yoyo";
    private static final String NTF_MESSAGE_KEY="ntf_message";
    private static final String NTF_TITLE_KEY="ntf_title";

    public GcmIntentService() {
        super("GcmIntentService");
    }
    
    public void onCreate()
    {
    	super.onCreate();	//crashes if we don't do this...nice!
    	//onCreate is called on main thread;
    	//access RunnerActivity here, so that it's static initialisers are created in main thread
    	//as there can be problems with eg 
    	//static Handler ViewHandler = new Handler();
    	//When you create a new Handler, it is bound to the thread / message queue of the thread that is creating it
    	//!don't want handler to be created by static initialiser in onHandleIntent, since it is called in temporary non-ui thread

    	//belt & braces - make sure RunnerActivity class is loaded on main thread!
    	try{
    		Class.forName("${YYAndroidPackageName}.RunnerActivity");
    	} catch(ClassNotFoundException ex) 	{
    	}
    }

    
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
		
		
		Log.i("yoyo", "GCMIntentService onHandleIntent called with "+ messageType);
	
		
		
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error:", extras.toString());
            	Log.i("yoyo", "GCM Send error:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server:", extras.toString());
            	Log.i("yoyo", "GCM Deleted messages on server:" + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                
                //erm...should instead convert bundle to json?
                String msgData = extras.toString();	
                Log.i(TAG, "GCM Received: ");// + msgData);
                
                //not sure toString is what we really want...
                //how about -
                Iterator<String> it = extras.keySet().iterator();
                String key;
                String value;
				//->JSON string? Problem with this if we have nested json objects;value is string (of JSON) with escaped quotes
                //OR we could just build the string - just don't add quotes if value starts with { or [
                String json = "{";
                while(it.hasNext()) {
                    key = it.next();
                    //class cast exception on wake lock int in bundle; don't want this data anyway
                    try{
                    	//value = extras.getString(key);
                    	value = extras.get(key).toString();	//OR do this
                    	//Log.i("yoyo", "key:" + key + "=" + value);
                    	json += "\"" + key + "\":";
                    	if( value.startsWith("{") || value.startsWith("[")) {
                    		json += value;	//array or json object - no quotes
                    	}
                    	else {
                    		json += "\"" + value + "\"";	//string value
                    	}
                    		
                    	if( it.hasNext()) {
                    		json += ",";
                    	}
                    }
                    catch (Exception e) {
						Log.i("yoyo", e.getMessage());
					}
                }
                json += "}";
                
                //Log.i("yoyo", "GCM payload as json string-");	//also fine-better i think
                //Log.i("yoyo", json );
                
                //try this first....
                //!RunnerActivity class will be loaded & static initialisers called-
                //...be careful of static initialisers which store ref to this thread, which is not main thread & will die on completeWakefulIntent
                //eg static Handler ViewHandler = new Handler();
                boolean bStoreMsg = false;
                if( RunnerActivity.CurrentActivity != null )
                {
                	//the app is running - will this actually work???
                	//Log.i("yoyo", "Runner Current Activity is set");
                	if( RunnerActivity.CurrentActivity.mbAppSuspended)
                	{
                		//in background - store the message (& send notification?)
                		bStoreMsg = true;
                	}
                	else
                	{
                		//send directly to running foreground app...
                		Log.i("yoyo", "Sending GCM message to running app...");
                		//RunnerJNILib.GCMPushResult(msgData, GCMPush.PUSH_EVENT_REMOTE, true );
                		GooglePlayServicesExtension.QueuePushNotification(json, GcmPush.PUSH_EVENT_REMOTE, true );
                	}
                }
                else
                {
                	//Log.i("yoyo", "App is not running");
                	bStoreMsg = true;
                }
                
                if( bStoreMsg )
                {
                	GcmPush.storeMessage( getApplicationContext(), GcmPush.PUSH_EVENT_REMOTE, json);
                	
					//do we want to make notification optional?
					//how do we set title & message for notification?   
					
					//eg- pre-defined keys in bundle -
					String notMessage = extras.getString(NTF_MESSAGE_KEY);
                	String notTitle = extras.getString(NTF_TITLE_KEY);
                	if( notMessage != null && notTitle != null)
                	{
                		GcmPush.sendNotification( getApplicationContext(), notTitle, notMessage );
                	}
                	else 
                	{
                		//also check for "header" and "title" from pushwoosh service...
                		notMessage = extras.getString("title");
                		notTitle = extras.getString("header");
                		if( notMessage != null && notTitle != null )
                		{
	                		GcmPush.sendNotification( getApplicationContext(), notTitle, notMessage );
	                	}
                	}
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}