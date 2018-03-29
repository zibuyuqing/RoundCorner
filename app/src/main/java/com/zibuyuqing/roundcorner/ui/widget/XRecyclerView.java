package com.zibuyuqing.roundcorner.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class XRecyclerView extends RecyclerView {
    private LoadStateListener mListener;
    private Context mContext;
    private boolean isLoadingData = false;
    private boolean isNoMore = false;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public void setLoadStateListener(LoadStateListener listener) {
        mListener = listener;
    }

    private void init() {

    }
    public void loadData(){
        if (mListener != null) {
            mListener.loadMore();
        }
    }

    public abstract interface LoadStateListener {
        void loadMore();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mListener != null && !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 12
                    && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && !isNoMore) {
                isLoadingData = true;
                if (mListener != null) {
                    mListener.loadMore();
                }
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
