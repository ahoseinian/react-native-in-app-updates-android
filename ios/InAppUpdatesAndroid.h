
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNInAppUpdatesAndroidSpec.h"

@interface InAppUpdatesAndroid : NSObject <NativeInAppUpdatesAndroidSpec>
#else
#import <React/RCTBridgeModule.h>

@interface InAppUpdatesAndroid : NSObject <RCTBridgeModule>
#endif

@end
