//
//  VideoSession.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/9.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface VideoSession : NSObject
@property (nonatomic, assign) NSUInteger uid;
@property (nonatomic, strong) NSView *hostingView;
@property (nonatomic, strong) AgoraRtcVideoCanvas *canvas;

- (instancetype)initWithUid:(NSUInteger)uid;
+ (instancetype)localSession;
@end
