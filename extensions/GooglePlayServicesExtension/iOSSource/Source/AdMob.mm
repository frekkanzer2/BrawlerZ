//
//  Released by YoYo Games Ltd. on 17/04/2014. Intended for use with GM: S EA97 and above ONLY.
//  Copyright YoYo Games Ltd., 2014.
//  For support please submit a ticket at help.yoyogames.com
//
//


#import "admob.h"


const int EVENT_OTHER_SOCIAL = 70;
extern int CreateDsMap( int _num, ... );
extern void CreateAsynEventWithDSMap(int dsmapindex, int event_index);
extern UIViewController *g_controller;
extern UIView *g_glView;
extern int g_DeviceWidth;
extern int g_DeviceHeight;

NSString *g_IntAdId;
BOOL g_InterstitialReady=false;
bool m_bUseTestAds = false;
NSString* m_DeviceId;
int m_BannerXPos;
int m_BannerYPos;
//int m_BannerIndex=0;
const int GoogleMobileAds_ASyncEvent = 9817;



@implementation GoogleMobileAdsExt


- (void) GoogleMobileAds_Init:(char *)_intId Arg2:(char *)_ApplicationId
{
	g_IntAdId  = [NSString stringWithCString:_intId encoding:NSUTF8StringEncoding];
	[g_IntAdId retain];
	//g_AdId = [NSString stringWithCString:IntID encoding:NSUTF8StringEncoding];
	//[g_AdId retain];
	
	g_InterstitialReady = false;
    
    NSString *appid = [NSString stringWithCString:_ApplicationId encoding:NSUTF8StringEncoding];
    [GADMobileAds configureWithApplicationID:appid];
}


-(void) GoogleMobileAds_AddBannerAt:(char*)_bannerId Arg2:(double)_sizeType Arg3:(double)_x Arg4:(double)_y
{
	m_BannerXPos = (int)_x;
	m_BannerYPos = (int)_y;
	int type = (int)(_sizeType + 0.5);

	GADAdSize bannerSize;
	switch(type)
	{
	case 1: bannerSize = kGADAdSizeBanner; break;
	case 2: bannerSize = kGADAdSizeMediumRectangle; break;
	case 3: bannerSize = kGADAdSizeFullBanner; break;
	case 4: bannerSize = kGADAdSizeLeaderboard; break;
	case 5: bannerSize = kGADAdSizeSkyscraper; break;
    case 6: bannerSize = kGADAdSizeSmartBannerPortrait; break;
	default: NSLog(@"AddBanner illegal banner size type %d", type); return;
	}
	
	//remove existing banner
	if( bannerView != nil )
	{
		//remove existing banner
		[bannerView removeFromSuperview];
		bannerView.delegate = nil;
		[bannerView release];
		bannerView = nil;
	}
	
	//bannerView = [[GADBannerView alloc] initWithAdSize:kGADAdSizeBanner];
	bannerView = [[GADBannerView alloc] initWithAdSize:bannerSize];
	bannerView.adUnitID = [NSString stringWithUTF8String:_bannerId];
	bannerView.rootViewController = g_controller;
	bannerView.delegate = self;
	[g_glView addSubview:bannerView];
	[self GoogleMobileAds_MoveBanner:_x Arg2:_y ];
	
	GADRequest *request = [GADRequest request];
	if( m_bUseTestAds )
	{
		request.testDevices = [NSArray arrayWithObject:m_DeviceId];
	}
	
	//request.testDevices = @[ @"a4dc6ddd7107dd26da54c7cc7033b399" ];
	[bannerView loadRequest:request];
}

-(void) GoogleMobileAds_AddBanner:(char*)_bannerId Arg2:(double)_size
{
	[self GoogleMobileAds_AddBannerAt:_bannerId Arg2:_size Arg3:0 Arg4:0];
}

-(void) GoogleMobileAds_RemoveBanner
{
	//remove existing banner
	if( bannerView != nil )
	{
		[bannerView removeFromSuperview];
		bannerView.delegate = nil;
		[bannerView release];
		bannerView = nil;
	}
}

-(void) GoogleMobileAds_HideBanner
{
    if( bannerView != nil )
	{
        bannerView.hidden = true;
    }

}

-(void) GoogleMobileAds_ShowBanner
{
    if( bannerView != nil )
	{
        bannerView.hidden = false;
    }

}

-(void) GoogleMobileAds_MoveBanner:(double)_x Arg2:(double)_y
{
	//NSLog(@"Move Banner: %d,%d", (int)_x, (int)_y );
	if( bannerView != nil )
	{
		
        //display->view coords
        int x = (int)(_x * g_glView.bounds.size.width) / g_DeviceWidth;
        int y = (int)(_y * g_glView.bounds.size.height) / g_DeviceHeight;
        
        CGRect frame = bannerView.frame;
        frame.origin.x = x;
        frame.origin.y = y;
        bannerView.frame =frame;
		
	}
}

-(double) GoogleMobileAds_BannerGetWidth
{
	if( bannerView != nil )
	{
		CGSize size = CGSizeFromGADAdSize( bannerView.adSize );
		int adW = size.width;
		//->display width
		int dispW = (int)(( adW * g_DeviceWidth ) / g_glView.bounds.size.width);
		return dispW;
	}
	return 0;
}

-(double) GoogleMobileAds_BannerGetHeight
{
	if( bannerView != nil )
	{
		CGSize size = CGSizeFromGADAdSize( bannerView.adSize );
		int adH = size.height;
		//->display height
		int dispH = (int)(( adH * g_DeviceHeight ) / g_glView.bounds.size.height);
		return dispH;
	}
	return 0;
}

-(void)GoogleMobileAds_UseTestAds:(double)_useTest Arg2:(char*)_deviceId
{
	m_bUseTestAds = (_useTest >=0.5);
	[m_DeviceId release];
	m_DeviceId = [NSString stringWithUTF8String:_deviceId];
	[m_DeviceId retain];
}

-(void)GoogleMobileAds_LoadInterstitial
{
	GADRequest *request = [GADRequest request];
	if( m_bUseTestAds )
	{
		request.testDevices = [NSArray arrayWithObject:m_DeviceId];
	}
	
	//must create a new interstitial object each time
	[interstitial release];
//	interstitial = [[GADInterstitial alloc] init];

	//interstitial.adUnitID = g_IntAdId;
	interstitial = [[GADInterstitial alloc] initWithAdUnitID:g_IntAdId];

	interstitial.delegate = self;
	[interstitial loadRequest:request];
	g_InterstitialReady = false;
}

-(NSString *)GoogleMobileAds_InterstitialStatus
{
	if(g_InterstitialReady)
		return @"Ready";
	else
		return @"Not Ready";
}

-(NSString *)GoogleMobileAds_RewardedVideoStatus
{
	if ([[GADRewardBasedVideoAd sharedInstance] isReady]) 
		return @"Ready";
	else
		return @"Not Ready";
}


-(void)GoogleMobileAds_ShowInterstitial
{
	[interstitial presentFromRootViewController:g_controller];
	g_InterstitialReady = false;	//must reload to display again
}

-(void)sendBannerLoadedEvent:(int)_loaded
{
	int dsMapIndex;
	if( _loaded != 0 )
	{
		double bannerWidth = [self GoogleMobileAds_BannerGetWidth];
		double bannerHeight = [self GoogleMobileAds_BannerGetHeight];
	
		dsMapIndex = CreateDsMap(5,
					"type", 0.0, "banner_load",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL,
					"loaded", 1.0, (void*)NULL,
					"width", bannerWidth, (void*)NULL,
					"height",bannerHeight,(void*)NULL
					);
	}
	else
	{
		dsMapIndex = CreateDsMap(3,
					"type", 0.0, "banner_load",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL,
					"loaded", 0.0, (void*)NULL );
	}
	
	//send async event 
	CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

-(void)sendInterstitialLoadedEvent:(int)_loaded
{
	double loaded = (double)_loaded;
	int dsMapIndex = CreateDsMap(3,
					"type", 0.0, "interstitial_load",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL,
					"loaded", loaded, (void*)NULL );

	//send async event 
	CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

-(void)sendInterstitialClosedEvent
{
	int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "interstitial_closed",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );

	//send async event 
	CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}


- (void)adViewDidReceiveAd:(GADBannerView *)_bannerView 
{
	NSLog(@"banner ad received");
	//send async event 
	[self sendBannerLoadedEvent:1];
}

- (void)adView:(GADBannerView *)_bannerView didFailToReceiveAdWithError:(GADRequestError *)_error
{
	NSLog(@"banner ad failed to receive");
	[self sendBannerLoadedEvent:0];
}

- (void)interstitialDidReceiveAd:(GADInterstitial *)ad
{
	NSLog(@"interstitialDidReceiveAd");
	g_InterstitialReady = true;
	[self sendInterstitialLoadedEvent:1];
}
- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error
{
	NSLog(@"Interstitial Ad failed to receive: %@",error);
	[self sendInterstitialLoadedEvent:0];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)ad
{
	NSLog(@"Interstitial will present screen");
}

// Sent before the interstitial is to be animated off the screen.
- (void)interstitialWillDismissScreen:(GADInterstitial *)ad
{
	NSLog(@"Interstitial will dismiss screen");
}

// Sent just after dismissing an interstitial and it has animated off the
// screen.
- (void)interstitialDidDismissScreen:(GADInterstitial *)ad
{
	NSLog(@"Interstitial did dismiss screen");
	[self sendInterstitialClosedEvent];
}

// Sent just before the application will background or terminate because the
// user clicked on an ad that will launch another application (such as the App
// Store).  The normal UIApplicationDelegate methods, like
// applicationDidEnterBackground:, will be called immediately before this.
- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad
{
	NSLog(@"Interstitial will leave app");
}


-(void)GoogleMobileAds_LoadRewardedVideo:(char*)appUnitId
{
    [GADRewardBasedVideoAd sharedInstance].delegate = self;
    NSString *adid = [NSString stringWithCString:appUnitId encoding:NSUTF8StringEncoding];
    
    
    GADRequest *request = [GADRequest request];
	if( m_bUseTestAds )
	{
		request.testDevices = [NSArray arrayWithObject:m_DeviceId];
	}
	
    
    [[GADRewardBasedVideoAd sharedInstance] loadRequest:request withAdUnitID:adid];
}
-(void)GoogleMobileAds_ShowRewardedVideo
{
    if ([[GADRewardBasedVideoAd sharedInstance] isReady]) 
    {
      [[GADRewardBasedVideoAd sharedInstance] presentFromRootViewController:g_controller];
    }
}

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
    didRewardUserWithReward:(GADAdReward *)reward {
  NSString *rewardMessage =
      [NSString stringWithFormat:@"Reward received with currency %@ , amount %lf",
          reward.type,
          [reward.amount doubleValue]];
  NSLog(@"%@",rewardMessage);
  

    int dsMapIndex = CreateDsMap(4,
					"type", 0.0, "rewardedvideo_watched",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL,
                    "currency",0.0,[reward.type UTF8String],
                    "amount",[reward.amount doubleValue],(void*)NULL
					 );

	//send async event 
	CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
  
  
}

- (void)rewardBasedVideoAdDidReceiveAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
  NSLog(@"Reward based video ad is received.");
  
  	int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_adloaded",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

- (void)rewardBasedVideoAdDidOpen:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
  NSLog(@"Opened reward based video ad.");
  
  int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_adopened",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

- (void)rewardBasedVideoAdDidStartPlaying:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
  NSLog(@"Reward based video ad started playing.");
  
   int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_videostarted",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

- (void)rewardBasedVideoAdDidClose:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
  NSLog(@"Reward based video ad is closed.");
  
     int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_adclosed",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

- (void)rewardBasedVideoAdWillLeaveApplication:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
  NSLog(@"Reward based video ad will leave application.");
  
     int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_leftapplication",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
    didFailToLoadWithError:(NSError *)error {
  NSLog(@"Reward based video ad failed to load.");
  NSString *errmsg = [error localizedDescription];
  
     int dsMapIndex = CreateDsMap(2,
					"type", 0.0, "rewardedvideo_loadfailed",
                    "id", (double)GoogleMobileAds_ASyncEvent,(void*)NULL,
                    "error","0.0",[errmsg UTF8String]
                    
					 );
    CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SOCIAL);
}


@end

