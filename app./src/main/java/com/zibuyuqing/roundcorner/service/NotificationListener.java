package com.zibuyuqing.roundcorner.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */

@SuppressLint("OverrideAbstract")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private static final String SYSTEM_UI = "com.android.systemui";
    private static final String ANDRIOD = "android";
    public static final String ME = "com.zibuyuqing.roundcorner";
    private static boolean sIsConnected;
    private static boolean sIsCreated;
    private static NotificationListener sInstance;
    private static NotificationsChangedListener sNotificationsChangedListener;
    private PackageManager mPackageManager;
    private String mCurrentWho = "";
    private long mLastPostTime = 0;
    public NotificationListener() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sIsCreated = true;
        mPackageManager = getPackageManager();
        sInstance = this;
        if (!isEnhanceNotificationEnable()) {
            if (!Utilities.isBeforeAndroidN()) {
                requestUnbind();
            }
        } else {
            Log.e(TAG, "NotificationListener onCreate -- ensureListenerRunning");
            if (Utilities.isBeforeAndroidN()) {
                toggleNotificationListenerService();
            } else {
                try {
                    ensureListenerRunning(getApplicationContext());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsCreated = false;
    }

    public static NotificationListener getInstanceIfConnected() {
        return sIsConnected ? sInstance : null;
    }


    //确认NotificationMonitor是否开启
    @TargetApi(Build.VERSION_CODES.N)
    private void ensureListenerRunning(Context context) {
        ComponentName component = new ComponentName(context, NotificationListener.class);
        NotificationListener.requestRebind(component);
    }

    private void disableListenerService() {
        mPackageManager.setComponentEnabledSetting(new ComponentName(this, NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    private void enableListenerService() {
        mPackageManager.setComponentEnabledSetting(new ComponentName(this, NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void requestRebind(Context context) {
        if (Utilities.isBeforeAndroidN()) {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            packageManager.setComponentEnabledSetting(new ComponentName(context, NotificationListener.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(new ComponentName(context, NotificationListener.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            NotificationListener.requestRebind(new ComponentName(
                    context, NotificationListener.class));
        }
    }

    private void toggleNotificationListenerService() {
        Log.e(TAG, "toggleNotificationListenerService");
        disableListenerService();
        enableListenerService();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, NotificationListener.class);
        context.startService(starter);
    }

    public static void setNotificationsChangedListener(NotificationsChangedListener listener) {
        Log.d(TAG, "NotificationListener setNotificationsChangedListener");
        sNotificationsChangedListener = listener;
    }

    public static void removeNotificationsChangedListener() {
        Log.d(TAG, "NotificationListener removeNotificationsChangedListener");
        sNotificationsChangedListener = null;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "NotificationListener onListenerConnected");
        sIsConnected = true;
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "NotificationListener onListenerDisconnected");
        sIsConnected = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sNotificationsChangedListener != null) {
            String who = sbn.getPackageName();
            long currentTime = System.currentTimeMillis();
            long duration = currentTime - mLastPostTime;
            Log.d(TAG, "onNotificationPosted sbn =:" + sbn +",\n duration =:" + duration + ",mCurrentWho =:" + mCurrentWho +",who =:" + who);
            if(duration > 1000){
                sNotificationsChangedListener.onNotificationPosted(sbn);
                mLastPostTime = currentTime;
                mCurrentWho = who;
            } else {
                if(!mCurrentWho.equals(who)){
                    sNotificationsChangedListener.onNotificationPosted(sbn);
                    mLastPostTime = currentTime;
                    mCurrentWho = who;
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if(sNotificationsChangedListener != null){
            sNotificationsChangedListener.onNotificationRemoved(sbn);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        toggleNotificationListenerService();
    }

    private boolean isEnhanceNotificationEnable() {
        return SettingsDataKeeper.getSettingsBoolean(this, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }

    public interface NotificationsChangedListener {
        void onNotificationPosted(StatusBarNotification sbn);
        void onNotificationRemoved(StatusBarNotification sbn);
    }
}
