//
//  SettingViewController.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "SettingsViewController.h"
#import "MediaInfo.h"
#import "MainViewController.h"

@interface SettingsViewController ()
@property (weak) IBOutlet NSPopUpButton *profilePopUpButton;
@property (nonatomic,strong) NSArray *profiles;
@end

@implementation SettingsViewController
- (NSArray *)profiles {
    if (!_profiles) {
        _profiles = @[@(AgoraVideoDimension160x120),
                      @(AgoraVideoDimension320x240),
                      @(AgoraVideoDimension640x360),
                      @(AgoraVideoDimension640x480),
                      @(AgoraVideoDimension1280x720)];
    }
    return _profiles;
}

- (void)setVideoProfile:(CGSize)videoProfile {
    _videoProfile = videoProfile;
    [self loadProfies];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self loadProfies];
}

- (void)loadProfies {
    for (NSNumber *profile in self.profiles) {
        [self.profilePopUpButton addItemWithTitle:[MediaInfo descriptionProfile:[profile sizeValue]]];
    }
    [self.profilePopUpButton selectItemWithTitle:[MediaInfo descriptionProfile:self.videoProfile]];
}

- (IBAction)doProfileChanged:(NSPopUpButton *)sender {
    self.videoProfile = [self.profiles[sender.indexOfSelectedItem] sizeValue];
}

- (IBAction)doConfirmClicked:(NSButton *)sender {
    if ([self.delegate respondsToSelector:@selector(settingsVC:didSelectProfile:)]) {
        [self.delegate settingsVC:self didSelectProfile:self.videoProfile];
    }
}
@end

