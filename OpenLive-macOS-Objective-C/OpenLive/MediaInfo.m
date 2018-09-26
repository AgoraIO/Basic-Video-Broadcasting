//
//  MediaInfo.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/9.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "MediaInfo.h"


@implementation MediaInfo
+ (NSString *)resolutionOfProfile:(AgoraVideoProfile)profile {
    switch (profile) {
        case AgoraVideoProfileLandscape120P:
            return @"160x120";
            break;
        case AgoraVideoProfileLandscape240P:
            return @"320x240";
            break;
        case AgoraVideoProfileLandscape360P:
            return @"640x360";
            break;
        case AgoraVideoProfileLandscape480P:
            return @"640x480";
            break;
        case AgoraVideoProfileLandscape720P:
            return @"1280x720";
            break;
        default:
            return @"";
            break;
    }
}

+ (NSString *)fpsOfProfile:(AgoraVideoProfile)profile {
    return @"15";
}

+ (NSString *)descriptionProfile:(AgoraVideoProfile)profile {
    NSString *resolution = [MediaInfo resolutionOfProfile:profile];
    NSString *fps = [MediaInfo fpsOfProfile:profile];
    return [NSString stringWithFormat:@"%@,%@fps",resolution,fps];
}

+ (void)cacheVideoProfile:(AgoraVideoProfile)profile {
    NSUserDefaults *videoProfile = [NSUserDefaults standardUserDefaults];
    NSString *value = [videoProfile objectForKey:@"videoProfile"];
    if (value) {
        [videoProfile setObject:@(profile) forKey:@"videoProfile"];
    }else {
        [videoProfile setObject:@(AgoraVideoProfileLandscape480P) forKey:@"videoProfile"];
    }
}

+ (AgoraVideoProfile)getCacheVideoProfile {
    NSUserDefaults *videoProfile = [NSUserDefaults standardUserDefaults];
    return  [[videoProfile objectForKey:@"videoProfile"] integerValue];
}

+ (void)removeCacheVideoProfile {
    NSUserDefaults *videoProfile = [NSUserDefaults standardUserDefaults];
    [videoProfile removeObjectForKey:@"videoProfile"];
}
@end
