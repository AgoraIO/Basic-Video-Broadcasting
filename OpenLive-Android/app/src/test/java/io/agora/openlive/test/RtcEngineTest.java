package io.agora.openlive.test;

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class RtcEngineTest {
    @Mock
    Context mockContext;

    @Test
    public void RtcEngineCreateTest() {
        // fake app id, not valid for joining a channel
        // but enough to create a rtc engine.
        String appId = "85ace02fa13321f4cb06b61a0e109080";

        boolean success = true;
        try {
            RtcEngine.create(mockContext, appId, new IRtcEngineEventHandler() {});
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        Assert.assertTrue(success);
    }
}
