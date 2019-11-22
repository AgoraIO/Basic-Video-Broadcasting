package io.agora.openlive.activities;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.openlive.Constants;
import io.agora.openlive.R;
import io.agora.openlive.ui.ResolutionAdapter;
import io.agora.openlive.utils.PrefManager;

import static io.agora.openlive.Constants.PREF_RESOLUTION_IDX;

public class SettingsActivity extends BaseActivity {
    private static final int DEFAULT_MIN_BITRATE = 100;
    private static final int DEFAULT_MAX_BITRATE = 2400;
    private static final int DEFAULT_MAX_MIN_BITRATE = 1000;
    private static final int DEFAULT_MIN_FEC_BW = 50;
    private static final int DEFAULT_MAX_FEC_BW = 100;

    private static final int DEFAULT_SPAN = 3;

    private TextView mVideoStatCheck;

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

    private String mBitrateLabelFormat;
    private String mMinBitrateLabelFormat;
    private String mFecOutsideBWLabelFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mBitrateLabelFormat = getResources().getString(R.string.label_prefer_bitrate);
        mMinBitrateLabelFormat = getResources().getString(R.string.label_min_bitrate);
        mFecOutsideBWLabelFormat = getResources().getString(R.string.label_fec_outside_bw);
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

        Spinner frameRateSpinner = findViewById(R.id.frame_rate_spinner);
        ArrayAdapter<String> frameAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.frame_rate_selector));
        frameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frameRateSpinner.setAdapter(frameAdapter);
        frameRateSpinner.setSelection(config().getFrameRateIndex());
        frameRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config().setFrameRateIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner codecTypeSpinner = findViewById(R.id.codec_type_spinner);
        ArrayAdapter<String> codecAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.codec_type_selector));
        codecAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codecTypeSpinner.setAdapter(codecAdapter);

        // codec type value starts from -1 to 1
        codecTypeSpinner.setSelection(config().getCodecType() + 1);

        codecTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config().setCodecType(position - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CheckBox useFaceDetectionCheck = findViewById(R.id.use_face_detection_checkbox);
        useFaceDetectionCheck.setChecked(config().isUseFaceDetection());
        useFaceDetectionCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config().setUseFaceDetection(isChecked);
            }
        });

        final TextView preferBitrateText = findViewById(R.id.label_prefer_bitrate);
        preferBitrateText.setText(String.format(mBitrateLabelFormat, config().getTargetBitrate()));

        SeekBar preferBitrateSeekBar = findViewById(R.id.prefer_bitrate_seek);
        preferBitrateSeekBar.setMax(DEFAULT_MAX_BITRATE);
        preferBitrateSeekBar.setProgress(config().getTargetBitrate());

        preferBitrateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // The values of min bitrate has a step of 50
                int progress = seekBar.getProgress();
                if (progress < DEFAULT_MIN_BITRATE) {
                    progress = DEFAULT_MIN_BITRATE;
                } else {
                    // Clip the values to a multiple of 100
                    int multiple = progress / 50;
                    int residual = progress % 50;
                    if (residual > 30) {
                        multiple++;
                    }

                    progress = multiple * 50;
                }

                seekBar.setProgress(progress);
                config().setTargetBitrate(progress);
                preferBitrateText.setText(String.format(
                        mBitrateLabelFormat,
                        config().getTargetBitrate()));
            }
        });

        if (config().isDebugMode()) {
            LinearLayout debugModeOptLayout = findViewById(R.id.settings_debug_options);
            debugModeOptLayout.setVisibility(View.VISIBLE);

            final TextView minBitrateText = findViewById(R.id.label_min_bitrate);
            minBitrateText.setText(String.format(mMinBitrateLabelFormat, config().getMinBitrate()));

            SeekBar minBitrateSeekBar = findViewById(R.id.min_bitrate_seek);
            minBitrateSeekBar.setMax(DEFAULT_MAX_MIN_BITRATE);
            minBitrateSeekBar.setProgress(config().getMinBitrate());

            minBitrateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // The values of min bitrate has a step of 50
                    int progress = seekBar.getProgress();
                    if (progress < DEFAULT_MIN_BITRATE) {
                        progress = DEFAULT_MIN_BITRATE;
                    } else {
                        // Clip the values to a multiple of 100
                        int multiple = progress / 100;
                        int residual = progress % 100;
                        if (residual > 30) {
                            multiple++;
                        }

                        progress = multiple * 100;
                    }

                    seekBar.setProgress(progress);
                    config().setMinBitrate(progress);
                    minBitrateText.setText(String.format(
                            mMinBitrateLabelFormat,
                            config().getMinBitrate()));
                }
            });

            final TextView fecText = findViewById(R.id.label_fec_outside_bw);
            fecText.setText(String.format(mFecOutsideBWLabelFormat, config().getFecOutsideBwRatio()));

            SeekBar fecSeekBar = findViewById(R.id.fec_outside_bw_ratio_seek);
            fecSeekBar.setMax(DEFAULT_MAX_FEC_BW);
            fecSeekBar.setProgress(config().getFecOutsideBwRatio());

            fecSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // The values of min bitrate has a step of 50
                    int progress = seekBar.getProgress();
                    if (progress < DEFAULT_MIN_FEC_BW) {
                        progress = DEFAULT_MIN_FEC_BW;
                    } else {
                        // Clip the values to a multiple of 100
                        int multiple = progress / 10;
                        int residual = progress % 10;
                        if (residual > 6) {
                            multiple++;
                        }

                        progress = multiple * 10;
                    }

                    seekBar.setProgress(progress);
                    config().setFecOutsideBwRatio(progress);
                    fecText.setText(String.format(
                            mFecOutsideBWLabelFormat,
                            config().getFecOutsideBwRatio()));
                }
            });
        }
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        // Adjust for status bar height
        RelativeLayout titleLayout = findViewById(R.id.role_title_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
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

    public void onStatsChecked(View view) {
        view.setActivated(!view.isActivated());
        statsManager().enableStats(view.isActivated());
    }
}
