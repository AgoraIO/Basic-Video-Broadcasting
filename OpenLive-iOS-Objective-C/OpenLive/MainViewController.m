//
//  MainViewController.m
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016年 Agora. All rights reserved.
//

#import "MainViewController.h"
#import "SettingsViewController.h"
#import "LiveRoomViewController.h"
#import "KeyCenter.h"

@interface MainViewController () <SettingsVCDelegate, LiveRoomVCDelegate, AgoraRtcEngineDelegate, UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *roomNameTextField;
@property (weak, nonatomic) IBOutlet UIView *popoverSourceView;

@property (weak, nonatomic) IBOutlet UIButton *lastmileTestButton;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *lastmileTestIndicator;
@property (weak, nonatomic) IBOutlet UILabel *qualityLabel;
@property (weak, nonatomic) IBOutlet UILabel *rttLabel;
@property (weak, nonatomic) IBOutlet UILabel *uplinkLabel;
@property (weak, nonatomic) IBOutlet UILabel *downlinkLabel;
@property (strong, nonatomic) IBOutletCollection(UILabel) NSArray *infoLabels;

@property (strong, nonatomic) AgoraRtcEngineKit *rtcEngine;
@property (assign, nonatomic) CGSize videoProfile;
@property (assign, nonatomic) BOOL isLastmileProbeTesting;

@end

@implementation MainViewController
- (void)setIsLastmileProbeTesting:(BOOL)isLastmileProbeTesting {
    _isLastmileProbeTesting = isLastmileProbeTesting;
    self.lastmileTestButton.hidden = isLastmileProbeTesting;
    if (isLastmileProbeTesting) {
        [self.lastmileTestIndicator startAnimating];
    } else {
        [self.lastmileTestIndicator stopAnimating];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.videoProfile = AgoraVideoDimension640x480;
    self.rtcEngine = [AgoraRtcEngineKit sharedEngineWithAppId:[KeyCenter AppId] delegate:self];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    NSString *segueId = segue.identifier;
    
    if ([segueId isEqualToString:@"mainToSettings"]) {
        SettingsViewController *settingsVC = segue.destinationViewController;
        settingsVC.videoProfile = self.videoProfile;
        settingsVC.delegate = self;
    } else if ([segueId isEqualToString:@"mainToLive"]) {
        LiveRoomViewController *liveVC = segue.destinationViewController;
        liveVC.roomName = self.roomNameTextField.text;
        liveVC.rtcEngine = self.rtcEngine;
        liveVC.videoProfile = self.videoProfile;
        liveVC.clientRole = [sender integerValue];
        liveVC.delegate = self;
    }
}

- (IBAction)doLastmileProbeTestPressed:(UIButton *)sender {
    AgoraLastmileProbeConfig *config = [[AgoraLastmileProbeConfig alloc] init];
    config.probeUplink = YES;
    config.probeDownlink = YES;
    config.expectedUplinkBitrate = 5000;
    config.expectedDownlinkBitrate = 5000;
    
    [self.rtcEngine startLastmileProbeTest:config];
    
    self.isLastmileProbeTesting = YES;
    
    for (UILabel *label in self.infoLabels) {
        label.hidden = YES;
    }
}

- (void)showRoleSelection {
    UIAlertController *sheet = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction *broadcaster = [UIAlertAction actionWithTitle:@"Broadcaster" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self joinWithRole:AgoraClientRoleBroadcaster];
    }];
    UIAlertAction *audience = [UIAlertAction actionWithTitle:@"Audience" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self joinWithRole:AgoraClientRoleAudience];
    }];
    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault handler:nil];
    [sheet addAction:broadcaster];
    [sheet addAction:audience];
    [sheet addAction:cancel];
    [sheet popoverPresentationController].sourceView = self.popoverSourceView;
    [sheet popoverPresentationController].permittedArrowDirections = UIPopoverArrowDirectionUp;
    [self presentViewController:sheet animated:YES completion:nil];
}

- (void)joinWithRole:(AgoraClientRole)role {
    [self performSegueWithIdentifier:@"mainToLive" sender:@(role)];
}

- (void)settingsVC:(SettingsViewController *)settingsVC didSelectProfile:(CGSize)profile {
    self.videoProfile = profile;
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)liveVCNeedClose:(LiveRoomViewController *)liveVC {
    [self.navigationController popViewControllerAnimated:YES];
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
    self.qualityLabel.text = [NSString stringWithFormat:@"quality: %@", string];
    self.qualityLabel.hidden = NO;
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine lastmileProbeTestResult:(AgoraLastmileProbeResult *)result {
    self.rttLabel.text = [NSString stringWithFormat:@"rtt: %lu", (unsigned long)result.rtt];
    self.rttLabel.hidden = NO;
    self.uplinkLabel.text = [self descriptionOfProbeOneWayResult:result.uplinkReport];
    self.uplinkLabel.hidden = NO;
    self.downlinkLabel.text = [self descriptionOfProbeOneWayResult:result.downlinkReport];
    self.downlinkLabel.hidden = NO;
    
    [self.rtcEngine stopLastmileProbeTest];
    self.isLastmileProbeTesting = NO;
}

- (NSString *)descriptionOfProbeOneWayResult:(AgoraLastmileProbeOneWayResult *)result {
    return [NSString stringWithFormat:@"packetLoss: %lu, jitter: %lu, availableBandwidth: %lu", (unsigned long)result.packetLossRate, result.jitter, result.availableBandwidth];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField.text.length) {
        [self showRoleSelection];
    }
    
    return YES;
}
@end
