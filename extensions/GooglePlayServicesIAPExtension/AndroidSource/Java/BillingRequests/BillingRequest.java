package ${YYAndroidPackageName};

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import ${YYAndroidPackageName}.RunnerActivity;

import com.android.vending.billing.IInAppBillingService;

/**
* The base class for all requests that use the BillingService.
* Each derived class overrides the run() method to call the appropriate
* service interface.  If we are already connected to the BillingService,
* then we call the run() method directly. Otherwise, we bind
* to the service and save the request on a queue to be run later when
* the service is connected.
*/
abstract class BillingRequest
{		
    abstract protected void run(IInAppBillingService billingService) throws RemoteException;	

	/** 
	 * Short cut to get the package name for each billing request
	 */
	protected String getPackageName()
	{
		return RunnerActivity.CurrentActivity.getComponentName().getPackageName();
	}

	/** 
	 * Workaround to bug where sometimes response codes come as Long instead of Integer
	 */	
    int getResponseCodeFromBundle(Bundle b) {

        Object o = b.get("RESPONSE_CODE");
        if (o == null) {
			Log.i("yoyo", "BILLING: Bundle with null response code, assuming OK (known issue)");
            return RunnerBilling.BILLING_RESPONSE_RESULT_OK;
        }
		else if (o instanceof Integer) { 
			return ((Integer)o).intValue();
		}
		else if (o instanceof Long) { 
			return (int)((Long)o).longValue();
		}
        else {
			Log.i("yoyo", "BILLING: Unexpected type for bundle response code " + o.getClass().getName());            
            throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
        }
    }
}
