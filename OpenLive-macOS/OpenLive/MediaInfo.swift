//
//  MediaInfo.swift
//  OpenLive
//
//  Created by GongYuhua on 4/11/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Foundation

extension AgoraRtcVideoProfile {
    static func validProfileList() -> [AgoraRtcVideoProfile] {
        return [._VideoProfile_120P,
                ._VideoProfile_240P,
                ._VideoProfile_360P,
                ._VideoProfile_480P,
                ._VideoProfile_720P]
    }
    
    func resolution() -> CGSize? {
        switch self {
        case ._VideoProfile_120P: return CGSize(width: 160, height: 120)
        case ._VideoProfile_240P: return CGSize(width: 320, height: 240)
        case ._VideoProfile_360P: return CGSize(width: 640, height: 360)
        case ._VideoProfile_480P: return CGSize(width: 640, height: 480)
        case ._VideoProfile_720P: return CGSize(width: 1280, height: 720)
        default: return nil
        }
    }
    
    func fps() -> Int {
        return 15
    }
    
    func bitRate() -> Int? {
        switch self {
        case ._VideoProfile_120P: return 65
        case ._VideoProfile_240P: return 200
        case ._VideoProfile_360P: return 400
        case ._VideoProfile_480P: return 500
        case ._VideoProfile_720P: return 1130
        default: return nil
        }
    }
    
    func description() -> String {
        if let resolution = resolution(), let bitRate = bitRate() {
            return "\(Int(resolution.width))×\(Int(resolution.height)), \(fps())fps, \(bitRate)k"
        } else {
            return "profile \(rawValue)"
        }
    }
}
