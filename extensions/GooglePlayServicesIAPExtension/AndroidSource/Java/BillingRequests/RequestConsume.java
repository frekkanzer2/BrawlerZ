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
class RequestConsume extends BillingRequest 
{		
    public final String mToken;
	public final IRunnerBilling.IBillingCallback mCallback;

    public RequestConsume(String token, IRunnerBilling.IBillingCallback callback) 
    {
		mToken = token;
		mCallback = callback;
    }

    @Override
    protected void run(IInAppBillingService billingService) throws RemoteException 
    {
		try {			
			int response = billingService.consumePurchase(RunnerBilling.API_VERSION, getPackageName(), mToken);
			mCallback.onComplete(response, null);
        }
        catch (RemoteException e) {
			Log.i("yoyo", "BILLING: RemoteException while launching purchase flow for " + mToken);
            e.printStackTrace();
        }
    }
}