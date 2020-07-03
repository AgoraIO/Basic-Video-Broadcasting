//
//  OpenLive_Tests.swift
//  OpenLive-Tests
//
//  Created by CavanSu on 2020/7/3.
//  Copyright © 2020 Agora. All rights reserved.
//

import XCTest
@testable import OpenLive
import AgoraRtcKit

class OpenLive_Tests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        let _ = AgoraRtcEngineKit.sharedEngine(withAppId: "", delegate: nil)
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        measure {
            // Put the code you want to measure the time of here.
        }
    }

}
