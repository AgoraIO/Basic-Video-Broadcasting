//
//  SettingsViewController.h
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@class SettingsViewController;
@protocol SettingsVCDelegate <NSObject>
- (void)settingsVC:(SettingsViewController *)settingsVC didSelectProfile:(CGSize)profile;
@end

@interface SettingsViewController : UIViewController
@property (assign, nonatomic) CGSize videoProfile;
@property (weak, nonatomic) id<SettingsVCDelegate> delegate;
@end
