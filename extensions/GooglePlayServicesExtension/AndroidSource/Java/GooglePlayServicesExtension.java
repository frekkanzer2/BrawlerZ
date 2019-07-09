
package ${YYAndroidPackageName};

import ${YYAndroidPackageName}.RunnerActivity;
import com.yoyogames.runner.RunnerJNILib;

import android.util.Log;
import android.os.Handler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.app.DialogFragment;
import android.os.Bundle;
import android.content.Intent;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import android.os.Build;
import ${YYAndroidPackageName}.R;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AbsoluteLayout;
import com.yoyogames.runner.RunnerJNILib;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;


import android.annotation.TargetApi;
import com.google.android.gms.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.SnapshotsClient;

import com.google.android.gms.games.leaderboard.ScoreSubmissionData;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import android.content.IntentSender.SendIntentException;


//Can't extend BaseGameUtils as we already extend RunnerSocial
public class GooglePlayServicesExtension extends RunnerSocial implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,RewardedVideoAdListener 
{
	public interface OnSnapshotResolvedListener
	{
		void onSuccess(Snapshot snapshot, int fileId);
		void onFailure(String reason, int fileId);
	}
	
	private GoogleSignInClient mGoogleSigninClient = null;
    private GoogleSignInAccount mGoogleSignInAccount = null;
	
	public GcmPush mGcmPush;	//Push notifications
	private int LastGSId=-1;
	public static GooglePlayServicesExtension CurrentGoogleExtension;

	private String mCurrentSaveName = "DefaultSave";
	private byte [] mSaveGameData = null;
		
	private RunnerBillingInterface iap_controller=null;
	
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
	
    // Bool to track whether the app is already resolving an error
	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInFlow = true;
	private boolean mSignInClicked = false;
	private boolean mSignOutClicked = false;
	private boolean mSignedInOnPause = false;
	
	private boolean mCloudServicesEnabled = false;
	private boolean mCloudSyncInProgress = false;
	private int mCloudSyncConflictRetries = 0;
	
	private RewardedVideoAd mRewardedVideoAd;
    private AdView adView=null;
	private InterstitialAd interstitialAd=null;
	//private String BannerId;
	private String InterstitialId;
	private String InterstitialStatus = "Not Ready";
    private String RewardedVideoStatus = "Not Ready";
	private String TestDeviceId;
	private boolean bUseTestAds=false;
	private AdSize BannerSize;
	private int BannerXPos;
	private int BannerYPos;
	
	// Request result event IDs
	private static final int GoogleMobileAds_ASyncEvent = 9817;
	private static final int GooglePlayServices_PostScoreResultEvent = 9818;
	private static final int GooglePlayServices_PostAchievementResultEvent = 9819;
	private static final int GooglePlayServices_IncrementAchievementResultEvent = 9820;
	private static final int GooglePlayServices_RevealAchievementResultEvent = 9821;
	private static final int RC_SIGN_IN = 9001;
	
	// Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
	
	public void GooglePlayServices_Init()
	{
		if(mGoogleSigninClient != null)
		{
			Log.i("yoyo","Attempting to initialise Google Play Signin Client when it has already been initialised.");
			return;
		}
		
		String appid = RunnerActivity.CurrentActivity.mYYPrefs.getString("com.google.android.gms.games.APP_ID");
		if(appid == null|| appid.isEmpty())
		{
			Log.i("yoyo", "Failed to find appid, Google Play Services will not be initialised.");
			return;
		}
		else
			Log.i("yoyo", "Initialising Google Play Services. App id: " + appid);

		// Initialise sign in client
		GoogleSignInOptions.Builder optionsBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
		optionsBuilder.requestScopes(Games.SCOPE_GAMES, Plus.SCOPE_PLUS_LOGIN);
		optionsBuilder.requestProfile();
		
		// Update scopes for cloud saving
		RunnerActivity.CurrentActivity.mYYPrefs.getBoolean("Debug");
		mCloudServicesEnabled = RunnerActivity.CurrentActivity.mYYPrefs.getBoolean("YYGoogleCloudSavingEnabled");
		if(mCloudServicesEnabled)
		{
			optionsBuilder.requestScopes(Drive.SCOPE_APPFOLDER);
		}
		
		// Create Google client
		Log.i("yoyo", "Signing into google game services. Cloud enabled: " + mCloudServicesEnabled + ". Options builder: " + optionsBuilder);
		mGoogleSigninClient = GoogleSignIn.getClient(RunnerJNILib.ms_context, optionsBuilder.build());
	}
	
    public void GoogleMobileAds_LoadRewardedVideo(String _unitid)
    {
        final String unitid = _unitid;
        
        if(mRewardedVideoAd==null)
        {
          
        }
        RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
                 
                if(mRewardedVideoAd==null)
                {
                  mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(RunnerActivity.CurrentActivity);
                  mRewardedVideoAd.setRewardedVideoAdListener(CurrentGoogleExtension);
                }
                
                AdRequest.Builder builder = new AdRequest.Builder();
                builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                if( bUseTestAds)
                    builder.addTestDevice(TestDeviceId);
                AdRequest adRequest = builder.build();
                 mRewardedVideoAd.loadAd(unitid,adRequest);
            }
        });
    }
    
    public void GoogleMobileAds_ShowRewardedVideo()
    {
        if(mRewardedVideoAd!=null)
        {
            RunnerActivity.ViewHandler.post( new Runnable() {
                public void run() 
                {
                   if (mRewardedVideoAd.isLoaded()) 
                   {
                        mRewardedVideoAd.show();
                   } 
                }
            });
        }
    }
    
    public String GoogleMobileAds_RewardedVideoStatus()
    {
        
        if(mRewardedVideoAd!=null)
		{
			RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
                if(mRewardedVideoAd!=null)
                {
                    if(mRewardedVideoAd.isLoaded())
                        RewardedVideoStatus="Ready";
                    else
                        RewardedVideoStatus = "Not Ready";
                }
			}});
		}

		return RewardedVideoStatus;
    }
   
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(RunnerActivity.CurrentActivity, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
                
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_watched" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
        RunnerJNILib.DsMapAddDouble( dsMapIndex,"amount",reward.getAmount());
        RunnerJNILib.DsMapAddString( dsMapIndex, "currency", reward.getType() );
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
                
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoAdLeftApplication",
          //      Toast.LENGTH_SHORT).show();
                
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_leftapplication" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_adclosed" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_loadfailed" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
        RunnerJNILib.DsMapAddDouble( dsMapIndex,"errorcode",errorCode);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
        
        RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
                if(mRewardedVideoAd!=null)
                {
                    if(mRewardedVideoAd.isLoaded())
                        RewardedVideoStatus="Ready";
                    else
                        RewardedVideoStatus = "Not Ready";
                }
			}});
        
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_adloaded" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
        
        RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
                if(mRewardedVideoAd!=null)
                {
                    if(mRewardedVideoAd.isLoaded())
                        RewardedVideoStatus="Ready";
                    else
                        RewardedVideoStatus = "Not Ready";
                }
			}});
    }

    @Override
    public void onRewardedVideoAdOpened() {
     //   Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_adopened" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(RunnerActivity.CurrentActivity, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "rewardedvideo_videostarted" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
    }   
	
	@Override
	public void Init()
	{
		CurrentGoogleExtension = this;

		//If they aren't available don't trigger the prompt for install flow as this may be a fire device etc - leave that up to the dev to decide if\when to do this.
		if(GooglePlayServices_Status() == ConnectionResult.SUCCESS)
		{
			Log.i("yoyo", "Google Play Services extension initialising" );
			GooglePlayServices_Init();
		}
		else
		{
			Log.i("yoyo", "Google Play Services extension not initialising as AreGooglePlayServicesAvailable returns false" );
		}
	
	}
	
	@Override
	public void onStart() 
	{
		Log.i("yoyo","googleplayservices extension onStart called");
		//super.onStart();
		
		//Uncomment these lines if you want auto sign-in
		// Login();
	}

	@Override
	public void onStop() 
	{
		Log.i("yoyo","googleplayservices extension onStop called");
		//super.onStop();
		
	}
	
	@Override
	public void onPause()
	{
		mSignedInOnPause = isSignedIn();
	}
	
	@Override
	public void onResume()
	{
		if(mGcmPush !=null )
		{
			mGcmPush.deliverStoredMessages();
		}
		
		// If the user was signed in when the activity was paused, log them in again
		Log.i("yoyo", "googleplayservices extension onResume called. Signed in on pause: " + mSignedInOnPause + ". Signed in now: " + isSignedIn());
		if(mSignedInOnPause == true && isSignedIn() == false)
		{
			Log.i("yoyo", "Re-logging in..");
			Login();
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) 
	{
	}

	public double GooglePlayServices_Status()
	{
		return (double)GooglePlayServicesUtil.isGooglePlayServicesAvailable(RunnerJNILib.ms_context);
	}
  
    // The rest of this code is all about building the error dialog
    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode,int requestCode) 
    {    
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,RunnerActivity.CurrentActivity, requestCode);
        if (dialog != null) 
        {
            dialog.show();
        }
        else 
        {
            // no built-in dialog: show the fallback error message
			// showAlert(RunnerActivity.CurrentActivity, );
			Log.i("yoyo","Google Play Services Error and unable to initialise GooglePlayServicesUtil error dialog");
        }
    }

	@Override
    public void onConnectionFailed(ConnectionResult result) {
		
		Log.i("yoyo","onConnectionFailed called with result:"+result);
    }

	@Override
	public void onConnectionSuspended(int i) 
	{
		// Attempt to reconnect
		
		Log.i("yoyo","onConnectionSuspended call with " + i);
		
	}
	
	@Override
	public	void onActivityResult(int requestCode, int responseCode, Intent intent) 
	{
		Log.i("yoyo","gps onActivityResult called. RequestCode: "+requestCode + ". ResponseCode: " + responseCode);
		
		 switch (requestCode) 
		 {
			case RC_SIGN_IN:
				mSignInClicked = false;
				
				try
				{
					Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
					if(task.isSuccessful())
					{
						mGoogleSignInAccount = task.getResult();
						onLoginSuccess(mGoogleSignInAccount);
					}
					else
					{
						Log.i("yoyo", "Login failed! Result: " + intent);
						onLoginFailed();
					}
				}
				catch(Exception exception)
				{
					Log.i("yoyo", "Exception when handling GP services login: " + exception);
					onLoginFailed();
				}
			break;
			
            default:
				Log.i("yoyo","onActivityResult called with " + requestCode);
                break;
        }
	}
	
	Object mRunnerBilling=null;
	
	public Object InitRunnerBilling()
	{
		
		if(mRunnerBilling==null)
		{
			String FullClassName = "${YYAndroidPackageName}.RunnerBilling";
			
			try
			{
				/*		
				Class c = Class.forName(FullClassName);
				if(c==null)
				{
					Log.i("yoyo","Unable to find class");
				}
				else
				{
					Method[] methods = c.getMethods();
					for (int i = 0; i < methods.length; i++) {
						Log.i("yoyo","public method: " + methods[i]);
					}
					
					  
					Constructor[] allConstructors = c.getDeclaredConstructors();
					for (Constructor ctor : allConstructors) {
						
						Log.i("yoyo","constuctor:" + ctor);
						
					}
				}
				*/
				
				iap_controller = (RunnerBillingInterface)(Class.forName(FullClassName).getConstructor().newInstance());
				
				Log.i("yoyo","Created iap_controller, about to call InitRunnerBilling");
				
				mRunnerBilling = iap_controller.InitRunnerBilling();
				Log.i("yoyo","iap_controller successfully created");
			}
			catch(Exception e)
			{
				Log.i("yoyo","Failed to initialize Google Play Services IAP functionality - could not initialise RunnerBilling:"+e);
				Log.i("yoyo","If you are intending to use Google Play Services IAP's please ensure you have the GooglePlayServicesIAPExtension added to your project");
				Log.i("yoyo",e.toString());
				Log.i("yoyo",e.getMessage());
				e.printStackTrace();
			}
		}
		
		return mRunnerBilling;
	}
	
	private GoogleSignInAccount getCurrentAccountSignedIn()
	{
		if(mGoogleSignInAccount != null)
			return mGoogleSignInAccount;
		
		return GoogleSignIn.getLastSignedInAccount(RunnerJNILib.ms_context);
	}
	
	public boolean isSignedIn()
	{
		return getCurrentAccountSignedIn() != null;
	}
	
	// ==================================================
	// AUTH - LOGIN
	// ==================================================
	@Override
	public void Login()
	{
		if(isSignedIn())
		{
			Log.i("yoyo","Called achievement_login when already logged in");
			return;
		}
		
		if(mGoogleSigninClient == null)
		{
			Log.i("yoyo", "Called achievement_login with a NULL GoogleSigninClient");
			return;
		}
		
		Log.i("yoyo","Signing in..");
		
		Task<GoogleSignInAccount> task = mGoogleSigninClient.silentSignIn();
		mSignInClicked = true;
		
		if (task.isComplete()) 
		{
			onLoginTaskComplete(task);
		}
		else 
		{
			// There's no immediate result ready, displays some progress indicator and waits for the
			// async callback.
			task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() 
			{
				@Override
				public void onComplete(Task<GoogleSignInAccount> loginTask) 
				{
					onLoginTaskComplete(loginTask);
				}
			});
		}
	}
	
	private void onLoginTaskComplete(Task<GoogleSignInAccount> task)
	{
		if(task.isSuccessful())
		{
			Log.i("yoyo", "Silent sign in successful");
			mSignInClicked = false;
			
			mGoogleSignInAccount = task.getResult();
			onLoginSuccess(mGoogleSignInAccount);
		}
		else
		{
			Log.i("yoyo", "Silent sign in failed, attempting normal sign in..");
			
			// No luck signing in silently, trigger normal sign in flow
			Intent signInIntent = mGoogleSigninClient.getSignInIntent();
			RunnerActivity.CurrentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
		}
	}
	
	private void onLoginSuccess(GoogleSignInAccount account)
	{
		// Set up achievement popup view
		GamesClient gamesClient = Games.getGamesClient(RunnerActivity.CurrentActivity, account);
		gamesClient.setViewForPopups(RunnerActivity.CurrentActivity.getWindow().getDecorView());
		
		// Notify runner
		String displayName = account.getDisplayName();
		String id = account.getId();
		Log.i("yoyo", "User " + displayName + " (ID: " + id + ") logged in successfully.");
		
		RunnerJNILib.OnLoginSuccess(displayName, id, "", "", "", "", "");
	}
	
	private void onLoginFailed()
	{
		// Send social event
		RunnerJNILib.OnLoginSuccess("Not logged in","-1","","","","","");
	}
	
	// ==================================================
	// AUTH - LOGOUT
	// ==================================================
	@Override
	public void Logout()
	{
		if(mSignOutClicked == false && mGoogleSigninClient != null)
		{
			mSignOutClicked = true;
			
			Task task = mGoogleSigninClient.signOut();
			if (task.isComplete()) 
			{
				onLogoutTaskComplete(task);
			}
			else 
			{
				// There's no immediate result ready, displays some progress indicator and waits for the
				// async callback.
				task.addOnCompleteListener(new OnCompleteListener() 
				{
					@Override
					public void onComplete(Task logoutTask) 
					{
						mSignOutClicked = false;
						onLogoutTaskComplete(logoutTask);
					}
				});
			}
		}
	}
	
	private void onLogoutTaskComplete(Task task)
	{
		mSignOutClicked = false;
		
		if(task.isSuccessful())
		{
			Log.i("yoyo", "Signed out successfully.");
			RunnerJNILib.OnLoginSuccess("Not logged in","-1","","","","","");
			mGoogleSignInAccount = null;
		}
		else
		{
			Log.i("yoyo", "Signing out failed.");
		}
	}
	
	// ==================================================
	// PUSH NOTIFICATIONS
	// ==================================================
	public void setupPushNotifications(String pushID)
	{
		
		mGcmPush = new GcmPush( RunnerActivity.CurrentActivity );
		mGcmPush.Init( pushID );
		//check for stored messages on startup- also done in onResume ( in case of messages received while in background),
		//but this is called after onResume on startup
		mGcmPush.deliverStoredMessages();

	}
	
	public static void QueuePushNotification( String msgData, Integer type, Boolean bSuccess )
	{
		if( CurrentGoogleExtension.mGcmPush != null ) 
		{
			RunnerJNILib.GCMPushResult(msgData, type, true );
		}
	}
	
	public int PushGetLocalNotification(Integer iIndex, Integer dsMap)
	{
		if( mGcmPush != null )
		{
			return mGcmPush.pushGetLocalNotification( iIndex, dsMap);
		}
		
		return -1;
	}
	public int pushCancelLocalNotification(Integer iIndex)
	{
		
		if( mGcmPush != null )
		{
			return mGcmPush.pushCancelLocalNotification( iIndex);
		}
		
		return -1;
		
	}
	
	public static void PushLocalNotification( Float fireTime, String title, String message, String data )
	{
		if( CurrentGoogleExtension.mGcmPush != null )
		{
			Log.i("yoyo", "Push notifications title:"+title + " message:" + message+ " fireTime:" + fireTime);
			CurrentGoogleExtension.mGcmPush.pushLocalNotification( fireTime, title, message, data );
		}
		else 
		{
			Log.i("yoyo", "Push notifications not enabled");
		}
	}
	
	// ==================================================
	// CLOUD LOAD/SYNC
	// ==================================================
	private static final int MAX_SNAPSHOT_RESOLVE_RETRIES = 5;
	
	// Called when a google cloud sync request is resolved
	private final OnSnapshotResolvedListener mOnCloudSyncListener = new OnSnapshotResolvedListener() 
	{
		@Override
		public void onSuccess(Snapshot snapshot, int fileId)
		{
			// Read the byte content of the saved game.
			try 
			{
				mSaveGameData = snapshot.getSnapshotContents().readFully();
				String s = new String(mSaveGameData);
				Log.i("yoyo", "Successfully loaded cloud save data:" + s);
				
				RunnerJNILib.CloudResultData(s.getBytes(), 0 /*STATUS_NEW_GAME_DATA*/, fileId); 
			}
			catch (IOException e) 
			{
				onFailure("Error while reading cloud save snapshot." + e, fileId);
			}
		}
		
		@Override
		public void onFailure(String reason, int fileId)
		{
			Log.i("yoyo", "Cloud sync failed: " + reason);
			
			// Send empty save data if we failed to resolve our conflicts
			RunnerJNILib.CloudResultData( null, 0 /*STATUS_NEW_GAME_DATA*/, fileId );
		}
	};
	
	public void onGSCloudSync(java.lang.Integer id)
	{
		if(!mCloudServicesEnabled)
		{
			Log.i("yoyo", "Could not perform cloud sync - please tick the 'Enable Google Cloud Saving' option in the Android options of your project.");
			return;
		}
		
		if(mCloudSyncInProgress)
		{
			Log.i("yoyo", "Could not perform cloud sync - another sync is already in progress.");
			return;
		}
		
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not perform cloud sync - not signed in.");
			return;
		}
		
		mCloudSyncInProgress = true;
		mCloudSyncConflictRetries = 0;
		
		final Integer fid = id;
		Log.i("yoyo", "Google Play Cloud Sync called with ID: " + id);
		
		try
		{
			final SnapshotsClient snapClient = Games.getSnapshotsClient(RunnerJNILib.ms_context, account);
			Task<SnapshotsClient.DataOrConflict<Snapshot>> task = snapClient.open(mCurrentSaveName, true);
			
			task.addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() 
			{
				@Override
				public void onComplete(Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask) 
				{
					onCloudSyncTaskComplete(openTask, fid, snapClient, mOnCloudSyncListener);
				}
			});
		}
		catch(Exception e)
		{
			Log.i("yoyo", "Error starting Google Play sync: " + e);
			mCloudSyncInProgress = false;
		}
	}
	
	// ==================================================
	// CLOUD SAVE
	// ==================================================
	public void onGSStringSave(String data, String desc, Integer id)
	{
		if(!mCloudServicesEnabled)
		{
			Log.i("yoyo", "Could not perform cloud save - please tick the 'Enable Google Cloud Saving' option in the Android options of your project.");
			return;
		}
		
		if(mCloudSyncInProgress)
		{
			Log.i("yoyo", "Could not perform cloud save - another sync is already in progress.");
			return;
		}
		
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not perform cloud save - not signed in.");
			return;
		}
		
		mCloudSyncInProgress = true;
		mCloudSyncConflictRetries = 0;
		
		final String _data = data;
		final String _desc = desc;
		final Integer _id = id;
		
		Log.i("yoyo","Performing cloud save. ID: "+id+" .Data: " + data);
		
		try
		{
			final SnapshotsClient snapClient = Games.getSnapshotsClient(RunnerJNILib.ms_context, account);
			Task<SnapshotsClient.DataOrConflict<Snapshot>> task = snapClient.open(mCurrentSaveName, true);
			
			// Called when the snapshot is opened, attampts to save our data
			final OnSnapshotResolvedListener onSnapshotResolved = new OnSnapshotResolvedListener() 
			{
				@Override
				public void onSuccess(Snapshot snapshot, final int fileId)
				{
					// Read the byte content of the saved game.
					try 
					{
						// Write the new data to the snapshot
						byte[] databytes = _data.getBytes();
						snapshot.getSnapshotContents().writeBytes(databytes);
						final String s = new String(snapshot.getSnapshotContents().readFully());
						Log.i("yoyo", "Saving snapshot:" + s);

						// Update metadata
						SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
							.fromMetadata(snapshot.getMetadata())
							.setDescription("Modified data at: " + Calendar.getInstance().getTime())
							.build();

						// Submit new snapshot
						Task<SnapshotMetadata> submitTask = snapClient.commitAndClose(snapshot, metadataChange);
						submitTask.addOnCompleteListener(new OnCompleteListener<SnapshotMetadata>() 
						{
							@Override
							public void onComplete(Task<SnapshotMetadata> submitCompletedTask) 
							{
								if(submitCompletedTask.isSuccessful())
								{
									Log.i("yoyo", "Cloud save successful!");
									RunnerJNILib.CloudResultData(s.getBytes(), 0 /*STATUS_NEW_GAME_DATA*/, fileId); 
								}
								else
								{
									onFailure("Error while saving snapshot." + submitCompletedTask.getException(), fileId);
								}
							}
						});
					}
					catch (Exception e) 
					{
						onFailure("Error while saving snapshot." + e, fileId);
					}
				}
				
				@Override
				public void onFailure(String reason, int fileId)
				{
					Log.i("yoyo", "Cloud save failed: " + reason);
					RunnerJNILib.CloudResultData( null, 0 /*STATUS_NEW_GAME_DATA*/, fileId );
				}
			};
			
			task.addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() 
			{
				@Override
				public void onComplete(Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask) 
				{
					onCloudSyncTaskComplete(openTask, _id, snapClient, onSnapshotResolved);
				}
			});
		}
		catch(Exception e)
		{
			Log.i("yoyo", "Error starting Google Play sync: " + e);
			mCloudSyncInProgress = false;
		}
	}
	
	// ==================================================
	// CLOUD SYNC COMPLETE
	// ==================================================
	public void onCloudSyncTaskComplete(Task<SnapshotsClient.DataOrConflict<Snapshot>> task, final int fileId, final SnapshotsClient snapClient, final OnSnapshotResolvedListener onResolved)
	{
		// Task failed, exit out early
		if(!task.isSuccessful())
		{
			Log.i("yoyo", "Cloud sync failed. Error: " + task.getException());
			mCloudSyncInProgress = false;
			return;
		}
		
		Log.i("yoyo", "Cloud sync successful! Retrieving data..");
		SnapshotsClient.DataOrConflict<Snapshot> result = task.getResult();
		if(result.isConflict())
		{
			// Make sure we limit the number of retries in save data resolution
			if(mCloudSyncConflictRetries < MAX_SNAPSHOT_RESOLVE_RETRIES)
			{
				++ mCloudSyncConflictRetries;
				Log.i("yoyo", "Detected conflict in cloud save. Attempting to resolve: " + mCloudSyncConflictRetries);
				
				// Attempt to resolve our conflict
				try
				{
					SnapshotsClient.SnapshotConflict conflict = result.getConflict();
					Task<SnapshotsClient.DataOrConflict<Snapshot>> conflictTask = snapClient.resolveConflict(conflict.getConflictId(), conflict.getConflictingSnapshot());
					conflictTask.addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() 
					{
						@Override
						public void onComplete(Task<SnapshotsClient.DataOrConflict<Snapshot>> currentConflictTask) 
						{
							onCloudSyncTaskComplete(currentConflictTask, fileId, snapClient, onResolved);
						}
					});
				}
				catch(Exception e)
				{
					mCloudSyncInProgress = false;
					onResolved.onFailure("Error while resolving cloud save conflict. " + e, fileId);
				}
			}
			else
			{
				mCloudSyncInProgress = false;
				onResolved.onFailure("Failed to resolve cloud save conflict.", fileId);
			}
		}
		else
		{
			mCloudSyncInProgress = false;
			onResolved.onSuccess(result.getData(), fileId);
		}
	}
	
	// Achievement & Leaderboards stuff
	
	// ==================================================
	// LEADERBOARD - POST SCORE
	// ==================================================
	@Override
	public void PostScore(final String lb, final int score)
	{
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not post score - not signed in.");
			return;
		}
		
		Log.i("yoyo", "Posting score to leaderboard " + lb + ": " + score);
		
		LeaderboardsClient lbClient = Games.getLeaderboardsClient(RunnerJNILib.ms_context, account);
		Task<ScoreSubmissionData> submitTask = lbClient.submitScoreImmediate (lb, score);
		
		submitTask.addOnCompleteListener(new OnCompleteListener<ScoreSubmissionData>() {
			@Override
			public void onComplete(Task<ScoreSubmissionData> task) 
			{
				onLeaderboardPostTaskComplete(task, lb);
			}
		});
	}
	
	public void onLeaderboardPostTaskComplete(Task<ScoreSubmissionData> task, String leaderboardId)
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "achievement_post_score_result" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "id", GooglePlayServices_PostScoreResultEvent);
		RunnerJNILib.DsMapAddString( dsMapIndex, "leaderboard_id", leaderboardId );
		
		if(task.isSuccessful())
		{
			ScoreSubmissionData result = task.getResult();
			Log.i("yoyo", "Score submit request processed. Result: "+result);
			
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 1);
			
			// Add score submission data for whether or not we've beaten the best scores
			ScoreSubmissionData.Result resultDaily = result.getScoreResult(LeaderboardVariant.TIME_SPAN_DAILY);
			if(resultDaily != null)
			{
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "daily_new_best", resultDaily.newBest ? 1 : 0);
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "daily_best", (double)resultDaily.rawScore);
			}
			
			ScoreSubmissionData.Result resultWeekly = result.getScoreResult(LeaderboardVariant.TIME_SPAN_WEEKLY);
			if(resultWeekly != null)
			{
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "weekly_new_best", resultWeekly.newBest ? 1 : 0);
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "weekly_best", (double)resultWeekly.rawScore);
			}
			
			ScoreSubmissionData.Result resultAllTime = result.getScoreResult(LeaderboardVariant.TIME_SPAN_ALL_TIME);
			if(resultAllTime != null)
			{
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "all_time_new_best", resultAllTime.newBest ? 1 : 0);
				RunnerJNILib.DsMapAddDouble( dsMapIndex, "all_time_best", (double)resultAllTime.rawScore);
			}
		}
		else
		{
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 0);
		}
		
		// Send event
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
	}
	
	// ==================================================
	// LEADERBOARD - SHOW
	// ==================================================
	public void onShowGSLeaderboards()
    {
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not show leaderboards - user not signed in.");
			return;
		}
		
		Log.i("yoyo", "Attempting to show leaderboards.");
		LeaderboardsClient lbClient = Games.getLeaderboardsClient(RunnerJNILib.ms_context, account);
		Task<Intent> showLbTask = lbClient.getAllLeaderboardsIntent();
		
		showLbTask.addOnSuccessListener(new OnSuccessListener<Intent>() 
		{
			@Override
			public void onSuccess(Intent intent) 
			{
				Log.i("yoyo", "Showing leaderboards.");
				RunnerActivity.CurrentActivity.startActivityForResult(intent, RC_GPS_ACTIVITY);
			}
		});
		
		showLbTask.addOnFailureListener(new OnFailureListener() 
		{
			@Override
			public void onFailure(Exception e) 
			{
				Log.i("yoyo", "Failed to show leaderboards: " + e);
			}
		});
	}
   
	// ==================================================
	// ACHIEVEMENT - INCREMENT
	// ==================================================
	public void onIncrementAchievement(final String id, final java.lang.Float Increment)
    {
		float increment = (float)Increment;
		
		if ( increment < 1 ) // as per: https://developer.android.com/reference/com/google/android/gms/games/achievement/Achievements.html#increment(com.google.android.gms.common.api.GoogleApiClient, java.lang.String, int)
		{
			Log.i("yoyo","achievement_increment must be called with a positive value above 1");
		}
		else
		{
			// Get current logged in account
			GoogleSignInAccount account = getCurrentAccountSignedIn();
			if(account == null)
			{
				Log.i("yoyo", "Could not increment achievement - not signed in.");
				return;
			}
			
			// Post achievement
			Log.i("yoyo","Incrementing achievement " + id + " by " + increment);
			AchievementsClient achClient = Games.getAchievementsClient(RunnerJNILib.ms_context, account);
			Task<Boolean> submitTask = achClient.incrementImmediate(id, (int)increment);
			
			submitTask.addOnCompleteListener(new OnCompleteListener<Boolean>() 
			{
				@Override
				public void onComplete(Task<Boolean> task) 
				{
					onAchievementIncrementTaskComplete(task, id);
				}
			});
		}
    }
	
	private void onAchievementIncrementTaskComplete(Task<Boolean> task, String achievementId)
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "achievement_increment_result" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "id", GooglePlayServices_IncrementAchievementResultEvent);
		RunnerJNILib.DsMapAddString( dsMapIndex, "achievement_id", achievementId);
		
		if(task.isSuccessful())
		{
			Log.i("yoyo", "Successfully incremented achievement: " + achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 1);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "complete", task.getResult() ? 1 : 0);
		}
		else
		{
			Log.i("yoyo", "Failed to increment achievement: " + achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 0);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "complete", 0);
		}
		
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
	}
    
	// ==================================================
	// ACHIEVEMENT - UNLOCK
	// ==================================================
    public void onPostAchievement(final String id, final java.lang.Float percentdone)
    {
		if(percentdone <= 0)
		{
			Log.i("yoyo","achievement_post must be called with a positive value");
		}
		else if(percentdone >= 100.0)
		{
			// Get current logged in account
			GoogleSignInAccount account = getCurrentAccountSignedIn();
			if(account == null)
			{
				Log.i("yoyo", "Could not post achievement - not signed in.");
				return;
			}
			
			// Post achievement
			Log.i("yoyo","Posting achievement: " + id);
			AchievementsClient achClient = Games.getAchievementsClient(RunnerJNILib.ms_context, account);
			Task submitTask = achClient.unlockImmediate(id);
			
			submitTask.addOnCompleteListener(new OnCompleteListener() 
			{
				@Override
				public void onComplete(Task task) 
				{
					onAchievementPostTaskComplete(task, id);
				}
			});
		}
		else
		{
			Log.i("yoyo","Google Play Services does not currently support posting partially complete achievements. Either call achievement_post() when the achievement is complete or use achievement_increment()");
		}
    
    }
	
	public void onAchievementPostTaskComplete(Task task, String achievementId)
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "achievement_post_result" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "id", GooglePlayServices_PostAchievementResultEvent);
		RunnerJNILib.DsMapAddString( dsMapIndex, "achievement_id", achievementId);
		
		if(task.isSuccessful())
		{
			Log.i("yoyo", "Successfully posted achievement progress for: "+achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 1);
		}
		else
		{
			Log.i("yoyo", "Failed to post achievement progress for: "+achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 0);
		}
		
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
	}
	
	// ==================================================
	// ACHIEVEMENT - REVEAL
	// ==================================================
    public void onRevealAchievement(final String id)
    {
		// Get current logged in account
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not reveal achievement - not signed in.");
			return;
		}
		
		// Post achievement
		Log.i("yoyo","Revealing achievement: " + id);
		AchievementsClient achClient = Games.getAchievementsClient(RunnerJNILib.ms_context, account);
		Task submitTask = achClient.revealImmediate(id);
		
		submitTask.addOnCompleteListener(new OnCompleteListener() 
		{
			@Override
			public void onComplete(Task task) 
			{
				onAchievementRevealTaskComplete(task, id);
			}
		});
    }
	
	public void onAchievementRevealTaskComplete(Task task, String achievementId)
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "achievement_reveal_result" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "id", GooglePlayServices_RevealAchievementResultEvent);
		RunnerJNILib.DsMapAddString( dsMapIndex, "achievement_id", achievementId);
		
		if(task.isSuccessful())
		{
			Log.i("yoyo", "Successfully revealed achievement: "+achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 1);
		}
		else
		{
			Log.i("yoyo", "Failed to reveal achievement: "+achievementId);
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "status", 0);
		}
		
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
	}
    
	// ==================================================
	// ACHIEVEMENT - SHOW
	// ==================================================
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001,RC_GPS_ACTIVITY = 5011;
    public void onShowGSAchievements()
    {
		GoogleSignInAccount account = getCurrentAccountSignedIn();
		if(account == null)
		{
			Log.i("yoyo", "Could not show achievements - user not signed in.");
			return;
		}
		
		Log.i("yoyo", "Attempting to show achievements.");
		
		AchievementsClient achClient = Games.getAchievementsClient(RunnerJNILib.ms_context, account);
		Task<Intent> showLbTask = achClient.getAchievementsIntent();
		
		showLbTask.addOnSuccessListener(new OnSuccessListener<Intent>() 
		{
			@Override
			public void onSuccess(Intent intent) 
			{
				Log.i("yoyo", "Showing achievements!");
				RunnerActivity.CurrentActivity.startActivityForResult(intent, RC_GPS_ACTIVITY);
			}
		});
		
		showLbTask.addOnFailureListener(new OnFailureListener() 
		{
			@Override
			public void onFailure(Exception e) 
			{
				Log.i("yoyo", "Failed to show achievements: " + e);
			}
		});
    }
	
	// ==================================================
	// MOBILE ADS
	// ==================================================
	private AdListener adlistener = new AdListener(){
		@Override
		 public void onAdLoaded() {
			Log.i("yoyo","onAdLoaded called");
			sendInterstitialLoadedEvent( true );
            RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
				if(interstitialAd.isLoaded())
					InterstitialStatus="Ready";
				else
					InterstitialStatus = "Not Ready";
			}});
		 }

		@Override
		public void onAdFailedToLoad(int errorCode) {
			Log.i("yoyo","onAdFailedToLoad called");
			sendInterstitialLoadedEvent( false );
            RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
				if(interstitialAd.isLoaded())
					InterstitialStatus="Ready";
				else
					InterstitialStatus = "Not Ready";
			}});
		}
		@Override
		public void onAdClosed()
		{
			sendInterstitialClosedEvent();
		}

	};
	
	
	private static final int EVENT_OTHER_SOCIAL = 70;
	
	public void GoogleMobileAds_Init(String _Arg1, String AppId)
	{
		InterstitialId = _Arg1;	
        MobileAds.initialize(RunnerActivity.CurrentActivity,AppId);
	}
	
	public void GoogleMobileAds_ShowInterstitial()
	{
		if(interstitialAd!=null)
		{
	
			RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
				Log.i("yoyo","showinterstitial called");
				if (interstitialAd.isLoaded()) 
				{
					RunnerActivity.CurrentActivity.runOnUiThread(new Runnable() {
						public void run() {
							interstitialAd.show();
						}
					});
				} 
				else
				{
					Log.i("yoyo", "Interstitial ad was not ready to be shown.");
				}
    		}});
		}
    	
	}
	
	private void initInterstitial()
	{
	
		interstitialAd = new InterstitialAd(RunnerActivity.CurrentActivity);
		interstitialAd.setAdUnitId(InterstitialId);
			
		interstitialAd.setAdListener(adlistener);
	}
	
	public void GoogleMobileAds_LoadInterstitial()
	{
		RunnerActivity.ViewHandler.post( new Runnable() {
    	public void run() 
    	{
			if(interstitialAd==null)
				initInterstitial();
				
			AdRequest.Builder builder = new AdRequest.Builder();
			builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			if( bUseTestAds)
				builder.addTestDevice(TestDeviceId);
			AdRequest adRequest = builder.build();

			// Load the interstitial ad.
			interstitialAd.loadAd(adRequest);
		}});
	}
	
	private void sendBannerLoadedEvent( boolean _bLoaded )
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "banner_load" );
		double loaded = (_bLoaded) ? 1 : 0;
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "loaded", loaded);
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		if( _bLoaded)
		{
			GoogleMobileAds_MoveBanner(BannerXPos, BannerYPos);
		
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "width",  GoogleMobileAds_BannerGetWidth());
			RunnerJNILib.DsMapAddDouble( dsMapIndex, "height",  GoogleMobileAds_BannerGetHeight());
		}
		
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
	}
	
	private void sendInterstitialLoadedEvent( boolean _bLoaded )
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "interstitial_load" );
		double loaded = (_bLoaded) ? 1 : 0;
		RunnerJNILib.DsMapAddDouble( dsMapIndex, "loaded", loaded);
        RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
	}
	
	private void sendInterstitialClosedEvent(  )
	{
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "interstitial_closed" );
		RunnerJNILib.DsMapAddDouble( dsMapIndex,"id",GoogleMobileAds_ASyncEvent);
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
	}
	
	public void GoogleMobileAds_AddBanner(String _bannerId, double _sizeType)
	{
		GoogleMobileAds_AddBannerAt( _bannerId, _sizeType, 0, 0);
	}
	
	
	@TargetApi(11)
	void SetLayerType()
	{
		int sdkVersion =Build.VERSION.SDK_INT;
		if (sdkVersion > 10)
			adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
    
    public void GoogleMobileAds_ShowBanner()
    {
        RunnerActivity.ViewHandler.post( new Runnable() {
            public void run() 
            {
                if(adView!=null)
                    adView.setVisibility(View.VISIBLE);
            }
        });
    }
    
    public void GoogleMobileAds_HideBanner()
    {
        RunnerActivity.ViewHandler.post( new Runnable() {
            public void run() 
            {
                if(adView!=null)
                    adView.setVisibility(View.GONE);
            }
        });
    }
	
	public void GoogleMobileAds_AddBannerAt(String _bannerId, double _sizeType, double _x, double _y)
	{
		final String bannerId = _bannerId;
		BannerXPos = (int)_x;
		BannerYPos = (int)_y;
		int type = (int)(_sizeType+0.5);
		
		switch(type)
		{
		case 1: BannerSize = AdSize.BANNER; break;
		case 2: BannerSize = AdSize.MEDIUM_RECTANGLE;break;
		case 3: BannerSize = AdSize.FULL_BANNER;break;
		case 4: BannerSize = AdSize.LEADERBOARD;break;
		case 5: BannerSize = AdSize.WIDE_SKYSCRAPER; break;
        case 6: BannerSize = AdSize.SMART_BANNER; break;
		default: Log.i("yoyo", "AddBanner illegal banner size type:" + _sizeType); return;
		}
		
		RunnerActivity.ViewHandler.post( new Runnable() {
    	public void run() 
    	{
    		AbsoluteLayout layout = (AbsoluteLayout)RunnerActivity.CurrentActivity.findViewById(R.id.ad);
			ViewGroup vg = (ViewGroup)layout;
			
			//remove existing banner
			if( adView != null )
			{
				if(vg!=null)
				{
					vg.removeView( adView );
				}
				adView.destroy();
				adView = null;
			}
			
    		//create new banner
			adView = new AdView(RunnerActivity.CurrentActivity);
			//adView.setAdListener(GoogleMobileAdsExt.this);
			adView.setAdListener( new AdListener() {
				/** Called when an ad is loaded. */
			    @Override
			    public void onAdLoaded() 
			    {
			    	Log.i("yoyo", "Banner Ad onAdLoaded");
			    	sendBannerLoadedEvent(true);
			    }
			    
			    @Override
			    public void onAdFailedToLoad(int errorCode)
			    {
			    	Log.i("yoyo", "Banner Ad onAdFailedToLoad");
			    	sendBannerLoadedEvent(false);
			    }
			    
			});    
		
			SetLayerType();
			
		
			//adView.setAdSize(AdSize.BANNER);	
			adView.setAdSize( BannerSize );
			adView.setAdUnitId(bannerId);
			
			if(vg != null)
			{
                
            
                
				vg.addView((View)adView);
			
				AdRequest.Builder builder = new AdRequest.Builder();
				builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
				if( bUseTestAds)
					builder.addTestDevice(TestDeviceId);
				AdRequest adRequest = builder.build();

				// Start loading the ad in the background.
				//adView.setVisibility(View.INVISIBLE);
				adView.loadAd(adRequest);
			}
		}});
	}
	
	public void GoogleMobileAds_RemoveBanner()
	{
		if( adView != null )
		{
			RunnerActivity.ViewHandler.post( new Runnable() {
			public void run() 
		    {
				AbsoluteLayout layout = (AbsoluteLayout)RunnerActivity.CurrentActivity.findViewById(R.id.ad);
				ViewGroup vg = (ViewGroup)layout;
				if(vg!=null)
				{
					vg.removeView( adView );
				}
				adView.destroy();
				adView = null;
				
		    }});
		}
	}
	
	public double GoogleMobileAds_BannerGetWidth()
	{
		if( BannerSize !=null )
		{
			int w =BannerSize.getWidthInPixels(RunnerJNILib.ms_context); 
			return w;
		}
		return 0;
	}
	
	public double GoogleMobileAds_BannerGetHeight()
	{
		if( BannerSize !=null )
		{
			int h = BannerSize.getHeightInPixels(RunnerJNILib.ms_context);
			return h;
		}
		return 0;
	}
	
	public void GoogleMobileAds_MoveBanner( double _x, double _y )
	{
		//Log.i("yoyo", "MoveBanner:" + _x + "," + _y);
		final int x = (int)_x;
		final int y = (int)_y;
		BannerXPos = x;
		BannerYPos = y;

		if( adView != null )
		{
			RunnerActivity.ViewHandler.post( new Runnable() {
			public void run()
			{
				//if( x < 0 || y < 0) {
					//adView.setVisibility(View.INVISIBLE);
				//}
				//else
				//{
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, x,y );
                adView.setLayoutParams( params);
                adView.requestLayout();
					//adView.setVisibility(View.VISIBLE);
				//}
			}});
		}
	}
	
	public void GoogleMobileAds_UseTestAds( double _useTest, String _deviceId )
	{
		bUseTestAds = (_useTest >=0.5);
		TestDeviceId = _deviceId;
	}
	
	public String GoogleMobileAds_InterstitialStatus()
	{
		if(interstitialAd!=null)
		{
			RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() 
    		{
				if(interstitialAd.isLoaded())
					InterstitialStatus="Ready";
				else
					InterstitialStatus = "Not Ready";
			}});
		}

		return InterstitialStatus;
	}
	
	
}