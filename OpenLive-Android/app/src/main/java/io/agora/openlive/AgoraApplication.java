package io.agora.openlive;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PrefManager;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class AgoraApplication extends Application {
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    @Override
    public void onCreate() {
        super.onCreate();
        String appid = getString(R.string.private_app_id);
        if (TextUtils.isEmpty(appid)) {
            throw new NullPointerException("please check \"strings_config.xml\"");
        }

        Log.d("AgoraApplication", "RTC Version: " + RtcEngine.getSdkVersion());
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = this;
        config.mAppId = appid;
        config.mEventHandler = mHandler;
        config.mChannelProfile = io.agora.rtc2.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        config.getLogConfig().filePath = FileUtil.initializeLogFile(this);

        try {
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();
    }

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(IRtcEngineEventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(IRtcEngineEventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}
