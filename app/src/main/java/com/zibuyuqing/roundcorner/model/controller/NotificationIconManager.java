package com.zibuyuqing.roundcorner.model.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.zibuyuqing.roundcorner.adapter.NotificationListAdapter;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.NotificationInfo;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.NotificationListener;
import com.zibuyuqing.roundcorner.ui.widget.DanmakuNotificationView;
import com.zibuyuqing.roundcorner.ui.widget.IconNotificationView;
import com.zibuyuqing.roundcorner.ui.widget.NotificationListWindow;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationIconManager implements NotificationListAdapter.OnItemClickListener, IconNotificationView.OnActionListener, NotificationListWindow.OnClearNotificationsListener {
    private static final String TAG = NotificationIconManager.class.getSimpleName();
    private static NotificationIconManager sInstance;
    private NotificationListWindow mNotificationListWindow;
    private IconNotificationView mNotificationIcon;
    private Context mContext;

    private NotificationIconManager(Context context){
        mContext = context;
        init();
    }

    private void init() {
        mNotificationListWindow = new NotificationListWindow(mContext);
        mNotificationListWindow.setOnItemClickListener(this);
        mNotificationListWindow.setOnClearNotificationsListener(this);
        mNotificationIcon = new IconNotificationView(mContext);
        mNotificationIcon.setOnActionListener(this);
    }

    public void removeNotification(StatusBarNotification sbn){
        if(isCollectNotificationEnable()){
            String who = sbn.getPackageName();//获取发起通知的包名
            mNotificationListWindow.removeNotification(who);
            if(mNotificationListWindow.isAttachedToWindow()){
                float iconX = mNotificationIcon.getPositionX();
                float iconY = mNotificationIcon.getPositionY();
                mNotificationListWindow.showAt(iconX, iconY);
            }
            if(mNotificationListWindow != null) {
                NotificationInfo firstInfo = mNotificationListWindow.getNotifications() == null? null : mNotificationListWindow.getNotifications().get(0);
                if(firstInfo != null) {
                    mNotificationIcon.updateIcon(firstInfo.getIcon(), firstInfo.getPackageName());
                } else {
                    mNotificationIcon.dismiss();
                }
            }
        }
    }
    public void animateShowNotificationIcon(StatusBarNotification sbn){
        mNotificationIcon.setConfig(Utilities.getIconConfig(mContext));
        String who = sbn.getPackageName();//获取发起通知的包名
        Notification notification = sbn.getNotification();//获取通知对象
        Bundle extras = notification.extras;
        CharSequence messageContent = extras.getCharSequence(Notification.EXTRA_TEXT);//获取通知内容
        CharSequence messageOwner = extras.getCharSequence(Notification.EXTRA_TITLE) ;//获取通知标题
        Log.d(TAG,"notificationText =:" + messageContent +",notificationTitle =:" + messageOwner);
        if(TextUtils.isEmpty(messageContent)){
            return ;
        }
        PackageManager packageManager = mContext.getPackageManager();
        try {
            Drawable icon = packageManager.getApplicationIcon(who);
            if(isCollectNotificationEnable()) {
                long time = System.currentTimeMillis();
                NotificationInfo info = new NotificationInfo(sbn.getId(),who, messageOwner.toString(), messageContent.toString(), icon,time);
                mNotificationListWindow.addNotification(info);
                if(mNotificationListWindow.isAttachedToWindow()) {
                    float iconX = mNotificationIcon.getPositionX();
                    float iconY = mNotificationIcon.getPositionY();
                    mNotificationListWindow.showAt(iconX, iconY);
                }
            }
            mNotificationIcon.animateShow(who,icon,false);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void showIconByConfigChanged() {
        try {
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            Log.d(TAG,"showIconByConfigChanged ;; ");
            if(mNotificationIcon != null){
                if(mNotificationIcon.isAttachedToWindow()){
                    mNotificationIcon.requestLayout();
                }
            }
            NotificationManager  manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0x66,Utilities.buildDemoNotification(mContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean appEnable(String who){
        return AppInfoDaoOpe.appEnable(mContext,who);
    }

    public void showNotificationIcon(StatusBarNotification sbn) {
        try {
            boolean isEnhanceNotificationEnable = isEnhanceNotificationEnable();
            Log.d(TAG,"showDanmu : isEnhanceNotificationEnable =:" + isEnhanceNotificationEnable);
            if (!isEnhanceNotificationEnable) {
                return;
            }
            String who = sbn.getPackageName();
            boolean appEnable = appEnable(who);
            Log.d(TAG,"showDanmu : appEnable =:" + appEnable);
            if(appEnable(who)) {
                animateShowNotificationIcon(sbn);
            }
        } catch (Exception e) {
            Log.e(TAG, "who ccc  =:" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void dismiss(){
        if(mNotificationListWindow != null){
            if(mNotificationListWindow.isAttachedToWindow()){
                mNotificationListWindow.dismiss();
            }
        }
        if(mNotificationIcon != null){
            if(mNotificationIcon.isAttachedToWindow()){
                mNotificationIcon.dismiss();
            }
        }
    }
    public void showOrHideIcon() {
        if (isEnhanceNotificationEnable()) {
            showIconByConfigChanged();
        } else {
            // TODO: 2018/6/1
            dismiss();
        }
    }

    private boolean isEnhanceNotificationEnable() {
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }
    private boolean isCollectNotificationEnable(){
        return SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.ICON_COLLECT_NOTIFICATION_ENABLE);
    }
    public static NotificationIconManager getInstance(Context context){
        if(sInstance == null){
            synchronized (NotificationLineManager.class){
                if(sInstance == null){
                    sInstance = new NotificationIconManager(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onIconClicked() {
        Log.e(TAG,"onIconClicked isCollectNotificationEnable =:" + isCollectNotificationEnable());
        if(isCollectNotificationEnable()){
            if(mNotificationListWindow != null){
                if(!mNotificationListWindow.isAttachedToWindow()) {
                    float iconX = mNotificationIcon.getPositionX();
                    float iconY = mNotificationIcon.getPositionY();
                    mNotificationListWindow.showAt(iconX, iconY);
                } else {
                    mNotificationListWindow.dismiss();
                }
            }
        } else {
            Utilities.startAppByPackageName(mContext, mNotificationIcon.getIdentify());
        }
    }

    @Override
    public void onIconMoved(float x, float y) {
        if(mNotificationListWindow != null){
            if(mNotificationListWindow.isAttachedToWindow()){
                mNotificationListWindow.showAt(x,y);
            }
        }
    }

    @Override
    public void onIconSizeChanged() {
        if(mNotificationListWindow != null){
            mNotificationListWindow.updateOffset();
        }
    }

    @Override
    public void onIconDismiss() {
        if(mNotificationListWindow != null){
            mNotificationListWindow.dismiss();
        }
    }

    @Override
    public void onItemClicked(int position, NotificationInfo info) {
        Utilities.startAppByPackageName(mContext,info.getPackageName());
    }

    @Override
    public void onNotificationsClear() {
        NotificationListener listener = NotificationListener.getInstanceIfConnected();
        Log.e(TAG,"onNotificationsClear === :" + listener);
        if(listener!= null){
            try {
                listener.cancelAllNotifications();
            } catch (Exception e){

            }
        }
    }

    public void onIconCollectEnableChanged() {
        if(mNotificationListWindow != null){
            if(mNotificationListWindow.isAttachedToWindow()){
                mNotificationListWindow.dismiss();
            }
        }
    }
}
