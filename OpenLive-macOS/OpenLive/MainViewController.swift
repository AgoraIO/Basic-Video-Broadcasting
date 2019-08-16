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
    
    @IBOutlet weak var lastmileTestButton: NSButton!
    @IBOutlet weak var lastmileTestIndicator: NSProgressIndicator!
    @IBOutlet weak var qualityLabel: NSTextField!
    @IBOutlet weak var rttLabel: NSTextField!
    @IBOutlet weak var uplinkLabel: NSTextField!
    @IBOutlet weak var downlinkLabel: NSTextField!
    
    lazy fileprivate var rtcEngine: AgoraRtcEngineKit = {
        let engine = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: self)
        return engine
    }()
    var videoProfile = AgoraVideoDimension640x480
    
    private var isLastmileProbeTesting = false {
        didSet {
            lastmileTestButton?.isHidden = isLastmileProbeTesting
            if isLastmileProbeTesting {
                lastmileTestIndicator?.startAnimation(nil)
            } else {
                lastmileTestIndicator?.stopAnimation(nil)
            }
        }
    }
    
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
            liveVC.rtcEngine = rtcEngine
            liveVC.videoProfile = videoProfile
            if let value = sender as? NSNumber, let role = AgoraClientRole(rawValue: value.intValue) {
                liveVC.clientRole = role
            }
            liveVC.delegate = self
        }
    }
    
    //MARK: - user actions
    @IBAction func doLastmileProbeTestPressed(_ sender: NSButton) {
        let config = AgoraLastmileProbeConfig()
        config.probeUplink = true
        config.probeDownlink = true
        config.expectedUplinkBitrate = 5000
        config.expectedDownlinkBitrate = 5000
        
        rtcEngine.startLastmileProbeTest(config)
        
        isLastmileProbeTesting = true
        
        qualityLabel.isHidden = true
        rttLabel.isHidden = true
        uplinkLabel.isHidden = true
        downlinkLabel.isHidden = true
    }
    
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
        rtcEngine.delegate = self
    }
}

extension MainViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, lastmileQuality quality: AgoraNetworkQuality) {
        qualityLabel.stringValue = "quality: " + quality.description()
        qualityLabel.isHidden = false
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, lastmileProbeTest result: AgoraLastmileProbeResult) {
        rttLabel.stringValue = "rtt: \(result.rtt)"
        rttLabel.isHidden = false
        uplinkLabel.stringValue = "up: \(result.uplinkReport.description())"
        uplinkLabel.isHidden = false
        downlinkLabel.stringValue = "down: \(result.downlinkReport.description())"
        downlinkLabel.isHidden = false
        
        rtcEngine.stopLastmileProbeTest()
        isLastmileProbeTesting = false
    }
}

extension AgoraNetworkQuality {
    func description() -> String {
        switch self {
        case .excellent: return "excellent"
        case .good:      return "good"
        case .poor:      return "poor"
        case .bad:       return "bad"
        case .vBad:      return "very bad"
        case .down:      return "down"
        case .unknown:   return "unknown"
        case .detecting: return "detecting"
        case .unsupported: return "unsupported"
        default:         return "unknown"
        }
    }
}

extension AgoraLastmileProbeOneWayResult {
    func description() -> String {
        return "packetLoss: \(packetLossRate), jitter: \(jitter), availableBandwidth: \(availableBandwidth)"
    }
}
