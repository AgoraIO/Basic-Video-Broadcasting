package io.agora.openlive.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import io.agora.openlive.R;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.internal.LastmileProbeConfig;

public class MainActivity extends BaseActivity {
    private TextView tvLastmileQualityResult;
    private TextView tvLastmileProbeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLastmileQualityResult = (TextView) findViewById(R.id.tv_lastmile_quality_result);
        tvLastmileProbeResult = (TextView) findViewById(R.id.tv_lastmile_Probe_result);
    }

    @Override
    protected void initUIandEvent() {
        resetLastMileInfo();
        EditText textRoomName = (EditText) findViewById(R.id.room_name);
        textRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                findViewById(R.id.button_join).setEnabled(!isEmpty);
            }
        });
    }


    @Override
    protected void deInitUIandEvent() {
        event().removeEventHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                forwardToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickJoin(View view) {
        // show dialog to choose role
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_choose_role);
        builder.setNegativeButton(R.string.label_audience, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.forwardToLiveRoom(Constants.CLIENT_ROLE_AUDIENCE);
            }
        });
        builder.setPositiveButton(R.string.label_broadcaster, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.forwardToLiveRoom(Constants.CLIENT_ROLE_BROADCASTER);
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void forwardToLiveRoom(int cRole) {
        final EditText v_room = (EditText) findViewById(R.id.room_name);
        String room = v_room.getText().toString();

        Intent i = new Intent(MainActivity.this, LiveRoomActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CROLE, cRole);
        i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, room);

        startActivity(i);
    }

    public void forwardToSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void onLastMileClick(View view) {
        boolean enableLastMileProbleTest = ((CheckBox) view).isChecked();
        if (enableLastMileProbleTest) {
            if (worker().getRtcEngine() != null) {
                LastmileProbeConfig lastmileProbeConfig = new LastmileProbeConfig();
                lastmileProbeConfig.probeUplink = true;
                lastmileProbeConfig.probeDownlink = true;
                lastmileProbeConfig.expectedUplinkBitrate = 5000;
                lastmileProbeConfig.expectedDownlinkBitrate = 5000;
                int result = worker().getRtcEngine().startLastmileProbeTest(lastmileProbeConfig);
            }
        } else {
            if (worker().getRtcEngine() != null) {
                worker().getRtcEngine().stopLastmileProbeTest();
                resetLastMileInfo();
            }
        }
    }


    @Override
    public void workThreadInited() {
        event().addEventHandler(this);
    }

    @Override
    public void onLastmileQuality(final int quality) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLastmileQualityResult.setText(
                        "onLastmileQuality " + quality);
            }
        });

    }

    @Override
    public void onLastmileProbeResult(final IRtcEngineEventHandler.LastmileProbeResult result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLastmileProbeResult.setText(
                        "onLastmileProbeResult state:" + result.state + " " + "rtt:" + result.rtt + "\n" +
                                "uplinkReport { packetLossRate:" + result.uplinkReport.packetLossRate + " " +
                                "jitter:" + result.uplinkReport.jitter + " " +
                                "availableBandwidth:" + result.uplinkReport.availableBandwidth + "}" + "\n" +
                                "downlinkReport { packetLossRate:" + result.downlinkReport.packetLossRate + " " +
                                "jitter:" + result.downlinkReport.jitter + " " +
                                "availableBandwidth:" + result.downlinkReport.availableBandwidth + "}");
            }
        });
    }


    private void resetLastMileInfo() {
        tvLastmileQualityResult.setText("");
        tvLastmileProbeResult.setText("");
    }


}
