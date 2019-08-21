package io.agora.openlive.utils;

import android.content.Context;
import android.content.SharedPreferences;

import io.agora.openlive.Constants;


public class PreferenceManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
