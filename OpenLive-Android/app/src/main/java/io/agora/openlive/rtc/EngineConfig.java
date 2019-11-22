package io.agora.openlive.rtc;

import io.agora.openlive.Constants;

public class EngineConfig {
    // private static final int DEFAULT_UID = 0;
    // private int mUid = DEFAULT_UID;

    private String mChannelName;
    private boolean mShowVideoStats;
    private int mDimenIndex = Constants.DEFAULT_PROFILE_IDX;
    private int mFrameRateIndex = 0;
    private int mCodecType = -1;
    private boolean mUseFaceDetection = true;
    private int mMinBitrate = 200;
    private int mFecOutsideBwRatio = 50;
    private int mTargetBitrate = 400;

    private boolean mDebugMode;

    public int getFrameRateIndex() {
        return mFrameRateIndex;
    }

    public void setFrameRateIndex(int mFrameRateIndex) {
        this.mFrameRateIndex = mFrameRateIndex;
    }

    public int getCodecType() {
        return mCodecType;
    }

    public void setCodecType(int mCodecType) {
        this.mCodecType = mCodecType;
    }

    public boolean isUseFaceDetection() {
        return mUseFaceDetection;
    }

    public void setUseFaceDetection(boolean mUseFaceDetection) {
        this.mUseFaceDetection = mUseFaceDetection;
    }

    public int getTargetBitrate() {
        return mTargetBitrate;
    }

    public void setTargetBitrate(int mTargetBitrate) {
        this.mTargetBitrate = mTargetBitrate;
    }

    public int getMinBitrate() {
        return mMinBitrate;
    }

    public void setMinBitrate(int minBitrate) {
        this.mMinBitrate = minBitrate;
    }

    public int getFecOutsideBwRatio() {
        return mFecOutsideBwRatio;
    }

    public void setFecOutsideBwRatio(int fecOutsideBwRatio) {
        this.mFecOutsideBwRatio = fecOutsideBwRatio;
    }

    public int getVideoDimenIndex() {
        return mDimenIndex;
    }

    public void setVideoDimenIndex(int index) {
        mDimenIndex = index;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String mChannel) {
        this.mChannelName = mChannel;
    }

    public boolean ifShowVideoStats() {
        return mShowVideoStats;
    }

    public void setIfShowVideoStats(boolean show) {
        mShowVideoStats = show;
    }

    public boolean isDebugMode() {
        return mDebugMode;
    }

    public void setDebugMode(boolean mDebugMode) {
        this.mDebugMode = mDebugMode;
    }
}
