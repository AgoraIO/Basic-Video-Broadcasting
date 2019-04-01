//
//  LiveRoomViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

protocol LiveRoomVCDelegate: NSObjectProtocol {
    func liveVCNeedClose(_ liveVC: LiveRoomViewController)
}

class LiveRoomViewController: UIViewController {
    
    @IBOutlet weak var roomNameLabel: UILabel!
    @IBOutlet weak var remoteContainerView: UIView!
    @IBOutlet weak var broadcastButton: UIButton!
    @IBOutlet var sessionButtons: [UIButton]!
    @IBOutlet weak var audioMuteButton: UIButton!
    @IBOutlet weak var beautyEffectButton: UIButton!
    @IBOutlet weak var superResolutionButton: UIButton!
    
    var roomName: String!
    var clientRole = AgoraClientRole.audience {
        didSet {
            updateButtonsVisiablity()
        }
    }
    var videoProfile: CGSize!
    weak var delegate: LiveRoomVCDelegate?
    
    //MARK: - engine & session view
    var rtcEngine: AgoraRtcEngineKit!
    fileprivate var isBroadcaster: Bool {
        return clientRole == .broadcaster
    }
    fileprivate var isMuted = false {
        didSet {
            rtcEngine?.muteLocalAudioStream(isMuted)
            audioMuteButton?.setImage(isMuted ? #imageLiteral(resourceName: "btn_mute_cancel.pdf") : #imageLiteral(resourceName: "btn_mute.pdf"), for: .normal)
        }
    }
    
    fileprivate var videoSessions = [VideoSession]() {
        didSet {
            guard remoteContainerView != nil else {
                return
            }
            updateInterface(withAnimation: true)
        }
    }
    fileprivate var fullSession: VideoSession? {
        didSet {
            if fullSession != oldValue && remoteContainerView != nil {
                updateInterface(withAnimation: true)
            }
        }
    }
    
    fileprivate let viewLayouter = VideoViewLayouter()
    
    //MARK: - super resolution
    var isEnableSuperResolution = false {
        didSet {
            superResolutionButton?.setImage(isEnableSuperResolution ? #imageLiteral(resourceName: "btn_sr_blue.pdf") : #imageLiteral(resourceName: "btn_sr.pdf"), for: .normal)
        }
    }
    var highPriorityRemoteUid: UInt? {
        didSet {
            for session in videoSessions {
                rtcEngine?.enableRemoteSuperResolution(session.uid, enabled: false)
                rtcEngine?.setRemoteUserPriority(session.uid, type: .normal)
            }
            if let highPriorityRemoteUid = highPriorityRemoteUid {
                if isEnableSuperResolution {
                    rtcEngine?.enableRemoteSuperResolution(highPriorityRemoteUid, enabled: true)
                }
                rtcEngine?.setRemoteUserPriority(highPriorityRemoteUid, type: .high)
            }
        }
    }
    
    //MARK: - Beauty
    var isBeautyOn = true {
        didSet {
            guard let rtcEngine = rtcEngine else {
                return
            }
            rtcEngine.setBeautyEffectOptions(isBeautyOn, options: beautyOptions)
            beautyEffectButton?.setImage(isBeautyOn ? #imageLiteral(resourceName: "btn_beautiful_cancel") : #imageLiteral(resourceName: "btn_beautiful"), for: .normal)
        }
    }
    let beautyOptions: AgoraBeautyOptions = {
        let options = AgoraBeautyOptions()
        options.lighteningContrastLevel = .normal
        options.lighteningLevel = 0.2
        options.smoothnessLevel = 0.2
        options.rednessLevel = 0.1
        return options
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        roomNameLabel.text = roomName
        updateButtonsVisiablity()
        
        loadAgoraKit()
        isBeautyOn = true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier else {
            return
        }
        
        switch segueId {
        case "roomVCPopBeautyList":
            let vc = segue.destination as! BeautyEffectTableViewController
            vc.isBeautyOn = isBeautyOn
            vc.smoothness = beautyOptions.smoothnessLevel
            vc.lightening = beautyOptions.lighteningLevel
            vc.contrast = beautyOptions.lighteningContrastLevel
            vc.redness = beautyOptions.rednessLevel
            vc.delegate = self
            vc.popoverPresentationController?.delegate = self
        default:
            break
        }
    }
    
    //MARK: - user action
    @IBAction func doSwitchCameraPressed(_ sender: UIButton) {
        rtcEngine?.switchCamera()
    }
    
    @IBAction func doMutePressed(_ sender: UIButton) {
        isMuted = !isMuted
    }
    
    @IBAction func doBroadcastPressed(_ sender: UIButton) {
        if isBroadcaster {
            clientRole = .audience
            if fullSession?.uid == 0 {
                fullSession = nil
            }
        } else {
            clientRole = .broadcaster
        }

        rtcEngine.setClientRole(clientRole)
        updateInterface(withAnimation :true)
    }
    
    @IBAction func doSuperResolutionPressed(_ sender: UIButton) {
        isEnableSuperResolution.toggle()
        highPriorityRemoteUid = highPriorityRemoteUid(in: videoSessions, fullSession: fullSession)
    }
    
    @IBAction func doDoubleTapped(_ sender: UITapGestureRecognizer) {
        if fullSession == nil {
            if let tappedSession = viewLayouter.responseSession(of: sender, inSessions: videoSessions, inContainerView: remoteContainerView) {
                fullSession = tappedSession
            }
        } else {
            fullSession = nil
        }
    }
    
    @IBAction func doLeavePressed(_ sender: UIButton) {
        leaveChannel()
    }
}

private extension LiveRoomViewController {
    func updateButtonsVisiablity() {
        guard let sessionButtons = sessionButtons else {
            return
        }
        
        broadcastButton?.setImage(isBroadcaster ? #imageLiteral(resourceName: "btn_join_cancel.pdf") : #imageLiteral(resourceName: "btn_join"), for: .normal)
        
        for button in sessionButtons {
            button.isHidden = !isBroadcaster
        }
    }
    
    func leaveChannel() {
        setIdleTimerActive(true)
        
        rtcEngine.setupLocalVideo(nil)
        rtcEngine.leaveChannel(nil)
        if isBroadcaster {
            rtcEngine.stopPreview()
        }
        
        for session in videoSessions {
            session.hostingView.removeFromSuperview()
        }
        videoSessions.removeAll()
        
        delegate?.liveVCNeedClose(self)
    }
    
    func setIdleTimerActive(_ active: Bool) {
        UIApplication.shared.isIdleTimerDisabled = !active
    }
    
    func alert(string: String) {
        guard !string.isEmpty else {
            return
        }
        
        let alert = UIAlertController(title: nil, message: string, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Ok", style: .cancel, handler: nil))
        present(alert, animated: true, completion: nil)
    }
}

private extension LiveRoomViewController {
    func updateInterface(withAnimation animation: Bool) {
        if animation {
            UIView.animate(withDuration: 0.3, animations: { [weak self] in
                self?.updateInterface()
                self?.view.layoutIfNeeded()
            })
        } else {
            updateInterface()
        }
    }
    
    func updateInterface() {
        var displaySessions = videoSessions
        if !isBroadcaster && !displaySessions.isEmpty {
            displaySessions.removeFirst()
        }
        viewLayouter.layout(sessions: displaySessions, fullSession: fullSession, inContainer: remoteContainerView)
        setStreamType(forSessions: displaySessions, fullSession: fullSession)
        highPriorityRemoteUid = highPriorityRemoteUid(in: displaySessions, fullSession: fullSession)
    }
    
    func setStreamType(forSessions sessions: [VideoSession], fullSession: VideoSession?) {
        if let fullSession = fullSession {
            for session in sessions {
                if session == fullSession {
                    rtcEngine.setRemoteVideoStream(fullSession.uid, type: .high)
                } else {
                    rtcEngine.setRemoteVideoStream(session.uid, type: .low)
                }
            }
        } else {
            for session in sessions {
                rtcEngine.setRemoteVideoStream(session.uid, type: .high)
            }
        }
    }
    
    func addLocalSession() {
        let localSession = VideoSession.localSession()
        videoSessions.append(localSession)
        rtcEngine.setupLocalVideo(localSession.canvas)
    }
    
    func fetchSession(ofUid uid: UInt) -> VideoSession? {
        for session in videoSessions {
            if session.uid == uid {
                return session
            }
        }
        
        return nil
    }
    
    func videoSession(ofUid uid: UInt) -> VideoSession {
        if let fetchedSession = fetchSession(ofUid: uid) {
            return fetchedSession
        } else {
            let newSession = VideoSession(uid: uid)
            videoSessions.append(newSession)
            return newSession
        }
    }
    
    func highPriorityRemoteUid(in sessions: [VideoSession], fullSession: VideoSession?) -> UInt? {
        if let fullSession = fullSession {
            return fullSession.uid
        } else {
            return sessions.last?.uid
        }
    }
}

//MARK: - Agora Media SDK
private extension LiveRoomViewController {
    func loadAgoraKit() {
        rtcEngine.delegate = self        
        rtcEngine.setChannelProfile(.liveBroadcasting)
        
        // Warning: only enable dual stream mode if there will be more than one broadcaster in the channel
        rtcEngine.enableDualStreamMode(true)
        
        rtcEngine.enableVideo()
        rtcEngine.setVideoEncoderConfiguration(
            AgoraVideoEncoderConfiguration(
                size: videoProfile,
                frameRate: .fps24,
                bitrate: AgoraVideoBitrateStandard,
                orientationMode: .adaptative
            )
        )
        
        rtcEngine.setClientRole(clientRole)
        
        if isBroadcaster {
            rtcEngine.startPreview()
        }
        
        addLocalSession()
        
        let code = rtcEngine.joinChannel(byToken: nil, channelId: roomName, info: nil, uid: 0, joinSuccess: nil)
        if code == 0 {
            setIdleTimerActive(false)
            rtcEngine.setEnableSpeakerphone(true)
        } else {
            DispatchQueue.main.async(execute: {
                self.alert(string: "Join channel failed: \(code)")
            })
        }
    }
}

extension LiveRoomViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        let userSession = videoSession(ofUid: uid)
        rtcEngine.setupRemoteVideo(userSession.canvas)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, firstLocalVideoFrameWith size: CGSize, elapsed: Int) {
        if let _ = videoSessions.first {
            updateInterface(withAnimation: false)
        }
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        var indexToDelete: Int?
        for (index, session) in videoSessions.enumerated() {
            if session.uid == uid {
                indexToDelete = index
            }
        }
        
        if let indexToDelete = indexToDelete {
            let deletedSession = videoSessions.remove(at: indexToDelete)
            deletedSession.hostingView.removeFromSuperview()
            
            if deletedSession == fullSession {
                fullSession = nil
            }
        }
    }
}

//MARK: - enhancer
extension LiveRoomViewController: BeautyEffectTableVCDelegate {
    func beautyEffectTableVCDidChange(_ enhancerTableVC: BeautyEffectTableViewController) {
        beautyOptions.lighteningLevel = enhancerTableVC.lightening
        beautyOptions.smoothnessLevel = enhancerTableVC.smoothness
        beautyOptions.lighteningContrastLevel = enhancerTableVC.contrast
        beautyOptions.rednessLevel = enhancerTableVC.redness
        isBeautyOn = enhancerTableVC.isBeautyOn
    }
}

//MARK: - vc
extension LiveRoomViewController: UIPopoverPresentationControllerDelegate {
    func adaptivePresentationStyle(for controller: UIPresentationController, traitCollection: UITraitCollection) -> UIModalPresentationStyle {
        return .none
    }
}
