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
#import "KeyCenter.h"

static NSString *settingIdentifier = @"mainToSettings";
static NSString *liveIdentifier = @"mainToLive";

@interface MainViewController ()<SettingsVCDelegate, LiveRoomVCDelegate, AgoraRtcEngineDelegate>
@property (weak) IBOutlet NSTextField *roomInputTextFiled;

@property (weak, nonatomic) IBOutlet NSButton *lastmileTestButton;
@property (weak, nonatomic) IBOutlet NSProgressIndicator *lastmileTestIndicator;
@property (weak, nonatomic) IBOutlet NSTextField *qualityLabel;
@property (weak, nonatomic) IBOutlet NSTextField *rttLabel;
@property (weak, nonatomic) IBOutlet NSTextField *uplinkLabel;
@property (weak, nonatomic) IBOutlet NSTextField *downlinkLabel;

@property (strong, nonatomic) AgoraRtcEngineKit *rtcEngine;
@property (nonatomic, assign) CGSize videoProfile;
@property (nonatomic, assign) AgoraClientRole clientRole;
@property (assign, nonatomic) BOOL isLastmileProbeTesting;
@end

@implementation MainViewController
- (void)setIsLastmileProbeTesting:(BOOL)isLastmileProbeTesting {
    _isLastmileProbeTesting = isLastmileProbeTesting;
    self.lastmileTestButton.hidden = isLastmileProbeTesting;
    if (isLastmileProbeTesting) {
        [self.lastmileTestIndicator startAnimation:nil];
    } else {
        [self.lastmileTestIndicator stopAnimation:nil];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.videoProfile = AgoraVideoDimension640x480;
    self.rtcEngine = [AgoraRtcEngineKit sharedEngineWithAppId:[KeyCenter AppId] delegate:self];
}

- (IBAction)doSettingsClicked:(NSButton *)sender {
    [self performSegueWithIdentifier:settingIdentifier sender:nil];
}

- (IBAction)doLastmileProbeTestPressed:(NSButton *)sender {
    AgoraLastmileProbeConfig *config = [[AgoraLastmileProbeConfig alloc] init];
    config.probeUplink = YES;
    config.probeDownlink = YES;
    config.expectedUplinkBitrate = 5000;
    config.expectedDownlinkBitrate = 5000;
    
    [self.rtcEngine startLastmileProbeTest:config];
    
    self.isLastmileProbeTesting = YES;
    self.qualityLabel.hidden = YES;
    self.rttLabel.hidden = YES;
    self.uplinkLabel.hidden = YES;
    self.downlinkLabel.hidden = YES;
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
        liveRoomVC.rtcEngine = self.rtcEngine;
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
    self.rtcEngine.delegate = self;
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine lastmileQuality:(AgoraNetworkQuality)quality {
    NSString *string;
    switch (quality) {
        case AgoraNetworkQualityExcellent:  string = @"excellent"; break;
        case AgoraNetworkQualityGood:       string = @"good"; break;
        case AgoraNetworkQualityPoor:       string = @"poor"; break;
        case AgoraNetworkQualityBad:        string = @"bad"; break;
        case AgoraNetworkQualityVBad:       string = @"very bad"; break;
        case AgoraNetworkQualityDown:       string = @"down"; break;
        case AgoraNetworkQualityUnknown:    string = @"unknown"; break;
    }
    self.qualityLabel.stringValue = [NSString stringWithFormat:@"quality: %@", string];
    self.qualityLabel.hidden = NO;
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine lastmileProbeTestResult:(AgoraLastmileProbeResult *)result {
    self.rttLabel.stringValue = [NSString stringWithFormat:@"rtt: %lu", (unsigned long)result.rtt];
    self.rttLabel.hidden = NO;
    self.uplinkLabel.stringValue = [self descriptionOfProbeOneWayResult:result.uplinkReport];
    self.uplinkLabel.hidden = NO;
    self.downlinkLabel.stringValue = [self descriptionOfProbeOneWayResult:result.downlinkReport];
    self.downlinkLabel.hidden = NO;
    
    [self.rtcEngine stopLastmileProbeTest];
    self.isLastmileProbeTesting = NO;
}

- (NSString *)descriptionOfProbeOneWayResult:(AgoraLastmileProbeOneWayResult *)result {
    return [NSString stringWithFormat:@"packetLoss: %lu, jitter: %lu, availableBandwidth: %lu", (unsigned long)result.packetLossRate, result.jitter, result.availableBandwidth];
}
@end
