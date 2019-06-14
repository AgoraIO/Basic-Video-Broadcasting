package io.agora.openlive.ui;

import android.view.View;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import io.agora.common.Constant;

import io.agora.openlive.model.VideoStatusData;

/**
 * Created by yong on 2019/6/14.
 */

public class VideoViewAdapterUtil {

    private final static Logger log = LoggerFactory.getLogger(VideoViewAdapterUtil.class);

    public static void updateUIData(VideoUserStatusHolder myHolder, VideoStatusData user, HashMap<Integer, VideoStatusData> mAllUserData) {
        if(mAllUserData==null){
            return;
        }
        VideoStatusData updateVideoStatusData = null;
        for (Integer key : mAllUserData.keySet()) {
            if (key == user.mUid) {
                updateVideoStatusData = mAllUserData.get(key);
                break;
            }
        }
        if(user.mUid == 0){
            updateVideoStatusData = getLocalUserData(mAllUserData);
        }
        if (updateVideoStatusData == null) {
            return;
        }
        if (updateVideoStatusData.isLocalUid()) {
            log.debug("updateVideoStatusData local " + user.mUid + " " +updateVideoStatusData.mUid+ " "+ myHolder + " " + mAllUserData);
            myHolder.localUserControl.setVisibility(View.VISIBLE);
            myHolder.remoteUserControl.setVisibility(View.INVISIBLE);
            myHolder.localResolutionInfo.setText(updateVideoStatusData.getLocalResolutionInfo());
            myHolder.localVideoSendRecvInfo.setText(updateVideoStatusData.getLocalVideoSendRecvInfo());
            myHolder.localAudioSendRecvInfo.setText(updateVideoStatusData.getLocalAudioSendRecvInfo());
            myHolder.localLastmileDelayInfo.setText(updateVideoStatusData.getLocalLastmileDelayInfo());
            myHolder.localSendRecvQualityInfo.setText(updateVideoStatusData.getSendRecvQualityInfo());
            myHolder.localCpuAppTotalInfo.setText(updateVideoStatusData.getLocalCpuAppTotalInfo());
            myHolder.localSendRecvLostInfo.setText(updateVideoStatusData.getLocalSendRecvLostInfo());
        } else {
            log.debug("updateVideoStatusData remote " + user.mUid + " " +updateVideoStatusData.mUid+ " " + myHolder + " " + mAllUserData);
            myHolder.localUserControl.setVisibility(View.INVISIBLE);
            myHolder.remoteUserControl.setVisibility(View.VISIBLE);
            myHolder.remoteResolutionInfo.setText(updateVideoStatusData.getRemoteResolutionInfo());
            myHolder.remoteSendRecvQualityInfo.setText(updateVideoStatusData.getSendRecvQualityInfo());
            myHolder.removeVideoDelayInfo.setText(updateVideoStatusData.getRemoteVideoDelayInfo());
            myHolder.remoteAudioDelayJitterInfo.setText(updateVideoStatusData.getRemoteAudioDelayJitterInfo());
            myHolder.remoteAudioLostQualityInfo.setText(updateVideoStatusData.getRemoteAudioLostQualityInfo());
        }

    }
    public static VideoStatusData getLocalUserData(HashMap<Integer, VideoStatusData> mAllUserData){
        for (Integer key : mAllUserData.keySet()) {
            if(mAllUserData.get(key).isLocalUid()){
                return mAllUserData.get(key);
            }
        }
        return null;
    }
}
