package io.agora.openlive.activities;

import android.os.Bundle;
import android.view.SurfaceView;

import java.util.Locale;

import io.agora.openlive.Constants;
import io.agora.openlive.rtc.EventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class RtcBaseActivity extends BaseActivity implements EventHandler {
    private static final String PRIVATE_PARAMETER_CODEC_TYPE_FORMAT = "{\"che.video.h264.hwenc\":%d}";
    private static final String PRIVATE_PARAMETER_FACE_DETECTION_FORMAT = "{\"che.video.camera.face_detection\":%b}";
    private static final String PRIVATE_PARAMETER_PREFER_BITRATE_FORMAT = "{\"rtc.video.custom_profile\":{\"minBitrate\":%d}}";
    private static final String PRIVATE_PARAMETER_FEC_OUTSIDE_BW_RATIO_FORMAT = "{\"che.video.fec_outside_bw_ratio\":%d}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRtcEventHandler(this);
        configVideo();
        joinChannel();
    }

    private void configVideo() {
        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                io.agora.openlive.Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                Constants.FRAME_RATE_OPTIONS[config().getFrameRateIndex()],
                config().getTargetBitrate(),
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ));
    }

    private void joinChannel() {
        preparePrivateParameters();
        rtcEngine().joinChannel(null, config().getChannelName(), "", 0);
    }

    private void preparePrivateParameters() {
        Locale locale = Locale.getDefault();
        rtcEngine().setParameters(String.format(locale, PRIVATE_PARAMETER_CODEC_TYPE_FORMAT, config().getCodecType()));
        rtcEngine().setParameters(String.format(locale, PRIVATE_PARAMETER_FACE_DETECTION_FORMAT, config().isUseFaceDetection()));
        rtcEngine().setParameters(String.format(locale, PRIVATE_PARAMETER_PREFER_BITRATE_FORMAT, config().getMinBitrate()));
        rtcEngine().setParameters(String.format(locale, PRIVATE_PARAMETER_FEC_OUTSIDE_BW_RATIO_FORMAT, config().getFecOutsideBwRatio()));
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid));
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
