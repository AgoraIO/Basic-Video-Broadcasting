package io.agora.openlive.stats;

import java.util.Locale;

public class RemoteStatsData extends StatsData {
    private static final String FORMAT = "Remote(%d)\n\n" +
            "%dx%d %dfps\n" +
            "Quality tx/rx: %s/%s\n" +
            "Video delay: %d ms\n" +
            "Audio net delay/jitter: %dms/%dms\n" +
            "Audio loss/quality: %d%%/%s";

    private int videoDelay;
    private int audioNetDelay;
    private int audioNetJitter;
    private int audioLoss;
    private String audioQuality;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), FORMAT,
                getUid(),
                getWidth(), getHeight(), getFramerate(),
                getSendQuality(), getRecvQuality(),
                getVideoDelay(),
                getAudioNetDelay(), getAudioNetJitter(),
                getAudioLoss(), getAudioQuality());
    }

    public static String getFORMAT() {
        return FORMAT;
    }

    public int getVideoDelay() {
        return videoDelay;
    }

    public void setVideoDelay(int videoDelay) {
        this.videoDelay = videoDelay;
    }

    public int getAudioNetDelay() {
        return audioNetDelay;
    }

    public void setAudioNetDelay(int audioNetDelay) {
        this.audioNetDelay = audioNetDelay;
    }

    public int getAudioNetJitter() {
        return audioNetJitter;
    }

    public void setAudioNetJitter(int audioNetJitter) {
        this.audioNetJitter = audioNetJitter;
    }

    public int getAudioLoss() {
        return audioLoss;
    }

    public void setAudioLoss(int audioLoss) {
        this.audioLoss = audioLoss;
    }

    public String getAudioQuality() {
        return audioQuality;
    }

    public void setAudioQuality(String audioQuality) {
        this.audioQuality = audioQuality;
    }
}
