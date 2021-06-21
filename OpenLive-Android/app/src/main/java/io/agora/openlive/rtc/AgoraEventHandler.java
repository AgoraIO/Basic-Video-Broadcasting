package io.agora.openlive.rtc;

import android.util.Log;

import java.util.ArrayList;

import io.agora.rtc2.IRtcEngineEventHandler;

public class AgoraEventHandler extends IRtcEngineEventHandler {
    private static final String TAG = "AgoraEventHandler";

    private ArrayList<IRtcEngineEventHandler> mHandler = new ArrayList<>();

    public void addHandler(IRtcEngineEventHandler handler) {
        mHandler.add(handler);
    }

    public void removeHandler(IRtcEngineEventHandler handler) {
        mHandler.remove(handler);
    }

    @Override
    public void onError(int err) {
        super.onError(err);
        Log.d(TAG, "onError() called with: err = [" + err + "]");
    }

    @Override
    public void onWarning(int warn) {
        super.onWarning(warn);
        Log.d(TAG, "onWarning() called with: warn = [" + warn + "]");
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess() called with: channel = [" + channel + "], uid = [" + uid + "], elapsed = [" + elapsed + "]");
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onJoinChannelSuccess(channel, uid, elapsed);
        }
    }

    @Override
    public void onLeaveChannel(RtcStats stats) {
        Log.d(TAG, "onLeaveChannel() called with: stats = [" + stats + "]");
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onLeaveChannel(stats);
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
        }
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined() called with: uid = [" + uid + "], elapsed = [" + elapsed + "]");
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onUserJoined(uid, elapsed);
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline() called with: uid = [" + uid + "], reason = [" + reason + "]");
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onUserOffline(uid, reason);
        }
    }

    @Override
    public void onLocalAudioStateChanged(LOCAL_AUDIO_STREAM_STATE state, LOCAL_AUDIO_STREAM_ERROR error) {
        super.onLocalAudioStateChanged(state, error);
        Log.d(TAG, "onLocalAudioStateChanged() called with: state = [" + state + "], error = [" + error + "]");
    }

    @Override
    public void onLocalVideoStateChanged(int state, int error) {
        super.onLocalVideoStateChanged(state, error);
        Log.d(TAG, "onLocalVideoStateChanged() called with: state = [" + state + "], error = [" + error + "]");
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onLocalVideoStats(stats);
        }
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onRtcStats(stats);
        }
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onNetworkQuality(uid, txQuality, rxQuality);
        }
    }

    @Override
    public void onRemoteAudioStateChanged(int uid, REMOTE_AUDIO_STATE state, REMOTE_AUDIO_STATE_REASON reason, int elapsed) {
        super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
        Log.d(TAG, "onRemoteAudioStateChanged() called with: uid = [" + uid + "], state = [" + state + "], reason = [" + reason + "], elapsed = [" + elapsed + "]");
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
        Log.d(TAG, "onRemoteVideoStateChanged() called with: uid = [" + uid + "], state = [" + state + "], reason = [" + reason + "], elapsed = [" + elapsed + "]");
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onRemoteVideoStats(stats);
        }
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onRemoteAudioStats(stats);
        }
    }

    @Override
    public void onLastmileQuality(int quality) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onLastmileQuality(quality);
        }
    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {
        for (IRtcEngineEventHandler handler : mHandler) {
            handler.onLastmileProbeResult(result);
        }
    }
}
