package io.agora.openlive;

import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class Constants {
    private static final int BEAUTY_EFFECT_DEFAULT_CONTRAST = BeautyOptions.LIGHTENING_CONTRAST_NORMAL;
    private static final float BEAUTY_EFFECT_DEFAULT_LIGHTNESS = 0.7f;
    private static final float BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = 0.5f;
    private static final float BEAUTY_EFFECT_DEFAULT_REDNESS = 0.1f;

    public static final BeautyOptions DEFAULT_BEAUTY_OPTIONS = new BeautyOptions(
            BEAUTY_EFFECT_DEFAULT_CONTRAST,
            BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
            BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
            BEAUTY_EFFECT_DEFAULT_REDNESS);

    public static VideoEncoderConfiguration.FRAME_RATE[] FRAME_RATE_OPTIONS = {
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
    };

    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[] {
            VideoEncoderConfiguration.VD_320x240,
            new VideoEncoderConfiguration.VideoDimensions(480, 270),
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            new VideoEncoderConfiguration.VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
    };

    public static final String PREF_NAME = "io.agora.openlive";
    public static final int DEFAULT_PROFILE_IDX = 1;
    public static final String PREF_RESOLUTION_IDX = "pref_profile_index";
    public static final String PREF_ENABLE_STATS = "pref_enable_stats";

    public static final String KEY_CLIENT_ROLE = "key_client_role";
}
