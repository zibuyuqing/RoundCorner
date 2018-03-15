package com.zibuyuqing.roundcorner.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

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
    private String who;
    private boolean isConnected;
    private boolean isCreated;

    @Override
    public void onCreate() {
        super.onCreate();
        isCreated = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCreated = false;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        isConnected = true;
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        isConnected = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        who = sbn.getPackageName();
    }
}
