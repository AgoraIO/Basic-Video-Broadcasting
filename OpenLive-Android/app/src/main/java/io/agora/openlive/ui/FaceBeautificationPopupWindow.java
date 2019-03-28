package io.agora.openlive.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import io.agora.common.Constant;
import io.agora.openlive.R;
import io.agora.ui.popup.BasePopupWindow;
import io.agora.ui.popup.ItemClickHandler;

public class FaceBeautificationPopupWindow extends BasePopupWindow {
    private UserEventHandler mUserEventHandler;

    public interface UserEventHandler {
        void onFBSwitch(boolean on);

        void onLightnessSet(float lightness);

        void onSmoothnessSet(float smoothness);

        void onRednessSet(float redness);
    }

    public FaceBeautificationPopupWindow(Context ctx) {
        mCtx = ctx;
        View contentView = LayoutInflater.from(ctx.getApplicationContext())
                .inflate(R.layout.beauty_effect_popup_window, null);

        int width = mCtx.getResources().getDimensionPixelOffset(R.dimen.fb_popup_window_width);
        int height = mCtx.getResources().getDimensionPixelOffset(R.dimen.fb_popup_window_height);

        newPopupWindow(contentView, width, height);

        CheckBox fbSwitch = (CheckBox) contentView.findViewById(R.id.face_beautification_switch);
        fbSwitch.setChecked(Constant.BEAUTY_EFFECT_ENABLED);
        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mUserEventHandler != null) {
                    mUserEventHandler.onFBSwitch(isChecked);
                }
            }
        });

        SeekBar lightnessSeekBar = (SeekBar) contentView.findViewById(R.id.set_lightness_seek_bar);
        lightnessSeekBar.setMax((int) (Constant.BEAUTY_EFFECT_MAX_LIGHTNESS * 100.f));
        lightnessSeekBar.setProgress((int) (Constant.BEAUTY_OPTIONS.lighteningLevel * 100.f));
        final TextView lightnessValueView = (TextView) contentView.findViewById(R.id.set_lightness_label);
        lightnessValueView.setText(mCtx.getString(R.string.label_lightness) + " " + Constant.BEAUTY_OPTIONS.lighteningLevel);
        lightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float realValue = progress * 1.f / 100.f;
                    lightnessValueView.setText(mCtx.getString(R.string.label_lightness) + " " + realValue);
                    if (mUserEventHandler != null) {
                        mUserEventHandler.onLightnessSet(realValue);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar smoothnessSeekBar = (SeekBar) contentView.findViewById(R.id.set_smoothness_seek_bar);
        smoothnessSeekBar.setMax((int) (Constant.BEAUTY_EFFECT_MAX_SMOOTHNESS * 100.f));
        smoothnessSeekBar.setProgress((int) (Constant.BEAUTY_OPTIONS.smoothnessLevel * 100.f));
        final TextView smoothnessValueView = (TextView) contentView.findViewById(R.id.set_smoothness_label);
        smoothnessValueView.setText(mCtx.getString(R.string.label_smoothness) + " " + Constant.BEAUTY_OPTIONS.smoothnessLevel);
        smoothnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float realValue = progress * 1.f / 100.f;
                    smoothnessValueView.setText(mCtx.getString(R.string.label_smoothness) + " " + realValue);
                    if (mUserEventHandler != null) {
                        mUserEventHandler.onSmoothnessSet(realValue);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar ctSeekBar = (SeekBar) contentView.findViewById(R.id.set_redness_seek_bar);
        ctSeekBar.setMax((int) (Constant.BEAUTY_EFFECT_MAX_REDNESS * 100.f));
        ctSeekBar.setProgress((int) (Constant.BEAUTY_OPTIONS.rednessLevel * 100.f));
        final TextView rednessValueView = (TextView) contentView.findViewById(R.id.set_redness_label);
        rednessValueView.setText(mCtx.getString(R.string.label_redness) + " " + Constant.BEAUTY_OPTIONS.rednessLevel);
        ctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float realValue = progress * 1.f / 100.f;
                    rednessValueView.setText(mCtx.getString(R.string.label_redness) + " " + realValue);
                    if (mUserEventHandler != null) {
                        mUserEventHandler.onRednessSet(realValue);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void show(View anchor, UserEventHandler handler) {
        synchronized (mUiLock) {
            mView.getContentView().setBackgroundResource(R.drawable.rounded_corner_bg);
            mView.showAsDropDown(anchor, 0, 16);

            mUserEventHandler = handler;
        }
    }

    @Override
    public final void addItemClickHandler(ItemClickHandler handler) {
        throw new IllegalAccessError("do not support this method");
    }
}

