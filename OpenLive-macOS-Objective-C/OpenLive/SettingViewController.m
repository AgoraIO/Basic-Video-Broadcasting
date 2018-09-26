//
//  SettingViewController.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "SettingViewController.h"
#import "MediaInfo.h"
#import "MainViewController.h"

static NSString *toMainIdentifier = @"settingToMain";
@interface SettingViewController ()
@property (weak) IBOutlet NSPopUpButton *profilePopUpButton;
@property (nonatomic,strong) NSArray *profiles;
@end

@implementation SettingViewController
- (NSArray *)profiles {
    if (!_profiles) {
        _profiles = @[@(AgoraVideoProfileLandscape120P),
                      @(AgoraVideoProfileLandscape240P),
                      @(AgoraVideoProfileLandscape360P),
                      @(AgoraVideoProfileLandscape480P),
                      @(AgoraVideoProfileLandscape720P)];
    }
    return _profiles;
}

- (void)setVideoProfile:(AgoraVideoProfile)videoProfile {
    _videoProfile = videoProfile;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do view setup here.
    [self.view setWantsLayer:YES];
    [self.view.layer setBackgroundColor:[[NSColor whiteColor] CGColor]];
    self.view.alphaValue = 1.0;
    [self loadProfies];
}

- (void)loadProfies {
    for (NSInteger i = 0;i< self.profiles.count; i++) {
        NSString *profile = self.profiles[i];
        [self.profilePopUpButton addItemWithTitle:[MediaInfo descriptionProfile:[profile integerValue]]];
    }
    [self.profilePopUpButton selectItemWithTitle:[MediaInfo descriptionProfile:self.videoProfile]];
}

- (void)prepareForSegue:(NSStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:toMainIdentifier]) {
        MainViewController *mainVC = segue.destinationController;
        mainVC.videoProfile = [MediaInfo getCacheVideoProfile];
    }
}

- (IBAction)doProfileChanged:(NSPopUpButton *)sender {
    AgoraVideoProfile videoProfile =   [self.profiles[sender.indexOfSelectedItem] integerValue];
    [MediaInfo cacheVideoProfile:videoProfile];
}
- (IBAction)doConfirmClicked:(NSButton *)sender {
    [self performSegueWithIdentifier:toMainIdentifier sender:nil];
}


@end
