package io.agora.openlive.model;

import io.agora.rtc.IRtcEngineEventHandler;

public interface AGEventHandler {
    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onUserJoined(int uid, int elapsed);

    void onLastmileQuality(int quality);

    void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result);

    void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats);

    void onRtcStats(IRtcEngineEventHandler.RtcStats stats);

    void onNetworkQuality(int uid, int txQuality, int rxQuality);

    void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats);

    void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats);

}
