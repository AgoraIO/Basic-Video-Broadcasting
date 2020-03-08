package io.agora.openlive.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import io.agora.openlive.Constants;
import io.agora.openlive.R;
import io.agora.openlive.ui.ResolutionAdapter;
import io.agora.openlive.utils.PrefManager;

import static io.agora.openlive.Constants.PREF_RESOLUTION_IDX;

public class SettingsActivity extends BaseActivity {
    private static final int DEFAULT_SPAN = 3;

    private TextView mVideoStatCheck, mMirrorLocalText, mMirrorRemoteText, mMirrorEncodeText;

    private int mItemPadding;
    private ResolutionAdapter mResolutionAdapter;
    private RecyclerView.ItemDecoration mItemDecoration =
            new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                           @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    outRect.top = mItemPadding;
                    outRect.bottom = mItemPadding;
                    outRect.left = mItemPadding;
                    outRect.right = mItemPadding;

                    int pos = parent.getChildAdapterPosition(view);
                    if (pos < DEFAULT_SPAN) {
                        outRect.top = 0;
                    }
                    if (pos % DEFAULT_SPAN == 0) outRect.left = 0;
                    else if (pos % DEFAULT_SPAN == (DEFAULT_SPAN - 1)) outRect.right = 0;
                }
            };

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPref = PrefManager.getPreferences(getApplicationContext());
        initUI();
    }

    private void initUI() {
        RecyclerView resolutionList = findViewById(R.id.resolution_list);
        resolutionList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, DEFAULT_SPAN);
        resolutionList.setLayoutManager(layoutManager);

        mResolutionAdapter = new ResolutionAdapter(this, config().getVideoDimenIndex());
        resolutionList.setAdapter(mResolutionAdapter);
        resolutionList.addItemDecoration(mItemDecoration);

        mItemPadding = getResources().getDimensionPixelSize(R.dimen.setting_resolution_item_padding);

        mVideoStatCheck = findViewById(R.id.setting_stats_checkbox);
        mVideoStatCheck.setActivated(config().ifShowVideoStats());

        mMirrorLocalText = findViewById(R.id.setting_mirror_local_value);
        resetText(mMirrorLocalText, config().getMirrorLocalIndex());

        mMirrorRemoteText = findViewById(R.id.setting_mirror_remote_value);
        resetText(mMirrorRemoteText, config().getMirrorRemoteIndex());

        mMirrorEncodeText = findViewById(R.id.setting_mirror_encode_value);
        resetText(mMirrorEncodeText, config().getMirrorEncodeIndex());
    }

    private void resetText(TextView view, int index) {
        if (view == null) {
            return;
        }
        String[] strings = getResources().getStringArray(R.array.mirror_modes);
        view.setText(strings[index]);
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        // Adjust for status bar height
        RelativeLayout titleLayout = findViewById(R.id.role_title_layout);
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
        params.height += mStatusBarHeight;
        titleLayout.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        onBackArrowPressed(null);
    }

    public void onBackArrowPressed(View view) {
        saveResolution();
        saveShowStats();
        finish();
    }

    private void saveResolution() {
        int profileIndex = mResolutionAdapter.getSelected();
        config().setVideoDimenIndex(profileIndex);
        mPref.edit().putInt(PREF_RESOLUTION_IDX, profileIndex).apply();
    }

    private void saveShowStats() {
        config().setIfShowVideoStats(mVideoStatCheck.isActivated());
        mPref.edit().putBoolean(Constants.PREF_ENABLE_STATS,
                mVideoStatCheck.isActivated()).apply();
    }

    private void saveVideoMirrorMode(String key, int value) {
        if (TextUtils.isEmpty(key))
            return;
        switch (key) {
            case Constants.PREF_MIRROR_LOCAL:
                config().setMirrorLocalIndex(value);
                break;
            case Constants.PREF_MIRROR_REMOTE:
                config().setMirrorRemoteIndex(value);
                break;
            case Constants.PREF_MIRROR_ENCODE:
                config().setMirrorEncodeIndex(value);
                break;
        }
        mPref.edit().putInt(key, value).apply();
    }

    public void onStatsChecked(View view) {
        view.setActivated(!view.isActivated());
        statsManager().enableStats(view.isActivated());
    }

    public void onClick(View view) {
        String key = null;
        TextView textView = null;
        switch (view.getId()) {
            case R.id.setting_mirror_local_view:
                key = Constants.PREF_MIRROR_LOCAL;
                textView = mMirrorLocalText;
                break;
            case R.id.setting_mirror_remote_view:
                key = Constants.PREF_MIRROR_REMOTE;
                textView = mMirrorRemoteText;
                break;
            case R.id.setting_mirror_encode_view:
                key = Constants.PREF_MIRROR_ENCODE;
                textView = mMirrorEncodeText;
                break;
        }
        if (textView != null) {
            showDialog(key, textView);
        }
    }

    private void showDialog(final String key, final TextView view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] strings = getResources().getStringArray(R.array.mirror_modes);
        int checkedItem = Arrays.asList(strings).indexOf(view.getText().toString());
        builder.setSingleChoiceItems(strings, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveVideoMirrorMode(key, which);
                resetText(view, which);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
