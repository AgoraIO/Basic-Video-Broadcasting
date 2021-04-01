//
//  RoomViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 2/20/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Cocoa
import Quartz.ImageKit
import AgoraRtcKit

protocol RoomVCDelegate: class {
    func roomVCNeedClose(_ roomVC: RoomViewController)
}

protocol RoomVCDataSource: NSObjectProtocol {
    func roomVCNeedAgoraKit() -> AgoraRtcEngineKit
    func roomVCNeedSettings() -> Settings
}

class RoomViewController: NSViewController {
    
    enum ScreenSharingStatus {
        case none, list, sharing
        
        func nextStatus() -> ScreenSharingStatus {
            switch self {
            case .none: return .list
            case .list: return .none
            case .sharing: return .none
            }
        }
    }
    
    @IBOutlet weak var roomNameLabel: NSTextField!
    @IBOutlet weak var totalVideoLabel: NSTextField!
    @IBOutlet weak var messageTableContainerView: NSView!
    @IBOutlet weak var containerView: AGEVideoContainer!
    
    @IBOutlet weak var muteVideoButton: NSButton!
    @IBOutlet weak var muteAudioButton: NSButton!
    @IBOutlet weak var hangupButton: NSButton!
    @IBOutlet weak var beautyButton: NSButton!
    
    @IBOutlet weak var roomNameWidth: NSLayoutConstraint!
    
    @IBOutlet weak var windowSharingButton: NSButton!
    @IBOutlet weak var windowCollectionView: NSCollectionView!
    @IBOutlet weak var windowsContainerView: NSScrollView!
    
    private var audioMuted = false {
        didSet {
            muteAudioButton?.image = NSImage(named: audioMuted ? "icon-micorophone off" : "icon-micorophone")
            agoraKit.muteLocalAudioStream(audioMuted)
        }
    }
    
    private var videoMuted = false {
        didSet {
            muteVideoButton?.image = NSImage(named: videoMuted ? "icon-camera off" : "icon-camera")
            agoraKit.muteLocalVideoStream(videoMuted)
            setVideoMuted(videoMuted, forUid: 0)
        }
    }
    
    private var isBeauty = false {
        didSet {
            guard oldValue != isBeauty else {
                return
            }
            beautyButton?.image = NSImage(named: isBeauty ? "icon-beauty" : "icon-beauty off")
            var options: AgoraBeautyOptions? = nil
            if isBeauty {
                options = AgoraBeautyOptions()
                options?.lighteningContrastLevel = .normal
                options?.lighteningLevel = 0.7
                options?.smoothnessLevel = 0.5
                options?.rednessLevel = 0.1
            }
            // improve local render view
            agoraKit.setBeautyEffectOptions(isBeauty, options: options)
        }
    }
    
    private var videoSessions = [VideoSession]() {
        didSet {
            updateTotalVideoLabel()
            updateBroadcastersView()
        }
    }
    
    private var screenSharingStatus = ScreenSharingStatus.none {
        didSet {
            let opacity: CGFloat = 0.3
            let grayColor = NSColor(red: 0, green: 0, blue: 0, alpha: opacity).cgColor
            let blueColor = NSColor(red: 0, green: 106.0 / 255.0, blue: 216.0 / 255.0, alpha: opacity).cgColor
            
            windowSharingButton.layer?.backgroundColor = (screenSharingStatus == .sharing) ? blueColor : grayColor
            
            if oldValue == .sharing {
                stopShareWindow()
            }
            
            showWindowList(screenSharingStatus == .list)
        }
    }
    
    private var agoraKit: AgoraRtcEngineKit {
        return dataSource!.roomVCNeedAgoraKit()
    }
    
    private var settings: Settings {
        return dataSource!.roomVCNeedSettings()
    }
    
    private var logVC: LogViewController?
    
    private var windows = WindowList()
    
    // MARK: - public var
    var dataSource: RoomVCDataSource?
    var delegate: RoomVCDelegate?
    
    // MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
        loadAgoraKit()
    }
    
    override func viewDidAppear() {
        super.viewDidAppear()
        configStyle(of: view.window!)
    }
    
    override func prepare(for segue: NSStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier , !segueId.isEmpty else {
            return
        }
        
        switch segueId {
        case "VideoVCEmbedChatMessageVC":
            logVC = segue.destinationController as? LogViewController
        default:
            break
        }
    }
    
    //MARK: - user action
    @IBAction func doMuteVideoClicked(_ sender: NSButton) {
        videoMuted.toggle()
    }
    
    @IBAction func doMuteAudioClicked(_ sender: NSButton) {
        audioMuted.toggle()
    }
    
    @IBAction func doBeautyClicked(_ sender: NSButton) {
        isBeauty.toggle()
    }
    
    @IBAction func doCloseClicked(_ sender: NSButton) {
        leaveChannel()
    }
    
    @IBAction func doShareScreenClicked(_ sender: NSButton) {
        screenSharingStatus = screenSharingStatus.nextStatus()
    }
}

//MARK: - Private UI
private extension RoomViewController {
    func updateViews() {
        let opacity: CGFloat = 0.3
        let layerColor = NSColor(red: 0, green: 0, blue: 0, alpha: opacity).cgColor
        
        roomNameLabel.layer?.backgroundColor = layerColor
        roomNameLabel.layer?.cornerRadius = 16
        
        if let size = roomNameLabel.font?.pointSize,
            let roomName = settings.roomName  {
            
            roomNameLabel.stringValue = roomName
            let string = roomName as NSString
            let rect = string.boundingRect(with: NSMakeSize(0, 32),
                                options: [.usesLineFragmentOrigin],
                                attributes: [NSAttributedString.Key.font: NSFont.systemFont(ofSize: size)])
            let space: CGFloat = 32.0
            roomNameWidth.constant = rect.width + space
        }
        
        if settings.role == .broadcaster {
            let cornerRadius: CGFloat = 45 * 0.5
            
            muteAudioButton.layer?.backgroundColor = layerColor
            muteAudioButton.layer?.cornerRadius = cornerRadius
            
            muteVideoButton.layer?.backgroundColor = layerColor
            muteVideoButton.layer?.cornerRadius = cornerRadius
            
            beautyButton.layer?.backgroundColor = layerColor
            beautyButton.layer?.cornerRadius = cornerRadius
            
            windowSharingButton.layer?.backgroundColor = layerColor
            windowSharingButton.layer?.cornerRadius = cornerRadius
            
            totalVideoLabel.layer?.backgroundColor = layerColor
            totalVideoLabel.layer?.cornerRadius = 4
        } else {
            muteAudioButton.isHidden = true
            muteVideoButton.isHidden = true
            
            beautyButton.isHidden = true
            windowSharingButton.isHidden = true
        }
        
        totalVideoLabel.layer?.backgroundColor = layerColor
        totalVideoLabel.layer?.cornerRadius = 4
        
        messageTableContainerView.isHidden = true
        
        setupWindowCollectionView()
        setupVideoLayout()
    }
    
    func setupWindowCollectionView() {
        let collectionViewHeight = 104.0
        let flowLayout = NSCollectionViewFlowLayout()
        flowLayout.itemSize = NSSize(width: collectionViewHeight,
                                     height: collectionViewHeight)
        flowLayout.minimumInteritemSpacing = 20.0
        flowLayout.scrollDirection = .horizontal
        windowCollectionView.layer?.backgroundColor = NSColor.black.cgColor
        windowCollectionView.collectionViewLayout = flowLayout
        windowCollectionView.delegate = self
        windowCollectionView.isSelectable = true
        windowCollectionView.register(WindowItem.self,
                                      forItemWithIdentifier: NSUserInterfaceItemIdentifier(rawValue: "WindowItem"))
    }
    
    func showWindowList(_ shouldShow: Bool) {
        if shouldShow {
            windows.getList()
            windowCollectionView.reloadData()
            windowsContainerView.isHidden = false
        } else {
            windowsContainerView.isHidden = true
        }
    }
    
    func configStyle(of window: NSWindow) {
        window.styleMask.insert([.fullSizeContentView, .resizable])
        if window.delegate == nil {
            window.delegate = self
        }
        window.collectionBehavior = [.fullScreenPrimary]

        let minSize = CGSize(width: 1280, height: 800)
        window.minSize = minSize
        window.maxSize = CGSize(width: CGFloat.greatestFiniteMagnitude,
                                height: CGFloat.greatestFiniteMagnitude)
        window.setContentSize(minSize)
    }
    
    func updateTotalVideoLabel() {
        if videoSessions.count < 2 {
            totalVideoLabel.isHidden = true
        } else {
            totalVideoLabel.isHidden = false
            totalVideoLabel.stringValue = "Total \(videoSessions.count) person"
        }
    }
    
    // MARK: - Video views layout
    func updateBroadcastersView() {
        containerView.reload(level: 0)
        containerView.reload(level: 1)
    }
    
    func setupVideoLayout() {
        let fullLayout = AGEVideoLayout(level: 0)

        let windowMinHeight = 800
        let scrollHeight = windowMinHeight - 91 - 34 - 32 - 20
        
        let scrollLayout = AGEVideoLayout(level: 1)
            .scrollType(.scroll(.vertical))
            .startPoint(x: 30, y: 91)
            .size(.constant(CGSize(width: 200, height: scrollHeight)))
            .itemSize(.constant(CGSize(width: 200, height: 150)))
        
        containerView
            .listCount { [unowned self] (level) -> Int in
                if level == 0 {
                    return (self.videoSessions.count > 0 ? 1 : 0)
                } else {
                    return self.videoSessions.count - 1
                }
        }.listItem { [unowned self] (index) -> NSView in
            if index.level == 0 {
                let videoSession = self.videoSessions[index.item]
                videoSession.descriptionType = .full
                return videoSession.hostingView
            } else {
                let videoSession = self.videoSessions[index.item + 1]
                videoSession.descriptionType = .brief
                return videoSession.hostingView
            }
        }
        
        containerView.setLayouts([fullLayout, scrollLayout])
        containerView.delegate = self
    }
    
    func getSession(of uid: UInt) -> VideoSession? {
        for session in videoSessions {
            if session.uid == uid {
                return session
            }
        }
        return nil
    }
    
    func videoSession(of uid: UInt) -> VideoSession {
        if let fetchedSession = getSession(of: uid) {
            return fetchedSession
        } else {
            let newSession = VideoSession(uid: uid)
            videoSessions.append(newSession)
            return newSession
        }
    }
    
    func setVideoMuted(_ muted: Bool, forUid uid: UInt) {
        getSession(of: uid)?.isVideoMuted = muted
    }
    
    // MARK: - Log
    func info(string: String) {
        guard !string.isEmpty else {
            return
        }
        logVC?.append(info: string)
    }
    
    func alert(string: String) {
        guard !string.isEmpty else {
            return
        }
        logVC?.append(alert: string)
    }
}

//MARK: - Agora Rtc Kit
private extension RoomViewController {
    func loadAgoraKit() {
        guard let roomName = settings.roomName else {
            return
        }
        
        // Step 1, set media device
        if let deviceId = settings.recordDevice.deviceId {
            agoraKit.setDevice(.audioRecording, deviceId: deviceId)
        }
        
        if let deviceId = settings.speakerDevice.deviceId {
            agoraKit.setDevice(.audioPlayout, deviceId: deviceId)
        }
        
        if let deviceId = settings.cameraDevice.deviceId {
            agoraKit.setDevice(.videoCapture, deviceId: deviceId)
        }
        
        // Step 2, set delegate
        agoraKit.delegate = self
        
        // Step 3, set liveBroadcasting mode, and set client role
        agoraKit.setChannelProfile(.liveBroadcasting)
        agoraKit.setClientRole(settings.role)
        
        // Step 4, enable the video module
        agoraKit.enableVideo()
        // set video configuration
        let configuration =
            AgoraVideoEncoderConfiguration(size: settings.dimension,
                                           frameRate: .fps15,
                                           bitrate: AgoraVideoBitrateStandard,
                                           orientationMode: .adaptative)
        agoraKit.setVideoEncoderConfiguration(configuration)
        
        // add local render view and start preview
        if settings.role == .broadcaster {
            addLocalSession()
            agoraKit.startPreview()
        }
       
        // Step 5, join channel and start group chat
        // If join  channel success, agoraKit triggers it's delegate function
        // 'rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int)'
        agoraKit.joinChannel(byToken: KeyCenter.Token,
                             channelId: roomName,
                             info: nil,
                             uid: 0,
                             joinSuccess: nil)
    }
    
    func addLocalSession() {
        let localSession = VideoSession.localSession()
        videoSessions.append(localSession)
        agoraKit.setupLocalVideo(localSession.canvas)
    }
    
    func leaveChannel() {
        // Step 1, release local AgoraRtcVideoCanvas instance
        agoraKit.setupLocalVideo(nil)
        // Step 2, leave channel and end group chat
        agoraKit.leaveChannel(nil)
        // Step 3, please attention, stop preview after leave channel
        agoraKit.stopPreview()
        
        // Step 4, remove all render views
        for session in videoSessions {
            session.hostingView.removeFromSuperview()
        }
        videoSessions.removeAll()
        
        delegate?.roomVCNeedClose(self)
    }
    
    // MARK: - screen sharing
    func startShareWindow(_ window: Window) {
        let windowId = window.id
        if windowId == 0 {
            agoraKit.startScreenCapture(byDisplayId: UInt(CGMainDisplayID()),
                                        rectangle: CGRect.zero,
                                        parameters: AgoraScreenCaptureParameters())
        } else {
            agoraKit.startScreenCapture(byWindowId: UInt(windowId),
                                        rectangle: CGRect.zero,
                                        parameters: AgoraScreenCaptureParameters())
        }
        videoSessions.first?.hostingView.switchToScreenShare(windowId == 0 || window.name == "OpenLive" || window.name == "Full Screen")
    }
    
    func stopShareWindow() {
        agoraKit.stopScreenCapture()
        videoSessions.first?.hostingView.switchToScreenShare(false)
    }
}

//MARK: - AgoraRtcEngineDelegate
extension RoomViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
        info(string: "Join channel: \(channel)")
    }
    
    func rtcEngineConnectionDidInterrupted(_ engine: AgoraRtcEngineKit) {
        alert(string: "RTC Connection Interrupted")
    }
    
    func rtcEngineConnectionDidLost(_ engine: AgoraRtcEngineKit) {
        alert(string: "RTC Connection Lost")
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurError errorCode: AgoraErrorCode) {
        alert(string: "RTC ErrorCode \(errorCode.description)")
    }
    
    // first local video frame
    func rtcEngine(_ engine: AgoraRtcEngineKit, firstLocalVideoFrameWith size: CGSize, elapsed: Int) {
        if let selfSession = videoSessions.first {
            selfSession.updateInfo(resolution: size)
        }
    }
    
    // local stats
    func rtcEngine(_ engine: AgoraRtcEngineKit, reportRtcStats stats: AgoraChannelStats) {
        if let selfSession = videoSessions.first {
            selfSession.updateChannelStats(stats)
        }
    }
    
    // first remote video frame
    func rtcEngine(_ engine: AgoraRtcEngineKit, firstRemoteVideoDecodedOfUid uid: UInt, size: CGSize, elapsed: Int) {
        let userSession = videoSession(of: uid)
        userSession.updateInfo(resolution: size)
    }

    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        let userSession = videoSession(of: uid)
        agoraKit.setupRemoteVideo(userSession.canvas)
    }
    
    // user offline
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
            
            // release canvas's view
            deletedSession.canvas.view = nil
        }
    }
    
    // video muted
    func rtcEngine(_ engine: AgoraRtcEngineKit, didVideoMuted muted: Bool, byUid uid: UInt) {
        setVideoMuted(muted, forUid: uid)
    }
    
    // remote video stats
    func rtcEngine(_ engine: AgoraRtcEngineKit, remoteVideoStats stats: AgoraRtcRemoteVideoStats) {
        if let session = getSession(of: stats.uid) {
            session.updateVideoStats(stats)
        }
    }
    
    // remote audio stats
    func rtcEngine(_ engine: AgoraRtcEngineKit, remoteAudioStats stats: AgoraRtcRemoteAudioStats) {
        if let session = getSession(of: stats.uid) {
            session.updateAudioStats(stats)
        }
    }
}

extension RoomViewController: AGEVideoContainerDelegate {
    func container(_ container: AGEVideoContainer, didSelected subView: AGEView, index: AGEIndex) {
        guard index.level == 1 else {
            return
        }
        
        let first = 0
        let swap = index.item + 1
        
        let firstSession = videoSessions[first]
        let swapSession = videoSessions[swap]
        
        firstSession.descriptionType = .brief
        swapSession.descriptionType = .full
        
        videoSessions.swapAt(first, swap)
    }
}

// MARK: - NSWindowDelegate
extension RoomViewController: NSWindowDelegate {
    func windowShouldClose(_ sender: NSWindow) -> Bool {
        leaveChannel()
        return false
    }
}

// MARK: - NSCollectionViewDelegate
extension RoomViewController: NSCollectionViewDelegate {
    func collectionView(_ collectionView: NSCollectionView, didSelectItemsAt indexPaths: Set<IndexPath>) {
        guard let index = indexPaths.first else {
            return
        }
        
        let item = windows.items[index.item]
        startShareWindow(item.window)
        screenSharingStatus = .sharing
    }
}

// MARK: - NSCollectionViewDataSource
extension RoomViewController: NSCollectionViewDataSource {
    func collectionView(_ collectionView: NSCollectionView, numberOfItemsInSection section: Int) -> Int {
        return windows.items.count
    }
    
    func collectionView(_ collectionView: NSCollectionView, itemForRepresentedObjectAt indexPath: IndexPath) -> NSCollectionViewItem {
        let cell = collectionView.makeItem(withIdentifier: NSUserInterfaceItemIdentifier(rawValue: "WindowItem"),
                                           for: indexPath)
        
        if let windowItem = cell as? WindowItem {
            let item = windows.items[indexPath.item]
            windowItem.image = item.window.image
            return windowItem
        } else {
            return cell
        }
    }
}
