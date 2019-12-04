package io.agora.largegroup.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtc.Constants;

public class StatsManager {
    private List<Integer> mUidList = new ArrayList<>();
    private Map<Integer, StatsData> mDataMap = new HashMap<>();
    private boolean mEnable = false;

    public void addUserStats(int uid, boolean ifLocal) {
        if (mUidList.contains(uid) && mDataMap.containsKey(uid)) {
            return;
        }

        StatsData data = ifLocal
                ? new LocalStatsData()
                : new RemoteStatsData();
        // in case 32-bit unsigned integer uid is received
        data.setUid(uid & 0xFFFFFFFFL);

        if (ifLocal) mUidList.add(0, uid);
        else mUidList.add(uid);

        mDataMap.put(uid, data);
    }

    public void removeUserStats(int uid) {
        if (mUidList.contains(uid) && mDataMap.containsKey(uid)) {
            mUidList.remove((Integer) uid);
            mDataMap.remove(uid);
        }
    }

    public StatsData getStatsData(int uid) {
        if (mUidList.contains(uid) && mDataMap.containsKey(uid)) {
            return mDataMap.get(uid);
        } else {
            return null;
        }
    }

    public String qualityToString(int quality) {
        switch (quality) {
            case Constants.QUALITY_EXCELLENT:
                return "Exc";
            case Constants.QUALITY_GOOD:
                return "Good";
            case Constants.QUALITY_POOR:
                return "Poor";
            case Constants.QUALITY_BAD:
                return "Bad";
            case Constants.QUALITY_VBAD:
                return "VBad";
            case Constants.QUALITY_DOWN:
                return "Down";
            default:
                return "Unk";
        }
    }

    public void enableStats(boolean enabled) {
        mEnable = enabled;
    }

    public boolean isEnabled() {
        return mEnable;
    }

    public void clearAllData() {
        mUidList.clear();
        mDataMap.clear();
    }
}
