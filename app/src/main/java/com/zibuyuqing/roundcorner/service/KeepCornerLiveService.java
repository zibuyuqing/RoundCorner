package com.zibuyuqing.roundcorner.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;
import com.zibuyuqing.roundcorner.widget.CornerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xijun.Wang on 2017/11/7.
 */

public class KeepCornerLiveService extends Service{
    private static final String LEFT_TOP = "left_top";
    private static final String LEFT_BOTTOM = "left_bottom";
    private static final String RIGHT_TOP = "right_top";
    private static final String RIGHT_BOTTOM = "right_bottom";
    public static final int NOTIFICATION_ID = 0x11;
    private static final String TAG = "KeepCornerLiveService";
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    Map<String,CornerView> corners = new HashMap<>();
    WindowManager manager;
    private boolean cornerAdded;
    private boolean cornerEnable;
    private boolean notifyEnable;
    private int currentOpacity;
    private int currentCornerSize;
    private int currentColor;
    private boolean leftTopEnable;
    private boolean leftBottomEnable;
    private boolean rightTopEnable;
    private boolean rightBottomEnable;
    private WindowManager.LayoutParams params;
    public KeepCornerLiveService(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void showOrHideNotify(boolean show){
        //API 18以下，直接发送Notification并将其置为前台
        Log.e(TAG,"showOrHideNotify ::show = ：" + show);
        if(show) {
            Notification notification = Utilities.buildNotification(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                startForeground(NOTIFICATION_ID, notification);
            } else {
                //API 18以上，发送Notification并将其置为前台后，启动InnerService
                startForeground(NOTIFICATION_ID, notification);
                startService(new Intent(this, InnerService.class));
            }
        } else {
            stopForeground(true);
        }
    }
    private void refreshSettings(){
        // settings data
        cornerEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE);
        notifyEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.NOTIFICATION_ENABLE);
        currentCornerSize = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_SIZE);
        currentOpacity = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_OPACITY);
        currentColor =  SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_COLOR);

        leftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        leftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        rightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        rightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }
    private void init(){
        // window
        manager = (WindowManager) this.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        // 系统提示类型,重要
        if (Utilities.isCanUseToastType()) {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if(Utilities.isCanUseApplicationOverlayType()){
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.format = 1;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.alpha = 1.0f;
        params.width = ViewUtil.getScreenSize(this).x;
        params.height = ViewUtil.getScreenSize(this).y;
        Log.e(TAG,"init :: params.width =:" + params.width +",params.height =:" + params.height);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        refreshSettings();
        if(intent != null) {
            String action = intent.getAction();
            Log.e(TAG,"action =:" + action);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case SettingsDataKeeper.CORNER_COLOR:
                        int color = intent.getIntExtra(SettingsDataKeeper.CORNER_COLOR, currentColor);
                        changeCornerColor(color);
                        break;
                    case SettingsDataKeeper.CORNER_SIZE:
                        int size = intent.getIntExtra(SettingsDataKeeper.CORNER_SIZE, currentCornerSize);
                        changeCornerSize(size);
                        break;
                    case SettingsDataKeeper.CORNER_OPACITY:
                        int opacity = intent.getIntExtra(SettingsDataKeeper.CORNER_OPACITY, currentOpacity);
                        changeOpacity(opacity);
                        break;
                    case SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE:
                        boolean leftTop = intent.getBooleanExtra(
                                SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE, leftTopEnable);
                        showOrHideLeftTopCorner(leftTop);
                        break;
                    case SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE:
                        boolean leftBottom = intent.getBooleanExtra(
                                SettingsDataKeeper.CORNER_COLOR, leftBottomEnable);
                        showOrHideLeftBottomCorner(leftBottom);
                        break;
                    case SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE:
                        boolean rightTop = intent.getBooleanExtra(
                                SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE, rightTopEnable);
                        showOrHideRightTopCorner(rightTop);
                        break;
                    case SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE:
                        boolean rightBottom = intent.getBooleanExtra(
                                SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE, rightBottomEnable);
                        showOrHideRightBottomCorner(rightBottom);
                        break;

                    case SettingsDataKeeper.CORNER_ENABLE:
                        boolean cornerDisplayEnable = intent.getBooleanExtra(
                                SettingsDataKeeper.CORNER_ENABLE, cornerEnable);
                        showOrHideCorner(cornerDisplayEnable);
                        break;
                    case SettingsDataKeeper.NOTIFICATION_ENABLE:
                        boolean  notifyShowEnable = intent.getBooleanExtra(
                                SettingsDataKeeper.NOTIFICATION_ENABLE,notifyEnable);
                        showOrHideNotify(notifyShowEnable);
                        break;
                }
            }
        }
        return Service.START_NOT_STICKY;
    }
    void showOrHideCorner(boolean cornerEnable){
        Log.e(TAG,"cornerEnable =:" + cornerEnable + ",cornerAdded = ：" + cornerAdded);
        if(cornerEnable){
            if(!cornerAdded) {
                addCornerViews();
            }
        } else {
            if(cornerAdded){
                remove();
            }
        }
    }
    void showOrHideLeftTopCorner(boolean leftTopEnable) {
        Log.e(TAG,"showOrHideLeftTopCorner :: leftTopEnable =:" + leftTopEnable);
        if(!leftTopEnable){
            hideCornerByTag(LEFT_TOP);
        } else {
            corners.get(LEFT_TOP).show();
        }
    }

    void showOrHideLeftBottomCorner(boolean leftBottomEnable) {
        if(!leftBottomEnable){
            hideCornerByTag(LEFT_BOTTOM);
        } else {
            corners.get(LEFT_BOTTOM).show();
        }
    }

    void showOrHideRightTopCorner(boolean rightTopEnable) {
        if(!rightTopEnable){
            hideCornerByTag(RIGHT_TOP);
        } else {
            corners.get(RIGHT_TOP).show();
        }
    }

    void showOrHideRightBottomCorner(boolean rightBottomEnable) {
        if(!rightBottomEnable){
            hideCornerByTag(RIGHT_BOTTOM);
        } else {
            corners.get(RIGHT_BOTTOM).show();
        }

    }

    private void addCornerViews() {
        Log.e(TAG, "addCornerViews --");
        for (String position : POSITION_TAGS) {
            addCornerViewByPosition(position);
        }
        cornerAdded = true;
    }
    public void addCornerViewByPosition(String position){
        boolean enable = true;
        switch (position) {
            case LEFT_TOP:
                enable = leftTopEnable;
                params.x = params.y = 0;
                params.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case RIGHT_TOP:
                enable = rightTopEnable;
                params.x = params.y = 0;
                params.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case LEFT_BOTTOM:
                enable = leftBottomEnable;
                params.x = 0;
                params.y = - ViewUtil.getNavigationBarHeight(this);
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case RIGHT_BOTTOM:
                enable = rightBottomEnable;
                params.x = 0;
                params.y = - ViewUtil.getNavigationBarHeight(this);
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
        }
        CornerView corner = buildCorner(enable,params.gravity);
        if(!corners.containsValue(corner)) {
            corners.put(position, corner);
            manager.addView(corner, params);
            Log.e(TAG,"manager =: add view position =:" + position +",params =:" + params.x +",y =:" + params.y);
        }
    }
    public void hideCornerByTag(String position) {
        CornerView corner = corners.get(position);
        corner.hide();
    }
    private void changeOpacity(int opacity) {
        Log.e(TAG, "changeOpacity :: opacity =:" + opacity);
        for (CornerView cornerView : corners.values()) {
            cornerView.setCornerOpacity(opacity);
        }
    }
    private void changeCornerSize(int cornerSize) {
        Log.e(TAG, "changeCornerSize :: cornerSize =:" + cornerSize);
        for (CornerView cornerView : corners.values()) {
            cornerView.setCornerSize(cornerSize);
        }
    }
    private void changeCornerColor(int color) {
        Log.e(TAG, "changeCornerColor :: color =:" + color);
        for (CornerView cornerView : corners.values()) {
            cornerView.setColor(color);
        }
    }

    private CornerView buildCorner(boolean enable,int position){
        CornerView corner = new CornerView(this);
        corner.setColor(currentColor);
        corner.setCornerOpacity(currentOpacity);
        corner.setCornerSize(currentCornerSize);
        corner.setLocation(position);
        if(enable){
            corner.show();
        } else {
            corner.hide();
        }
        return corner;
    }
    public void remove(){
        for (CornerView corner : corners.values()) {
            manager.removeView(corner);
        }
        corners.clear();
        cornerAdded = false;
    }
    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy :::");
        Intent intent = new Intent(this, KeepCornerLiveService.class);
        intent.putExtra(SettingsDataKeeper.CORNER_ENABLE,cornerEnable);
        intent.setAction(SettingsDataKeeper.CORNER_ENABLE);
        startService(intent);
        super.onDestroy();
    }

    public class InnerService extends Service{

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification notification = Utilities.buildNotification(this);
            startForeground(NOTIFICATION_ID, notification);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            },100);
        }
    }
}
