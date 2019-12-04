package io.agora.largegroup.ui;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;

import io.agora.largegroup.AgoraApplication;

public class GridVideoLayout extends AbsVideoLayout {
    public GridVideoLayout(Context context, ViewGroup container, AgoraApplication application) {
        super(context, container, application);
    }

    public GridVideoLayout(Context context) {
        super(context);
    }

    @Override
    public void onCreated(Context context, ViewGroup container, AgoraApplication application) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void addUser(int uid, SurfaceView surface, boolean isLocal) {

    }

    @Override
    public void removeUser(int uid, boolean isLocal) {

    }

    @Override
    public void onStats() {

    }
}
