package io.agora.largegroup.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.largegroup.AgoraApplication;
import io.agora.largegroup.R;
import io.agora.largegroup.stats.LocalStatsData;
import io.agora.largegroup.stats.RemoteStatsData;
import io.agora.largegroup.stats.StatsManager;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

public class HostVideoLayout extends AbsVideoLayout {
    private static final String TAG = HostVideoLayout.class.getSimpleName();

    private FrameLayout mLocalLayout;
    private FrameLayout mRemoteLayout;
    private int mUid;
    private TextView mLocalStatText;
    private RecyclerView mRecyclerView;
    private RemoteUserRecyclerAdapter mRemoteAdapter;
    private SurfaceView mLocalPreview;
    private SparseArray<SurfaceView> mRemoteUserViews;
    private SparseArray<TextView> mRemoteStatsTexts;

    private RtcEngine mRtcEngine;
    private StatsManager mStatsManager;

    public HostVideoLayout(Context context, ViewGroup container, AgoraApplication application) {
        super(context, container, application);
        mRtcEngine = application.rtcEngine();
        mStatsManager = application.statsManager();
    }

    public HostVideoLayout(Context context) {
        super(context);
    }

    @Override
    public void onCreated(Context context, ViewGroup container, AgoraApplication application) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.host_video_layout, container, true);

        View localOuterLayout = layout.findViewById(R.id.host_video_local_preview_layout);
        View localLayout = inflater.inflate(R.layout.host_video_local_preview_layout,
                (ViewGroup) localOuterLayout, true);
        mLocalLayout = localLayout.findViewById(R.id.host_local_preview_layout);
        mLocalStatText = localLayout.findViewById(R.id.host_local_stats);

        mRemoteLayout = layout.findViewById(R.id.host_video_remote_preview_layout);
        mRemoteUserViews = new SparseArray<>();
        mRecyclerView = new RecyclerView(context);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(manager);
        mRemoteAdapter = new RemoteUserRecyclerAdapter(context);
        mRecyclerView.setAdapter(mRemoteAdapter);
        RemoteScrollListener scrollListener = new RemoteScrollListener();
        mRecyclerView.addOnScrollListener(scrollListener);
        mRemoteLayout.addView(mRecyclerView);

        mRemoteStatsTexts = new SparseArray<>();
    }

    @Override
    public void onDestroy() {
        mRemoteUserViews.clear();

        if (mLocalLayout.getChildCount() > 0) {
            mLocalLayout.removeAllViews();
        }

        mRemoteUserViews.clear();
        mRemoteStatsTexts.clear();
        mRemoteAdapter.notifyDataSetChanged();
    }

    @Override
    public void addUser(int uid, SurfaceView surface, boolean isLocal) {
        if (isLocal && mLocalLayout.getChildCount() == 0) {
            mLocalLayout.addView(surface);
            mLocalPreview = surface;
            mUid = uid;
        } else if (!isLocal) {
            mRemoteUserViews.append(uid, surface);
            mRemoteAdapter.notifyDataSetChanged();
            mRtcEngine.setRemoteVideoStreamType(uid, Constants.VIDEO_STREAM_LOW);
        }
    }

    @Override
    public void removeUser(int uid, boolean isLocal) {
        if (isLocal && mLocalLayout.getChildCount() > 0) {
            mLocalLayout.removeView(mLocalPreview);
        } else if (!isLocal) {
            // Detach the SurfaceView instance from
            // the recycler first.
            mRecyclerView.removeViewAt(mRemoteUserViews.indexOfKey(uid));
            mRemoteUserViews.delete(uid);
            mRemoteStatsTexts.delete(uid);
            mRemoteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStats() {
        LocalStatsData data = (LocalStatsData) mStatsManager.getStatsData(mUid);
        if (mLocalStatText != null && data != null) mLocalStatText.setText(data.toString());

        showRemoteStat();
    }

    private void showRemoteStat() {
        if (mRemoteUserViews == null || mRemoteUserViews.size() == 0) {
            return;
        }

        LinearLayoutManager manager =
                (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int first = manager.findFirstVisibleItemPosition();
        int count = manager.getChildCount();

        for (int i = first; i < count; i++) {
            int uid = mRemoteUserViews.keyAt(i);
            RemoteStatsData data = (RemoteStatsData) mStatsManager.getStatsData(uid);
            if (data != null) {
                TextView textView = mRemoteStatsTexts.get(uid);
                if (textView != null) textView.setText(data.toString());
            }
        }
    }

    private class RemoteUserRecyclerAdapter extends RecyclerView.Adapter {
        private LayoutInflater mInflater;

        RemoteUserRecyclerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.remote_video_list_item, parent, false);
            RemoteUserRecyclerHolder holder = new RemoteUserRecyclerHolder(view);
            holder.statsView = view.findViewById(R.id.remote_video_stat);
            holder.viewLayout = view.findViewById(R.id.remote_video_item);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int pos = holder.getAdapterPosition();
            RemoteUserRecyclerHolder recyclerHolder =
                    (RemoteUserRecyclerHolder) holder;
            int uid = mRemoteUserViews.keyAt(pos);
            mRemoteStatsTexts.append(uid, recyclerHolder.statsView);
            recyclerHolder.viewLayout.addView(mRemoteUserViews.valueAt(pos));
        }

        @Override
        public int getItemCount() {
            return mRemoteUserViews == null ? 0 : mRemoteUserViews.size();
        }
    }

    private class RemoteUserRecyclerHolder extends RecyclerView.ViewHolder {
        TextView statsView;
        FrameLayout viewLayout;
        RemoteUserRecyclerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class RemoteScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager =
                    (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int visibleCount = layoutManager.getChildCount();
            int all = layoutManager.getItemCount();
            for (int i = 0; i < all; i++) {
                int uid = mRemoteUserViews.keyAt(i);
                if (i <= firstVisible && i < firstVisible + visibleCount) {
                    mRtcEngine.muteRemoteVideoStream(uid, false);
                } else {
                    mRtcEngine.muteRemoteVideoStream(uid, true);
                }
            }
        }
    };
}
