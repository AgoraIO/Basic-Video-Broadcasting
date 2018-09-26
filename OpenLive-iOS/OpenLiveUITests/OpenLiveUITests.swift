//
//  OpenLiveUITests.swift
//  OpenLiveUITests
//
//  Created by GongYuhua on 2017/1/12.
//  Copyright © 2017年 Agora. All rights reserved.
//

import XCTest

class OpenLiveUITests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        
        continueAfterFailure = false
        XCUIApplication().launch()
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func testJoinAndLeaveChannel() {
        
        let app = XCUIApplication()
        let inputNameOfLiveTextField = app.textFields["Input name of Live"]
        inputNameOfLiveTextField.tap()
        inputNameOfLiveTextField.typeText("uiTestChannel\n")
        app.sheets.buttons["Broadcaster"].tap()
        
        let closeButton = app.buttons["btn close"]
        expectation(for: NSPredicate(format: "exists == 1"), evaluatedWith: closeButton, handler: nil)
        waitForExpectations(timeout: 5, handler: nil)
        
        closeButton.tap()
        
        let textField = app.textFields["Input name of Live"]
        expectation(for: NSPredicate(format: "exists == 1"), evaluatedWith: textField, handler: nil)
        waitForExpectations(timeout: 5, handler: nil)
    }
}
