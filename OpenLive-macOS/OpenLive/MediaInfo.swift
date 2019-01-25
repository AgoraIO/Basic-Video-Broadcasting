//
//  MediaInfo.swift
//  OpenLive
//
//  Created by GongYuhua on 4/11/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Foundation

extension CGSize {
    static func validProfileList() -> [CGSize] {
        return [AgoraVideoDimension160x120,
                AgoraVideoDimension320x240,
                AgoraVideoDimension640x360,
                AgoraVideoDimension640x480,
                AgoraVideoDimension1280x720]
    }
    
    func resolution() -> String? {
        switch self {
        case AgoraVideoDimension160x120: return "160×120"
        case AgoraVideoDimension320x240: return "320×240"
        case AgoraVideoDimension640x360: return "640×360"
        case AgoraVideoDimension640x480: return "640×480"
        case AgoraVideoDimension1280x720: return "1280×720"
        default: return nil
        }
    }
    
    func fps() -> Int {
        return 24
    }
    
    func description() -> String {
        return "\(resolution), \(fps())fps"
    }
}
