//
//  VideoViewLayouter.swift
//  OpenLive
//
//  Created by GongYuhua on 6/25/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import UIKit

class VideoViewLayouter {
    
    fileprivate var layoutConstraints = [NSLayoutConstraint]()
    
    func layout(sessions: [VideoSession], fullSession: VideoSession?, inContainer container: UIView) {
        
        guard !sessions.isEmpty else {
            return
        }
        
        NSLayoutConstraint.deactivate(layoutConstraints)
        layoutConstraints.removeAll()
        
        for session in sessions {
            session.hostingView.removeFromSuperview()
        }
        
        if let fullSession = fullSession {
            layoutConstraints.append(contentsOf: layoutFullScreenView(fullSession.hostingView, inContainerView: container))
            let smallViews = viewList(from: sessions, maxCount: 3, ignorSession: fullSession)
            layoutConstraints.append(contentsOf: layoutSmallViews(smallViews, inContainerView: container))
        } else {
            let allViews = viewList(from: sessions, maxCount: 4, ignorSession: nil)
            layoutConstraints.append(contentsOf: layoutGridViews(allViews, inContainerView: container))
        }
        
        if !layoutConstraints.isEmpty {
            NSLayoutConstraint.activate(layoutConstraints)
        }
    }
    
    func responseSession(of gesture: UIGestureRecognizer, inSessions sessions: [VideoSession], inContainerView container: UIView) -> VideoSession? {
        let location = gesture.location(in: container)
        for session in sessions {
            if let view = session.hostingView , view.frame.contains(location) {
                return session
            }
        }
        return nil
    }
}

//MARK: - layouts
private extension VideoViewLayouter {
    func viewList(from sessions: [VideoSession], maxCount: Int, ignorSession: VideoSession?) -> [UIView] {
        var views = [UIView]()
        for session in sessions {
            if session == ignorSession {
                continue
            }
            views.append(session.hostingView)
            if views.count >= maxCount {
                break
            }
        }
        return views
    }
    
    func layoutFullScreenView(_ view: UIView, inContainerView container: UIView) -> [NSLayoutConstraint] {
        container.addSubview(view)
        var layouts = [NSLayoutConstraint]()
        
        let constraintsH = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view]|", options: [], metrics: nil, views: ["view": view])
        let constraintsV = NSLayoutConstraint.constraints(withVisualFormat: "V:|[view]|", options: [], metrics: nil, views: ["view": view])
        layouts.append(contentsOf: constraintsH)
        layouts.append(contentsOf: constraintsV)
        
        return layouts
    }
    
    func layoutSmallViews(_ smallViews: [UIView], inContainerView container: UIView) -> [NSLayoutConstraint] {
        var layouts = [NSLayoutConstraint]()
        var lastView: UIView?
        
        let itemSpace: CGFloat = 5
        
        for view in smallViews {
            container.addSubview(view)
            let viewWidth = NSLayoutConstraint(item: view, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: 100)
            let viewHeight = NSLayoutConstraint(item: view, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: 100)
            
            let viewTop = NSLayoutConstraint(item: view, attribute: .top, relatedBy: .equal, toItem: container, attribute: .top, multiplier: 1, constant: 60 + itemSpace)
            let viewLeft: NSLayoutConstraint
            if let lastView = lastView {
                viewLeft = NSLayoutConstraint(item: view, attribute: .left, relatedBy: .equal, toItem: lastView, attribute: .right, multiplier: 1, constant: itemSpace)
            } else {
                viewLeft = NSLayoutConstraint(item: view, attribute: .left, relatedBy: .equal, toItem: container, attribute: .left, multiplier: 1, constant: itemSpace)
            }
            
            layouts.append(contentsOf: [viewWidth, viewHeight, viewLeft, viewTop])
            lastView = view
        }
        
        return layouts
    }
    
    func layoutGridViews(_ allViews: [UIView], inContainerView container: UIView) -> [NSLayoutConstraint] {
        let viewInset: CGFloat = 0.5
        var layouts = [NSLayoutConstraint]()
        
        switch allViews.count {
        case 0: break
        case 1:
            layouts.append(contentsOf: layoutFullScreenView(allViews.first!, inContainerView: container))
        case 2:
            let firstView = allViews.first!
            let lastView = allViews.last!
            container.addSubview(firstView)
            container.addSubview(lastView)
            
            let h1 = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view]|", options: [], metrics: nil, views: ["view": firstView])
            let h2 = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view]|", options: [], metrics: nil, views: ["view": lastView])
            let v = NSLayoutConstraint.constraints(withVisualFormat: "V:|[view1]-(\(viewInset))-[view2]|", options: [], metrics: nil, views: ["view1": firstView, "view2": lastView])
            let equal = NSLayoutConstraint(item: firstView, attribute: .height, relatedBy: .equal, toItem: lastView, attribute: .height, multiplier: 1, constant: 0)
            layouts.append(contentsOf: h1)
            layouts.append(contentsOf: h2)
            layouts.append(contentsOf: v)
            layouts.append(equal)
        case 3:
            let firstView = allViews.first!
            let secondView = allViews[1]
            let lastView = allViews.last!
            container.addSubview(firstView)
            container.addSubview(secondView)
            container.addSubview(lastView)
            
            let h1 = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": firstView, "view2": secondView])
            let v1 = NSLayoutConstraint.constraints(withVisualFormat: "V:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": firstView, "view2": lastView])
            let left = NSLayoutConstraint(item: lastView, attribute: .left, relatedBy: .equal, toItem: container, attribute: .left, multiplier: 1, constant: 0)
            let top = NSLayoutConstraint(item: secondView, attribute: .top, relatedBy: .equal, toItem: container, attribute: .top, multiplier: 1, constant: 0)
            let equalWidth1 = NSLayoutConstraint(item: firstView, attribute: .width, relatedBy: .equal, toItem: secondView, attribute: .width, multiplier: 1, constant: 0)
            let equalWidth2 = NSLayoutConstraint(item: firstView, attribute: .width, relatedBy: .equal, toItem: lastView, attribute: .width, multiplier: 1, constant: 0)
            let equalHeight1 = NSLayoutConstraint(item: firstView, attribute: .height, relatedBy: .equal, toItem: secondView, attribute: .height, multiplier: 1, constant: 0)
            let equalHeight2 = NSLayoutConstraint(item: firstView, attribute: .height, relatedBy: .equal, toItem: lastView, attribute: .height, multiplier: 1, constant: 0)
            layouts.append(contentsOf: h1)
            layouts.append(contentsOf: v1)
            layouts.append(contentsOf: [left, top, equalWidth1, equalWidth2, equalHeight1, equalHeight2])
        default:
            let firstView = allViews.first!
            let secondView = allViews[1]
            let thirdView = allViews[2]
            let lastView = allViews[3]
            container.addSubview(firstView)
            container.addSubview(secondView)
            container.addSubview(thirdView)
            container.addSubview(lastView)
            
            let h1 = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": firstView, "view2": secondView])
            let h2 = NSLayoutConstraint.constraints(withVisualFormat: "H:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": thirdView, "view2": lastView])
            let v1 = NSLayoutConstraint.constraints(withVisualFormat: "V:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": firstView, "view2": thirdView])
            let v2 = NSLayoutConstraint.constraints(withVisualFormat: "V:|[view1]-\(viewInset)-[view2]|", options: [], metrics: nil, views: ["view1": secondView, "view2": lastView])
            
            let equalWidth1 = NSLayoutConstraint(item: firstView, attribute: .width, relatedBy: .equal, toItem: secondView, attribute: .width, multiplier: 1, constant: 0)
            let equalWidth2 = NSLayoutConstraint(item: firstView, attribute: .width, relatedBy: .equal, toItem: thirdView, attribute: .width, multiplier: 1, constant: 0)
            let equalHeight1 = NSLayoutConstraint(item: firstView, attribute: .height, relatedBy: .equal, toItem: secondView, attribute: .height, multiplier: 1, constant: 0)
            let equalHeight2 = NSLayoutConstraint(item: firstView, attribute: .height, relatedBy: .equal, toItem: thirdView, attribute: .height, multiplier: 1, constant: 0)
            
            layouts.append(contentsOf: h1)
            layouts.append(contentsOf: v1)
            layouts.append(contentsOf: h2)
            layouts.append(contentsOf: v2)
            layouts.append(contentsOf: [equalWidth1, equalWidth2, equalHeight1, equalHeight2])
        }
        
        return layouts
    }
}
