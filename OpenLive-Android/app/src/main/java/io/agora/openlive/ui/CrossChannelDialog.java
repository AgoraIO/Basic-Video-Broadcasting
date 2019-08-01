package io.agora.openlive.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

import io.agora.common.Constant;
import io.agora.openlive.R;
import io.agora.rtc.video.ChannelMediaInfo;
import io.agora.rtc.video.ChannelMediaRelayConfiguration;


public class CrossChannelDialog extends DialogFragment {

    protected boolean mTouchOutside;
    private ListView channelListView;
    private List<ChannelMediaInfo> channelMediaInfos;
    private CrossChannelAdapter mAdapter;
    private String[] sourceItems;
    private int selectedNum = 0;
    private Context context = null;
    private CrossChannelDialogHandler handler;

    public interface CrossChannelDialogHandler {
        void startCrossChannel(ChannelMediaRelayConfiguration configuration);

        void stopCrossChannel();

        void updateCrossChannel(ChannelMediaRelayConfiguration configuration);
    }

    public void initCrossChannelDialog(Context context) {
        channelMediaInfos = new ArrayList<>(4);
        this.context = context;
        selectedNum = 0;
    }

    public void setDialogHandler(CrossChannelDialogHandler handler) {
        this.handler = handler;
    }

    public void show(FragmentManager manager, String tag, boolean touchOutside) {
        this.mTouchOutside = touchOutside;
        super.show(manager, tag);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_cross_channel, null));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final Button startButton = (Button) dialog.findViewById(R.id.bt_start_cross_channel);
        final Button stopButton = (Button) dialog.findViewById(R.id.bt_stop_cross_channel);
        final Button updateButton = (Button) dialog.findViewById(R.id.bt_update_cross_channel);
        sourceItems = context.getResources().getStringArray(R.array.string_array_cross_channel_num);
        final Spinner corssChannelSpinner = (Spinner) dialog.findViewById(R.id.sp_crosss_channel);
        ArrayAdapter<String> corssChannelAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, sourceItems);
        //corssChannelSpinner.setOnItemClickListener(null);
        corssChannelSpinner.setAdapter(corssChannelAdapter);
        corssChannelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                selectedNum = Integer.valueOf(sourceItems[position])-1;
                updateDestChannelInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        corssChannelSpinner.setSelection(selectedNum);
        channelListView = (ListView) dialog.findViewById(R.id.lv_channel_list);
        mAdapter = new CrossChannelAdapter(this.context, channelMediaInfos);
        channelListView.setAdapter(mAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelMediaRelayConfiguration configuration = getAllListViewInfos(channelListView);
                handler.startCrossChannel(configuration);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.stopCrossChannel();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelMediaRelayConfiguration configuration = getAllListViewInfos(channelListView);
                handler.updateCrossChannel(configuration);
            }
        });

        //initDestChannelInfo();
        dialog.setCanceledOnTouchOutside(mTouchOutside);
    }

    public void initDestChannelInfo() {
        mAdapter.notifyDataSetChanged();
    }

    public void updateDestChannelInfo() {
        if(channelMediaInfos.size()<(selectedNum+1)){
            int tempNum = channelMediaInfos.size();
            for (int i = 0; i < (selectedNum+1-tempNum); i++) {
                channelMediaInfos.add(new ChannelMediaInfo(null, null, 0));
            }
        }else{
            int tempNum = channelMediaInfos.size();
            for (int i = ((tempNum-1)); i >= (selectedNum+1); i--) {
                channelMediaInfos.remove(channelMediaInfos.get(i));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public ChannelMediaRelayConfiguration getAllListViewInfos(ListView list) {
        ChannelMediaRelayConfiguration configuration = new ChannelMediaRelayConfiguration();
        ChannelMediaInfo tempSrcInfo = new ChannelMediaInfo(null, null, 0);
        configuration.setSrcChannelInfo(tempSrcInfo);
        channelMediaInfos.clear();
        for (int i = 0; i < list.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) list.getChildAt(i);
            EditText etDestName = (EditText) layout.findViewById(R.id.et_channel_name);
            String tempDestName = etDestName.getText().toString();
            if (tempDestName.length() == 0 || tempDestName.equals("")) {
                return null;
            }
            EditText etDestToken = (EditText) layout.findViewById(R.id.et_token);
            String tempDestToken = etDestToken.getText().toString();
            EditText etDestUid = (EditText) layout.findViewById(R.id.et_uid);
            int tempDestUid = Integer.valueOf(etDestUid.getText().toString());
            ChannelMediaInfo tempDestInfo = new ChannelMediaInfo(tempDestName, tempDestToken, tempDestUid);
            channelMediaInfos.add(tempDestInfo);
            configuration.setDestChannelInfo(tempDestInfo.channelName, tempDestInfo);
        }
        return configuration;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.dismissAllowingStateLoss();
    }

}
