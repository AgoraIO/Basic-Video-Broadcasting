package io.agora.openlive.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class VideoGridContainer extends RelativeLayout {
    private static final int MAX_USER = 4;

    private SparseArray<SurfaceView> mUserViewList = new SparseArray<>(MAX_USER);
    private List<Integer> mUidList = new ArrayList<>(MAX_USER);


    public VideoGridContainer(Context context) {
        super(context);
    }

    public VideoGridContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoGridContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addUser(int uid, SurfaceView surfaceView, boolean isLocal) {
        if (surfaceView == null) {
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

        surfaceView.setId(surfaceView.hashCode());
        mUserViewList.append(uid, surfaceView);

        requestGridLayout();
    }

    private void removeAllVideo() {
        removeAllViews();
        mUserViewList.clear();
        mUidList.clear();
    }

    public void removeUser(int uid, boolean isLocal) {
        if (isLocal && mUidList.contains(0)) {
            mUidList.remove((Integer) 0);
            mUserViewList.remove(0);
        } else if (mUidList.contains(uid)) {
            mUidList.remove((Integer) uid);
            mUserViewList.remove(uid);
        }
        requestGridLayout();
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

        SurfaceView view;

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                array[i] = new RelativeLayout.LayoutParams(width, height);
                array[i].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[i].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 1) {
                array[i] = new RelativeLayout.LayoutParams(width, height / 2);
                array[i - 1].height = array[i].height;
                array[i].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(i - 1)).getId());
                array[i].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 2) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, height / 2);
                array[i - 1].width = array[i].width;
                view = mUserViewList.get(mUidList.get(i - 1));
                array[i].addRule(RelativeLayout.RIGHT_OF, view.getId());
                array[i].addRule(RelativeLayout.ALIGN_TOP, view.getId());
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
        removeAllVideo();
    }
}
