package io.agora.ui.popup;

import android.content.Context;
import android.view.LayoutInflater;

public class ClickableListAdapter {
    ItemClickHandler mHandler;
    BasePopupWindow.HookingCloseWindow mHookingWindowHandler;

    protected Context mContext;
    protected LayoutInflater mInflater;

    ClickableListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    void addItemClickHandler(BasePopupWindow.HookingCloseWindow closeWindow, ItemClickHandler handler) {
        mHookingWindowHandler = closeWindow;
        mHandler = handler;
    }
}
