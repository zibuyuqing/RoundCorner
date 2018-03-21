package com.zibuyuqing.roundcorner.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
    private String who;
    private boolean isConnected;
    private boolean isCreated;
    private static NotificationListener sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"oncreate");
        isCreated = true;
        ensureCollectorRunning();
        sInstance = this;
    }

    //确认NotificationMonitor是否开启
    private void ensureCollectorRunning() {
        Log.e(TAG,"ensureCollectorRunning");
        ComponentName collectorComponent = new ComponentName(this, NotificationListener.class);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null ) {
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                if (service.pid == Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            return;
        }
        toggleNotificationListenerService();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreated = false;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.e(TAG,"onListenerConnected");
        isConnected = true;
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, NotificationListener.class);
        context.startService(starter);
    }
    public void stop(){
        NotificationListener.this.stopSelf();
    }
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.e(TAG,"onListenerDisconnected");
        isConnected = false;
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        who = sbn.getPackageName();
        if(who.equals(SYSTEM_UI)||who.equals(ANDRIOD)){
            return;
        }
        Log.e(TAG,"onNotificationPosted who =:" + who);
        LocalControllerService.tryToAddNotificationLine(this,who);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.e(TAG,"onRebind =:" + intent);
        toggleNotificationListenerService();
    }
    private void toggleNotificationListenerService() {
        Log.e(TAG,"toggleNotificationListenerService");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}
