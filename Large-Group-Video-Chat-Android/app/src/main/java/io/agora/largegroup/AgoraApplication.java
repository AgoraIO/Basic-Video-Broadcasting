package io.agora.largegroup;

import android.app.Application;
import android.content.SharedPreferences;

import io.agora.largegroup.rtc.EngineConfig;
import io.agora.largegroup.rtc.AgoraEventHandler;
import io.agora.largegroup.rtc.EventHandler;
import io.agora.largegroup.stats.StatsManager;
import io.agora.largegroup.utils.FileUtil;
import io.agora.largegroup.utils.PrefManager;
import io.agora.rtc.RtcEngine;

public class AgoraApplication extends Application {
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableDualStreamMode(true);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();
    }

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext() );
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);
    }

    public EngineConfig engineConfig() { return mGlobalConfig; }

    public RtcEngine rtcEngine() { return mRtcEngine; }

    public StatsManager statsManager() { return mStatsManager; }

    public void registerEventHandler(EventHandler handler) { mHandler.addHandler(handler); }

    public void removeEventHandler(EventHandler handler) { mHandler.removeHandler(handler); }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}
