package io.agora.largegroup.rtc;

import io.agora.largegroup.Constants;

public class EngineConfig {
    // private static final int DEFAULT_UID = 0;
    // private int mUid = DEFAULT_UID;

    private String mChannelName;
    private boolean mShowVideoStats;
    private int mDimenIndex = Constants.DEFAULT_PROFILE_IDX;


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
}
