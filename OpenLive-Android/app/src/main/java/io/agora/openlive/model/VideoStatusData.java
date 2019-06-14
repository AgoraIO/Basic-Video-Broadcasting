package io.agora.openlive.model;

import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.agora.openlive.R;
import io.agora.rtc.Constants;

public class VideoStatusData {
    public static final int DEFAULT_STATUS = 0;
    public static final int VIDEO_MUTED = 1;
    public static final int AUDIO_MUTED = VIDEO_MUTED << 1;
    public static final int DEFAULT_VOLUME = 0;

    public VideoStatusData(int uid, SurfaceView view, int status, int volume) {
        this.mUid = uid;
        this.mView = view;
        this.mStatus = status;
        this.mVolume = volume;
    }


    public VideoStatusData(){

    }

    public int mUid;
    public SurfaceView mView;
    public int mStatus;
    public int mVolume;
    public boolean isLocalUid = false;

    public boolean isLocalUid() {
        return isLocalUid;
    }

    public void setLocalUid(boolean localUid) {
        isLocalUid = localUid;
    }

    public String remoteResolutionInfo = null ;

    public String remoteVideoDelayInfo= null ;

    public String remoteAudioDelayJitterInfo = null ;

    public String remoteAudioLostQualityInfo= null ;

    public String localResolutionInfo= null ;

    public String localLastmileDelayInfo= null ;

    public String localVideoSendRecvInfo= null ;

    public String localAudioSendRecvInfo= null ;

    public String localCpuAppTotalInfo = null ;

    public String sendRecvQualityInfo = null ;

    public String localSendRecvLostInfo= null ;


    public String getRemoteResolutionInfo() {
        return remoteResolutionInfo;
    }

    public String getRemoteVideoDelayInfo() {
        return remoteVideoDelayInfo;
    }

    public String getRemoteAudioDelayJitterInfo() {
        return remoteAudioDelayJitterInfo;
    }

    public String getRemoteAudioLostQualityInfo() {
        return remoteAudioLostQualityInfo;
    }

    public String getLocalResolutionInfo() {
        return localResolutionInfo;
    }

    public String getLocalLastmileDelayInfo() {
        return localLastmileDelayInfo;
    }

    public String getLocalVideoSendRecvInfo() {
        return localVideoSendRecvInfo;
    }

    public String getLocalAudioSendRecvInfo() {
        return localAudioSendRecvInfo;
    }

    public String getLocalCpuAppTotalInfo() {
        return localCpuAppTotalInfo;
    }

    public String getSendRecvQualityInfo() {
        return sendRecvQualityInfo;
    }

    public String getLocalSendRecvLostInfo() {
        return localSendRecvLostInfo;
    }


    public void setLocalLastmileDelayInfo(int localLastmileDelayInfo) {
        this.localLastmileDelayInfo = "Lastmile Delay:"+localLastmileDelayInfo+"ms";
    }

    public void setLocalResolutionInfo(int width, int height, int sentFrameRate) {
        this.localResolutionInfo = width+"×"+height +"  "+sentFrameRate+"fps";
    }

    public void setLocalVideoSendRecvInfo(int txVideoKBitRate, int rxVideoKBitRate) {
        this.localVideoSendRecvInfo = "Video Send/Recv:"+txVideoKBitRate+"kbps/"+rxVideoKBitRate+"kbps";
    }

    public void setLocalAudioSendRecvInfo(int txAudioKBitRate, int rxAudioKBitRate) {
        this.localAudioSendRecvInfo = "Auido Send/Recv:"+txAudioKBitRate+"kbps/"+rxAudioKBitRate+"kbps";
    }

    public void setLocalCpuAppTotalInfo(double cpuAppUsage, double cpuTotalUsage) {
        this.localCpuAppTotalInfo = "CPU:App/Total:"+cpuAppUsage*100+"%/"+cpuTotalUsage*100+"%";
    }

    public void setLocalSendRecvLostInfo(int txPacketLossRate, int rxPacketLossRate) {
        this.localSendRecvLostInfo = "Send/Recv Lost:"+txPacketLossRate+"%/"+rxPacketLossRate+"%";
    }

    public void setSendRecvQualityInfo(int txQuality, int rxQuality) {
        String txQualityString = translateQualityLevelInfo(txQuality);
        String rxQualityString = translateQualityLevelInfo(rxQuality);
        this.sendRecvQualityInfo = "Send/Recv Quality:"+txQualityString+"/"+rxQualityString;
    }

    public void setRemoteResolutionInfo(int width, int height, int decoderOutputFrameRate) {
        this.remoteResolutionInfo = width+"×"+height +"  "+decoderOutputFrameRate+"fps";
    }

    public void setRemoteAudioDelayJitterInfo(int networkTransportDelay, int jitterBufferDelay) {
        this.remoteAudioDelayJitterInfo =  "Audio Net Delay/Jitter:"+networkTransportDelay+"ms/"+jitterBufferDelay+"ms";
    }

    public void setRemoteAudioLostQualityInfo(int audioLossRate, int quality) {
        String qualityString = translateQualityLevelInfo(quality);
        this.remoteAudioLostQualityInfo = "Audio Lost/Quality:"+audioLossRate+"%/"+qualityString;
    }

    public void setRemoteVideoDelayInfo(int remoteVideoDelayInfo) {
        this.remoteVideoDelayInfo = "Video Delay:"+remoteVideoDelayInfo+"ms";
    }

    private static String translateQualityLevelInfo(int quality) {
        String qualityLabel;
        switch (quality) {
            case Constants.QUALITY_EXCELLENT:
                qualityLabel = "excellent";
                break;
            case Constants.QUALITY_GOOD:
                qualityLabel = "good";
                break;
            case Constants.QUALITY_POOR:
                qualityLabel = "poor";
                break;
            case Constants.QUALITY_BAD:
                qualityLabel = "bad";
                break;
            case Constants.QUALITY_VBAD:
                qualityLabel = "vbad";
                break;
            case Constants.QUALITY_DOWN:
                qualityLabel = "down";
                break;
            case Constants.QUALITY_UNKNOWN:
            default:
                qualityLabel = "unknown";
                break;
        }

        return qualityLabel;
    }

    @Override
    public String toString() {
        return "VideoStatusData{" +
                "mUid=" + (mUid & 0xFFFFFFFFL) +
                ", mView=" + mView +
                ", mStatus=" + mStatus +
                ", mVolume=" + mVolume +
                ", isLocalUid=" + isLocalUid +
                ", remoteResolutionInfo='" + remoteResolutionInfo + '\'' +
                ", remoteVideoDelayInfo='" + remoteVideoDelayInfo + '\'' +
                ", remoteAudioDelayJitterInfo='" + remoteAudioDelayJitterInfo + '\'' +
                ", remoteAudioLostQualityInfo='" + remoteAudioLostQualityInfo + '\'' +
                ", localResolutionInfo='" + localResolutionInfo + '\'' +
                ", localLastmileDelayInfo='" + localLastmileDelayInfo + '\'' +
                ", localVideoSendRecvInfo='" + localVideoSendRecvInfo + '\'' +
                ", localAudioSendRecvInfo='" + localAudioSendRecvInfo + '\'' +
                ", localCpuAppTotalInfo='" + localCpuAppTotalInfo + '\'' +
                ", sendRecvQualityInfo='" + sendRecvQualityInfo + '\'' +
                ", localSendRecvLostInfo='" + localSendRecvLostInfo + '\'' +
                '}';
    }
}
