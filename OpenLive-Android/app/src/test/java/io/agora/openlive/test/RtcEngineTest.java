package io.agora.openlive.test;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;

public class RtcEngineTest {
    @Mock
    Context mockContext;

    @Test
    public void RtcEngineCreateTest() {
        // fake app id, not valid for joining a channel
        // but enough to create a rtc engine.
    }
}
