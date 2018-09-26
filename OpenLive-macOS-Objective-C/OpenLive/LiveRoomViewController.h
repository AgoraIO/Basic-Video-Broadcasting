//
//  LiveRoomViewController.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

@interface LiveRoomViewController : NSViewController
@property (nonatomic, copy) NSString *roomName;
@property (nonatomic, assign) AgoraClientRole clientRole;
@property (nonatomic, assign) AgoraVideoProfile videoProfile;
@end
