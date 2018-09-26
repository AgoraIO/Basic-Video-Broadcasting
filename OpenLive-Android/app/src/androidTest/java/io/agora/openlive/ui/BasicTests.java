package io.agora.openlive.ui;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import io.agora.openlive.R;

public class BasicTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public BasicTests() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public String getString(int resId) {
        return solo.getString(resId);
    }

    public void testJoinAsBroadcaster() throws Exception {
        String AUTO_TEST_CHANNEL_NAME = "auto_test_" + System.currentTimeMillis();

        solo.unlockScreen();

        solo.assertCurrentActivity("Expected MainActivity activity", "MainActivity");
        solo.clearEditText(0);
        solo.enterText(0, AUTO_TEST_CHANNEL_NAME);
        solo.waitForText(AUTO_TEST_CHANNEL_NAME, 1, 2000L);

        solo.clickOnView(solo.getView(R.id.button_join));

        solo.waitForDialogToOpen(1000);

        solo.clickOnButton(getString(R.string.label_broadcaster));

        String targetActivity = LiveRoomActivity.class.getSimpleName();

        solo.waitForLogMessage("onJoinChannelSuccess " + AUTO_TEST_CHANNEL_NAME, JOIN_CHANNEL_SUCCESS_THRESHOLD + 1000);

        solo.waitForLogMessage("onFirstLocalVideoFrame ", FIRST_LOCAL_VIDEO_SHOWN_THRESHOLD + 500);

        solo.assertCurrentActivity("Expected " + targetActivity + " activity", targetActivity);
    }

    private static final int FIRST_REMOTE_VIDEO_RECEIVED_THRESHOLD = 5000;
    private static final int FIRST_LOCAL_VIDEO_SHOWN_THRESHOLD = 1500;
    private static final int JOIN_CHANNEL_SUCCESS_THRESHOLD = 5000;

    public void testJoinAsAudience() throws Exception {
        String AUTO_TEST_CHANNEL_NAME = "for_auto_test";

        solo.unlockScreen();

        solo.assertCurrentActivity("Expected MainActivity activity", "MainActivity");
        solo.clearEditText(0);
        solo.enterText(0, AUTO_TEST_CHANNEL_NAME);
        solo.waitForText(AUTO_TEST_CHANNEL_NAME, 1, 2000L);

        solo.clickOnView(solo.getView(R.id.button_join));

        solo.waitForDialogToOpen(1000);

        solo.clickOnButton(getString(R.string.label_audience));

        String targetActivity = LiveRoomActivity.class.getSimpleName();

        solo.waitForLogMessage("onJoinChannelSuccess " + AUTO_TEST_CHANNEL_NAME, JOIN_CHANNEL_SUCCESS_THRESHOLD);

        long firstRemoteVideoTs = System.currentTimeMillis();
        solo.waitForLogMessage("onFirstRemoteVideoDecoded ", FIRST_REMOTE_VIDEO_RECEIVED_THRESHOLD + 1000);

        solo.assertCurrentActivity("Expected " + targetActivity + " activity", targetActivity);

        assertTrue("first remote video frame not received", System.currentTimeMillis() - firstRemoteVideoTs <= FIRST_REMOTE_VIDEO_RECEIVED_THRESHOLD);
    }
}
