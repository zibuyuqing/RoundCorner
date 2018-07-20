package com.zibuyuqing.roundcorner.ui.widget;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.NotificationListAdapter;
import com.zibuyuqing.roundcorner.model.bean.NotificationInfo;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.List;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListWindow extends FrameLayout{
    private String TAG = NotificationListWindow.class.getSimpleName();
    View mNotificationWindow;
    Button mBtnClear;
    RecyclerView mRvNotificationList;
    NotificationListAdapter mAdapter;
    Context mContext;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mOffsetX;
    private int mOffsetY;
    private OnClearNotificationsListener mListener;
    private Handler mHandler;
    private final Runnable mRemoveViewCallBack = new Runnable() {
        @Override
        public void run() {
            if(isAttachedToWindow()){
                mWindowManager.removeView(NotificationListWindow.this);
            }
        }
    };
    public NotificationListWindow(@NonNull Context context) {
        this(context,null);
    }

    public NotificationListWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, null,0);
    }

    public NotificationListWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager)
                    mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (mWindowParams == null) {
            mWindowParams = new WindowManager.LayoutParams();
            mWindowParams.format = PixelFormat.RGBA_8888;
            mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION

                    // 以下属性设置加载我们圆角window 不抢焦点,不拦截事件
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            mWindowParams.type = ensureWindowType();
        }
        mHandler = new Handler(Looper.getMainLooper());
        mNotificationWindow = LayoutInflater.from(mContext).inflate(R.layout.layout_notification_list,this);
        mBtnClear = (Button) mNotificationWindow.findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_260dp);
        mHeight = mItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_48_dp);
        int iconSize = ViewUtil.getIconSize(mContext);
        mOffsetY = (int) (iconSize * 1.3f);
        mOffsetX = iconSize / 2;
        mRvNotificationList = (RecyclerView)mNotificationWindow.findViewById(R.id.rv_notification_list);
        mAdapter = new NotificationListAdapter(mContext);
        mRvNotificationList.setAdapter(mAdapter);
        mRvNotificationList.setLayoutManager(new LinearLayoutManager(mContext));
    }
    private int ensureWindowType() {
        boolean hasNav = ViewUtil.getNavigationBarHeight(mContext) > 0;
        if (Utilities.isBeforeAndroidN()) {
            return WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Utilities.isCanUseApplicationOverlayType()) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            if (hasNav) {
                return WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateWindow();
        setMeasuredDimension(mWidth,mHeight);
    }

    private void updateWindow(){
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
        mWindowParams.width = mWidth;
        mWindowParams.height = mHeight;
        if(isAttachedToWindow()){
            mWindowManager.updateViewLayout(NotificationListWindow.this,mWindowParams);
        } else {
            mWindowManager.addView(NotificationListWindow.this,mWindowParams);
        }
    }
    public void showAt(float x,float y){
        if( x + mWidth + mOffsetX>= mScreenWidth){
            mWindowParams.x = mScreenWidth - mWidth - mOffsetX;
        } else {
            mWindowParams.x = (int) x + mOffsetX;
        }
        if( y > mScreenHeight * 0.5f){
            mWindowParams.y = (int) y - mOffsetY - mHeight / 2;
        } else {
            mWindowParams.y = (int) y + mOffsetY;
        }

        Log.e(TAG,"showAt x =:" + x +",y =:" + y +",mOffsetY =:" + mOffsetY);
        if(isAttachedToWindow()){
            mWindowManager.updateViewLayout(NotificationListWindow.this,mWindowParams);
        } else {
            mWindowManager.addView(NotificationListWindow.this,mWindowParams);
        }
    }
    public void addNotification(NotificationInfo info){
        if(!mAdapter.checkNotification(info)) {
            mHandler.removeCallbacks(mRemoveViewCallBack);
            mAdapter.addNotificationInfo(info);
            int count = mAdapter.getItemCount();
            if (count > 4) {
                count = 4;
            }
            mHeight = mItemHeight * (count + 2);
            requestLayout();
        }
    }
    public void removeNotification(String who){
        mAdapter.removeNotificationInfoByPackageName(who);
        int count = mAdapter.getItemCount();
        Log.e(TAG,"removeNotification  count =:" + count);
        if(count >= 1) {
            if (count > 4) {
                count = 4;
            }
            mHeight = mItemHeight * (count + 2);
            requestLayout();
        } else {
            dismiss();
        }
    }
    public void removeNotification(NotificationInfo info){
        mAdapter.removeNotificationInfo(info);
    }
    public void clear(){
        Log.e(TAG,"clear ");
        if(mListener != null){
            mListener.onNotificationsClear();
        }
    }
    public void setOnItemClickListener(NotificationListAdapter.OnItemClickListener listener){
        mAdapter.setOnItemClickListener(listener);
    }

    public void dismiss() {
        mHandler.postDelayed(mRemoveViewCallBack,100);
    }
    public List<NotificationInfo> getNotifications(){
        if(mAdapter != null){
            return mAdapter.getNotificationInfos();
        } else {
            return null;
        }
    }
    public void setOnClearNotificationsListener(OnClearNotificationsListener listener){
        mListener = listener;
    }

    public void updateOffset() {
        int iconSize = ViewUtil.getIconSize(mContext);
        mOffsetY = (int) (iconSize * 1.3f);
        mOffsetX = iconSize / 2;
        Log.e("hahah","updateOffset mOffsetY =:" + mOffsetY);
    }

    public abstract interface OnClearNotificationsListener{
        void onNotificationsClear();
    }
}
