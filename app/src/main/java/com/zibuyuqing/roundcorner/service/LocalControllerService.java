package com.zibuyuqing.roundcorner.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import com.zibuyuqing.roundcorner.model.controller.NotificationLineManager;
import com.zibuyuqing.roundcorner.ui.activity.AppConfigActivity;
import com.zibuyuqing.roundcorner.ui.widget.DanmakuNotificationView;
import com.zibuyuqing.roundcorner.ui.widget.EdgeLineView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

/**
 * Created by Xijun.Wang on 2017/11/7.
 */

public class LocalControllerService extends Service implements NotificationListener.NotificationsChangedListener {
    private static final String TAG = LocalControllerService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 0x11;
    public static final String ACTION_TRY_TO_ADD_CORNERS = "try_to_add_corners";
    public static final String ACTION_TRY_TO_ADD_NOTIFICATION_LINE = "try_to_add_notification_line";
    public static final String NOTIFICATION_IDENTIFY = "notification_identify";
    public static final String ME = "com.zibuyuqing.roundcorner";
    public static final String ACTION_APP_ENABLE_STATE_CHANGED = "com.zibuyuqing.roundcorner.ACTION_APP_ENABLE_STATE_CHANGED";
    private BroadcastReceiver mReceiver;
    private LocalBinder mBinder;
    private LocalConn mConnection;
    private CornerManager mCornerManager;
    private NotificationLineManager mLineManager;
    private NotificationDanmuManager mDanmakuManager;

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
        Log.e(TAG, "showOrHideNotify ::show = ：" + isNotifyEnable);
        if (isNotifyEnable) {
            Notification notification = Utilities.buildNotification(this);
            startForeground(NOTIFICATION_ID, notification);
        } else {
            stopForeground(true);
        }
    }

    private void init() {
        Log.e(TAG, "LocalControllerService init");
        mBinder = new LocalBinder();
        if (mConnection == null) {
            mConnection = new LocalConn();
        }
        if (mCornerManager == null) {
            mCornerManager = CornerManager.getInstance(this);
        }
        if (mLineManager == null) {
            mLineManager = NotificationLineManager.getInstance(this);
        }
        if(mDanmakuManager == null){
            mDanmakuManager = NotificationDanmuManager.getInstance(this);
        }
        NotificationListener.start(this.getApplicationContext());
        NotificationListener.setNotificationsChangedListener(this);
        tryToAddCorners(this);
        tryToAddNotificationLine(this, ME);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_APP_ENABLE_STATE_CHANGED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_APP_ENABLE_STATE_CHANGED.equals(intent.getAction())) {
                    Log.e(TAG, "intent.getExtras() =:" + intent.getExtras());
                    if (intent.getExtras() != null) {
                        AppInfo info = intent.getParcelableExtra(AppConfigActivity.EXTRA_KEY);
                        mLineManager.updateAppMap(info);
                        return;
                    }
                    mLineManager.updateEnableAppMap();
                }
            }
        };
        manager.registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //绑定远程服务
        bindService(new Intent(LocalControllerService.this, RemoteService.class),
                mConnection,
                Context.BIND_IMPORTANT);
        if (intent != null) {
            String action = intent.getAction();
            Log.e(TAG, "action =:" + action);
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
            case SettingsDataKeeper.BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE:
            case SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION:
                Intent i = new Intent(LocalControllerService.ACTION_APP_ENABLE_STATE_CHANGED);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            case SettingsDataKeeper.MIXED_COLOR_ONE:
            case SettingsDataKeeper.MIXED_COLOR_TWO:
            case SettingsDataKeeper.MIXED_COLOR_THREE:
                showEdgeLineForPreview();
                break;
        }
    }

    private void startOrStopListenNotification() {
        mLineManager.showOrHideEdgeLine();
        mDanmakuManager.showOrHideDanmaku();
    }

    private void showEdgeLineForPreview() {
        mLineManager.showEdgeLineByConfigChanged();
    }

    private void showEdgeLine(Intent intent) {
        String who = intent.getStringExtra(NOTIFICATION_IDENTIFY);
        //mLineManager.showEdgeLine(who);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "keep corner service killed--------");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mReceiver = null;
        NotificationListener.removeNotificationsChangedListener();
        Intent intent = new Intent(this, LocalControllerService.class);
        intent.putExtra(SettingsDataKeeper.CORNER_ENABLE, true);
        intent.setAction(SettingsDataKeeper.CORNER_ENABLE);
        startService(intent);
        super.onDestroy();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e(TAG, "onNotificationPosted :: sbn =:" + sbn);
        if(mDanmakuManager != null){
            mDanmakuManager.showDanmu(sbn);
        }
    }

    @Override
    public void onNotificationPosted(String who) {
        Log.e(TAG, "onNotificationPosted :: who =:" + who);
        //tryToAddNotificationLine(this, who);
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
            Log.e(TAG, "Local 链接远程服务成功   *******");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //远程服务被干掉；连接断掉的时候走此回调
            //在连接RemoateService异常断时，会回调；也就是RemoteException
            Log.e(TAG, "RemoteService killed--------");
            startService(new Intent(LocalControllerService.this, RemoteService.class));
            //绑定远程服务
            bindService(new Intent(LocalControllerService.this, RemoteService.class),
                    mConnection, Context.BIND_IMPORTANT);
        }
    }
}
