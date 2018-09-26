//
//  LiveRoomViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 2/20/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Cocoa

protocol LiveRoomVCDelegate: NSObjectProtocol {
    func liveRoomVCNeedClose(_ liveVC: LiveRoomViewController)
}

class LiveRoomViewController: NSViewController {
    
    //MARK: IBOutlet
    @IBOutlet weak var roomNameLabel: NSTextField!
    @IBOutlet weak var remoteContainerView: NSView!
    @IBOutlet weak var muteAudioButton: NSButton!
    @IBOutlet weak var broadcastButton: NSButton!
    
    //MARK: public var
    var roomName: String!
    var clientRole = AgoraClientRole.audience {
        didSet {
            updateButtonsVisiablity()
        }
    }
    var videoProfile: AgoraVideoProfile!
    var delegate: LiveRoomVCDelegate?
    
    //MARK: engine & session
    var rtcEngine: AgoraRtcEngineKit!
    fileprivate var isBroadcaster: Bool {
        return clientRole == .broadcaster
    }
    fileprivate var isMuted = false {
        didSet {
            rtcEngine.muteLocalAudioStream(isMuted)
            muteAudioButton?.image = NSImage(named: isMuted ? "btn_mute_blue" : "btn_mute")
        }
    }
    fileprivate var videoSessions = [VideoSession]() {
        didSet {
            guard remoteContainerView != nil else {
                return
            }
            updateInterface()
        }
    }
    fileprivate var fullSession: VideoSession? {
        didSet {
            if fullSession != oldValue && remoteContainerView != nil {
                updateInterface()
            }
        }
    }
    fileprivate let viewLayouter = VideoViewLayouter()
    
    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        roomNameLabel.stringValue = roomName
        updateButtonsVisiablity()
        
        loadAgoraKit()
    }
    
    override func viewDidAppear() {
        super.viewDidAppear()
        view.window?.delegate = self
    }
    
    //MARK: - user action
    @IBAction func doMuteClicked(_ sender: NSButton) {
        isMuted = !isMuted
    }
    
    @IBAction func doBroadcastClicked(_ sender: NSButton) {
        if isBroadcaster {
            clientRole = .audience
            if fullSession?.uid == 0 {
                fullSession = nil
            }
        } else {
            clientRole = .broadcaster
        }
        
        rtcEngine.setClientRole(clientRole)
        updateInterface()
    }
    
    override func mouseUp(with theEvent: NSEvent) {
        if theEvent.clickCount == 2 {
            if fullSession == nil {
                if let tappedSession = viewLayouter.responseSession(of: theEvent, inSessions: videoSessions, inContainerView: remoteContainerView) {
                    fullSession = tappedSession
                }
            } else {
                fullSession = nil
            }
        }
    }
    
    @IBAction func doLeaveClicked(_ sender: NSButton) {
        leaveChannel()
    }
}

//MARK: - private
private extension LiveRoomViewController {
    func updateButtonsVisiablity() {
        broadcastButton?.image = NSImage(named: isBroadcaster ? "btn_join_cancel" : "btn_join")
        muteAudioButton?.isHidden = !isBroadcaster
    }
    
    func leaveChannel() {
        rtcEngine.setupLocalVideo(nil)
        rtcEngine.leaveChannel(nil)
        if isBroadcaster {
            rtcEngine.stopPreview()
        }
        
        for session in videoSessions {
            session.hostingView.removeFromSuperview()
        }
        videoSessions.removeAll()
        
        delegate?.liveRoomVCNeedClose(self)
    }
    
    func alert(string: String) {
        guard !string.isEmpty else {
            return
        }
        
        let alert = NSAlert()
        alert.messageText = string
        alert.addButton(withTitle: "OK")
        alert.beginSheetModal(for: view.window!, completionHandler: nil)
    }
}

private extension LiveRoomViewController {
    func updateInterface() {
        var displaySessions = videoSessions
        if !isBroadcaster && !displaySessions.isEmpty {
            displaySessions.removeFirst()
        }
        viewLayouter.layout(sessions: displaySessions, fullSession: fullSession, inContainer: remoteContainerView)
        setStreamType(forSessions: displaySessions, fullSession: fullSession)
    }
    
    func setStreamType(forSessions sessions: [VideoSession], fullSession: VideoSession?) {
        if let fullSession = fullSession {
            for session in sessions {
                rtcEngine.setRemoteVideoStream(UInt(session.uid), type: (session == fullSession ? .high : .low))
            }
        } else {
            for session in sessions {
                rtcEngine.setRemoteVideoStream(UInt(session.uid), type: .high)
            }
        }
    }
    
    func addLocalSession() {
        let localSession = VideoSession.localSession()
        videoSessions.append(localSession)
        rtcEngine.setupLocalVideo(localSession.canvas)
    }
    
    func fetchSession(ofUid uid: Int64) -> VideoSession? {
        for session in videoSessions {
            if session.uid == uid {
                return session
            }
        }
        
        return nil
    }
    
    func videoSession(ofUid uid: Int64) -> VideoSession {
        if let fetchedSession = fetchSession(ofUid: uid) {
            return fetchedSession
        } else {
            let newSession = VideoSession(uid: uid)
            videoSessions.append(newSession)
            return newSession
        }
    }
}

//MARK: - Agora SDK
private extension LiveRoomViewController {
    func loadAgoraKit() {
        rtcEngine = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: self)
        rtcEngine.setChannelProfile(.liveBroadcasting)
        rtcEngine.enableVideo()
        rtcEngine.enableDualStreamMode(true)
        rtcEngine.setVideoProfile(videoProfile, swapWidthAndHeight: true)
        rtcEngine.setClientRole(clientRole)
        
        if isBroadcaster {
            rtcEngine.startPreview()
        }
        
        addLocalSession()
        
        let code = rtcEngine.joinChannel(byToken: nil, channelId: roomName, info: nil, uid: 0, joinSuccess: nil)
        if code != 0 {
            DispatchQueue.main.async(execute: {
                self.alert(string: "Join channel failed: \(code)")
            })
        }
    }
}

extension LiveRoomViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        let userSession = videoSession(ofUid: Int64(uid))
        rtcEngine.setupRemoteVideo(userSession.canvas)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, firstLocalVideoFrameWith size: CGSize, elapsed: Int) {
        if let _ = videoSessions.first {
            updateInterface()
        }
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        var indexToDelete: Int?
        for (index, session) in videoSessions.enumerated() {
            if session.uid == Int64(uid) {
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

//MARK: - window
extension LiveRoomViewController: NSWindowDelegate {
    func windowShouldClose(_ sender: Any) -> Bool {
        leaveChannel()
        return false
    }
}
