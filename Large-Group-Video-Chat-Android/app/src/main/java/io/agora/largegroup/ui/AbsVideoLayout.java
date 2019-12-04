package io.agora.largegroup.ui;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.agora.largegroup.AgoraApplication;

public abstract class AbsVideoLayout extends RelativeLayout {
    public AbsVideoLayout(Context context) {
        super(context);
    }

    public AbsVideoLayout(Context context, ViewGroup container,
                          AgoraApplication application) {
        this(context);
        onCreated(context, container, application);
    }

    public abstract void onCreated(Context context, ViewGroup container,
                                   AgoraApplication application);

    public abstract void onDestroy();

    public abstract void addUser(int uid, SurfaceView surface, boolean isLocal);

    public abstract void removeUser(int uid, boolean isLocal);

    public abstract void onStats();
}
