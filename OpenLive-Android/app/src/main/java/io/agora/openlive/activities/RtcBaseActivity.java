package io.agora.openlive.activities;

import android.os.Bundle;
import android.view.SurfaceView;
import android.text.TextUtils;

import io.agora.openlive.Constants;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.rtc.RtcVideoConsumer;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.mediaio.IVideoSource;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.openlive.R;

import static io.agora.rtc.Constants.VIDEO_MIRROR_MODE_AUTO;
import static io.agora.rtc.Constants.VIDEO_MIRROR_MODE_DISABLED;

public abstract class RtcBaseActivity extends BaseActivity implements EventHandler {



    private static final Integer LOW_VIDEO_STREAM = 1;  //    视频小流，即低分辨率、低码率视频流。
    private static final Integer HIGH_VIDEO_STREAM = 0; //    视频大流，即高分辨率、高码率视频流。
    private RtcVideoConsumer rtcVideoConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rtcVideoConsumer = new RtcVideoConsumer();
        registerRtcEventHandler(this);        
//        joinChannel();
    }

    private void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setRemoteDefaultVideoStreamType(LOW_VIDEO_STREAM);
        rtcEngine().enableDualStreamMode(true);
        rtcEngine().setVideoEncoderConfiguration(configuration);
    }

    private void joinChannel() {
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
        rtcEngine().setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcEngine().enableVideo();
        rtcEngine().enableLocalVideo(false);
//        rtcEngine().setVideoSource(getVideoSource());
        configVideo();
        rtcEngine().joinChannel(token, config().getChannelName(), "", 0);
    }

    private IVideoSource getVideoSource(){
        return rtcVideoConsumer;
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView

        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());

        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_FIT,
                            0,
                            VIDEO_MIRROR_MODE_AUTO
                    )
            );
        } else {
            surface.setZOrderMediaOverlay(true);
            surface.setZOrderOnTop(true);
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            VIDEO_MIRROR_MODE_DISABLED
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
        removeRtcEventHandler(this);
        rtcEngine().leaveChannel();
    }
}
