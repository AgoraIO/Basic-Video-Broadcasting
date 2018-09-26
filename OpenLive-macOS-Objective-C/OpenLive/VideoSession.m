//
//  VideoSession.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/9.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "VideoSession.h"

@implementation VideoSession
- (instancetype)initWithUid:(NSUInteger)uid {
    if (self = [super init]) {
        self.uid = uid;
        
        self.hostingView = [[NSView alloc] init];
        self.hostingView.translatesAutoresizingMaskIntoConstraints = false;
        
        self.canvas = [[AgoraRtcVideoCanvas alloc] init];
        self.canvas.uid = uid;
        self.canvas.view = self.hostingView;
        self.canvas.renderMode = AgoraVideoRenderModeHidden;
    }
    return self;
}

+ (instancetype)localSession {
    return [[VideoSession alloc] initWithUid:0];
}
@end
