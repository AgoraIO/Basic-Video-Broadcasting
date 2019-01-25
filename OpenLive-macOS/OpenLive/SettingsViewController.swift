//
//  SettingsViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 2/20/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Cocoa

protocol SettingsVCDelegate: class {
    func settingsVC(_ settingsVC: SettingsViewController, closeWithProfile videoProfile: CGSize)
}

class SettingsViewController: NSViewController {

    @IBOutlet weak var profilePopUpButton: NSPopUpButton!
    
    var videoProfile: CGSize!
    var delegate: SettingsVCDelegate?
    fileprivate let profiles: [CGSize] = CGSize.validProfileList()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        loadProfileItems()
    }
    
    @IBAction func doProfileChanged(_ sender: NSPopUpButton) {
        let profile = profiles[sender.indexOfSelectedItem]
        videoProfile = profile
    }
    
    @IBAction func doConfirmClicked(_ sender: NSButton) {
        delegate?.settingsVC(self, closeWithProfile: videoProfile)
    }
}

private extension SettingsViewController {
    func loadProfileItems() {
        profilePopUpButton.addItems(withTitles: profiles.map { (res) -> String in
            return res.description()
        })
        profilePopUpButton.selectItem(withTitle: videoProfile.description())
    }
}
