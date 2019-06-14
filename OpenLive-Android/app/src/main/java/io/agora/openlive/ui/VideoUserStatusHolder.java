package io.agora.openlive.ui;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.agora.openlive.R;

public class VideoUserStatusHolder extends RecyclerView.ViewHolder {
    public  RelativeLayout remoteUserControl=null;
    public  TextView remoteResolutionInfo;
    public  TextView remoteSendRecvQualityInfo;
    public  TextView removeVideoDelayInfo;
    public  TextView remoteAudioDelayJitterInfo;
    public  TextView remoteAudioLostQualityInfo;
    private boolean isUsed = false;
    public  RelativeLayout localUserControl;
    public  TextView localResolutionInfo;
    public  TextView localVideoSendRecvInfo;
    public  TextView localAudioSendRecvInfo;
    public  TextView localLastmileDelayInfo;
    public  TextView localSendRecvQualityInfo;
    public  TextView localCpuAppTotalInfo;
    public  TextView localSendRecvLostInfo;
    public VideoUserStatusHolder(View v) {
        super(v);

        remoteUserControl = (RelativeLayout) v.findViewById(R.id.remote_user_control_mask);

        remoteResolutionInfo = (TextView) v.findViewById(R.id.remote_resolution_info);

        remoteSendRecvQualityInfo = (TextView) v.findViewById(R.id.remote_send_recv_quality_info);

        removeVideoDelayInfo = (TextView) v.findViewById(R.id.remote_video_delay_info);

        remoteAudioDelayJitterInfo = (TextView) v.findViewById(R.id.remote_audio_delay_jitter_info);

        remoteAudioLostQualityInfo = (TextView) v.findViewById(R.id.remote_audio_lost_quality_info);


        localUserControl = (RelativeLayout) v.findViewById(R.id.local_user_control_mask);

        localResolutionInfo = (TextView) v.findViewById(R.id.local_resolution_info);

        localLastmileDelayInfo = (TextView) v.findViewById(R.id.local_lastmile_delay_info);

        localVideoSendRecvInfo = (TextView) v.findViewById(R.id.local_video_send_recv_info);

        localAudioSendRecvInfo = (TextView) v.findViewById(R.id.local_audio_send_recv_info);

        localCpuAppTotalInfo = (TextView) v.findViewById(R.id.local_cpu_app_total_info);

        localSendRecvQualityInfo = (TextView) v.findViewById(R.id.local_send_recv_quality_info);

        localSendRecvLostInfo = (TextView) v.findViewById(R.id.local_send_recv_lost_info);

    }
    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

}
