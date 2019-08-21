package io.agora.openlive;

import android.app.Application;
import android.content.SharedPreferences;

import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PreferenceManager;
import io.agora.rtc.RtcEngine;

public class AgoraApplication extends Application {
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initEngineConfig();
    }

    private void initEngineConfig() {
        SharedPreferences pref = PreferenceManager.getPreferences(this);
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));
        mGlobalConfig.setIfShowVideoStats(pref.getBoolean(
                Constants.PREF_SHOW_VIDEO_STATISTICS, false));
    }

    public EngineConfig engineConfig() { return mGlobalConfig; }

    public RtcEngine rtcEngine() { return mRtcEngine; }

    public void registerEventHandler(EventHandler handler) { mHandler.addHandler(handler); }

    public void removeEventHandler(EventHandler handler) { mHandler.removeHandler(handler); }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}
