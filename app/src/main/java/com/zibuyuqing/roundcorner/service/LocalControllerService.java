package com.zibuyuqing.roundcorner.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.zibuyuqing.roundcorner.IProcessService;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.controller.CornerManager;
import com.zibuyuqing.roundcorner.model.controller.NotificationDanmuManager;
import com.zibuyuqing.roundcorner.model.controller.NotificationIconManager;
import com.zibuyuqing.roundcorner.model.controller.NotificationLineManager;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import static com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment.NOTIFICATION_STYLE_DANMAKU;
import static com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment.NOTIFICATION_STYLE_ICON;
import static com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment.NOTIFICATION_STYLE_LINE;

/**
 * Created by Xijun.Wang on 2017/11/7.
 */

public class LocalControllerService extends Service implements NotificationListener.NotificationsChangedListener {
    private static final String TAG = LocalControllerService.class.getSimpleName();
    private static final int MSG_SHOW_DANMAKU = 0x22;
    private static final int MSG_SHOW_ICON = 0x33;
    public static final int NOTIFICATION_ID = 0x11;
    public static final String ACTION_TRY_TO_ADD_CORNERS = "try_to_add_corners";
    public static final String ACTION_TRY_TO_ADD_NOTIFICATION_LINE = "try_to_add_notification_line";
    public static final String NOTIFICATION_IDENTIFY = "notification_identify";
    public static final String ME = "com.zibuyuqing.roundcorner";
    public static final String ACTION_APP_ENABLE_STATE_CHANGED = "com.zibuyuqing.roundcorner.ACTION_APP_ENABLE_STATE_CHANGED";
    private LocalBinder mBinder;
    private LocalConn mConnection;
    private CornerManager mCornerManager;
    private NotificationLineManager mLineManager;
    private NotificationDanmuManager mDanmakuManager;
    private NotificationIconManager mIconManager;
    private Handler mHandler;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void showOrHideNotify() {
        //API 18以下，直接发送Notification并将其置为前台
        boolean isNotifyEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.NOTIFICATION_ENABLE);
        if (isNotifyEnable) {
            Notification notification = Utilities.buildNotification(this);
            startForeground(NOTIFICATION_ID, notification);
        } else {
            stopForeground(true);
        }
    }

    private void init() {
        mBinder = new LocalBinder();
        if (mConnection == null) {
            mConnection = new LocalConn();
        }
        if (mCornerManager == null) {
            mCornerManager = CornerManager.getInstance(this.getApplicationContext());
        }
        if (mLineManager == null) {
            mLineManager = NotificationLineManager.getInstance(this.getApplicationContext());
        }
        if(mDanmakuManager == null){
            mDanmakuManager = NotificationDanmuManager.getInstance(this.getApplicationContext());
        }
        if(mIconManager == null){
            mIconManager = NotificationIconManager.getInstance(this.getApplicationContext());
        }
        NotificationListener.start(this.getApplicationContext());
        NotificationListener.setNotificationsChangedListener(this);
        tryToAddCorners(this);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_SHOW_DANMAKU:
                        if(mDanmakuManager != null){
                            StatusBarNotification sbn = (StatusBarNotification) msg.obj;
                            mDanmakuManager.showDanmu(sbn);
                        }
                        break;
                    case MSG_SHOW_ICON:
                        if(mIconManager != null){
                            StatusBarNotification sbn = (StatusBarNotification) msg.obj;
                            mIconManager.showNotificationIcon(sbn);
                        }
                }
            }
        };
    }

    private void showDanmaku(StatusBarNotification sbn){
        Message message = new Message();
        message.what = MSG_SHOW_DANMAKU;
        message.obj = sbn;
        mHandler.sendMessage(message);
    }
    private void showIcon(StatusBarNotification sbn){
        Message message = new Message();
        message.what = MSG_SHOW_ICON;
        message.obj = sbn;
        mHandler.sendMessage(message);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //绑定远程服务
        bindService(new Intent(LocalControllerService.this, RemoteService.class),
                mConnection,
                Context.BIND_IMPORTANT);
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "action =:" + action);
            if (!TextUtils.isEmpty(action)) {
                handleAction(intent, action);
            }
            NotificationListener.setNotificationsChangedListener(this);
        } else {
            NotificationListener.start(this.getApplicationContext());
            NotificationListener.setNotificationsChangedListener(this);
            RemoteService.start(LocalControllerService.this);
            tryToAddCorners(LocalControllerService.this);
        }
        return Service.START_STICKY;
    }

    public static void tryToAddCorners(Context context) {
        Intent intent = new Intent(context, LocalControllerService.class);
        intent.setAction(ACTION_TRY_TO_ADD_CORNERS);
        context.startService(intent);
    }

    public static void tryToAddNotificationLine(Context context, String who) {
        Intent intent = new Intent(context, LocalControllerService.class);
        intent.setAction(ACTION_TRY_TO_ADD_NOTIFICATION_LINE);
        intent.putExtra(NOTIFICATION_IDENTIFY, who);
        context.startService(intent);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LocalControllerService.class);
        context.startService(starter);
    }

    private void handleAction(Intent intent, String action) {
        switch (action) {
            case ACTION_TRY_TO_ADD_CORNERS:
                mCornerManager.tryToAddCorners();
                break;
            case SettingsDataKeeper.CORNER_COLOR:
                mCornerManager.changeCornersColor();
                break;
            case SettingsDataKeeper.CORNER_SIZE:
                mCornerManager.changeCornersSize();
                break;
            case SettingsDataKeeper.CORNER_OPACITY:
                mCornerManager.changeCornersOpacity();
                break;
            case SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE:
                mCornerManager.showOrHideLeftTopCorner();
                break;
            case SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE:
                mCornerManager.showOrHideLeftBottomCorner();
                break;
            case SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE:
                mCornerManager.showOrHideRightTopCorner();
                break;
            case SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE:
                mCornerManager.showOrHideRightBottomCorner();
                break;
            case SettingsDataKeeper.CORNER_ENABLE:
                showEdgeLineForPreview();
                mCornerManager.showOrHideCorners();
                break;
            case SettingsDataKeeper.NOTIFICATION_ENABLE:
                showOrHideNotify();
                break;
            case ACTION_TRY_TO_ADD_NOTIFICATION_LINE:
                showEdgeLine(intent);
                break;

            case SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE:
                startOrStopListenNotification();
                break;
            case SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG:
            case SettingsDataKeeper.NOTIFICATION_ANIMATION_STYLE:
            case SettingsDataKeeper.NOTIFICATION_LINE_SIZE:
            case SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION:
            case SettingsDataKeeper.MIXED_COLOR_ONE:
            case SettingsDataKeeper.MIXED_COLOR_TWO:
            case SettingsDataKeeper.MIXED_COLOR_THREE:
                showEdgeLineForPreview();
                break;
            case SettingsDataKeeper.DANMU_BG_OPACITY:
            case SettingsDataKeeper.DANMU_MOVE_SPEED:
            case SettingsDataKeeper.DANMU_PRIMARY_COLOR:
            case SettingsDataKeeper.DANMU_REPEAT_COUNT:
            case SettingsDataKeeper.DANMU_TEXT_COLOR:
                showDanmuForPreview();
                break;
            case SettingsDataKeeper.ICON_COLLECT_NOTIFICATION_ENABLE:
                onIconCollectEnableChanged();
                break;
            case SettingsDataKeeper.ICON_BG_COLOR:
            case SettingsDataKeeper.ICON_SHAPE:
            case SettingsDataKeeper.ICON_SIZE:
            case SettingsDataKeeper.ICON_SHOW_DURATION:
                showIconForPreview();
                break;
            case SettingsDataKeeper.FULL_SCREEN_ENABLE:
                mLineManager.updateWindowForFullScreen();
                mCornerManager.updateWindowForFullScreen();
                break;
            case SettingsDataKeeper.ENHANCE_NOTIFICATION_STYLE:
                onNotificationStyleChanged();
                break;
        }
    }

    private void onNotificationStyleChanged() {
        if(getNotificationStyle() != NOTIFICATION_STYLE_ICON){
            if(mIconManager != null){
                mIconManager.dismiss();
            }
        }
    }

    private void showIconForPreview() {
        mIconManager.showIconByConfigChanged();
    }

    private void onIconCollectEnableChanged() {
        mIconManager.onIconCollectEnableChanged();
    }

    private void showDanmuForPreview() {
        mDanmakuManager.showDanmakuByConfigChanged();
    }

    private void startOrStopListenNotification() {
        switch (getNotificationStyle()){
            case NOTIFICATION_STYLE_LINE:
                mLineManager.showOrHideEdgeLine();
                break;
            case NOTIFICATION_STYLE_DANMAKU:
                mDanmakuManager.showOrHideDanmaku();
                break;
            case NOTIFICATION_STYLE_ICON:
                mIconManager.showOrHideIcon();
                break;
        }
    }

    private void showEdgeLineForPreview() {
        mLineManager.showEdgeLineByConfigChanged();
    }

    private void showEdgeLine(Intent intent) {
        String who = intent.getStringExtra(NOTIFICATION_IDENTIFY);
        mLineManager.showEdgeLine(who);
    }

    private int getNotificationStyle(){
        return SettingsDataKeeper.getSettingsInt(this,SettingsDataKeeper.ENHANCE_NOTIFICATION_STYLE);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "keep corner service killed--------");
        NotificationListener.removeNotificationsChangedListener();
        Intent intent = new Intent(this, LocalControllerService.class);
        intent.putExtra(SettingsDataKeeper.CORNER_ENABLE, true);
        intent.setAction(SettingsDataKeeper.CORNER_ENABLE);
        startService(intent);
        super.onDestroy();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        int style = getNotificationStyle();
        Log.d(TAG, "onNotificationPosted :: sbn =:" + sbn +" ,style= :" + style +",mDanmakuManager =:" + mDanmakuManager);
        switch (style){
            case NOTIFICATION_STYLE_LINE:
                Notification notification = sbn.getNotification();//获取通知对象
                if(notification != null) {
                    Bundle extras = notification.extras;
                    if(extras != null) {
                        CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);//获取通知内容
                        if(!TextUtils.isEmpty(notificationText)) {
                            tryToAddNotificationLine(this, sbn.getPackageName());
                        }
                    }
                }
                break;
            case NOTIFICATION_STYLE_DANMAKU:
                showDanmaku(sbn);
                break;
            case NOTIFICATION_STYLE_ICON:
                showIcon(sbn);
                break;
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(getNotificationStyle() == NOTIFICATION_STYLE_ICON){
            if(mIconManager != null){
                mIconManager.removeNotification(sbn);
            }
        }
    }

    private class LocalBinder extends IProcessService.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return TAG;
        }
    }

    private class LocalConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Local 链接远程服务成功   *******");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //远程服务被干掉；连接断掉的时候走此回调
            //在连接RemoateService异常断时，会回调；也就是RemoteException
            Log.d(TAG, "RemoteService killed--------");
            startService(new Intent(LocalControllerService.this, RemoteService.class));
            //绑定远程服务
            bindService(new Intent(LocalControllerService.this, RemoteService.class),
                    mConnection, Context.BIND_IMPORTANT);
        }
    }
}
