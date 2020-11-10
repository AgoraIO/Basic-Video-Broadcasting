//
//  MainViewController.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit
import AgoraRtcKit

class MainViewController: UIViewController {

    @IBOutlet weak var roomNameTextField: UITextField!
    @IBOutlet weak var startButton: UIButton!
    @IBOutlet weak var inputTextField: UITextField!
    @IBOutlet weak var logoTop: NSLayoutConstraint!
    @IBOutlet weak var inputTextFieldTop: NSLayoutConstraint!
    
    private lazy var agoraKit: AgoraRtcEngineKit = {
        let engine = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.AppId, delegate: nil)
        engine.setLogFilter(AgoraLogFilter.info.rawValue)
        engine.setLogFile(FileCenter.logFilePath())
        return engine
    }()
    
    private var settings = Settings()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        inputTextField.endEditing(true)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier,
            segueId.count > 0 else {
            return
        }
        
        switch segueId {
        case "mainToSettings":
            let settingsVC = segue.destination as? SettingsViewController
            settingsVC?.delegate = self
            settingsVC?.dataSource = self
        case "mainToRole":
            let roleVC = segue.destination as? RoleViewController
            roleVC?.delegate = self
        default:
            break
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        inputTextField.endEditing(true)
    }
    
    @IBAction func doStartButton(_ sender: UIButton) {
        guard let roomName = roomNameTextField.text,
            roomName.count > 0 else {
                return
        }
        settings.roomName = roomName
        performSegue(withIdentifier: "mainToRole", sender: nil)
    }
    
    @IBAction func doExitPressed(_ sender: UIStoryboardSegue) {
    }
}

private extension MainViewController {
    func updateViews() {
        let key = NSAttributedString.Key.foregroundColor
        let color = UIColor(red: 156.0 / 255.0, green: 217.0 / 255.0, blue: 1.0, alpha: 1)
        let attributed = [key: color]
        let attributedString = NSMutableAttributedString(string: "Enter a channel name", attributes: attributed)
        inputTextField.attributedPlaceholder = attributedString
        
        startButton.layer.shadowOpacity = 0.3
        startButton.layer.shadowColor = UIColor.black.cgColor
        
        if UIScreen.main.bounds.height <= 568 {
            logoTop.constant = 69
            inputTextFieldTop.constant = 37
        }
    }
}

extension MainViewController: LiveVCDataSource {
    func liveVCNeedSettings() -> Settings {
        return settings
    }
    
    func liveVCNeedAgoraKit() -> AgoraRtcEngineKit {
        return agoraKit
    }
}

extension MainViewController: SettingsVCDelegate {
    func settingsVC(_ vc: SettingsViewController, didSelect dimension: CGSize) {
        settings.dimension = dimension
    }
    
    func settingsVC(_ vc: SettingsViewController, didSelect frameRate: AgoraVideoFrameRate) {
        settings.frameRate = frameRate
    }
}

extension MainViewController: SettingsVCDataSource {
    func settingsVCNeedSettings() -> Settings {
        return settings
    }
}

extension MainViewController: RoleVCDelegate {
    func roleVC(_ vc: RoleViewController, didSelect role: AgoraClientRole) {
        settings.role = role
    }
}

extension MainViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        inputTextField.endEditing(true)
        return true
    }
}
