package io.agora.openlive.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.openlive.AgoraApplication;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.WindowUtil;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public abstract class BaseActivity extends AppCompatActivity implements EventHandler {
    protected DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    protected int mStatusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtil.hideWindowStatusBar(getWindow());
        setGlobalLayoutListener();
        getDisplayMetrics();
        initStatusBarHeight();
    }

    private void setGlobalLayoutListener() {
        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onGlobalLayoutCompleted();
            }
        });
    }

    /**
     * Give a chance to obtain view layout attributes when the
     * content view layout process is completed.
     * Some layout attributes will be available here but not
     * in onCreate(), like measured width/height.
     * This callback will be called ONLY ONCE before the whole
     * window content is ready to be displayed for first time.
     */
    protected void onGlobalLayoutCompleted() {

    }

    private void getDisplayMetrics() {
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    private void initStatusBarHeight() {
        mStatusBarHeight = WindowUtil.getSystemStatusBarHeight(this);
    }

    protected AgoraApplication application() {
        return (AgoraApplication) getApplication();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }

    protected EngineConfig config() {
        return application().engineConfig();
    }

    protected StatsManager statsManager() { return application().statsManager(); }

    protected void registerRtcEventHandler(EventHandler handler) {
        application().registerEventHandler(handler);
    }

    protected void removeRtcEventHandler(EventHandler handler) {
        application().removeEventHandler(handler);
    }

    /**
     * Occurs when the first remote video frame is received and decoded.
     * This callback is triggered in either of the following scenarios:
     *
     *     The remote user joins the channel and sends the video stream.
     *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
     *         The remote user leaves channel.
     *         The remote user drops offline.
     *         The remote user calls the muteLocalVideoStream method.
     *         The remote user calls the disableVideo method.
     *
     * @param uid User ID of the remote user sending the video streams.
     * @param width Width (pixels) of the video stream.
     * @param height Height (pixels) of the video stream.
     * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
     */
    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    /**
     * Occurs when the local user joins a specified channel.
     *
     * The channel name assignment is based on channelName specified in the joinChannel method.
     *
     * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
     *
     * @param channel Channel name.
     * @param uid User ID.
     * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
     */
    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    /**
     *Occurs when a user leaves the channel.
     *
     * When the app calls the leaveChannel method, the SDK uses this callback to notify the app when the user leaves the channel.
     *
     * With this callback, the application retrieves the channel information, such as the call duration and statistics.
     *
     * @param stats Statistics of the call: RtcStats
     */
    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    /**
     * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
     *
     * There are two reasons for users to become offline:
     *
     *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
     *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
     *
     * @param uid ID of the user or host who leaves the channel or goes offline.
     * @param reason Reason why the user goes offline:
     *
     *     USER_OFFLINE_QUIT(0): The user left the current channel.
     *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
     *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
     */
    @Override
    public void onUserOffline(int uid, int reason) {

    }

    /**
     * Occurs when a remote user (Communication)/host (Live Broadcast) joins the channel.
     *
     *     Communication profile: This callback notifies the app when another user joins the channel. If other users are already in the channel, the SDK also reports to the app on the existing users.
     *     Live Broadcast profile: This callback notifies the app when the host joins the channel. If other hosts are already in the channel, the SDK also reports to the app on the existing hosts. We recommend having at most 17 hosts in a channel
     *
     * The SDK triggers this callback under one of the following circumstances:
     *
     *     A remote user/host joins the channel by calling the joinChannel method.
     *     A remote user switches the user role to the host by calling the setClientRole method after joining the channel.
     *     A remote user/host rejoins the channel after a network interruption.
     *     The host injects an online media stream into the channel by calling the addInjectStreamUrl method.
     *
     * @param uid ID of the user or host who joins the channel.
     * @param elapsed Time delay (ms) from the local user calling joinChannel/setClientRole until this callback is triggered.
     */
    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    /**
     * Reports the last mile network quality of the local user once every two seconds before the user joins the channel. Last mile refers to the connection between the local device and Agora's edge server. After the application calls the enableLastmileTest method, this callback reports once every two seconds the uplink and downlink last mile network conditions of the local user before the user joins the channel.
     * @param quality The last mile network quality based on the uplink and dowlink packet loss rate and jitter:
     *
     *     QUALITY_UNKNOWN(0): The quality is unknown.
     *     QUALITY_EXCELLENT(1): The quality is excellent.
     *     QUALITY_GOOD(2): The quality is quite good, but the bitrate may be slightly lower than excellent.
     *     QUALITY_POOR(3): Users can feel the communication slightly impaired.
     *     QUALITY_BAD(4): Users can communicate not very smoothly.
     *     QUALITY_VBAD(5): The quality is so bad that users can barely communicate.
     *     QUALITY_DOWN(6): The network is disconnected and users cannot communicate at all.
     *     QUALITY_DETECTING(8): The SDK is detecting the network quality.
     */
    @Override
    public void onLastmileQuality(final int quality) {

    }

    /**
     * Reports the last-mile network probe result.
     * The SDK triggers this callback within 30 seconds after the app calls the startLastmileProbeTest method.
     * @param result The uplink and downlink last-mile network probe test result. For details, see LastmileProbeResult.
     */
    @Override
    public void onLastmileProbeResult(final IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    /**
     * Reports the statistics of the local video streams.
     *
     * The SDK triggers this callback once every two seconds for each user/host. If there are multiple users/hosts in the channel, the SDK triggers this callback as many times.
     *
     * @param stats The statistics of the local video stream. See LocalVideoStats.
     */
    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {

    }

    /**
     * Reports the statistics of the RtcEngine once every two seconds.
     * @param stats RTC engine statistics: RtcStats.
     */
    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    /**
     * Reports the last mile network quality of each user in the channel once every two seconds.
     *
     * Last mile refers to the connection between the local device and Agora's edge server. This callback reports once every two seconds the last mile network conditions of each user in the channel. If a channel includes multiple users, then this callback will be triggered as many times.
     *
     * @param uid User ID. The network quality of the user with this uid is reported. If uid is 0, the local network quality is reported.
     * @param txQuality Uplink transmission quality of the user in terms of the transmission bitrate, packet loss rate, average RTT (Round-Trip Time) and jitter of the uplink network. txQuality is a quality rating helping you understand how well the current uplink network conditions can support the selected VideoEncoderConfiguration. For example, a 1000 Kbps uplink network may be adequate for video frames with a resolution of 680 × 480 and a frame rate of 30 fps, but may be inadequate for resolutions higher than 1280 × 720.
     *
     *     QUALITY_UNKNOWN(0): The quality is unknown.
     *     QUALITY_EXCELLENT(1): The quality is excellent.
     *     QUALITY_GOOD(2): The quality is quite good, but the bitrate may be slightly lower than excellent.
     *     QUALITY_POOR(3): Users can feel the communication slightly impaired.
     *     QUALITY_BAD(4): Users can communicate not very smoothly.
     *     QUALITY_VBAD(5): The quality is so bad that users can barely communicate.
     *     QUALITY_DOWN(6): The network is disconnected and users cannot communicate at all.
     *     QUALITY_DETECTING(8): The SDK is detecting the network quality.
     *
     * @param rxQuality Downlink network quality rating of the user in terms of packet loss rate, average RTT, and jitter of the downlink network.
     *
     *     QUALITY_UNKNOWN(0): The quality is unknown.
     *     QUALITY_EXCELLENT(1): The quality is excellent.
     *     QUALITY_GOOD(2): The quality is quite good, but the bitrate may be slightly lower than excellent.
     *     QUALITY_POOR(3): Users can feel the communication slightly impaired.
     *     QUALITY_BAD(4): Users can communicate not very smoothly.
     *     QUALITY_VBAD(5): The quality is so bad that users can barely communicate.
     *     QUALITY_DOWN(6): The network is disconnected and users cannot communicate at all.
     *     QUALITY_DETECTING(8): The SDK is detecting the network quality.
     */
    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {

    }

    /**
     * Reports the statistics of the video stream from each remote user/host. The SDK triggers this callback once every two seconds for each remote user/host. If a channel includes multiple remote users, the SDK triggers this callback as many times.
     * @param stats Statistics of the received remote video streams: RemoteVideoStats.
     */
    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    /**
     * Reports the statistics of the audio stream from each remote user/host.
     * The SDK triggers this callback once every two seconds for each remote user/host. If a channel includes multiple remote users, the SDK triggers this callback as many times.
     *
     * Schemes such as FEC (Forward Error Correction) or retransmission counter the frame loss rate. Hence, users may find the overall audio quality acceptable even when the packet loss rate is high.
     *
     * @param stats Statistics of the received remote audio streams: RemoteAudioStats.
     */
    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

    }
}
