//
//  BeautyEffectTableViewController.h
//  OpenLive
//
//  Created by GongYuhua on 2019/3/26.
//  Copyright © 2019 Agora. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

NS_ASSUME_NONNULL_BEGIN

@class BeautyEffectTableViewController;
@protocol BeautyEffectTableVCDelegate <NSObject>
- (void)beautyEffectTableVCDidChange:(BeautyEffectTableViewController *)enhancerTableVC;
@end

@interface BeautyEffectTableViewController : UITableViewController

@property (assign, nonatomic) BOOL isBeautyOn;
@property (assign, nonatomic) CGFloat lightening;
@property (assign, nonatomic) CGFloat smoothness;
@property (assign, nonatomic) CGFloat redness;
@property (assign, nonatomic) AgoraLighteningContrastLevel contrast;
@property (weak, nonatomic) id<BeautyEffectTableVCDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
