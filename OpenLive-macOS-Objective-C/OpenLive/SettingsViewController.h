//
//  SettingViewController.h
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

@class SettingsViewController;
@protocol SettingsVCDelegate <NSObject>
- (void)settingsVC:(SettingsViewController *)settingsVC didSelectProfile:(CGSize)profile;
@end

@interface SettingsViewController : NSViewController
@property (nonatomic, assign) CGSize videoProfile;
@property (strong, nonatomic) id<SettingsVCDelegate> delegate;
@end
