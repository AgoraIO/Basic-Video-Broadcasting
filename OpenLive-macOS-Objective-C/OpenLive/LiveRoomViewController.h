//
//  LiveRoomViewController.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@class LiveRoomViewController;
@protocol LiveRoomVCDelegate <NSObject>
- (void)liveVCNeedClose:(LiveRoomViewController *)liveVC;
@end

@interface LiveRoomViewController : NSViewController
@property (nonatomic, copy) NSString *roomName;
@property (nonatomic, strong) AgoraRtcEngineKit *rtcEngine;
@property (nonatomic, assign) AgoraClientRole clientRole;
@property (nonatomic, assign) CGSize videoProfile;
@property (nonatomic, strong) id<LiveRoomVCDelegate> delegate;
@end
