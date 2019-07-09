package io.agora.openlive.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;

import io.agora.common.Constant;
import io.agora.openlive.R;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.VideoStatusData;
import io.agora.rtc.AgoraMediaRecorder;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.ISnapshotCallback;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtc.AgoraMediaRecorder;

public class LiveRoomActivity extends BaseActivity implements AGEventHandler {

    private final static Logger log = LoggerFactory.getLogger(LiveRoomActivity.class);

    private GridVideoViewContainer mGridVideoViewContainer;

    private RelativeLayout mSmallVideoViewDock;
    private VideoEncoderConfiguration.VideoDimensions localVideoDimensions = null;
    private HashMap<Integer,VideoStatusData> mAllUserData =  new HashMap<>();
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == Constants.CLIENT_ROLE_BROADCASTER;
    }

    private boolean isBroadcaster() {
        return isBroadcaster(config().mClientRole);
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent i = getIntent();
        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);

        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }

        String roomName = i.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);

        doConfigEngine(cRole);

        mGridVideoViewContainer = (GridVideoViewContainer) findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new VideoViewEventListener() {
            @Override
            public void onItemDoubleClick(View v, Object item) {
                log.debug("onItemDoubleClick " + v + " " + item);

                if (mUidsList.size() < 2) {
                    return;
                }

                if (mViewType == VIEW_TYPE_DEFAULT)
                    switchToSmallVideoView(((VideoStatusData) item).mUid);
                else
                    switchToDefaultVideoView();
            }
        });

        ImageView button1 = (ImageView) findViewById(R.id.btn_1);
        ImageView button2 = (ImageView) findViewById(R.id.btn_2);
        ImageView button3 = (ImageView) findViewById(R.id.btn_3);
        //screenshot btn
        ImageView recordBtn = (ImageView) findViewById(R.id.btn_6);
        //record btn
        ImageView screenshotBtn = (ImageView) findViewById(R.id.btn_5);

        if (isBroadcaster(cRole)) {
            SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0));

            mUidsList.put(0, surfaceV); // get first surface view
            mAllUserData.put(0,new VideoStatusData());
            mGridVideoViewContainer.initViewContainer(getApplicationContext(), 0, mUidsList); // first is now full view
            worker().preview(true, surfaceV, 0);
            broadcasterUI(button1, button2, button3, recordBtn, screenshotBtn);
        } else {
            audienceUI(button1, button2, button3, recordBtn, screenshotBtn);
        }

        worker().joinChannel(roomName, config().mUid);

        TextView textRoomName = (TextView) findViewById(R.id.room_name);
        textRoomName.setText(roomName);
    }

    private void broadcasterUI(final ImageView button1, ImageView button2, ImageView button3, ImageView recordBtn, ImageView screenshotBtn) {
        button1.setTag(true);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                button1.setEnabled(false);
                if (tag != null && (boolean) tag) {
                    doSwitchToBroadcaster(false);
                } else {
                    doSwitchToBroadcaster(true);
                }
            }
        });
        button1.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worker().getRtcEngine().switchCamera();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                boolean flag = true;
                if (tag != null && (boolean) tag) {
                    flag = false;
                }
                worker().getRtcEngine().muteLocalAudioStream(flag);
                ImageView button = (ImageView) v;
                button.setTag(flag);
                if (flag) {
                    button.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);
                } else {
                    button.clearColorFilter();
                }
            }
        });

        //record btn
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                boolean flag = true;
                if (tag != null && (boolean) tag) {
                    flag = false;
                }
                ImageView button = (ImageView) v;
                button.setTag(flag);
                if (flag) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File mp4file = new File(sdcard, "test.mp4");
                    AgoraMediaRecorder.MediaRecorderConfiguration config = new AgoraMediaRecorder.MediaRecorderConfiguration(mp4file.getAbsolutePath(), 1, 3, 120, 0);
                    worker().getMediaRecorder().startRecording(config);
                    button.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);
                } else {
                    worker().getMediaRecorder().stopRecording();
                    button.clearColorFilter();
                }
            }
        });

        //screenshot btn
        screenshotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sdcard = Environment.getExternalStorageDirectory();
                File screenshot = new File(sdcard, "screenshot.jpeg");
                worker().getRtcEngine().takeSnapshot(80, screenshot.getAbsolutePath(), new ISnapshotCallback() {
                    @Override
                    public void onSnapshotTaken(String filePath, int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLongToast("Snapshot taken!");
                            }
                        });
                    }
                });
            }
        });
    }

    private void audienceUI(final ImageView button1, ImageView button2, ImageView button3, ImageView recordBtn, ImageView screenshotBtn) {
        button1.setTag(null);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                button1.setEnabled(false);
                if (tag != null && (boolean) tag) {
                    doSwitchToBroadcaster(false);
                } else {
                    doSwitchToBroadcaster(true);
                }
            }
        });
        button1.clearColorFilter();
        button2.setVisibility(View.GONE);
        button3.setTag(null);
        button3.setVisibility(View.GONE);
        button3.clearColorFilter();
        recordBtn.setVisibility(View.GONE);
        recordBtn.clearColorFilter();
        screenshotBtn.setVisibility((View.GONE));
        screenshotBtn.clearColorFilter();
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        if (prefIndex > ConstantApp.VIDEO_DIMENSIONS.length - 1) {
            prefIndex = ConstantApp.DEFAULT_PROFILE_IDX;
        }
        VideoEncoderConfiguration.VideoDimensions dimension = ConstantApp.VIDEO_DIMENSIONS[prefIndex];
        localVideoDimensions = dimension;
        worker().configEngine(cRole, dimension);
    }

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);

        mUidsList.clear();
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        if (isBroadcaster()) {
            worker().preview(false, null, 0);
        }
    }

    public void onClickClose(View view) {
        finish();
    }

    public void onShowHideClicked(View view) {
        boolean toHide = true;
        if (view.getTag() != null && (boolean) view.getTag()) {
            toHide = false;
        }
        view.setTag(toHide);

        doShowButtons(toHide);
    }

    private void doShowButtons(boolean hide) {
        View topArea = findViewById(R.id.top_area);
        topArea.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        View button1 = findViewById(R.id.btn_1);
        button1.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);

        View button2 = findViewById(R.id.btn_2);
        View button3 = findViewById(R.id.btn_3);
        View button4 = findViewById(R.id.btn_4);
        if (isBroadcaster()) {
            button2.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            button3.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            button4.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        } else {
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
        }
    }

    private FaceBeautificationPopupWindow mFaceBeautificationPopupWindow;

    public void onBtn4Clicked(View view) {
        if (isBroadcaster()) {
            if (mFaceBeautificationPopupWindow == null) {
                mFaceBeautificationPopupWindow = new FaceBeautificationPopupWindow(this.getBaseContext());
            }
        } else {
            return;
        }

        if (!mFaceBeautificationPopupWindow.isShowing()) {
            mFaceBeautificationPopupWindow.show(view, new FaceBeautificationPopupWindow.UserEventHandler() {
                @Override
                public void onFBSwitch(boolean on) {
                    if (on) {
                        Constant.BEAUTY_EFFECT_ENABLED = true;
                        worker().enablePreProcessor();
                    } else {
                        worker().disablePreProcessor();
                        Constant.BEAUTY_EFFECT_ENABLED = false;
                    }
                }

                @Override
                public void onContrastLevelSet(float contrast) {
                    worker().setBeautyEffectParameters(Constant.BEAUTY_OPTIONS.lighteningLevel, Constant.BEAUTY_OPTIONS.smoothnessLevel, Constant.BEAUTY_OPTIONS.rednessLevel);
                }

                @Override
                public void onLightnessSet(float lightness) {
                    worker().setBeautyEffectParameters(lightness, Constant.BEAUTY_OPTIONS.smoothnessLevel, Constant.BEAUTY_OPTIONS.rednessLevel);
                }

                @Override
                public void onSmoothnessSet(float smoothness) {
                    worker().setBeautyEffectParameters(Constant.BEAUTY_OPTIONS.lighteningLevel, smoothness, Constant.BEAUTY_OPTIONS.rednessLevel);
                }

                @Override
                public void onRednessSet(float redness) {
                    worker().setBeautyEffectParameters(Constant.BEAUTY_OPTIONS.lighteningLevel, Constant.BEAUTY_OPTIONS.smoothnessLevel, redness);
                }
            });
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
    }

    private void doSwitchToBroadcaster(boolean broadcaster) {
        final int currentHostCount = mUidsList.size();
        final int uid = config().mUid;
        log.debug("doSwitchToBroadcaster " + currentHostCount + " " + (uid & 0XFFFFFFFFL) + " " + broadcaster);

        final ImageView button1 = (ImageView) findViewById(R.id.btn_1);
        final ImageView button2 = (ImageView) findViewById(R.id.btn_2);
        final ImageView button3 = (ImageView) findViewById(R.id.btn_3);
        //screenshot btn
        final ImageView recordBtn = (ImageView) findViewById(R.id.btn_6);
        //record btn
        final ImageView screenshotBtn = (ImageView) findViewById(R.id.btn_5);
        if (broadcaster) {
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doRenderRemoteUi(uid);
                    broadcasterUI(button1, button2, button3, recordBtn, screenshotBtn);
                    button1.setEnabled(true);
                    doShowButtons(false);
                }
            }, 1000); // wait for reconfig engine
        } else {
            button1.setEnabled(true);
            stopInteraction(currentHostCount, uid);
        }
    }

    private void stopInteraction(final int currentHostCount, final int uid) {
        doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doRemoveRemoteUi(uid);

                ImageView button1 = (ImageView) findViewById(R.id.btn_1);
                ImageView button2 = (ImageView) findViewById(R.id.btn_2);
                ImageView button3 = (ImageView) findViewById(R.id.btn_3);
                //screenshot btn
                ImageView recordBtn = (ImageView) findViewById(R.id.btn_6);
                //record btn
                ImageView screenshotBtn = (ImageView) findViewById(R.id.btn_5);
                audienceUI(button1, button2, button3, recordBtn, screenshotBtn);

                doShowButtons(false);
            }
        }, 1000); // wait for reconfig engine
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);
                mAllUserData.put(uid,new VideoStatusData());
                if (config().mUid == uid) {
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                } else {
                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                }

                if (mViewType == VIEW_TYPE_DEFAULT) {
                    log.debug("doRenderRemoteUi VIEW_TYPE_DEFAULT" + " " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi VIEW_TYPE_SMALL" + " " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    log.debug("already added to UI, ignore it " + (uid & 0xFFFFFFFFL) + " " + mUidsList.get(uid));
                    return;
                }

                final boolean isBroadcaster = isBroadcaster();
                log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + elapsed + " " + isBroadcaster);

                worker().getEngineConfig().mUid = uid;

                SurfaceView surfaceV = mUidsList.remove(0);
                mAllUserData.remove(0);
                if (surfaceV != null) {
                    mUidsList.put(uid, surfaceV);
                    VideoStatusData videoStatusData = new VideoStatusData();
                    videoStatusData.setLocalUid(true);
                    mAllUserData.put(uid,videoStatusData);
                }
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid);
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        doRenderRemoteUi(uid);
    }

    @Override
    public void onLocalVideoStats(final IRtcEngineEventHandler.LocalVideoStats stats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                VideoStatusData videoLocalStatusData = getSecialUserDataInfo(config().mUid);
                if(videoLocalStatusData!=null){
                    videoLocalStatusData.setLocalResolutionInfo(localVideoDimensions.width,localVideoDimensions.height,stats.sentFrameRate);
                }

            }
        });
    }

    @Override
    public void onRtcStats(final IRtcEngineEventHandler.RtcStats stats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                VideoStatusData videoLocalStatusData = getSecialUserDataInfo(config().mUid);
                if (videoLocalStatusData != null) {
                    videoLocalStatusData.setLocalVideoSendRecvInfo(stats.txVideoKBitRate, stats.rxVideoKBitRate);
                    videoLocalStatusData.setLocalAudioSendRecvInfo(stats.txAudioKBitRate, stats.rxAudioKBitRate);
                    videoLocalStatusData.setLocalLastmileDelayInfo(stats.lastmileDelay);
                    videoLocalStatusData.setLocalCpuAppTotalInfo(stats.cpuAppUsage, stats.cpuTotalUsage);
                    videoLocalStatusData.setLocalSendRecvLostInfo(stats.txPacketLossRate, stats.rxPacketLossRate);
                }
            }
        });
    }

    @Override
    public void onNetworkQuality(final int uid, final int txQuality, final int rxQuality) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                VideoStatusData videoStatusData = null;
                if(uid == 0){
                    videoStatusData = VideoViewAdapterUtil.getLocalUserData(mAllUserData);
                }else{
                    videoStatusData = getSecialUserDataInfo(uid);
                }

                if(videoStatusData!=null) {
                    videoStatusData.setSendRecvQualityInfo(txQuality, rxQuality);
                }
                if(mGridVideoViewContainer!=null){
                    mGridVideoViewContainer.notifyDataChange(mAllUserData);
                }
//                if(mSmallVideoViewAdapter!=null){
//                    mSmallVideoViewAdapter.notifyDataChange(mAllUserData);
//                }
            }
        });
    }

    @Override
    public void onRecorderStateChanged(final int state, final int code) {
        super.onRecorderStateChanged(state, code);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLongToast("onRecorderStageChanged state: " + state + " code: " + code);
            }
        });
    }

    @Override
    public void onRecorderInfoUpdate(final Object info) {
        super.onRecorderInfoUpdate(info);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AgoraMediaRecorder.RecorderInfo obj = (AgoraMediaRecorder.RecorderInfo)info;
                showLongToast("onRecorderInfoUpdate fileSize: " + obj.fileSize + ", fileName: " + obj.fileName + ", duration: " + obj.duration);
            }
        });
    }

    @Override
    public void onRemoteVideoStats(final IRtcEngineEventHandler.RemoteVideoStats stats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                VideoStatusData videoRemoteStatusData = getSecialUserDataInfo(stats.uid);
                if(videoRemoteStatusData!=null) {
                    videoRemoteStatusData.setRemoteResolutionInfo(stats.width, stats.height, stats.decoderOutputFrameRate);
                    videoRemoteStatusData.setRemoteVideoDelayInfo(stats.delay);
                }
            }
        });

    }

    @Override
    public void onRemoteAudioStats(final IRtcEngineEventHandler.RemoteAudioStats stats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                VideoStatusData videoRemoteStatusData = getSecialUserDataInfo(stats.uid);
                if(videoRemoteStatusData!=null) {
                    videoRemoteStatusData.setRemoteAudioDelayJitterInfo(stats.networkTransportDelay, stats.jitterBufferDelay);
                    videoRemoteStatusData.setRemoteAudioLostQualityInfo(stats.audioLossRate, stats.quality);
                }
            }
        });
    }

    public VideoStatusData getSecialUserDataInfo(int uid){
        return mAllUserData.get(uid);
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap.Entry<Integer, SurfaceView> highest = null;
                for (HashMap.Entry<Integer, SurfaceView> pair : mUidsList.entrySet()) {
                    log.debug("requestRemoteStreamType " + currentHostCount + " local " + (config().mUid & 0xFFFFFFFFL) + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getHeight() + " " + pair.getValue().getWidth());
                    if (pair.getKey() != config().mUid && (highest == null || highest.getValue().getHeight() < pair.getValue().getHeight())) {
                        if (highest != null) {
                            rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_LOW);
                            log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                        }
                        highest = pair;
                    } else if (pair.getKey() != config().mUid && (highest != null && highest.getValue().getHeight() >= pair.getValue().getHeight())) {
                        rtcEngine().setRemoteVideoStreamType(pair.getKey(), Constants.VIDEO_STREAM_LOW);
                        log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getWidth() + " " + pair.getValue().getHeight());
                    }
                }
                if (highest != null && highest.getKey() != 0) {
                    rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_HIGH);
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                }
            }
        }, 500);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                mUidsList.remove(uid);
                mAllUserData.remove(uid);
                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));

                if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null)
            mSmallVideoViewDock.setVisibility(View.GONE);
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList);

        mViewType = VIEW_TYPE_DEFAULT;

        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                rtcEngine().setRemoteVideoStreamType(uid, Constants.VIDEO_STREAM_HIGH);
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
            }
        }
        boolean setRemoteUserPriorityFlag = false;
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true;
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORANL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                }
            }
        }
    }

    private void switchToSmallVideoView(int uid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(uid, mUidsList.get(uid));
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), uid, slice);

        bindToSmallVideoView(uid);

        mViewType = VIEW_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    public static final int VIEW_TYPE_SMALL = 1;

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    switchToDefaultVideoView();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        for (Integer tempUid : mUidsList.keySet()) {
            if (config().mUid != tempUid) {
                if (tempUid == exceptUid) {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_NORANL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                }
            }
        }


        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }
}
