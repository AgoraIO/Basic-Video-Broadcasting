//
//  RoleViewController.swift
//  OpenLive
//
//  Created by CavanSu on 2019/8/28.
//  Copyright © 2019 Agora. All rights reserved.
//

import UIKit
import AgoraRtcKit

protocol RoleVCDelegate: NSObjectProtocol {
    func roleVC(_ vc: RoleViewController, didSelect role: AgoraClientRole)
}

class RoleViewController: UIViewController {

    weak var delegate: RoleVCDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let segueId = segue.identifier,
            segueId.count > 0 else {
            return
        }
        
        switch segueId {
        case "roleToLive":
            guard let mainVC = navigationController?.viewControllers.first as? MainViewController,
                let liveVC = segue.destination as? LiveRoomViewController else {
                return
            }
            
            liveVC.dataSource = mainVC
        default:
            break
        }
    }
    
    func selectedRoleToLive(role: AgoraClientRole) {
        delegate?.roleVC(self, didSelect: role)
        performSegue(withIdentifier: "roleToLive", sender: nil)
    }
    
    @IBAction func doBroadcasterTap(_ sender: UITapGestureRecognizer) {
        selectedRoleToLive(role: .broadcaster)
    }
    
    @IBAction func doAudienceTap(_ sender: UITapGestureRecognizer) {
        selectedRoleToLive(role: .audience)
    }
}
