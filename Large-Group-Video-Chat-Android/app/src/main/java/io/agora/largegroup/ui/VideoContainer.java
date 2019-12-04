package io.agora.largegroup.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import io.agora.largegroup.AgoraApplication;
import io.agora.largegroup.R;
import io.agora.largegroup.stats.StatsManager;

public class VideoContainer extends RelativeLayout implements Runnable {
    private static final int MODE_HOST = 1;
    private static final int MODE_GRID = 2;
    private static final int STATS_REFRESH_INTERVAL = 2000;

    private int mCurrentMode = MODE_HOST;
    private AbsVideoLayout mVideoLayout;
    private List<Integer> mUidList = new ArrayList<>();
    private SparseArray<SurfaceView> mSurfaces = new SparseArray<>();
    private AgoraApplication mApplication;
    private StatsManager mStatsManager;
    private Handler mHandler;

    public VideoContainer(Context context) {
        super(context);
    }

    public VideoContainer(Context context, AgoraApplication application) {
        this(context);
        mApplication = application;
        mStatsManager = mApplication.statsManager();
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.live_room_bg);
        initVideoLayout();
        initStat();
    }

    private void initVideoLayout() {
        if (mCurrentMode == MODE_GRID) {
            mVideoLayout = new GridVideoLayout(getContext(), this, mApplication);
        } else {
            mVideoLayout = new HostVideoLayout(getContext(), this, mApplication);
        }
        addView(mVideoLayout);
    }

    private void initStat() {
        if (mApplication.statsManager().isEnabled()) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(this);
        }
    }

    public void addUser(int uid, SurfaceView surface, boolean isLocal) {
        if (!mUidList.contains(uid)) {
            mUidList.add(uid);
            mSurfaces.append(uid, surface);
            mStatsManager.addUserStats(uid, isLocal);
            if (mVideoLayout != null) mVideoLayout.addUser(uid, surface, isLocal);
        }
    }

    public void removeUser(int uid, boolean isLocal) {
        mUidList.remove((Integer) uid);
        mSurfaces.delete(uid);
        mStatsManager.removeUserStats(uid);
        if (mVideoLayout != null) mVideoLayout.removeUser(uid, isLocal);
    }

    public void destroy() {
        if (mVideoLayout != null) {
            mVideoLayout.onDestroy();
            removeView(mVideoLayout);
        }
    }

    public void changeLayout() {
        mVideoLayout.onDestroy();
        mCurrentMode = nextMode();
        initVideoLayout();
    }

    private int nextMode() {
        return (mCurrentMode == MODE_GRID) ? MODE_HOST : mCurrentMode + 1;
    }

    @Override
    public void run() {
        if (mStatsManager != null && mStatsManager.isEnabled()) {
            if (mVideoLayout != null) mVideoLayout.onStats();
            mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
        }
    }
}
