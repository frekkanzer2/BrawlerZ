package ${YYAndroidPackageName};

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import ${YYAndroidPackageName}.BillingRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.android.vending.billing.IInAppBillingService;

//----------------------------------------------------------------------------------------------------
public class RunnerBillingService extends Service implements ServiceConnection 
{
    /** The service connection to the remote MarketBillingService. */
	private IInAppBillingService mService = null;

	/** Potential callback for when the billing service is connected **/
	private IRunnerBilling.IBillingCallback mServiceConnectedCallback = null;

	/**
	 * Injected context
	 */ 
	private Context mContext;

	/**
	 * Constructor
	 */
	public RunnerBillingService(Context context)
	{
		mContext = context;
	}

	/**
     * We don't support binding to this service, only starting the service.
     */
    @Override
    public IBinder onBind(Intent intent) 
    {
        return null;
    }

	/**
     * Binds to the Billing Service and returns true if the bind succeeded.
     * @return true if the bind succeeded; false otherwise
     */
    public boolean bindToBillingService(IRunnerBilling.IBillingCallback _serviceConnectedCallback)
    {
		mServiceConnectedCallback = _serviceConnectedCallback;		
        try
		{
			Log.i("yoyo", "BILLING: Attempting to bind to Billing Service");
			Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
			serviceIntent.setPackage("com.android.vending");
			if (!mContext.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty()) {

				mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
	            return true;
			}
			else {
				Log.i("yoyo", "BILLING: Could not bind to Billing Service");
				return false;
			}
        }
        catch (SecurityException e) {
            Log.i("yoyo", "BILLING: Security exception: " + e);
        }
        catch (Exception e) {
        	Log.i("yoyo", "BILLING: Exception: " + e);
        }
        return false;
    }

	/**
     * This is called when we are connected to the Billing Service.
     * This runs in the main UI thread.
     */
    public void onServiceConnected(ComponentName name, IBinder service) {        

        Log.i("yoyo", "BILLING: Service connected");
        mService = IInAppBillingService.Stub.asInterface(service);		

		String packageName = mContext.getPackageName();
    	try {
        	// check for in-app billing v3 support
        	int response = mService.isBillingSupported(RunnerBilling.API_VERSION, packageName, "inapp");
			if (response != RunnerBilling.BILLING_RESPONSE_RESULT_OK) {
				Log.i("yoyo", "BILLING: Billing v3 support not available");
				if (mServiceConnectedCallback != null) {
					mServiceConnectedCallback.onComplete(RunnerBilling.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE, null);
					return;
				}
			}		
		}
		catch (RemoteException e) {
        	e.printStackTrace();        	
        }
		if (mServiceConnectedCallback != null) {
			Log.i("yoyo", "BILLING: Billing v3 support available");
			mServiceConnectedCallback.onComplete(RunnerBilling.BILLING_RESPONSE_RESULT_OK, null);
		}        
    }

    /**
     * This is called when we are disconnected from the BillingService.
     */
    public void onServiceDisconnected(ComponentName name) {

        Log.i("yoyo", "BILLING: Service disconnected");
        mService = null;
    }

	/*
	 * Check with the billing service whether or not subscriptions are supported
	 */
	public boolean SubscriptionsSupported()
	{
		boolean retValue = true;
		if (mService == null) {
			retValue = false;
		}
		else {
			String packageName = mContext.getPackageName();
        	try {                            	    
            	int response = mService.isBillingSupported(RunnerBilling.API_VERSION, packageName, "subs");
            	if (response != RunnerBilling.BILLING_RESPONSE_RESULT_OK) {                
					retValue = false;
            	}
			}
			catch (RemoteException e) {
        	    e.printStackTrace();
        	    retValue = false;
        	}
		}
		return retValue;
	}

	/**
     * Run the request, starting the connection if necessary.
     * @return true if the request was executed or queued; false if there
     * was an error starting the connection
     */
    private boolean runBillingRequest(BillingRequest request) 
    {
        if (runRequestIfConnected(request)) 
        {
            return true;
        }
		return false;
	}

    /**
     * Try running the request directly if the service is already connected.
     * @return true if the request ran successfully; false if the service
     * is not connected or there was an error when trying to use it
     */
    private boolean runRequestIfConnected(BillingRequest _request) 
    {        
        if (mService != null)
        {
			final IInAppBillingService service = mService;
			final BillingRequest request = _request;
			new Thread(new Runnable() {
				public void run() {
					try {
						request.run(service);
					}
					catch (RemoteException e) {
						Log.i("yoyo", "BILLING: Billing request failed " + e.getMessage());
					}
				}
			}).start();
			return true;
		}
        return false;
	}

    /**
     * Requests that the given item be offered to the user for purchase. When
     * the purchase succeeds (or is canceled) the {@link BillingReceiver}
     * receives an intent with the action {@link Consts#ACTION_NOTIFY}.
     * Returns false if there was an error trying to connect to Android Market.
     * @param productId an identifier for the item being offered for purchase
     * @param developerPayload a payload that is associated with a given
     * purchase, if null, no payload is sent
     * @return false if there was an error connecting to Android Market
     */
    public boolean requestPurchase(String productId, String developerPayload, int requestCode, IRunnerBilling.IBillingCallback callback) 
    {
    	Log.i("yoyo", "BILLING: Requesting " + productId + " for purchase");
		return runBillingRequest(new RequestPurchase(productId, developerPayload, requestCode, callback));
    }

	/**
     * New to Version3 API. Requests a purchase be consumed
     * @return false if there was an error connecting to Android Market
     */
	public boolean requestConsume(String token, IRunnerBilling.IBillingCallback callback) 
	{
		Log.i("yoyo", "BILLING: Consuming product with token " + token);
		return runBillingRequest(new RequestConsume(token, callback));
	}

	/**
     * New to Version3 API. Requests the details of a sku item from 
     * application is first installed or after a database wipe. Do NOT call this
     * every time the application starts up.
     * @return false if there was an error connecting to Android Market
     */
	public boolean requestSkuDetails(String[] productIds, IRunnerBilling.IBillingCallback callback)
	{
		Log.i("yoyo", "BILLING: Request sku details");
		return runBillingRequest(new GetSkuDetails(productIds, callback));
	}

	/**
	 * Query the set of purchases concerning this app
	 */
	public boolean requestPurchases(IRunnerBilling.IBillingCallback callback)
	{
		Log.i("yoyo", "BILLING: Request purchase information");
		return runBillingRequest(new GetPurchases(callback));
	}

    /**
     * Unbinds from the MarketBillingService. Call this when the application
     * terminates to avoid leaking a ServiceConnection.
     */
    public void unbind() {

        try {
            unbindService(this);
        } 
        catch (IllegalArgumentException e) {
			Log.i("yoyo", "BILLING: Failing to unbind service " + e.getMessage());
        }
		catch (NullPointerException e) {
			Log.i("yoyo", "BILLING: Failing to unbind service " + e.getMessage());
		}
    }	
}