//
//  ProfileCell.swift
//  OpenLive
//
//  Created by GongYuhua on 6/26/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

class ProfileCell: UITableViewCell {
    
    @IBOutlet weak var resLabel: UILabel!
    @IBOutlet weak var frameLabel: UILabel!
    
    func update(withProfile profile: CGSize, isSelected: Bool) {
        resLabel.text = profile.resolution()
        frameLabel.text = profile.fps()
        backgroundColor = isSelected ? UIColor(red: 0, green: 0, blue: 0.5, alpha: 0.3) : UIColor.white
    }
}

private extension CGSize {
    func resolution() -> String? {
        switch self {
        case AgoraVideoDimension160x120: return "160×120"
        case AgoraVideoDimension320x180: return "320×180"
        case AgoraVideoDimension320x240: return "320×240"
        case AgoraVideoDimension640x360: return "640×360"
        case AgoraVideoDimension640x480: return "640×480"
        case AgoraVideoDimension1280x720: return "1280×720"
        default: return nil
        }
    }
    
    func fps() -> String? {
        return "15"
    }
}
