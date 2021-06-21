package io.agora.openlive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.agora.openlive.R;
import io.agora.rtc2.Constants;

public class RoleActivity extends BaseActivity implements View.OnKeyListener {

    private TextView tvBack;
    private RelativeLayout broadcaster_layout;
    private RelativeLayout audience_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        tvBack = findViewById(R.id.tvBack);
        broadcaster_layout = findViewById(R.id.broadcaster_layout);
        audience_layout = findViewById(R.id.audience_layout);
    }

    public void onJoinAsBroadcaster(View view) {
        gotoLiveActivity(Constants.CLIENT_ROLE_BROADCASTER);
    }

    public void onJoinAsAudience(View view) {
        gotoLiveActivity(Constants.CLIENT_ROLE_AUDIENCE);
    }

    private void gotoLiveActivity(int role) {
        Intent intent = new Intent(getIntent());
        intent.putExtra(io.agora.openlive.Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(getApplicationContext(), LiveActivity.class);
        startActivity(intent);
    }

    public void onBackArrowPressed(View view) {
        finish();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (v == tvBack) {
                onBackArrowPressed(v);
            } else if (v == broadcaster_layout) {
                onJoinAsBroadcaster(v);
            } else if (v == audience_layout) {
                onJoinAsAudience(v);
            }
        }
        return false;
    }
}
