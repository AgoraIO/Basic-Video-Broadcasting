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
    
    func update(withProfile profile: AgoraRtcVideoProfile, isSelected: Bool) {
        resLabel.text = profile.resolution()
        frameLabel.text = profile.fps()
        backgroundColor = isSelected ? UIColor(red: 0, green: 0, blue: 0.5, alpha: 0.3) : UIColor.white
    }
}

private extension AgoraRtcVideoProfile {
    func resolution() -> String? {
        switch self {
        case ._VideoProfile_120P: return "160×120"
        case ._VideoProfile_180P: return "320×180"
        case ._VideoProfile_240P: return "320×240"
        case ._VideoProfile_360P: return "640×360"
        case ._VideoProfile_480P: return "640×480"
        case ._VideoProfile_720P: return "1280×720"
        default: return nil
        }
    }
    
    func fps() -> String? {
        return "15"
    }
}
