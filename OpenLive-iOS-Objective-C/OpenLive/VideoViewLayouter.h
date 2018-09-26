//
//  VideoViewLayouter.h
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016年 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "VideoSession.h"

@interface VideoViewLayouter : NSObject
- (void)layoutSessions:(NSArray<VideoSession *> *)sessions
           fullSession:(VideoSession *)fullSession
           inContainer:(UIView *)container;
- (VideoSession *)responseSessionOfGesture:(UIGestureRecognizer *)gesture
                                inSessions:(NSArray<VideoSession *> *)sessions
                           inContainerView:(UIView *)container;
@end
