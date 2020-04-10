//
//  VideoSession.h
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface VideoSession : NSObject
@property (assign, nonatomic) NSUInteger uid;
@property (strong, nonatomic) UIView *hostingView;
@property (strong, nonatomic) AgoraRtcVideoCanvas *canvas;

- (instancetype)initWithUid:(NSUInteger)uid;
+ (instancetype)localSession;
@end
