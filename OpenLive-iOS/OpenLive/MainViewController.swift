//
//  MainViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

class MainViewController: UIViewController {

    @IBOutlet weak var roomNameTextField: UITextField!
    @IBOutlet weak var popoverSourceView: UIView!
    
    @IBOutlet weak var lastmileTestButton: UIButton!
    @IBOutlet weak var lastmileTestIndicator: UIActivityIndicatorView!
    @IBOutlet weak var qualityLabel: UILabel!
    @IBOutlet weak var rttLabel: UILabel!
    @IBOutlet weak var uplinkLabel: UILabel!
    @IBOutlet weak var downlinkLabel: UILabel!
    @IBOutlet var infoLabels: [UILabel]!
    
    lazy fileprivate var rtcEngine: AgoraRtcEngineKit = {
        let engine = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: self)
        return engine
    }()
    fileprivate var videoProfile = AgoraVideoDimension640x480
    
    private var isLastmileProbeTesting = false {
        didSet {
            lastmileTestButton?.isHidden = isLastmileProbeTesting
            if isLastmileProbeTesting {
                lastmileTestIndicator?.startAnimating()
            } else {
                lastmileTestIndicator?.stopAnimating()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier else {
            return
        }
        
        switch segueId {
        case "mainToSettings":
            let settingsVC = segue.destination as! SettingsViewController
            settingsVC.videoProfile = videoProfile
            settingsVC.delegate = self
        case "mainToLive":
            let liveVC = segue.destination as! LiveRoomViewController
            liveVC.roomName = roomNameTextField.text!
            liveVC.rtcEngine = rtcEngine
            liveVC.videoProfile = videoProfile
            if let value = sender as? NSNumber, let role = AgoraClientRole(rawValue: value.intValue) {
                liveVC.clientRole = role
            }
            liveVC.delegate = self
        default:
            break
        }
    }
    
    @IBAction func doLastmileProbeTestPressed(_ sender: UIButton) {
        let config = AgoraLastmileProbeConfig()
        config.probeUplink = true
        config.probeDownlink = true
        config.expectedUplinkBitrate = 5000
        config.expectedDownlinkBitrate = 5000
        
        rtcEngine.startLastmileProbeTest(config)
        
        isLastmileProbeTesting = true
        
        for label in infoLabels {
            label.isHidden = true
        }
    }
}

private extension MainViewController {
    func showRoleSelection() {
        let sheet = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        let broadcaster = UIAlertAction(title: "Broadcaster", style: .default) { [weak self] _ in
            self?.join(withRole: .broadcaster)
        }
        let audience = UIAlertAction(title: "Audience", style: .default) { [weak self] _ in
            self?.join(withRole: .audience)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        sheet.addAction(broadcaster)
        sheet.addAction(audience)
        sheet.addAction(cancel)
        sheet.popoverPresentationController?.sourceView = popoverSourceView
        sheet.popoverPresentationController?.permittedArrowDirections = .up
        present(sheet, animated: true, completion: nil)
    }
}

private extension MainViewController {
    func join(withRole role: AgoraClientRole) {
        performSegue(withIdentifier: "mainToLive", sender: NSNumber(value: role.rawValue as Int))
    }
}

extension MainViewController: SettingsVCDelegate {
    func settingsVC(_ settingsVC: SettingsViewController, didSelectProfile profile: CGSize) {
        videoProfile = profile
        dismiss(animated: true, completion: nil)
    }
}

extension MainViewController: LiveRoomVCDelegate {
    func liveVCNeedClose(_ liveVC: LiveRoomViewController) {
        let _ = navigationController?.popViewController(animated: true)
        rtcEngine.delegate = self
    }
}

extension MainViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, lastmileQuality quality: AgoraNetworkQuality) {
        qualityLabel.text = "quality: " + quality.description()
        qualityLabel.isHidden = false
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, lastmileProbeTest result: AgoraLastmileProbeResult) {
        rttLabel.text = "rtt: \(result.rtt)"
        rttLabel.isHidden = false
        uplinkLabel.text = "up: \(result.uplinkReport.description())"
        uplinkLabel.isHidden = false
        downlinkLabel.text = "down: \(result.downlinkReport.description())"
        downlinkLabel.isHidden = false
        
        rtcEngine.stopLastmileProbeTest()
        isLastmileProbeTesting = false
    }
}

extension MainViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if let string = textField.text , !string.isEmpty {
            showRoleSelection()
        }
        return true
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
        }
    }
}

extension AgoraLastmileProbeOneWayResult {
    func description() -> String {
        return "packetLoss: \(packetLossRate), jitter: \(jitter), availableBandwidth: \(availableBandwidth)"
    }
}
