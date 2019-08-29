//
//  DimensionCell.swift
//  OpenVideoCall
//
//  Created by GongYuhua on 6/26/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit

class DimensionCell: UICollectionViewCell {
    
    @IBOutlet weak var dimensionLabel: UILabel!
    
    func update(with dimension: CGSize, isSelected: Bool) {
        dimensionLabel.text = "\(Int(dimension.width))x\(Int(dimension.height))"
        dimensionLabel.textColor = isSelected ? UIColor.white : UIColor.AGTextGray
        dimensionLabel.backgroundColor = isSelected ? UIColor.AGBlue : UIColor.white
        dimensionLabel.layer.borderColor = isSelected ? UIColor.AGBlue.cgColor : UIColor.AGGray.cgColor
    }
}

extension UIColor {
    static var AGTextGray: UIColor {
        return UIColor(red: 102.0 / 255.0, green: 102.0 / 255.0, blue: 102.0 / 255.0, alpha: 1)
    }
    
    static var AGGray: UIColor {
        return UIColor(red: 204.0 / 255.0, green: 204.0 / 255.0, blue: 204.0 / 255.0, alpha: 1)
    }
    
    static var AGBlue: UIColor {
        return UIColor(red: 0, green: 106.0 / 255.0, blue: 216.0 / 255.0, alpha: 1)
    }
}
