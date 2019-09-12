//
//  LiveRoomViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

protocol LiveVCDataSource: NSObjectProtocol {
    func liveVCNeedAgoraKit() -> AgoraRtcEngineKit
    func liveVCNeedSettings() -> Settings
}

class LiveRoomViewController: UIViewController {
    
    @IBOutlet weak var broadcastersView: AGVideoCollectionView!
    @IBOutlet weak var placeholderView: UIImageView!
    
    @IBOutlet weak var videoMuteButton: UIButton!
    @IBOutlet weak var audioMuteButton: UIButton!
    @IBOutlet weak var beautyEffectButton: UIButton!
    
    @IBOutlet var sessionButtons: [UIButton]!
    
    private let beautyOptions: AgoraBeautyOptions = {
        let options = AgoraBeautyOptions()
        options.lighteningContrastLevel = .normal
        options.lighteningLevel = 0.7
        options.smoothnessLevel = 0.5
        options.rednessLevel = 0.1
        return options
    }()
    
    private var agoraKit: AgoraRtcEngineKit {
        return dataSource!.liveVCNeedAgoraKit()
    }
    
    private var settings: Settings {
        return dataSource!.liveVCNeedSettings()
    }
    
    private var isMutedVideo = false {
        didSet {
            // mute local video
            agoraKit.muteLocalVideoStream(isMutedVideo)
            videoMuteButton.isSelected = isMutedVideo
        }
    }
    
    private var isMutedAudio = false {
        didSet {
            // mute local audio
            agoraKit.muteLocalAudioStream(isMutedAudio)
            audioMuteButton.isSelected = isMutedAudio
        }
    }
    
    private var isBeautyOn = false {
        didSet {
            // improve local render view
            agoraKit.setBeautyEffectOptions(isBeautyOn,
                                            options: isBeautyOn ? beautyOptions : nil)
            beautyEffectButton.isSelected = isBeautyOn
        }
    }
    
    private var isSwitchCamera = false {
        didSet {
            agoraKit.switchCamera()
        }
    }
    
    private var videoSessions = [VideoSession]() {
        didSet {
            placeholderView.isHidden = (videoSessions.count == 0 ? false : true)
            // update render view layout
            updateBroadcastersView()
        }
    }
    
    weak var dataSource: LiveVCDataSource?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateButtonsVisiablity()
        loadAgoraKit()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    //MARK: - ui action
    @IBAction func doSwitchCameraPressed(_ sender: UIButton) {
        isSwitchCamera.toggle()
    }
    
    @IBAction func doBeautyPressed(_ sender: UIButton) {
        isBeautyOn.toggle()
    }
    
    @IBAction func doMuteVideoPressed(_ sender: UIButton) {
        isMutedVideo.toggle()
    }
    
    @IBAction func doMuteAudioPressed(_ sender: UIButton) {
        isMutedAudio.toggle()
    }
    
    @IBAction func doLeavePressed(_ sender: UIButton) {
        leaveChannel()
    }
}

private extension LiveRoomViewController {
    func updateBroadcastersView() {
        var rank: Int
        var row: Int
        
        if videoSessions.count == 0 {
            return
        } else if videoSessions.count == 1 {
            rank = 1
            row = 1
        } else if videoSessions.count == 2 {
            rank = 1
            row = 2
        } else {
            rank = 2
            row = Int(ceil(Double(videoSessions.count) / Double(rank)))
        }
        
        let itemWidth = CGFloat(1.0) / CGFloat(rank)
        let itemHeight = CGFloat(1.0) / CGFloat(row)
        let layout = AGVideoCollectionLayout(level: 0)
                     .itemSize(width: itemWidth,
                               height: itemHeight)
        
        broadcastersView
            .listCount { [unowned self] (_) -> Int in
                return self.videoSessions.count
            }.listItem { [unowned self] (index) -> UIView in
                return self.videoSessions[index.item].hostingView
            }
        
        broadcastersView.setLayouts([layout], animated: true)
    }
    
    func updateButtonsVisiablity() {
        guard let sessionButtons = sessionButtons else {
            return
        }
        
        let isHidden = settings.role == .audience
        
        for item in sessionButtons {
            item.isHidden = isHidden
        }
    }
    
    func setIdleTimerActive(_ active: Bool) {
        UIApplication.shared.isIdleTimerDisabled = !active
    }
}

private extension LiveRoomViewController {
    func fetchSession(of uid: UInt) -> VideoSession? {
        for session in videoSessions {
            if session.uid == uid {
                return session
            }
        }
        return nil
    }
    
    func videoSession(of uid: UInt) -> VideoSession {
        if let fetchedSession = fetchSession(of: uid) {
            return fetchedSession
        } else {
            let newSession = VideoSession(uid: uid)
            videoSessions.append(newSession)
            return newSession
        }
    }
}

//MARK: - Agora Media SDK
private extension LiveRoomViewController {
    func loadAgoraKit() {
        guard let channelId = settings.roomName else {
            return
        }
        
        setIdleTimerActive(false)
        
        // Step 1, set delegate
        agoraKit.delegate = self
        // Step 2, set live broadcasting mode
        agoraKit.setChannelProfile(.liveBroadcasting)
        // set client role
        agoraKit.setClientRole(settings.role)
        
        // Step 3, Warning: only enable dual stream mode if there will be more than one broadcaster in the channel
        agoraKit.enableDualStreamMode(true)
        
        // Step 4, enable the video module
        agoraKit.enableVideo()
        // set video configuration
        agoraKit.setVideoEncoderConfiguration(
            AgoraVideoEncoderConfiguration(
                size: settings.dimension,
                frameRate: settings.frameRate,
                bitrate: AgoraVideoBitrateStandard,
                orientationMode: .adaptative
            )
        )
        
        // if current role is broadcaster, add local render view and start preview
        if settings.role == .broadcaster {
            addLocalSession()
            agoraKit.startPreview()
        }
        
        // Step 5, join channel and start group chat
        // If join  channel success, agoraKit triggers it's delegate function
        // 'rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int)'
        agoraKit.joinChannel(byToken: KeyCenter.Token, channelId: channelId, info: nil, uid: 0, joinSuccess: nil)
        
        // Step 6, set speaker audio route
        agoraKit.setEnableSpeakerphone(true)
    }
    
    func addLocalSession() {
        let localSession = VideoSession.localSession()
        videoSessions.append(localSession)
        agoraKit.setupLocalVideo(localSession.canvas)
        
        let mediaInfo = MediaInfo(dimension: settings.dimension,
                                  fps: settings.frameRate.rawValue)
        localSession.mediaInfo = mediaInfo
    }
    
    func leaveChannel() {
        // Step 1, release local AgoraRtcVideoCanvas instance
        agoraKit.setupLocalVideo(nil)
        // Step 2, leave channel and end group chat
        agoraKit.leaveChannel(nil)
        
        // Step 3, if current role is broadcaster,  stop preview after leave channel
        if settings.role == .broadcaster {
            agoraKit.stopPreview()
        }
        
        setIdleTimerActive(true)
        
        navigationController?.popViewController(animated: true)
    }
}

// MARK: - AgoraRtcEngineDelegate
extension LiveRoomViewController: AgoraRtcEngineDelegate {
    // first remote video frame
    func rtcEngine(_ engine: AgoraRtcEngineKit, firstRemoteVideoDecodedOfUid uid: UInt, size: CGSize, elapsed: Int) {
        guard videoSessions.count < 5 else {
            return
        }
        
        let userSession = videoSession(of: uid)
        userSession.updateMediaInfo(resolution: size)
        agoraKit.setupRemoteVideo(userSession.canvas)
    }
    
    // user offline
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        var indexToDelete: Int?
        for (index, session) in videoSessions.enumerated() where session.uid == uid {
            indexToDelete = index
            break
        }
        
        if let indexToDelete = indexToDelete {
            let deletedSession = videoSessions.remove(at: indexToDelete)
            deletedSession.hostingView.removeFromSuperview()
            
            // release canvas's view
            deletedSession.canvas.view = nil
        }
    }
    
    // remote stat
    func rtcEngine(_ engine: AgoraRtcEngineKit, remoteVideoStats stats: AgoraRtcRemoteVideoStats) {
        if let session = fetchSession(of: stats.uid) {
            session.updateMediaInfo(resolution: CGSize(width: CGFloat(stats.width), height: CGFloat(stats.height)),
                                    fps: Int(stats.rendererOutputFrameRate))
        }
    }
}
