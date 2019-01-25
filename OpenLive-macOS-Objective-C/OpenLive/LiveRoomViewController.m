//
//  LiveRoomViewController.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "LiveRoomViewController.h"
#import "KeyCenter.h"
#import "VideoSession.h"
#import "VideoViewLayouter.h"

@interface LiveRoomViewController ()<AgoraRtcEngineDelegate>
@property (weak) IBOutlet NSView *remoteContainerView;
@property (weak) IBOutlet NSButton *muteAudioButton;
@property (weak) IBOutlet NSButton *broadcastButton;
@property (weak) IBOutlet NSTextField *roomNameLabel;

@property (nonatomic, strong) AgoraRtcEngineKit *rtcEngine;
@property (nonatomic, assign) BOOL isBroadcaster;
@property (nonatomic, assign) BOOL isMuted;
@property (nonatomic, strong) NSMutableArray *videoSessions;
@property (nonatomic, strong) VideoViewLayouter *viewLayouter;
@property (nonatomic, strong) VideoSession *fullSession;
@end

@implementation LiveRoomViewController
- (BOOL)isBroadcaster {
    return self.clientRole == AgoraClientRoleBroadcaster;
}

- (VideoViewLayouter *)viewLayouter {
    if (!_viewLayouter) {
        _viewLayouter = [[VideoViewLayouter alloc] init];
    }
    return _viewLayouter;
}

- (NSMutableArray *)videoSessions {
    if (!_videoSessions) {
        _videoSessions = [NSMutableArray array];
    }
    return _videoSessions;
}

- (void)setStreamTypeForSessions:(NSArray<VideoSession *> *)sessions fullSession:(VideoSession *)fullSession {
    if (fullSession) {
        for (VideoSession *session in sessions) {
            [self.rtcEngine setRemoteVideoStream:session.uid type:(session == self.fullSession ? AgoraVideoStreamTypeHigh : AgoraVideoStreamTypeLow)];
        }
    } else {
        for (VideoSession *session in sessions) {
            [self.rtcEngine setRemoteVideoStream:session.uid type:AgoraVideoStreamTypeHigh];
        }
    }
}

- (void)setFullSession:(VideoSession *)fullSession {
    _fullSession = fullSession;
    if (self.remoteContainerView) {
        [self updateInterface];
    }
}

- (void)setRoomName:(NSString *)roomName {
    _roomName = roomName;
}

- (void)setClientRole:(AgoraClientRole)clientRole {
    _clientRole = clientRole;
    [self updateButtonsVisiablity];
}

- (void)setIsMuted:(BOOL)isMuted {
    _isMuted = isMuted;
    [self.rtcEngine muteLocalAudioStream:isMuted];
    [self.muteAudioButton setImage:[NSImage imageNamed:(isMuted ? @"btn_mute_blue" : @"btn_mute")]];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.roomNameLabel.stringValue = self.roomName;
    [self loadAgoraKit];
}

- (void)loadAgoraKit {
    self.rtcEngine = [AgoraRtcEngineKit sharedEngineWithAppId:[KeyCenter AppId] delegate:self];
    [self.rtcEngine setChannelProfile:(AgoraChannelProfileLiveBroadcasting)];
    
    // Warning: only enable dual stream mode if there will be more than one broadcaster in the channel
    [self.rtcEngine enableDualStreamMode:true];
    
    [self.rtcEngine enableVideo];
    AgoraVideoEncoderConfiguration *configuration = [[AgoraVideoEncoderConfiguration alloc] initWithSize:self.videoProfile
                                                                                               frameRate:AgoraVideoFrameRateFps24
                                                                                                 bitrate:AgoraVideoBitrateStandard
                                                                                         orientationMode:AgoraVideoOutputOrientationModeAdaptative];
    [self.rtcEngine setVideoEncoderConfiguration:configuration];
    [self.rtcEngine setClientRole:(self.clientRole)];
    
    if (self.isBroadcaster) {
        [self.rtcEngine startPreview];
    }
    [self addLocalSession];
    int code =   [self.rtcEngine joinChannelByToken:nil channelId:self.roomName info:nil uid:0 joinSuccess:^(NSString * _Nonnull channel, NSUInteger uid, NSInteger elapsed) {

    }];
    if (code != 0) {
        [self alertString:[NSString stringWithFormat:@"Join channel failed %d",code]];
    }
    [self updateButtonsVisiablity];
    NSLog(@"sdk version - %@",[AgoraRtcEngineKit getSdkVersion]);
    
}

- (IBAction)doMuteClicked:(NSButton *)sender {
    self.isMuted = !self.isMuted;
}

- (IBAction)doBroadcastClicked:(NSButton *)sender {
    if (self.isBroadcaster) {
        self.clientRole = AgoraClientRoleAudience;
        if (self.fullSession.uid == 0) {
            self.fullSession = nil;
        }
    } else {
        self.clientRole = AgoraClientRoleBroadcaster;
    }
    [self.rtcEngine setClientRole:self.clientRole];
    [self updateInterface];
}

- (IBAction)doLeaveClicked:(NSButton *)sender {
    [self leaveChannel];
}

- (void)addLocalSession {
    VideoSession *localSession =  [VideoSession localSession];
    [self.videoSessions addObject:localSession];
    [self.rtcEngine setupLocalVideo:localSession.canvas];
}

- (VideoSession *)fetchSessionOfUid:(NSUInteger)uid {
    for (VideoSession *session in self.videoSessions) {
        if (session.uid == uid) {
            return session;
        }
    }
    return nil;
}

- (VideoSession *)videoSessionOfUid:(NSUInteger)uid {
    VideoSession *fetchedSession = [self fetchSessionOfUid:uid];
    if (fetchedSession) {
        return fetchedSession;
    } else {
        VideoSession *newSession = [[VideoSession alloc] initWithUid:uid];
        [self.videoSessions addObject:newSession];
        [self updateInterface];
        return newSession;
    }
}

- (void)updateInterface {
    NSArray *displaySessions = _videoSessions;
    if (self.videoSessions.count && !self.isBroadcaster) {
        displaySessions = [self.videoSessions subarrayWithRange:NSMakeRange(1, self.videoSessions.count - 1)];
    } else {
        displaySessions = [self.videoSessions copy];
    }
    [self.viewLayouter layoutSessions:displaySessions fullSession:self.fullSession inContainer:self.remoteContainerView];
    [self setStreamTypeForSessions:displaySessions fullSession:self.fullSession];
}

- (void)leaveChannel {
    [self.rtcEngine setupLocalVideo:nil];
    [self.rtcEngine leaveChannel:nil];
    if (self.isBroadcaster) {
        [self.rtcEngine stopPreview];
    }
    for (VideoSession *session in self.videoSessions) {
        [session.hostingView removeFromSuperview];
    }
    [self.videoSessions removeAllObjects];
    
    if ([self.delegate respondsToSelector:@selector(liveVCNeedClose:)]) {
        [self.delegate liveVCNeedClose:self];
    }
}

- (void)mouseUp:(NSEvent *)event {
    if (event.clickCount == 2) {
        if (self.fullSession == nil) {
           VideoSession *videoSession =   [self.viewLayouter responseSessionOfEvent:event inSessions:self.videoSessions inContainerView:self.remoteContainerView];
            self.fullSession = videoSession;
        }else {
            self.fullSession = nil;
        }
    }
}

- (void)updateButtonsVisiablity {
    [self.broadcastButton setImage:[NSImage imageNamed:self.isBroadcaster ? @"btn_join_cancel" : @"btn_join"]];
    self.muteAudioButton.hidden = !self.isBroadcaster;
}

#pragma mark - alert
- (void)alertString:(NSString *)string {
    NSAlert *alert = [[NSAlert alloc] init];
    alert.messageText = string;
    [alert addButtonWithTitle:@"OK"];
    [alert beginSheetModalForWindow:self.view.window completionHandler:nil];
}

#pragma mark -------AgoraRtcEngineKit delagate ------------

- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOccurWarning:(AgoraWarningCode)warningCode {
    NSLog(@"warningcode - %ld",warningCode);
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOccurError:(AgoraErrorCode)errorCode {
    NSLog(@"errorcode - %ld",errorCode);
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine didJoinedOfUid:(NSUInteger)uid elapsed:(NSInteger)elapsed {
    VideoSession *userSession = [self videoSessionOfUid:uid];
    [self.rtcEngine setupRemoteVideo:userSession.canvas];
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine firstLocalVideoFrameWithSize:(CGSize)size elapsed:(NSInteger)elapsed {
    if (self.videoSessions.count) {
        [self updateInterface];
    }
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOfflineOfUid:(NSUInteger)uid reason:(AgoraUserOfflineReason)reason {
    VideoSession *deleteSession;
    for (VideoSession *session in self.videoSessions) {
        if (session.uid == uid) {
            deleteSession = session;
        }
    }
    if (deleteSession) {
        [self.videoSessions removeObject:deleteSession];
        [deleteSession.hostingView removeFromSuperview];
        [self updateInterface];
        
        if (deleteSession == self.fullSession) {
            self.fullSession = nil;
        }
    }
}
@end
