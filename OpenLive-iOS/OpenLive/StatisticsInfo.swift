//
//  MediaInfo.swift
//  OpenVideoCall
//
//  Created by GongYuhua on 4/11/16.
//  Copyright © 2016 Agora. All rights reserved.
//

import Foundation
import AgoraRtcEngineKit

struct StatisticsInfo {
    struct LocalInfo {
        var stats = AgoraChannelStats()
    }
    
    struct RemoteInfo {
        var videoStats = AgoraRtcRemoteVideoStats()
        var audioStats = AgoraRtcRemoteAudioStats()
    }
    
    enum StatisticsType {
        case local(LocalInfo), remote(RemoteInfo)
        
        var isLocal: Bool {
            switch self {
            case .local:  return true
            case .remote: return false
            }
        }
    }
    
    var dimension = CGSize.zero
    var fps = 0
    
    var txQuality: AgoraNetworkQuality = .unknown
    var rxQuality: AgoraNetworkQuality = .unknown
    
    var type: StatisticsType
    
    init(type: StatisticsType) {
        self.type = type
    }
    
    mutating func updateChannelStats(_ stats: AgoraChannelStats) {
        guard self.type.isLocal else {
            return
        }
        let info = LocalInfo(stats: stats)
        self.type = .local(info)
    }
    
    mutating func updateVideoStats(_ stats: AgoraRtcRemoteVideoStats) {
        switch type {
        case .remote(let info):
            var new = info
            new.videoStats = stats
            self.type = .remote(new)
        default:
            break
        }
    }
    
    mutating func updateAudioStats(_ stats: AgoraRtcRemoteAudioStats) {
        switch type {
        case .remote(let info):
            var new = info
            new.audioStats = stats
            self.type = .remote(new)
        default:
            break
        }
    }
    
    func description() -> String {
        var full: String
        switch type {
        case .local(let info):  full = localDescription(info: info)
        case .remote(let info): full = remoteDescription(info: info)
        }
        return full
    }
    
    func localDescription(info: LocalInfo) -> String {
        let join = "\n"
        
        let dimensionFps = "\(Int(dimension.width))×\(Int(dimension.height)), \(fps)fps"
        let quality = "Send/Recv Quality: \(txQuality.description())/\(rxQuality.description())"
        
        let lastmile = "Lastmile Delay: \(info.stats.lastmileDelay)ms"
        let videoSendRecv = "Video Send/Recv: \(info.stats.txVideoKBitrate)kbps/\(info.stats.rxVideoKBitrate)kbps"
        let audioSendRecv = "Audio Send/Recv: \(info.stats.txAudioKBitrate)kbps/\(info.stats.rxAudioKBitrate)kbps"
        
        let cpu = "CPU: App/Total \(info.stats.cpuAppUsage)%/\(info.stats.cpuTotalUsage)%"
        let sendRecvLoss = "Send/Recv Loss: \(info.stats.txPacketLossRate)%/\(info.stats.rxPacketLossRate)%"
        return dimensionFps + join + lastmile + join + videoSendRecv + join + audioSendRecv + join + cpu + join + quality +  join + sendRecvLoss
    }
    
    func remoteDescription(info: RemoteInfo) -> String {
        let join = "\n"
        
        let dimensionFpsBit = "\(Int(dimension.width))×\(Int(dimension.height)), \(fps)fps, \(info.videoStats.receivedBitrate)kbps"
        let quality = "Send/Recv Quality: \(txQuality.description())/\(rxQuality.description())"
        
        var audioQuality: AgoraNetworkQuality
        if let quality = AgoraNetworkQuality(rawValue: info.audioStats.quality) {
            audioQuality = quality
        } else {
            audioQuality = AgoraNetworkQuality.unknown
        }
        
        let audioNet = "Audio Net Delay/Jitter: \(info.audioStats.networkTransportDelay)ms/\(info.audioStats.jitterBufferDelay)ms)"
        let audioLoss = "Audio Loss/Quality: \(info.audioStats.audioLossRate)% \(audioQuality.description())"
        
        return dimensionFpsBit + join + quality + join + audioNet + join + audioLoss
    }
}

extension CGSize {
    static func defaultDimension() -> CGSize {
        return AgoraVideoDimension640x360
    }
    
    static func validDimensionList() -> [CGSize] {
        return [AgoraVideoDimension160x120,
                AgoraVideoDimension240x180,
                AgoraVideoDimension320x240,
                AgoraVideoDimension640x360,
                AgoraVideoDimension640x480,
                AgoraVideoDimension960x720]
    }
}

extension AgoraVideoFrameRate {
    var description: String {
        switch self {
        case .fps1:    return "1 fps"
        case .fps7:    return "7 fps"
        case .fps10:   return "10 fps"
        case .fps15:   return "15 fps"
        case .fps24:   return "24 fps"
        case .fps30:   return "30 fps"
        default:       return "unsported"
        }
    }
    
    var value: Int {
        switch self {
        case .fps1:    return 1
        case .fps7:    return 7
        case .fps10:   return 10
        case .fps15:   return 15
        case .fps24:   return 24
        case .fps30:   return 30
        default:       return -1
        }
    }
    
    static var defaultValue = AgoraVideoFrameRate.fps15
    
    static func validList() -> [AgoraVideoFrameRate] {
        return [.fps1,
                .fps7,
                .fps10,
                .fps15,
                .fps24,
                .fps30]
    }
}

extension AgoraNetworkQuality {
    func description() -> String {
        switch self {
        case .excellent: return "excellent"
        case .good:      return "good"
        case .poor:      return "poor"
        case .bad:       return "bad"
        case .vBad:      return "very bad"
        case .down:      return "down"
        case .unknown:   return "unknown"
        default:         return "unknown"
        }
    }
}
