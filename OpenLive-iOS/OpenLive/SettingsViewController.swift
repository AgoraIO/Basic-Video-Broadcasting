//
//  SettingsViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

protocol SettingsVCDelegate: NSObjectProtocol {
    func settingsVC(_ settingsVC: SettingsViewController, didSelectProfile profile: AgoraRtcVideoProfile)
}

class SettingsViewController: UIViewController {
    
    @IBOutlet weak var profileTableView: UITableView!

    var videoProfile: AgoraRtcVideoProfile! {
        didSet {
            profileTableView?.reloadData()
        }
    }
    weak var delegate: SettingsVCDelegate?
    
    fileprivate let profiles: [AgoraRtcVideoProfile] = AgoraRtcVideoProfile.list()
    
    @IBAction func doConfirmPressed(_ sender: UIButton) {
        delegate?.settingsVC(self, didSelectProfile: videoProfile)
    }
}

extension SettingsViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return profiles.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "profileCell", for: indexPath) as! ProfileCell
        let selectedProfile = profiles[(indexPath as NSIndexPath).row]
        cell.update(withProfile: selectedProfile, isSelected: (selectedProfile == videoProfile))
        
        return cell
    }
}

extension SettingsViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let selectedProfile = profiles[(indexPath as NSIndexPath).row]
        videoProfile = selectedProfile
    }
}

private extension AgoraRtcVideoProfile {
    static func list() -> [AgoraRtcVideoProfile] {
        return [._VideoProfile_120P,
                ._VideoProfile_180P,
                ._VideoProfile_240P,
                ._VideoProfile_360P,
                ._VideoProfile_480P,
                ._VideoProfile_720P]
    }
}
