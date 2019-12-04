package io.agora.largegroup.stats;

import java.util.Locale;

public class RemoteStatsData extends StatsData {
    private static final String FORMAT = "Remote\n(%d)\n" +
            "%dx%d, %d kbps\n";

    private int bitrate;

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), FORMAT,
                getUid(), getWidth(), getHeight(), getBitrate());
    }
}
