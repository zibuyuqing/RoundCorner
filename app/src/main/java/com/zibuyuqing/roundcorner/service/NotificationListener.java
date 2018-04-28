package com.zibuyuqing.roundcorner.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.List;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListener extends NotificationListenerService{
    private static final String TAG = "NotificationListener" ;
    private static final String SYSTEM_UI = "com.android.systemui";
    private static final String ANDRIOD = "android";
    public static final String ME = "com.zibuyuqing.roundcorner";
    private String who;
    private static boolean sIsConnected;
    private static boolean sIsCreated;
    private static NotificationListener sInstance;
    private static NotificationsChangedListener sNotificationsChangedListener;
    private PackageManager mPackageManager;
    public NotificationListener(){
        super();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "NotificationListener onCreate " + Process.myPid() +",   ===" + Process.myUid() +",," + Process.myUserHandle());
        sIsCreated = true;
        mPackageManager = getPackageManager();
        if(!isEnhanceNotificationEnable()){
            if(!Utilities.isBeforeAndroidN()) {
                requestUnbind();
            } else {
                disableListenerService();
            }
        } else {
            Log.e(TAG, "NotificationListener onCreate -- ensureListenerRunning");
            if(Utilities.isBeforeAndroidN()){
                toggleNotificationListenerService();
            } else {
                ensureListenerRunning(getApplicationContext());
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsCreated = false;
    }
    public static NotificationListener getInstanceIfConnected(){
        return sIsConnected ? sInstance : null;
    }



    //确认NotificationMonitor是否开启
    @TargetApi(Build.VERSION_CODES.N)
    private void ensureListenerRunning(Context context) {
        Log.e(TAG,"ensureCollectorRunning");
        ComponentName component = new ComponentName(context, NotificationListener.class);
        NotificationListener.requestRebind(component);
    }
    private void disableListenerService(){
        mPackageManager.setComponentEnabledSetting(new ComponentName(this, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    private void enableListenerService(){
        mPackageManager.setComponentEnabledSetting(new ComponentName(this,  com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void requestRebind(Context context){
        if(Utilities.isBeforeAndroidN()){
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            packageManager.setComponentEnabledSetting(new ComponentName(context, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(new ComponentName(context,  com.zibuyuqing.roundcorner.service.NotificationListener.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            NotificationListener.requestRebind(new ComponentName(
                    context, NotificationListener.class));
        }
    }

    private void toggleNotificationListenerService() {
        Log.e(TAG,"toggleNotificationListenerService");
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
        NotificationListener notificationListener = getInstanceIfConnected();
        if (notificationListener != null) {
            notificationListener.postMe();
        } else if (!sIsCreated && sNotificationsChangedListener != null) {
            Log.d(TAG, "NotificationListener User turned off ");
        }
    }

    public static void removeNotificationsChangedListener() {
        Log.e(TAG, "NotificationListener removeNotificationsChangedListener");
        sNotificationsChangedListener = null;
    }

    private void postMe(){
        Log.e(TAG,"postMe :: sNotificationsChangedListener =:" + sNotificationsChangedListener);
        if(sNotificationsChangedListener != null){
            sNotificationsChangedListener.onNotificationPosted(ME);
        }
    }
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.e(TAG, "NotificationListener onListenerConnected");
        sIsConnected = true;
        postMe();
    }
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.e(TAG, "NotificationListener onListenerDisconnected");
        sIsConnected = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.e(TAG,"onNotificationPosted sbn =:" + sbn);
        if(sNotificationsChangedListener != null){
            sNotificationsChangedListener.onNotificationPosted(sbn);
            sNotificationsChangedListener.onNotificationPosted(sbn.getPackageName());
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG,"onRebind =:" + intent);
        return super.onUnbind(intent);
    }
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG,"onRebind =:" + intent);
        toggleNotificationListenerService();
    }

    private boolean isEnhanceNotificationEnable(){
        return SettingsDataKeeper.getSettingsBoolean(this,SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }
    public interface NotificationsChangedListener {
        void onNotificationPosted(StatusBarNotification sbn);
        void onNotificationPosted(String who);
    }
}
