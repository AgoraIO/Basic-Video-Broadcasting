//
//  VideoViewLayouter.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cocoa/Cocoa.h>
#import "VideoSession.h"

@interface VideoViewLayouter : NSObject
- (void)layoutSessions:(NSArray<VideoSession *> *)sessions
           fullSession:(VideoSession *)fullSession
           inContainer:(NSView *)container;
- (VideoSession *)responseSessionOfEvent:(NSEvent *)gesture
                                inSessions:(NSArray<VideoSession *> *)sessions
                           inContainerView:(NSView *)container;
@end
