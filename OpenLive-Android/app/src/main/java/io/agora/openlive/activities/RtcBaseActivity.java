package io.agora.openlive.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;

import io.agora.openlive.Constants;
import io.agora.openlive.R;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.CameraCapturerConfiguration;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public abstract class RtcBaseActivity extends BaseActivity {

    private IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserOffline(final int uid, int reason) {
            RtcBaseActivity.this.onUserOffline(uid, reason);
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            RtcBaseActivity.this.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
        }

        @Override
        public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
            RtcBaseActivity.this.onLocalVideoStats(stats);
        }

        @Override
        public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
            RtcBaseActivity.this.onRtcStats(stats);
        }

        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            RtcBaseActivity.this.onNetworkQuality(uid, txQuality, rxQuality);
        }

        @Override
        public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
            RtcBaseActivity.this.onRemoteVideoStats(stats);
        }

        @Override
        public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
            RtcBaseActivity.this.onRemoteAudioStats(stats);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRtcEventHandler(mIRtcEngineEventHandler);
        joinChannel();
    }

    private void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);
        rtcEngine().setParameters("{\"engine.video.enable_hw_encoder\":\"false\"}");
        rtcEngine().setParameters("{\"rtc.camera_rotation\":0}");
    }

    protected void joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }

        // Sets the channel profile of the Agora RtcEngine.
        // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
        rtcEngine().setChannelProfile(io.agora.rtc2.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcEngine().setCameraCapturerConfiguration(new CameraCapturerConfiguration(CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_REAR));
        rtcEngine().enableVideo();
        rtcEngine().enableAudio();
        configVideo();

        int role = getIntent().getIntExtra(
                io.agora.openlive.Constants.KEY_CLIENT_ROLE,
                io.agora.rtc2.Constants.CLIENT_ROLE_AUDIENCE);
        rtcEngine().setClientRole(role);
        rtcEngine().joinChannel(token, config().getChannelName(), "", 0);
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView
        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()].getValue(),
                            0
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()].getValue(),
                            uid
                    )
            );
        }
        return surface;
    }

    protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            rtcEngine().setupLocalVideo(null);
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeRtcEventHandler(mIRtcEngineEventHandler);
        rtcEngine().leaveChannel();
    }

    public void onUserOffline(final int uid, int reason) {

    }

    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {

    }

    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {

    }

    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {

    }

    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

    }
}
