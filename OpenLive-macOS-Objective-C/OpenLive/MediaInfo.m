//
//  MediaInfo.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/9.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "MediaInfo.h"


@implementation MediaInfo
+ (NSString *)resolutionOfProfile:(CGSize)profile {
    return [NSString stringWithFormat:@"%dx%d", (int)profile.width, (int)profile.height];
}

+ (NSString *)fpsOfProfile:(CGSize)profile {
    return @"24";
}

+ (NSString *)descriptionProfile:(CGSize)profile {
    NSString *resolution = [MediaInfo resolutionOfProfile:profile];
    NSString *fps = [MediaInfo fpsOfProfile:profile];
    return [NSString stringWithFormat:@"%@,%@fps",resolution,fps];
}
@end
