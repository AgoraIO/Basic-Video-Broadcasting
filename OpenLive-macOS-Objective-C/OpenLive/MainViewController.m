//
//  MainViewController.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "MainViewController.h"
#import "SettingsViewController.h"
#import "LiveRoomViewController.h"
#import "MediaInfo.h"

static NSString *settingIdentifier = @"mainToSettings";
static NSString *liveIdentifier = @"mainToLive";

@interface MainViewController ()<SettingsVCDelegate, LiveRoomVCDelegate>
@property (weak) IBOutlet NSTextField *roomInputTextFiled;
@property (nonatomic, assign) AgoraClientRole clientRole;
@end

@implementation MainViewController
- (void)setVideoProfile:(CGSize)videoProfile {
    _videoProfile = videoProfile;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.videoProfile = AgoraVideoDimension640x480;
}

- (IBAction)doSettingsClicked:(NSButton *)sender {
    [self performSegueWithIdentifier:settingIdentifier sender:nil];
}

- (IBAction)doJoinAsBroadcasterClicked:(NSButton *)sender {
    self.clientRole = AgoraClientRoleBroadcaster;
    [self pushLiveRoomVC];
}

- (IBAction)doJoinAsAudienceClicked:(NSButton *)sender {
     self.clientRole = AgoraClientRoleAudience;
    [self pushLiveRoomVC];
}

- (void)pushLiveRoomVC {
    if (!self.roomInputTextFiled.stringValue.length) {
        return;
    }
    [self performSegueWithIdentifier:liveIdentifier sender:nil];
}

- (void)prepareForSegue:(NSStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:settingIdentifier]) {
        SettingsViewController *settingVC = segue.destinationController;
        settingVC.videoProfile = self.videoProfile;
        settingVC.delegate = self;
    }else if ([segue.identifier isEqualToString:liveIdentifier]) {
        LiveRoomViewController *liveRoomVC = segue.destinationController;
        liveRoomVC.roomName = self.roomInputTextFiled.stringValue;
        liveRoomVC.videoProfile = self.videoProfile;
        liveRoomVC.clientRole = self.clientRole;
        liveRoomVC.delegate = self;
    }
}

- (void)settingsVC:(SettingsViewController *)settingsVC didSelectProfile:(CGSize)profile {
    settingsVC.view.window.contentViewController = self;
    settingsVC.delegate = nil;
    
    self.videoProfile = profile;
}

- (void)liveVCNeedClose:(LiveRoomViewController *)liveVC {
    liveVC.view.window.contentViewController = self;
    liveVC.delegate = nil;
}
@end
