package io.agora.ui;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewUtil {
    protected static final boolean DEBUG_ENABLED = true;

    private final static Logger log = LoggerFactory.getLogger(ViewUtil.class);

    private static final int DEFAULT_TOUCH_TIMESTAMP = -1; // first time

    private static final int TOUCH_COOL_DOWN_TIME = 500; // ms

    private static long mLastTouchTime = DEFAULT_TOUCH_TIMESTAMP;

    /* package */
    static final boolean checkDoubleTouchEvent(MotionEvent event, View view) {
        if (DEBUG_ENABLED && false) {
            log.debug("dispatchTouchEvent " + mLastTouchTime + " " + event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) { // only check touch down event
            if (mLastTouchTime == DEFAULT_TOUCH_TIMESTAMP || (SystemClock.elapsedRealtime() - mLastTouchTime) >= TOUCH_COOL_DOWN_TIME) {
                mLastTouchTime = SystemClock.elapsedRealtime();
            } else {
                log.warn("too many touch events " + view + " " + MotionEvent.ACTION_DOWN);
                return true;
            }
        }
        return false;
    }

    /* package */
    static final boolean checkDoubleKeyEvent(KeyEvent event, View view) {
        if (DEBUG_ENABLED && false) {
            log.debug("dispatchKeyEvent " + mLastTouchTime + " " + event);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (mLastTouchTime != DEFAULT_TOUCH_TIMESTAMP && (SystemClock.elapsedRealtime() - mLastTouchTime) < TOUCH_COOL_DOWN_TIME) {
                log.warn("too many key events " + view + " " + KeyEvent.ACTION_DOWN);
                return true;
            }
            mLastTouchTime = SystemClock.elapsedRealtime();
        }

        return false;
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
