//
//  MainViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 2/20/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Cocoa

class MainViewController: NSViewController {
    
    @IBOutlet weak var roomInputTextField: NSTextField!
    
    var videoProfile = AgoraVideoDimension640x480
    fileprivate var agoraKit: AgoraRtcEngineKit!
    
    override func viewDidAppear() {
        super.viewDidAppear()
        roomInputTextField.becomeFirstResponder()
    }
    
    override func prepare(for segue: NSStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier , !segueId.isEmpty else {
            return
        }
        
        if segueId == "mainToSettings" {
            let settingsVC = segue.destinationController as! SettingsViewController
            settingsVC.videoProfile = videoProfile
            settingsVC.delegate = self
        } else if segueId == "mainToLive" {
            let liveVC = segue.destinationController as! LiveRoomViewController
            liveVC.roomName = roomInputTextField.stringValue
            liveVC.videoProfile = videoProfile
            if let value = sender as? NSNumber, let role = AgoraClientRole(rawValue: value.intValue) {
                liveVC.clientRole = role
            }
            liveVC.delegate = self
        }
    }
    
    //MARK: - user actions
    @IBAction func doJoinAsAudienceClicked(_ sender: NSButton) {
        guard let roomName = roomInputTextField?.stringValue , !roomName.isEmpty else {
            return
        }
        join(withRole: .audience)
    }
    
    @IBAction func doJoinAsBroadcasterClicked(_ sender: NSButton) {
        guard let roomName = roomInputTextField?.stringValue , !roomName.isEmpty else {
            return
        }
        join(withRole: .broadcaster)
    }
    
    @IBAction func doSettingsClicked(_ sender: NSButton) {
        performSegue(withIdentifier: "mainToSettings", sender: nil)
    }
}

private extension MainViewController {
    func join(withRole role: AgoraClientRole) {
        performSegue(withIdentifier: "mainToLive", sender: NSNumber(value: role.rawValue as Int))
    }
}

extension MainViewController: SettingsVCDelegate {
    func settingsVC(_ settingsVC: SettingsViewController, closeWithProfile profile: CGSize) {
        videoProfile = profile
        settingsVC.delegate = nil
        settingsVC.view.window?.contentViewController = self
    }
}

extension MainViewController: LiveRoomVCDelegate {
    func liveRoomVCNeedClose(_ liveVC: LiveRoomViewController) {
        guard let window = liveVC.view.window else {
            return
        }
        
        window.delegate = nil
        window.contentViewController = self
        
        liveVC.delegate = nil
    }
}
