package io.agora.openlive.rtc;

import io.agora.rtc.video.VideoEncoderConfiguration;

public class EngineConfig {
    private static final int DEFAULT_UID = 0;
    private static final VideoEncoderConfiguration.VideoDimensions DEFAULT_DIMEN =
            VideoEncoderConfiguration.VD_640x360;

    private int mUid = DEFAULT_UID;
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension = DEFAULT_DIMEN;
    private String mChannelName;

    public int getUid() {
        return mUid;
    }

    public void setUid(int mUid) {
        this.mUid = mUid;
    }

    public VideoEncoderConfiguration.VideoDimensions getVideoDimension() {
        return mVideoDimension;
    }

    public void setVideoDimension(VideoEncoderConfiguration.VideoDimensions mVideoDimension) {
        this.mVideoDimension = mVideoDimension;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public void setChannelName(String mChannel) {
        this.mChannelName = mChannel;
    }
}
