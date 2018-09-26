//
//  MainViewController.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "MainViewController.h"
#import "SettingViewController.h"
#import "LiveRoomViewController.h"
#import "MediaInfo.h"

static NSString *settingIdentifier = @"mainToSettings";
static NSString *liveIdentifier = @"mainToLive";


@interface MainViewController ()
@property (weak) IBOutlet NSTextField *roomInputTextFiled;
@property (nonatomic, assign) AgoraClientRole clientRole;
@end

@implementation MainViewController
- (void)setVideoProfile:(AgoraVideoProfile)videoProfile {
    _videoProfile = videoProfile;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do view setup here.
    [self.view setWantsLayer:YES];
    [self.view.layer setBackgroundColor:[[NSColor whiteColor] CGColor]];
    [MediaInfo cacheVideoProfile:self.videoProfile];
}

- (void)viewDidAppear {
    [super viewDidAppear];
    
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
        SettingViewController *settingVC = segue.destinationController;
        settingVC.videoProfile = [MediaInfo getCacheVideoProfile];
    }else if ([segue.identifier isEqualToString:liveIdentifier]) {
        LiveRoomViewController *liveRoomVC = segue.destinationController;
        liveRoomVC.roomName = self.roomInputTextFiled.stringValue;
        liveRoomVC.videoProfile = [MediaInfo getCacheVideoProfile];
        liveRoomVC.clientRole = self.clientRole;
    }
}
@end
