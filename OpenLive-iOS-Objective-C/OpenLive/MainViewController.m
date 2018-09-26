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

@interface MainViewController () <SettingsVCDelegate, LiveRoomVCDelegate, UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *roomNameTextField;
@property (weak, nonatomic) IBOutlet UIView *popoverSourceView;

@property (assign, nonatomic) AgoraVideoProfile videoProfile;
@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.videoProfile = AgoraVideoProfileLandscape480P;
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
        liveVC.videoProfile = self.videoProfile;
        liveVC.clientRole = [sender integerValue];
        liveVC.delegate = self;
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

- (void)settingsVC:(SettingsViewController *)settingsVC didSelectProfile:(AgoraVideoProfile)profile {
    self.videoProfile = profile;
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)liveVCNeedClose:(LiveRoomViewController *)liveVC {
    [self.navigationController popViewControllerAnimated:YES];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField.text.length) {
        [self showRoleSelection];
    }
    
    return YES;
}
@end
