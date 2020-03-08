//
//  SettingsViewController.m
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016年 Agora. All rights reserved.
//

#import "SettingsViewController.h"
#import "ProfileCell.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface SettingsViewController () <UITableViewDataSource, UITableViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *profileTableView;
@property (strong, nonatomic) NSArray *profiles;
@end

@implementation SettingsViewController
- (NSArray *)profiles {
    if (!_profiles) {
        _profiles = @[@(AgoraVideoDimension160x120),
                      @(AgoraVideoDimension320x180),
                      @(AgoraVideoDimension320x240),
                      @(AgoraVideoDimension640x360),
                      @(AgoraVideoDimension640x480),
                      @(AgoraVideoDimension1280x720)];
    }
    return _profiles;
}

- (void)setVideoProfile:(CGSize)videoProfile {
    _videoProfile = videoProfile;
    [self.profileTableView reloadData];
}

- (IBAction)doConfirmPressed:(UIButton *)sender {
    if ([self.delegate respondsToSelector:@selector(settingsVC:didSelectProfile:)]) {
        [self.delegate settingsVC:self didSelectProfile:self.videoProfile];
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.profiles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    ProfileCell *cell = [tableView dequeueReusableCellWithIdentifier:@"profileCell" forIndexPath:indexPath];
    CGSize selectedProfile = [self.profiles[indexPath.row] CGSizeValue];
    [cell updateWithProfile:selectedProfile isSelected:CGSizeEqualToSize(selectedProfile, self.videoProfile)];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CGSize selectedProfile = [self.profiles[indexPath.row] CGSizeValue];
    self.videoProfile = selectedProfile;
}
@end
