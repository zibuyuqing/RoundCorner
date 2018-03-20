package com.zibuyuqing.roundcorner.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
    private String who;
    private boolean isConnected;
    private boolean isCreated;
    private static NotificationListener sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"oncreate");
        isCreated = true;
        sInstance = this;
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
        context.startActivity(starter);
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
        if(who.equals(SYSTEM_UI)){
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
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, com.zibuyuqing.roundcorner.service.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}
