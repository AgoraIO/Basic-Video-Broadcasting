package io.agora.openlive.utils;

import java.util.Locale;

import io.agora.rtc.IRtcEngineEventHandler;

public class StatsUtil {
    public static String localStatsToString(IRtcEngineEventHandler.LocalVideoStats stats) {
        return String.format(Locale.getDefault(),
                "TargetBitrate:%d, TargetFrameRate:%d," +
                "SentBitrate:%d, SentFrameRate:%d," +
                "EncoderOutFrameRate:%d, RenderOutFrameRate:%d," +
                "QualityIndication:%d",
                stats.targetBitrate, stats.targetFrameRate,
                stats.sentBitrate, stats.sentFrameRate,
                stats.encoderOutputFrameRate, stats.rendererOutputFrameRate,
                stats.qualityAdaptIndication);
    }
}
