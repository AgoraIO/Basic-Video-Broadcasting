//
//  MediaInfo.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/9.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

@interface MediaInfo : NSObject
+ (NSString *)resolutionOfProfile:(AgoraVideoProfile)profile;
+ (NSString *)fpsOfProfile:(AgoraVideoProfile)profile;
+ (NSString *)descriptionProfile:(AgoraVideoProfile)profile;
// cache video parameter
+ (void)cacheVideoProfile:(AgoraVideoProfile)profile;
+ (AgoraVideoProfile)getCacheVideoProfile ;
+ (void)removeCacheVideoProfile;
@end
