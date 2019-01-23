package com.geetion.microscope_android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by WongzYe on 16/1/10.
 */
public class VideoPlayerContainer extends FrameLayout implements GestureDetector.OnGestureListener {

    private GestureDetector mDetector;
    private ScrollviewListener listener;

    public VideoPlayerContainer(Context context) {
        super(context);
        initDetector();
    }

    public VideoPlayerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDetector();
    }

    public VideoPlayerContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDetector();
    }

    private void initDetector() {
        mDetector = new GestureDetector(this);
    }

    public interface ScrollviewListener {
        void onScroll(MotionEvent e1, MotionEvent e2);

        void onEndGesture();
    }

    public void setListenr(ScrollviewListener listenr) {
        this.listener = listenr;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDetector != null && mDetector.onTouchEvent(ev))
            return true;

        // 处理手势结束
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onEndGesture();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (listener != null) {
            listener.onScroll(e1, e2);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }
}
