package io.agora.common;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;

public class Constant {

    public static final String MEDIA_SDK_VERSION;

    static {
        String sdk = "undefined";
        try {
            sdk = RtcEngine.getSdkVersion();
        } catch (Throwable e) {
        }
        MEDIA_SDK_VERSION = sdk;
    }

    public static boolean BEAUTY_EFFECT_ENABLED = true;

    public static final int BEAUTY_EFFECT_DEFAULT_CONTRAST = 1;
    public static final float BEAUTY_EFFECT_DEFAULT_LIGHTNESS = .7f;
    public static final float BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = .5f;
    public static final float BEAUTY_EFFECT_DEFAULT_REDNESS = .1f;

    public static final BeautyOptions BEAUTY_OPTIONS = new BeautyOptions(BEAUTY_EFFECT_DEFAULT_CONTRAST, BEAUTY_EFFECT_DEFAULT_LIGHTNESS, BEAUTY_EFFECT_DEFAULT_SMOOTHNESS, BEAUTY_EFFECT_DEFAULT_REDNESS);

    public static final float BEAUTY_EFFECT_MAX_LIGHTNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_SMOOTHNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_REDNESS = 1.0f;
}
