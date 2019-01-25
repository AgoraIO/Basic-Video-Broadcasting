//
//  ProfileCell.m
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016年 Agora. All rights reserved.
//

#import "ProfileCell.h"

@interface ProfileCell()
@property (weak, nonatomic) IBOutlet UILabel *resLabel;
@property (weak, nonatomic) IBOutlet UILabel *frameLabel;
@end

@implementation ProfileCell

- (void)updateWithProfile:(CGSize)profile isSelected:(BOOL)isSelected {
    self.resLabel.text = [self resolutionOfProfile:profile];
    self.frameLabel.text = [self fpsOfProfile:profile];
    self.backgroundColor = isSelected ? [UIColor colorWithRed:0 green:0 blue:0.5 alpha:0.3] : [UIColor whiteColor];
}

- (NSString *)resolutionOfProfile:(CGSize)profile {
    return [NSString stringWithFormat:@"%d×%d", (int)profile.width, (int)profile.height];
}

- (NSString *)fpsOfProfile:(CGSize)profile {
    return @"24";
}
@end
