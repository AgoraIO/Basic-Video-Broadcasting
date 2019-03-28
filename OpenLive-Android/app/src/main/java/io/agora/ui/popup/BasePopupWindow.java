package io.agora.ui.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

public class BasePopupWindow {
    protected static final int SPAN_COUNT = 3;

    protected PopupWindow mView;
    protected Context mCtx;

    protected ClickableListAdapter mAdapter;

    protected final byte[] mUiLock = new byte[0];

    public boolean isShowing() {
        synchronized (mUiLock) {
            return mView.isShowing();
        }
    }

    public void addItemClickHandler(ItemClickHandler handler) {
        synchronized (mUiLock) {
            if (mAdapter == null) {
                throw new IllegalStateException("Call this method after show() called");
            }

            mAdapter.addItemClickHandler(new HookingCloseWindow(), handler);
        }
    }

    public final void newPopupWindow(View contentView, int width, int height) {
        mView = new PopupWindow(contentView, width, height);
        mView.setBackgroundDrawable(new BitmapDrawable()); // magic code for can not close PopupWindow
        mView.setFocusable(true);
        mView.setOutsideTouchable(true);
    }

    class HookingCloseWindow {
        void checkIfCloseWindow() {
            closeWindow();
        }
    }

    protected void closeWindow() {

    }
}
