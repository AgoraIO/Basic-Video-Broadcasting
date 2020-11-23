//
//  MainViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 2/20/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Cocoa
import AgoraRtcKit

class MainViewController: NSViewController {
    
    @IBOutlet weak var roomInputTextField: NSTextField!
    @IBOutlet weak var roomInputLineView: NSView!
    @IBOutlet weak var joinButton: AGEButton!
    @IBOutlet weak var settingsButton: NSButton!
    
    @IBOutlet weak var broadcasterImageView: NSImageView!
    @IBOutlet weak var audienceImageView: NSImageView!
    @IBOutlet weak var broadcasterBox: NSButton!
    @IBOutlet weak var audienceBox: NSButton!
    
    private lazy var agoraKit: AgoraRtcEngineKit = {
        let engine = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: nil)
        engine.setLogFilter(AgoraLogFilter.info.rawValue)
        engine.setLogFile(FileCenter.logFilePath())
        return engine
    }()
    
    private var role = AgoraClientRole.audience {
        didSet {
            switch role {
            case .broadcaster:
                broadcasterImageView.image = NSImage(named: "broadcaster-blue")
                broadcasterBox.setTitle(" Host", with: NSColor.AGTextBlue)
                broadcasterBox.state = .on
                
                audienceBox.setTitle(" Audience", with: NSColor.AGTextGray)
                audienceImageView.image = NSImage(named: "audience-gray")
                audienceBox.state = .off
            case .audience:
                broadcasterImageView.image = NSImage(named: "broadcaster-gray")
                broadcasterBox.setTitle(" Host", with: NSColor.AGTextGray)
                broadcasterBox.state = .off
                
                audienceImageView.image = NSImage(named: "audience-blue")
                audienceBox.setTitle(" Audience", with: NSColor.AGTextBlue)
                audienceBox.state = .on
            }
        }
    }
    
    private var settings = Settings()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
    }
    
    override func viewDidAppear() {
        super.viewDidAppear()
        roomInputTextField.becomeFirstResponder()
    }
    
    override func prepare(for segue: NSStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier , !segueId.isEmpty else {
            return
        }
        
        switch segueId {
        case "mainVCToSettingsVC":
            let settingsVC = segue.destinationController as! SettingsViewController
            settingsVC.dataSource = self
            settingsVC.delegate = self
        case "mainVCToRoomVC":
            let roomVC = segue.destinationController as! RoomViewController
            roomVC.dataSource = self
            roomVC.delegate = self
        default:
            break
        }
    }
    
    override func mouseEntered(with event: NSEvent) {
        if event.trackingArea?.owner as? NSButton == joinButton, joinButton.isEnabled {
            joinButton.image = NSImage(named: "icon-join-hover")
        }
    }
    
    override func mouseExited(with event: NSEvent) {
        if event.trackingArea?.owner as? NSButton == joinButton, joinButton.isEnabled {
            joinButton.image = NSImage(named: "icon-join")
        }
    }
    
    // MARK: - user actions
    @IBAction func doJoinClicked(_ sender: NSButton) {
        enter(roomName: roomInputTextField.stringValue)
    }
    
    @IBAction func doSettingsClicked(_ sender: NSButton) {
        performSegue(withIdentifier: "roomVCToSettingsVC", sender: nil)
    }
    
    @IBAction func doAudienceClicked(_ sender: NSButton) {
        role = .audience
    }
    
    @IBAction func doBroadcasterClicked(_ sender: NSButton) {
        role = .broadcaster
    }
}

private extension MainViewController {
    func updateViews() {
        view.layer?.backgroundColor = NSColor.white.cgColor
        
        settingsButton.layer?.backgroundColor = NSColor.clear.cgColor
        
        roomInputTextField.layer?.backgroundColor = NSColor.white.cgColor
        roomInputTextField.textColor = NSColor.AGDarkGray
        roomInputTextField.setPlacehold("Enter a channel name", with: NSColor.AGTextGray)
        
        roomInputLineView.layer?.backgroundColor = NSColor.AGGray.cgColor
        
        joinButton.layer?.cornerRadius = 22
        joinButton.addTrackingArea(.default)
        joinButton.image = NSImage(named: "icon-join")
        joinButton.setTitle("Start Live Streaming", with: NSColor.white)
        
        role = .audience
    }
    
    func enter(roomName: String?) {
        guard let roomName = roomName, !roomName.isEmpty else {
            return
        }
        settings.role = role
        settings.roomName = roomName
        joinButton.image = NSImage(named: "icon-join")
        performSegue(withIdentifier: "mainVCToRoomVC", sender: nil)
    }
}

extension MainViewController: SettingsVCDelegate {
    func settingsVC(_ vc: SettingsViewController, didUpdate settings: Settings) {
        self.settings = settings
    }
    
    func settingsVCNeedClose(_ vc: SettingsViewController) {
        vc.view.window?.contentViewController = self
    }
}

extension MainViewController: SettingsVCDataSource {
    func settingsVCNeedAgoraKit() -> AgoraRtcEngineKit {
        return agoraKit
    }
    
    func settingsVCNeedSettings() -> Settings {
        return settings
    }
}

extension MainViewController: RoomVCDelegate {
    func roomVCNeedClose(_ roomVC: RoomViewController) {
        guard let window = roomVC.view.window else {
            return
        }
        
        if window.styleMask.contains(.fullScreen) {
            window.toggleFullScreen(nil)
        }
        
        if window.styleMask.contains(.resizable) {
            window.styleMask.remove(.resizable)
        }
        
        window.delegate = nil
        window.collectionBehavior = NSWindow.CollectionBehavior()

        window.contentViewController = self
        
        let size = CGSize(width: 700, height: 500)
        window.minSize = size
        window.setContentSize(size)
        window.maxSize = size
    }
}

extension MainViewController: RoomVCDataSource {
    func roomVCNeedSettings() -> Settings {
        return settings
    }
    
    func roomVCNeedAgoraKit() -> AgoraRtcEngineKit {
        return agoraKit
    }
}

//MARK: - text field
extension MainViewController: NSControlTextEditingDelegate {
    func controlTextDidChange(_ obj: Notification) {
        guard let field = obj.object as? NSTextField else {
            return
        }
        
        let legalString = MediaCharacter.updateToLegalMediaString(from: field.stringValue)
        field.stringValue = legalString
    }
}
