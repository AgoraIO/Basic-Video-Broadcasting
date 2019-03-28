//
//  EnhancerTableViewController.swift
//  AgoraLiveBroadcast
//
//  Created by GongYuhua on 16/7/6.
//  Copyright © 2016年 Agora. All rights reserved.
//

import UIKit
import AgoraRtcEngineKit

protocol BeautyEffectTableVCDelegate: NSObjectProtocol {
    func beautyEffectTableVCDidChange(_ enhancerTableVC: BeautyEffectTableViewController)
}

extension AgoraLighteningContrastLevel {
    static func allValue() -> [AgoraLighteningContrastLevel] {
        return [.low, .normal, .high]
    }
}

class BeautyEffectTableViewController: UITableViewController {

    @IBOutlet weak var switcher: UISegmentedControl!
    @IBOutlet weak var lighteningSlider: UISlider!
    @IBOutlet weak var lighteningLabel: UILabel!
    @IBOutlet weak var contrastSwitcher: UISegmentedControl!
    @IBOutlet weak var smoothnessSlider: UISlider!
    @IBOutlet weak var smoothnessLabel: UILabel!
    @IBOutlet weak var rednessSlider: UISlider!
    @IBOutlet weak var rednessLabel: UILabel!
    
    var isBeautyOn = false
    var lightening: Float = 0
    var smoothness: Float = 0
    var redness: Float = 0
    var contrast: AgoraLighteningContrastLevel = .normal
    
    weak var delegate: BeautyEffectTableVCDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        preferredContentSize = CGSize(width: 300, height: 219)
        
        switcher.selectedSegmentIndex = isBeautyOn ? 0 : 1
        
        lighteningSlider.value = lightening
        lighteningLabel.text = lightening.displayString()
        
        if let index = AgoraLighteningContrastLevel.allValue().index(of: contrast) {
            contrastSwitcher.selectedSegmentIndex = index
        }
        
        smoothnessSlider.value = smoothness
        smoothnessLabel.text = smoothness.displayString()
        
        rednessSlider.value = redness
        rednessLabel.text = redness.displayString()
    }
    
    @IBAction func doSwitched(_ sender: UISegmentedControl) {
        let index = sender.selectedSegmentIndex
        isBeautyOn = (index == 0)
        
        delegate?.beautyEffectTableVCDidChange(self)
    }
    
    @IBAction func doLighteningSliderChanged(_ sender: UISlider) {
        lightening = sender.value
        lighteningLabel.text = lightening.displayString()
        
        delegate?.beautyEffectTableVCDidChange(self)
    }
    
    @IBAction func doConstrastChanged(_ sender: UISegmentedControl) {
        let index = sender.selectedSegmentIndex
        if index < AgoraLighteningContrastLevel.allValue().count {
            contrast = AgoraLighteningContrastLevel.allValue()[index]
        }
        
        delegate?.beautyEffectTableVCDidChange(self)
    }
    
    @IBAction func doSmoothnessSliderChanged(_ sender: UISlider) {
        smoothness = sender.value
        smoothnessLabel.text = smoothness.displayString()
        
        delegate?.beautyEffectTableVCDidChange(self)
    }
    
    @IBAction func doRednessSliderChanged(_ sender: UISlider) {
        redness = sender.value
        rednessLabel.text = redness.displayString()
        
        delegate?.beautyEffectTableVCDidChange(self)
    }
}

private extension Float {
    func displayString() -> String {
        return String(format: "%.1f", self)
    }
}
