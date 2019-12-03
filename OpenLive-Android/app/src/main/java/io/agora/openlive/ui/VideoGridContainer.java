package io.agora.openlive.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.R;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.stats.StatsManager;

public class VideoGridContainer extends RelativeLayout implements Runnable {
    private static final int MAX_USER = 4;
    private static final int STATS_REFRESH_INTERVAL = 2000;
    private static final int STAT_LEFT_MARGIN = 34;
    private static final int STAT_TEXT_SIZE = 10;

    private SparseArray<ViewGroup> mUserViewList = new SparseArray<>(MAX_USER);
    private List<Integer> mUidList = new ArrayList<>(MAX_USER);
    private StatsManager mStatsManager;
    private Handler mHandler;
    private int mStatMarginBottom;

    public VideoGridContainer(Context context) {
        super(context);
        init();
    }

    public VideoGridContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoGridContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.live_room_bg);
        mStatMarginBottom = getResources().getDimensionPixelSize(
                R.dimen.live_stat_margin_bottom);
        mHandler = new Handler(getContext().getMainLooper());
    }

    public void setStatsManager(StatsManager manager) {
        mStatsManager = manager;
    }

    public void addUserVideoSurface(int uid, SurfaceView surface, boolean isLocal) {
        if (surface == null) {
            return;
        }

        int id = -1;
        if (isLocal) {
            if (mUidList.contains(0)) {
                mUidList.remove((Integer) 0);
                mUserViewList.remove(0);
            }

            if (mUidList.size() == MAX_USER) {
                mUidList.remove(0);
                mUserViewList.remove(0);
            }
            id = 0;
        } else {
            if (mUidList.contains(uid)) {
                mUidList.remove((Integer) uid);
                mUserViewList.remove(uid);
            }

            if (mUidList.size() < MAX_USER) {
                id = uid;
            }
        }

        if (id == 0) mUidList.add(0, uid);
        else mUidList.add(uid);

        if (id != -1) {
            mUserViewList.append(uid, createVideoView(surface));

            if (mStatsManager != null) {
                mStatsManager.addUserStats(uid, isLocal);
                if (mStatsManager.isEnabled()) {
                    mHandler.removeCallbacks(this);
                    mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
                }
            }

            requestGridLayout();
        }
    }

    private ViewGroup createVideoView(SurfaceView surface) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setId(surface.hashCode());

        RelativeLayout.LayoutParams videoLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(surface, videoLayoutParams);

        TextView text = new TextView(getContext());
        text.setId(layout.hashCode());
        RelativeLayout.LayoutParams textParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        textParams.bottomMargin = mStatMarginBottom;
        textParams.leftMargin = STAT_LEFT_MARGIN;
        text.setTextColor(Color.GREEN);
        text.setTextSize(STAT_TEXT_SIZE);

        layout.addView(text, textParams);
        return layout;
    }

    public void removeUserVideo(int uid, boolean isLocal) {
        if (isLocal && mUidList.contains(0)) {
            mUidList.remove((Integer) 0);
            mUserViewList.remove(0);
        } else if (mUidList.contains(uid)) {
            mUidList.remove((Integer) uid);
            mUserViewList.remove(uid);
        }

        mStatsManager.removeUserStats(uid);
        requestGridLayout();

        if (getChildCount() == 0) {
            mHandler.removeCallbacks(this);
        }
    }

    private void requestGridLayout() {
        removeAllViews();
        layout(mUidList.size());
    }

    private void layout(int size) {
        RelativeLayout.LayoutParams[] params = getParams(size);
        for (int i = 0; i < size; i++) {
            addView(mUserViewList.get(mUidList.get(i)), params[i]);
        }
    }

    private RelativeLayout.LayoutParams[] getParams(int size) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        RelativeLayout.LayoutParams[] array =
                new RelativeLayout.LayoutParams[size];

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                array[0] = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 1) {
                array[1] = new RelativeLayout.LayoutParams(width, height / 2);
                array[0].height = array[1].height;
                array[1].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 2) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, height / 2);
                array[i - 1].width = array[i].width;
                array[i].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(i - 1)).getId());
                array[i].addRule(RelativeLayout.ALIGN_TOP, mUserViewList.get(mUidList.get(i - 1)).getId());
            } else if (i == 3) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, height / 2);
                array[0].width = width / 2;
                array[1].addRule(RelativeLayout.BELOW, 0);
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                array[1].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.RIGHT_OF, 0);
                array[2].addRule(RelativeLayout.ALIGN_TOP, 0);
                array[2].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[3].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(1)).getId());
                array[3].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(2)).getId());
            }
        }

        return array;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllVideo();
    }

    private void clearAllVideo() {
        removeAllViews();
        mUserViewList.clear();
        mUidList.clear();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (mStatsManager != null && mStatsManager.isEnabled()) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                RelativeLayout layout = (RelativeLayout) getChildAt(i);
                TextView text = layout.findViewById(layout.hashCode());
                if (text != null) {
                    StatsData data = mStatsManager.getStatsData(mUidList.get(i));
                    String info = data != null ? data.toString() : null;
                    if (info != null) text.setText(info);
                }
            }

            mHandler.postDelayed(this, STATS_REFRESH_INTERVAL);
        }
    }
}
