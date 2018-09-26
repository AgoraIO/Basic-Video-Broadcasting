//
//  OpenLiveUITests.m
//  OpenLiveUITests
//
//  Created by GongYuhua on 2017/1/13.
//  Copyright © 2017年 Agora. All rights reserved.
//

#import <XCTest/XCTest.h>

@interface OpenLiveUITests : XCTestCase

@end

@implementation OpenLiveUITests

- (void)setUp {
    [super setUp];
    
    self.continueAfterFailure = NO;
    [[[XCUIApplication alloc] init] launch];
}

- (void)tearDown {
    [super tearDown];
}

- (void)testJoinAndLeaveChannel {
    XCUIApplication *app = [[XCUIApplication alloc] init];
    XCUIElement *inputNameOfLiveTextField = app.textFields[@"Input name of Live"];
    [inputNameOfLiveTextField tap];
    [inputNameOfLiveTextField typeText:@"uiTestChannel\n"];
    [app.sheets.buttons[@"Broadcaster"] tap];
    
    XCUIElement *closeButton = app.buttons[@"btn close"];
    [self expectationForPredicate:[NSPredicate predicateWithFormat:@"exists == 1"] evaluatedWithObject:closeButton handler:nil];
    [self waitForExpectationsWithTimeout:5 handler:nil];
    
    [closeButton tap];
    
    XCUIElement *textField = app.textFields[@"Input name of Live"];
    [self expectationForPredicate:[NSPredicate predicateWithFormat:@"exists == 1"] evaluatedWithObject:textField handler:nil];
    [self waitForExpectationsWithTimeout:5 handler:nil];
}

@end
