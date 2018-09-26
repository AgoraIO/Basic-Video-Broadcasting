//
//  ReplacementSegue.m
//  OpenLive
//
//  Created by yangmoumou on 2018/2/8.
//  Copyright © 2018年 yangmoumou. All rights reserved.
//

#import "ReplacementSegue.h"

@implementation ReplacementSegue
- (void)perform {
    ((NSViewController *)self.sourceController).view.window.contentViewController = self.destinationController;
}
@end
