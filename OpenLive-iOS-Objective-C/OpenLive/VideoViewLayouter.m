//
//  VideoViewLayouter.m
//  OpenLive
//
//  Created by GongYuhua on 2016/9/12.
//  Copyright © 2016年 Agora. All rights reserved.
//

#import "VideoViewLayouter.h"

@interface VideoViewLayouter ()
@property (strong, nonatomic) NSMutableArray<NSLayoutConstraint *> *layoutConstraints;
@end

@implementation VideoViewLayouter
- (NSMutableArray<NSLayoutConstraint *> *)layoutConstraints {
    if (!_layoutConstraints) {
        _layoutConstraints = [[NSMutableArray<NSLayoutConstraint *> alloc] init];
    }
    return _layoutConstraints;
}

- (void)layoutSessions:(NSArray<VideoSession *> *)sessions
           fullSession:(VideoSession *)fullSession
           inContainer:(UIView *)container {
    [NSLayoutConstraint deactivateConstraints:self.layoutConstraints];
    [self.layoutConstraints removeAllObjects];
    
    for (VideoSession *session in sessions) {
        [session.hostingView removeFromSuperview];
    }
    
    if (fullSession) {
        [self.layoutConstraints addObjectsFromArray:[self layoutFullScreenView:fullSession.hostingView inContainerView:container]];
        NSArray *smallViews = [self viewListFromSessions:sessions maxCount:3 ignorSession:fullSession];
        [self.layoutConstraints addObjectsFromArray:[self layoutSmallViews:smallViews inContainerView:container]];
    } else {
        NSArray *allViews = [self viewListFromSessions:sessions maxCount:4 ignorSession:nil];
        [self.layoutConstraints addObjectsFromArray:[self layoutGridViews:allViews inContainerView:container]];
    }
    
    if (self.layoutConstraints.count) {
        [NSLayoutConstraint activateConstraints:self.layoutConstraints];
    }
}

- (VideoSession *)responseSessionOfGesture:(UIGestureRecognizer *)gesture
                                inSessions:(NSArray<VideoSession *> *)sessions
                           inContainerView:(UIView *)container {
    CGPoint location = [gesture locationInView:container];
    for (VideoSession *session in sessions) {
        CGRect rect = session.hostingView.frame;
        if (CGRectContainsPoint(rect, location)) {
            return session;
        }
    }
    
    return nil;
}

- (NSArray<UIView *> *)viewListFromSessions:(NSArray<VideoSession *> *)sessions maxCount:(NSUInteger)maxCount ignorSession:(VideoSession *)ignorSession {
    NSMutableArray *views = [[NSMutableArray alloc] init];
    for (VideoSession *session in sessions) {
        if (session == ignorSession) {
            continue;
        }
        [views addObject:session.hostingView];
        if (views.count >= maxCount) {
            break;
        }
    }
    return [views copy];
}

- (NSArray<NSLayoutConstraint *> *)layoutFullScreenView:(UIView *)view inContainerView:(UIView *)container {
    NSMutableArray *layouts = [[NSMutableArray alloc] init];
    [container addSubview:view];
    
    NSArray<NSLayoutConstraint *> *constraintsH = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view]|" options:0 metrics:nil views:@{@"view": view}];
    NSArray<NSLayoutConstraint *> *constraintsV = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view]|" options:0 metrics:nil views:@{@"view": view}];
    [layouts addObjectsFromArray:constraintsH];
    [layouts addObjectsFromArray:constraintsV];
    
    return [layouts copy];
}

- (NSArray<NSLayoutConstraint *> *)layoutSmallViews:(NSArray<UIView *> *)smallViews inContainerView:(UIView *)container {
    NSMutableArray *layouts = [[NSMutableArray alloc] init];
    UIView *lastView;
    
    CGFloat itemSpace = 5;
    
    for (UIView *view in smallViews) {
        [container addSubview:view];
        NSLayoutConstraint *viewWidth = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:100];
        NSLayoutConstraint *viewHeight = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:100];
        
        NSLayoutConstraint *viewTop = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:container attribute:NSLayoutAttributeTop multiplier:1 constant:60 + itemSpace];
        NSLayoutConstraint *viewLeft;
        if (lastView) {
            viewLeft = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:lastView attribute:NSLayoutAttributeRight multiplier:1 constant:itemSpace];
        } else {
            viewLeft = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:container attribute:NSLayoutAttributeLeft multiplier:1 constant:itemSpace];
        }
        
        [layouts addObjectsFromArray:@[viewWidth, viewHeight, viewLeft, viewTop]];
        lastView = view;
    }
    
    return [layouts copy];
}

- (NSArray<NSLayoutConstraint *> *)layoutGridViews:(NSArray<UIView *> *)allViews inContainerView:(UIView *)container {
    NSMutableArray *layouts = [[NSMutableArray alloc] init];
    NSUInteger viewCount = allViews.count;
    
    if (viewCount == 1) {
        [layouts addObjectsFromArray:[self layoutFullScreenView:allViews.firstObject inContainerView:container]];
    } else if (viewCount == 2) {
        UIView *firstView = allViews.firstObject;
        UIView *lastView = allViews.lastObject;
        [container addSubview:firstView];
        [container addSubview:lastView];
        
        NSArray<NSLayoutConstraint *> *h1 = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view]|" options:0 metrics:nil views:@{@"view": firstView}];
        NSArray<NSLayoutConstraint *> *h2 = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view]|" options:0 metrics:nil views:@{@"view": lastView}];
        NSArray<NSLayoutConstraint *> *v = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": firstView, @"view2": lastView}];
        NSLayoutConstraint *equal = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:lastView attribute:NSLayoutAttributeHeight multiplier:1 constant:0];
        
        [layouts addObjectsFromArray:h1];
        [layouts addObjectsFromArray:h2];
        [layouts addObjectsFromArray:v];
        [layouts addObject:equal];
        
    } else if (viewCount == 3) {
        UIView *firstView = allViews.firstObject;
        UIView *secondView = allViews[1];
        UIView *lastView = allViews.lastObject;
        [container addSubview:firstView];
        [container addSubview:secondView];
        [container addSubview:lastView];
        
        NSArray<NSLayoutConstraint *> *h1 = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": firstView, @"view2": secondView}];
        NSArray<NSLayoutConstraint *> *v1 = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": firstView, @"view2": lastView}];
        NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:lastView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:container attribute:NSLayoutAttributeLeft multiplier:1 constant:0];
        NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:secondView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:container attribute:NSLayoutAttributeTop multiplier:1 constant:0];
        NSLayoutConstraint *equalWidth1 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:secondView attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
        NSLayoutConstraint *equalWidth2 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:lastView attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
        NSLayoutConstraint *equalHeight1 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:secondView attribute:NSLayoutAttributeHeight multiplier:1 constant:0];
        NSLayoutConstraint *equalHeight2 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:lastView attribute:NSLayoutAttributeHeight multiplier:1 constant:0];
        
        [layouts addObjectsFromArray:h1];
        [layouts addObjectsFromArray:v1];
        [layouts addObjectsFromArray:@[left, top, equalWidth1, equalWidth2, equalHeight1, equalHeight2]];
        
    } else if (viewCount >= 4) {
        UIView *firstView = allViews.firstObject;
        UIView *secondView = allViews[1];
        UIView *thirdView = allViews[2];
        UIView *lastView = allViews.lastObject;
        [container addSubview:firstView];
        [container addSubview:secondView];
        [container addSubview:thirdView];
        [container addSubview:lastView];
        
        NSArray<NSLayoutConstraint *> *h1 = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": firstView, @"view2": secondView}];
        NSArray<NSLayoutConstraint *> *h2 = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": thirdView, @"view2": lastView}];
        NSArray<NSLayoutConstraint *> *v1 = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": firstView, @"view2": thirdView}];
        NSArray<NSLayoutConstraint *> *v2 = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view1]-1-[view2]|" options:0 metrics:nil views:@{@"view1": secondView, @"view2": lastView}];
        
        NSLayoutConstraint *equalWidth1 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:secondView attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
        NSLayoutConstraint *equalWidth2 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:thirdView attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
        NSLayoutConstraint *equalHeight1 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:secondView attribute:NSLayoutAttributeHeight multiplier:1 constant:0];
        NSLayoutConstraint *equalHeight2 = [NSLayoutConstraint constraintWithItem:firstView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:thirdView attribute:NSLayoutAttributeHeight multiplier:1 constant:0];
        
        [layouts addObjectsFromArray:h1];
        [layouts addObjectsFromArray:h2];
        [layouts addObjectsFromArray:v1];
        [layouts addObjectsFromArray:v2];
        [layouts addObjectsFromArray:@[equalWidth1, equalWidth2, equalHeight1, equalHeight2]];
    }
    
    return [layouts copy];
}
@end
