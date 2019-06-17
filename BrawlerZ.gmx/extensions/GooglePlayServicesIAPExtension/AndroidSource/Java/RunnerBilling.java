package ${YYAndroidPackageName};

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.net.Uri;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import ${YYAndroidPackageName}.RequestPurchase;

import com.android.vending.billing.IInAppBillingService;
import com.yoyogames.runner.RunnerJNILib;

//This is version 1.0.2
//----------------------------------------------------------------------------------------------------

public class RunnerBilling  extends IRunnerBilling implements RunnerBillingInterface
{	
	private HashMap<Integer, PurchaseDetails> mPurchaseRequests = new HashMap<Integer, PurchaseDetails>();	

	/** Error codes **/
    public static final int BILLING_ERROR_BASE = -1000;
    public static final int BILLING_REMOTE_EXCEPTION = -1001;
    public static final int BILLING_BAD_RESPONSE = -1002;
    public static final int BILLING_VERIFICATION_FAILED = -1003;
    public static final int BILLING_SEND_INTENT_FAILED = -1004;
    public static final int BILLING_USER_CANCELLED = -1005;
    public static final int BILLING_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int BILLING_MISSING_TOKEN = -1007;
    public static final int BILLING_UNKNOWN_ERROR = -1008;
    public static final int BILLING_SUBSCRIPTIONS_NOT_AVAILABLE = -1009;
    public static final int BILLING_INVALID_CONSUMPTION = -1010;

	/** Response codes **/
	public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;	


	/** Constant for the current api version **/
	public static final int API_VERSION = 3;

	/** Identify Intent result as related to purchases **/
	private static int mRequestCodeBase = 7;

	/** The developer payload that is sent with subsequent purchase requests. **/
    private String mBillingPayloadContents = null;

	/* The billing service used for communications */
    private RunnerBillingService mBillingService = null;

	/*
	 * Constructor
	 */
	public RunnerBilling()
	{
    	// And the billing service (NB: This can't happen on the UI thread)
	    mBillingService = new RunnerBillingService(RunnerJNILib.ms_context);
		
		Log.i("yoyo","RunnerBillingService created");
	}

	/*
	 * Called when onDestroy() is hit for the main Activity
	 */
	public void Destroy()
	{
	    mBillingService.unbind();
	}

	public Object InitRunnerBilling()
	{
		return (Object)this;
	}

	/*
	 * Called whilst enabling the store at the IRunnerBilling level
	 * This is a little bit messy as it's trying to put a linear
	 * model through a bunch of async w/ callback routines
	 */
	public void loadStore(String[] _productIds)
	{
		final String[] productIds = _productIds;

		// a) Bind to the billing service
		mBillingService.bindToBillingService(new IBillingCallback() {
			public void onComplete(int responseCode, Object callbackData) {

				if (responseCode == BILLING_RESPONSE_RESULT_OK) {

					// b) Get the details of each product listed by the user
					RetrieveSkuDetails(productIds, new IBillingCallback() {
						public void onComplete(int responseCode, Object callbackData) {

							// I don't mind whether or not the sku details retrieval worked
							// since it's not a fatal problem for the store to function

							// c) Retrieve all known purchases
							RetrievePurchases(new IBillingCallback() {
								public void onComplete(int responseCode, Object callbackData) {

									if (responseCode == BILLING_RESPONSE_RESULT_OK) {										
										RunnerJNILib.IAPStoreLoadEvent(eStoreLoadSuccess);
									}
									else {
										Log.i("yoyo", "BILLING: Failed to retrieve purchase data");
										RunnerJNILib.IAPStoreLoadEvent(eStoreLoadFailure);
									}
								}
							});
						}
					});
				}
				else {
					Log.i("yoyo", "BILLING: Unable to bind to Billing Service");
					RunnerJNILib.IAPStoreLoadEvent(eStoreLoadFailure);
				}
			}
		});
    }

	/**
	 * Called once the billing service is bound to find out the available products and their statuses
	 */
	private void RetrieveSkuDetails(String[] _productIds, IBillingCallback _callback)
	{		
		final IBillingCallback callback = _callback;

		mBillingService.requestSkuDetails(_productIds, new IBillingCallback() {
			public void onComplete(int _responseCode, Object _callbackData) {

				final int responseCode = _responseCode;
				final Object callbackData = _callbackData;

				RunnerActivity.ViewHandler.post(new Runnable() {
					public void run() {
						if (responseCode == BILLING_RESPONSE_RESULT_OK)
						{
							ArrayList<String> responseList = (ArrayList<String>)callbackData;
							for (String thisResponse : responseList) 
							{								
								RunnerJNILib.IAPProductDetailsReceived(thisResponse);
        					}
							if (callback != null) {
								callback.onComplete(responseCode, null);
							}
						}
						else if (callback != null) {
							callback.onComplete(responseCode, null);
						}
					}
				});
			}
		});
	}

	/**
	 * Called once the billing service is bound to find out the available products and their statuses
	 */
	private void RetrievePurchases(IBillingCallback _callback)
	{		
		final IBillingCallback callback = _callback;

		mBillingService.requestPurchases(new IBillingCallback() {

			public void onComplete(int _responseCode, Object _callbackData) {

				final int responseCode = _responseCode;
				final Object callbackData = _callbackData;

				RunnerActivity.ViewHandler.post(new Runnable() {
					public void run() {

						if (responseCode == BILLING_RESPONSE_RESULT_OK)
						{
							// We get back the Bundle in this case
							Bundle ownedItems = (Bundle)callbackData;

							if (!ownedItems.containsKey("INAPP_PURCHASE_ITEM_LIST") || 
								!ownedItems.containsKey("INAPP_PURCHASE_DATA_LIST") || 
								!ownedItems.containsKey("INAPP_DATA_SIGNATURE_LIST"))
							{
								Log.i("yoyo", "BILLING: Bundle returned from getPurchases() doesn't contain required fields.");
								if (callback != null) {
									callback.onComplete(BILLING_RESPONSE_RESULT_ERROR, null);
								}
							}
							else {
								ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
								ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
								ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
								// If the continuation token is not empty then we should really run another request using it...
								String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");								

								for (int i = 0; i < purchaseDataList.size(); ++i) {

                					String purchaseData = purchaseDataList.get(i);
                					String signature = signatureList.get(i);
                					String sku = ownedSkus.get(i);
                					if (RunnerBillingSecurity.verifyPurchase(purchaseData, signature)) 
									{										
										RunnerJNILib.IAPProductPurchaseEvent(purchaseData);
									}
									else {
										Log.i("yoyo", "BILLING: Purchase signature verification **FAILED**. Ignoring purchase for " + sku);
									}
								}
								// Report successful result to the callback routine
								if (callback != null) {
									callback.onComplete(BILLING_RESPONSE_RESULT_OK, null);
								}
							}
						}
						else {
							if (callback != null) {
								callback.onComplete(BILLING_RESPONSE_RESULT_ERROR, null);
							}
						}
					}
				});
            }
		});
	}

	/**
     * Called from the VC_Runner when the current game decides it wants all previously purchased items restored
     */
    public void restorePurchasedItems()
    {
		// Restore not supported by APIv3
		RunnerJNILib.IAPRestoreEvent(0);
    }	
    
    /**
     * Called from the VC_Runner when the current application decides it wants to purchase an item
     */
    public void purchaseCatalogItem(String _productId, String _payload, int _purchaseIndex) 
    {	
		final int purchaseIndex = _purchaseIndex;
		final String productId = _productId;
		final String payload = _payload;

		// Generate a request code for the purchase request
		final int requestCode = mRequestCodeBase + mPurchaseRequests.size();
		Log.i("yoyo", "BILLING: RequestCode=" + requestCode);
		mPurchaseRequests.put(Integer.valueOf(requestCode), new PurchaseDetails(_productId, _purchaseIndex));

        mBillingService.requestPurchase(
			productId,
			payload,
			requestCode,
			new IBillingCallback() {
				public void onComplete(int responseCode, Object callbackData) {
					
					if (responseCode != BILLING_RESPONSE_RESULT_OK)
					{			
						// Already owns means it's a "successful" purchase really
						if (responseCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
							PurchaseSuccessNotification(responseCode, productId, purchaseIndex);
						}
						else {							
							PurchaseFailureNotification(responseCode, productId, purchaseIndex);
						}
						mPurchaseRequests.remove(requestCode);
					}
					// Otherwise the billing status will be set back after the store
					// returns the results of the purchase attempt in handleActivityResult()
				}
			});
	}

	/**
     * Called from the VC_Runner when the current application decides it wants to consume an item
     */
	public void consumeCatalogItem(String _productId, String _token)
	{
		final String product = _productId;
		final String token = _token;
		
        mBillingService.requestConsume(
			token,
			new IBillingCallback() {
				public void onComplete(int _responseCode, Object _callbackData) {

					final int responseCode = _responseCode;
					final Object callbackData = _callbackData;

					// Post the event to the main thread for thread synchronisation purposes
					RunnerActivity.ViewHandler.post(new Runnable() {
						public void run() {							

							boolean consumed = true;
							switch (responseCode)
							{		
								case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
								case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
								case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
								case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
								case BILLING_RESPONSE_RESULT_USER_CANCELED:
								case BILLING_RESPONSE_RESULT_ERROR:
								{
									Log.i("yoyo", "BILLING: Consume Failed " + getResponseDesc(responseCode));
									consumed = false;
								}
								break;

								case BILLING_RESPONSE_RESULT_OK:
								case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
								{
									// Theoretically "not owned" is a failure, but we might've got here
									// via a second attempt at a consume due to a prior timeout error (6)
									// so I'm counting this as a successful attempt to consume
									Log.i("yoyo", "BILLING: Consume result " + getResponseDesc(responseCode));
								}
								break;
							}

							// Build up a JSON object to pass back
							String json = "{ \"productId\":\"" + product + "\"" + 
									", \"token\":\"" + token + "\"" +
									", \"consumed\":\"" + String.valueOf(consumed) + "\"" +									
									", \"response\":" + responseCode + " }";

							Log.i("yoyo", "BILLING: Consumed => " + json);
							RunnerJNILib.IAPConsumeEvent(json);
						}
					});
				}
			});
	}

	/**
	 * Get details of the product from the store
	 */
	public void getCatalogItemDetails(String _productId)
	{				
		final String productId = _productId;
		Log.i("yoyo", "BILLING: Retrieving sku details for " + productId);

		// Re-use the RetrieveSkuDetails as used in the startup
		String[] productIds = new String[]{ productId };
		RetrieveSkuDetails(productIds, new IBillingCallback() {
			public void onComplete(int _responseCode, Object _data) {

				// Retrieve sku details will cheerfully send through a successful result
				// but won't let us know about any failures so send that now if needs be
				if (_responseCode != BILLING_RESPONSE_RESULT_OK) {					

					// Send a failure response now...
					String json = "{ \"response\":" + _responseCode + " }";
					Log.i("yoyo", "BILLING: Get product details failed " + json);

					RunnerJNILib.IAPProductDetailsReceived(json);
				}
			}
		});
	}
	

	/**
	  * A successfully completed response has been received from the Android Market
	  */
	public void purchaseSucceeded(String purchaseId)
	{
		registerContentPurchased(purchaseId, true);
	}
    
    /**
     * Gets a consistent key for the content ID when checking/registering purchased state
     */
    public String getContentPurchasedKey(String contentId)
    {
    	return md5encode("yoyo_purchase_" + contentId + "_punky_juular");
    }

	/**
	  * Responds to onActivityResult from the main Activity and deals with it if
	  * the Intent was created from a Billing Request of some sort or other
	  */
	@Override
	public boolean handleActivityResult(int _requestCode, int _resultCode, Intent _data)
	{
		// Request code allows us to double check it's a purchase attempt we triggered
		PurchaseDetails purchaseDetails = mPurchaseRequests.get(Integer.valueOf(_requestCode));		
		if (purchaseDetails == null) {
			Log.i("yoyo", "BILLING: Request code not found " + _requestCode);
			return false;
		}
		mPurchaseRequests.remove(_requestCode);		
						
        if (_data == null) {
			
			Log.i("yoyo", "BILLING: Null data in activity result.");
			PurchaseFailureNotification(BILLING_RESPONSE_RESULT_ERROR, purchaseDetails.mProductId, purchaseDetails.mPurchaseIndex);
        }
		else 
		{
			int resultCode = _resultCode;
			int responseCode = getResponseCodeFromIntent(_data);
        	String purchaseData = _data.getStringExtra("INAPP_PURCHASE_DATA");
        	String dataSignature = _data.getStringExtra("INAPP_DATA_SIGNATURE");		
			Log.i("yoyo", "BILLING: Extras=" + _data.getExtras().toString());

        	if ((resultCode == Activity.RESULT_OK) && (responseCode == BILLING_RESPONSE_RESULT_OK)) {

				Log.i("yoyo", "BILLING: Successful result from purchase activity.");
        	    if (purchaseData == null || dataSignature == null) 
				{
        	        Log.i("yoyo", "BILLING: either purchaseData or dataSignature is null.");
					PurchaseFailureNotification(responseCode, purchaseDetails.mProductId, purchaseDetails.mPurchaseIndex);    		        
        	    }
				else 
				{
        	    	// Verify the purchase with the provided signature
        	    	if (!RunnerBillingSecurity.verifyPurchase(purchaseData, dataSignature)) {
						Log.i("yoyo", "BILLING: Purchase signature verification FAILED => " + purchaseData);
						PurchaseFailureNotification(responseCode, purchaseDetails.mProductId, purchaseDetails.mPurchaseIndex);
        	    	}
					else {					
						// Extend the purchase data to include the response code					
						try {
							JSONObject o = new JSONObject(purchaseData);
							o.put("response", responseCode);
							o.put("purchaseIndex", purchaseDetails.mPurchaseIndex);
							purchaseData = o.toString();	
						}
						catch (JSONException e) {
							Log.i("yoyo", "BILLING: Malformed JSON data, proceeding regardless...");
						}
						RunnerJNILib.IAPProductPurchaseEvent(purchaseData);
					}
				}
        	}
			else {
        		if (resultCode == Activity.RESULT_OK) {
					Log.i("yoyo", "BILLING: Result code was OK but in-app billing response was not OK. Response=" + getResponseDesc(responseCode));
        		}
        		else if (resultCode == Activity.RESULT_CANCELED) {
					Log.i("yoyo", "BILLING: Purchase canceled - Response=" + getResponseDesc(responseCode));
        		}
        		else {
					Log.i("yoyo", "BILLING: Purchase failed. Result code=" + Integer.toString(resultCode) + ". Response=" + getResponseDesc(responseCode));
        		}
				// If the response code is BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED then the runner needs to know
				// that the product should be set to having been purchased. This can happen if a purchase attempt
				// quietly succeeded but responses from the store suggested failure and we're here from a subsequent purchase				
				if (responseCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
					PurchaseSuccessNotification(responseCode, purchaseDetails.mProductId, purchaseDetails.mPurchaseIndex);
				}
				else {
					// Make sure we deliver a purchase event that informs the user of the failure
					PurchaseFailureNotification(responseCode, purchaseDetails.mProductId, purchaseDetails.mPurchaseIndex);
				}
			}
		}
        return true;
	}


	/**
     * Returns a human-readable description for the given response code.
     *
     * @param code The response code
     * @return A human-readable string explaining the result code.
     *     It also includes the result code numerically.
     */
	public static String getResponseDesc(int code) {
        String[] msgs = ("0:OK/1:User Canceled/2:Unknown/" +
                		 "3:Billing Unavailable/4:Item unavailable/" +
                		 "5:Developer Error/6:Error/7:Item Already Owned/" +
                		 "8:Item not owned").split("/");

        String[] helper_msgs = ("0:OK/-1001:Remote exception during initialization/" +
                                "-1002: Bad response received/" +
                                "-1003: Purchase signature verification failed/" +
                                "-1004: Send intent failed/" +
                                "-1005: User cancelled/" +
                                "-1006: Unknown purchase response/" +
                                "-1007: Missing token/" +
                                "-1008: Unknown error/" +
                                "-1009: Subscriptions not available/" +
                                "-1010: Invalid consumption attempt").split("/");

        if (code <= BILLING_ERROR_BASE) {
            int index = BILLING_ERROR_BASE - code;
			if (index >= 0 && index < helper_msgs.length) { 
				return helper_msgs[index];
			}
			else { 
				return String.valueOf(code) + ": Unknown IAB Helper Error";
			}
        }
		else if (code < 0 || code >= msgs.length) {
            return String.valueOf(code) + ": Unknown";
		}
		else {
            return msgs[code];
		}
    }

	/* 
	 * Workaround to bug where sometimes response codes come as Long instead of Integer
	 */
    int getResponseCodeFromIntent(Intent i) {

        Object o = i.getExtras().get("RESPONSE_CODE");
        if (o == null) {
			Log.i("yoyo", "BILLING: Intent with no response code, assuming OK (known issue)");
            return BILLING_RESPONSE_RESULT_OK;
        }
		else if (o instanceof Integer) { 
			return ((Integer)o).intValue();
		}
		else if (o instanceof Long) {
			return (int)((Long)o).longValue();
		}
        else {
            Log.i("yoyo", "BILLING: Unexpected type for intent response code.");
            Log.i("yoyo", "BILLING: " + o.getClass().getName());
			throw new RuntimeException("BILLING: Unexpected type for intent response code: " + o.getClass().getName());
        }
    }
}