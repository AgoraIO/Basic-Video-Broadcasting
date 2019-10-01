package io.agora.openlive.stats;

public class StatsData {
    private long uid;
    private int width;
    private int height;
    private int framerate;
    private String recvQuality;
    private String sendQuality;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public String getRecvQuality() {
        return recvQuality;
    }

    public void setRecvQuality(String recvQuality) {
        this.recvQuality = recvQuality;
    }

    public String getSendQuality() {
        return sendQuality;
    }

    public void setSendQuality(String sendQuality) {
        this.sendQuality = sendQuality;
    }
}
