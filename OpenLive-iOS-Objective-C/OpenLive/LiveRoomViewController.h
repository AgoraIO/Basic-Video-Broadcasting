//
//  LiveRoomViewController.h
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@class LiveRoomViewController;
@protocol LiveRoomVCDelegate <NSObject>
- (void)liveVCNeedClose:(LiveRoomViewController *)liveVC;
@end

@interface LiveRoomViewController : UIViewController
@property (copy, nonatomic) NSString *roomName;
@property (strong, nonatomic) AgoraRtcEngineKit *rtcEngine;
@property (assign, nonatomic) AgoraClientRole clientRole;
@property (assign, nonatomic) CGSize videoProfile;
@property (weak, nonatomic) id<LiveRoomVCDelegate> delegate;
@end
