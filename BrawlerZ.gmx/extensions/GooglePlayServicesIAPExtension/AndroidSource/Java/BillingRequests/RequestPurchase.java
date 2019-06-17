package ${YYAndroidPackageName};

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import ${YYAndroidPackageName}.BillingRequest;
import ${YYAndroidPackageName}.RunnerBilling;

import com.android.vending.billing.IInAppBillingService;

/**
 * Wrapper class that requests a purchase.
 */
class RequestPurchase extends BillingRequest 
{	
	public final int mRequestCode;
    public final String mProductId;
    public final String mDeveloperPayload;
	public final IRunnerBilling.IBillingCallback mCallback;

    public RequestPurchase(String productId, String developerPayload, int requestCode, IRunnerBilling.IBillingCallback callback) 
    {
		mProductId = productId;
		mDeveloperPayload = developerPayload;
		mRequestCode = requestCode;
		mCallback = callback;
    }

    @Override
    protected void run(IInAppBillingService billingService) throws RemoteException 
    {
		try {
			Bundle buyIntentBundle = billingService.getBuyIntent(RunnerBilling.API_VERSION, getPackageName(), mProductId, "inapp", mDeveloperPayload);			

			int response = getResponseCodeFromBundle(buyIntentBundle);
        	if (response != RunnerBilling.BILLING_RESPONSE_RESULT_OK) {

				Log.i("yoyo", "BILLING: Unable to buy item, Error response=" + RunnerBilling.getResponseDesc(response));        	    
        	    mCallback.onComplete(response, null);
        	}
			else {
        		PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");				

				Log.i("yoyo", "BILLING: Launching Buy Intent for " + mProductId);
        		RunnerActivity.CurrentActivity.startIntentSenderForResult(
					pendingIntent.getIntentSender(), 
					mRequestCode,
					new Intent(),
					Integer.valueOf(0), 
					Integer.valueOf(0), 
					Integer.valueOf(0));
			}
		}
		catch (SendIntentException e) {

			Log.i("yoyo", "BILLING: SendIntentException while launching purchase flow for " + mProductId);
            e.printStackTrace();
        }
        catch (RemoteException e) {
			Log.i("yoyo", "BILLING: RemoteException while launching purchase flow for " + mProductId);
            e.printStackTrace();
        }
		catch (NullPointerException e) {
			Log.i("yoyo", "BILLING: NullPointerException while launching purchase flow for " + mProductId);
			e.printStackTrace();
		}
    }
}