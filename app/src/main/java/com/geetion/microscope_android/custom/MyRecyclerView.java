package com.geetion.microscope_android.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.geetion.log.Logger;

public class MyRecyclerView extends RecyclerView {

    private OnBottomCallback mOnBottomCallback;

    public interface OnBottomCallback {
        void onBottom();
    }

    public void setOnBottomCallback(OnBottomCallback onBottomCallback) {
        this.mOnBottomCallback = onBottomCallback;
    }
    public boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;

        if (recyclerView.computeHorizontalScrollExtent() + recyclerView.computeHorizontalScrollOffset()>= recyclerView.computeHorizontalScrollRange())
            return true;
        return false;

    }

    /**
     * 记录当前第一个View
     */
    private int mCurrentPosition;

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
    }

    private OnItemScrollChangeListener mItemScrollChangeListener;

    public void setOnItemScrollChangeListener(
            OnItemScrollChangeListener mItemScrollChangeListener) {
        this.mItemScrollChangeListener = mItemScrollChangeListener;
    }

    public interface OnItemScrollChangeListener {
        void onChange(View view, int position);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.setOnScrollListener(listener);
    }

    private OnScrollListener listener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        /**
         * 滚动时，判断当前第一个View是否发生变化，发生才回调
         */
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(isSlideToBottom(recyclerView)){
                mOnBottomCallback.onBottom();
            }
            LayoutManager manager = recyclerView.getLayoutManager();
            int childCount = manager.getChildCount();
            View firstView = getChildAt(0);
            View lastView = getChildAt(childCount - 1);
            if (mItemScrollChangeListener != null) {
                if (mCurrentPosition == getChildAdapterPosition(firstView) || mCurrentPosition == getChildAdapterPosition(lastView))
                    return;
                mCurrentPosition = getChildAdapterPosition(firstView);
                mItemScrollChangeListener.onChange(firstView, getChildAdapterPosition(firstView));
            }
//            Logger.e("onScrolled-after-position(" + getChildCount() + "," + recyclerView.getRight() + ") = " + "(current)" + mCurrentPosition + "---(first)" + getChildAdapterPosition(firstView) + "---(last)" + getChildAdapterPosition(lastView));
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Logger.e("onLayout-position = " + getChildLayoutPosition(getChildAt(0)));
    }
}
