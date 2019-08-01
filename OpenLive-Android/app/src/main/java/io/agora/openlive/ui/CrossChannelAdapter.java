package io.agora.openlive.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import io.agora.openlive.R;

import java.util.List;

import io.agora.rtc.video.ChannelMediaInfo;

/**
 * Created by yong on 2019/7/31.
 */

public class CrossChannelAdapter extends BaseAdapter {
    private List<ChannelMediaInfo> mData;
    private Context mContext;

    public CrossChannelAdapter(Context mContext, List<ChannelMediaInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cross_channel, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ChannelMediaInfo info = mData.get(position);

        holder.nameText.setText(info.channelName);
        holder.tokenText.setText(info.token);
        holder.uidText.setText(String.valueOf(info.uid));

        holder.nameView.setText("dest channelName:");
        holder.tokenView.setText("dest token:");
        holder.uidView.setText("dest uid:");


        return convertView;
    }

    private class ViewHolder {
        private EditText nameText;
        private EditText tokenText;
        private EditText uidText;
        private TextView nameView;
        private TextView tokenView;
        private TextView uidView;

        public ViewHolder(View convertView) {
            nameText = (EditText) convertView.findViewById(R.id.et_channel_name);
            tokenText = (EditText) convertView.findViewById(R.id.et_token);
            uidText = (EditText) convertView.findViewById(R.id.et_uid);
            nameView = (TextView) convertView.findViewById(R.id.tv_channel_name);
            tokenView = (TextView) convertView.findViewById(R.id.tv_token);
            uidView = (TextView) convertView.findViewById(R.id.tv_uid);
        }
    }
}
