//
//  BeautyEffectTableViewController.m
//  OpenLive
//
//  Created by GongYuhua on 2019/3/26.
//  Copyright © 2019 Agora. All rights reserved.
//

#import "BeautyEffectTableViewController.h"

@interface BeautyEffectTableViewController ()

@property (weak, nonatomic) IBOutlet UISegmentedControl *switcher;
@property (weak, nonatomic) IBOutlet UISlider *lighteningSlider;
@property (weak, nonatomic) IBOutlet UILabel *lighteningLabel;
@property (weak, nonatomic) IBOutlet UISegmentedControl *contrastSwitcher;
@property (weak, nonatomic) IBOutlet UISlider *smoothnessSlider;
@property (weak, nonatomic) IBOutlet UILabel *smoothnessLabel;
@property (weak, nonatomic) IBOutlet UISlider *rednessSlider;
@property (weak, nonatomic) IBOutlet UILabel *rednessLabel;
@end

@implementation BeautyEffectTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.preferredContentSize = CGSizeMake(300, 219);
    
    self.switcher.selectedSegmentIndex = self.isBeautyOn ? 0 : 1;
    
    self.lighteningSlider.value = self.lightening;
    self.lighteningLabel.text = [self displayStringOfValue:self.lightening];
    
    NSInteger index = [self indexOfLevel:self.contrast];
    self.contrastSwitcher.selectedSegmentIndex = index;
    
    self.smoothnessSlider.value = self.smoothness;
    self.smoothnessLabel.text = [self displayStringOfValue:self.smoothness];
    
    self.rednessSlider.value = self.redness;
    self.rednessLabel.text = [self displayStringOfValue:self.redness];
}

- (IBAction)doSwitched:(UISegmentedControl *)sender {
    NSInteger index = sender.selectedSegmentIndex;
    self.isBeautyOn = (index == 0);
    
    if ([self.delegate respondsToSelector:@selector(beautyEffectTableVCDidChange:)]) {
        [self.delegate beautyEffectTableVCDidChange:self];
    }
}

- (IBAction)doLighteningSliderChanged:(UISlider *)sender {
    self.lightening = sender.value;
    self.lighteningLabel.text = [self displayStringOfValue:self.lightening];
    
    if ([self.delegate respondsToSelector:@selector(beautyEffectTableVCDidChange:)]) {
        [self.delegate beautyEffectTableVCDidChange:self];
    }
}

- (IBAction)doConstrastChanged:(UISegmentedControl *)sender {
    NSInteger index = sender.selectedSegmentIndex;
    self.contrast = [self levelAtIndex:index];
    
    if ([self.delegate respondsToSelector:@selector(beautyEffectTableVCDidChange:)]) {
        [self.delegate beautyEffectTableVCDidChange:self];
    }
}

- (IBAction)doSmoothnessSliderChanged:(UISlider *)sender {
    self.smoothness = sender.value;
    self.smoothnessLabel.text = [self displayStringOfValue:self.smoothness];
    
    if ([self.delegate respondsToSelector:@selector(beautyEffectTableVCDidChange:)]) {
        [self.delegate beautyEffectTableVCDidChange:self];
    }
}

- (IBAction)doRednessSliderChanged:(UISlider *)sender {
    self.redness = sender.value;
    self.rednessLabel.text = [self displayStringOfValue:self.redness];
    
    if ([self.delegate respondsToSelector:@selector(beautyEffectTableVCDidChange:)]) {
        [self.delegate beautyEffectTableVCDidChange:self];
    }
}

- (NSString *)displayStringOfValue:(CGFloat)value {
    return [NSString stringWithFormat:@"%.1f", value];
}

- (NSInteger)indexOfLevel:(AgoraLighteningContrastLevel)level {
    switch (level) {
        case AgoraLighteningContrastLow:    return 0;
        case AgoraLighteningContrastNormal: return 1;
        case AgoraLighteningContrastHigh:   return 2;
    }
}

- (AgoraLighteningContrastLevel)levelAtIndex:(NSInteger)index {
    switch (index) {
        case 0: return AgoraLighteningContrastLow;
        case 1: return AgoraLighteningContrastNormal;
        case 2: return AgoraLighteningContrastHigh;
        default: return AgoraLighteningContrastNormal;
    }
}
@end
