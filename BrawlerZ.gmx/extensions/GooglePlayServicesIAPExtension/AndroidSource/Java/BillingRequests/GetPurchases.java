package ${YYAndroidPackageName};

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import ${YYAndroidPackageName}.BillingRequest;
import ${YYAndroidPackageName}.RunnerBilling;

import com.android.vending.billing.IInAppBillingService;

/**
 * Wrapper class that sends a RESTORE_TRANSACTIONS message to the server.
 */
class GetPurchases extends BillingRequest
{	
	IRunnerBilling.IBillingCallback mCallback;

    public GetPurchases(IRunnerBilling.IBillingCallback callback) 
    {		
		mCallback = callback;
    }

    @Override
    protected void run(IInAppBillingService billingService) throws RemoteException 
    {		
		// If a user has a large number of purchases a continuation token will be required
		String continueToken = null;
        Bundle ownedItems = billingService.getPurchases(RunnerBilling.API_VERSION, getPackageName(), "inapp", continueToken);
        int response = getResponseCodeFromBundle(ownedItems);            
		if (mCallback != null) {
			mCallback.onComplete(response, ownedItems);
		}
    }
}
