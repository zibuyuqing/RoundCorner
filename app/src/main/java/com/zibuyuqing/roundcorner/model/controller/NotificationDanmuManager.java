package com.zibuyuqing.roundcorner.model.controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.ui.widget.DanmakuNotificationView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

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
public class NotificationDanmuManager implements DanmakuNotificationView.AnimationStateListener {
    private static final String TAG = NotificationDanmuManager.class.getSimpleName();
    private static NotificationDanmuManager sInstance;
    private Context mContext;
    private List<DanmakuNotificationView> mDanmuList;

    private NotificationDanmuManager(Context context){
        mContext = context;
        init();
    }

    private void init() {
        if(mDanmuList == null){
            mDanmuList = new ArrayList<>();
        }
    }
    private DanmakuNotificationView buildNewDanmaku(String text, Drawable icon){
        DanmakuNotificationView danmaku = new DanmakuNotificationView(mContext);
        if(isUseRandomStyle()){
            danmaku.configRandomStyle();
        } else {
            danmaku.setConfig(Utilities.getDanmuConfig(mContext));
        }
        danmaku.show("【屏幕圆角】 ",text,icon);
        danmaku.addDanmakuStateListener(this);
        return danmaku;
    }
    public DanmakuNotificationView buildNewDanmaku(StatusBarNotification sbn){
        DanmakuNotificationView danmaku = new DanmakuNotificationView(mContext);
        if(isUseRandomStyle()) {
            danmaku.configRandomStyle();
        } else {
            danmaku.setConfig(Utilities.getDanmuConfig(mContext));
        }
        String who = sbn.getPackageName();//获取发起通知的包名
        Notification notification = sbn.getNotification();//获取通知对象
        Bundle extras = notification.extras;
        CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);//获取通知内容
        CharSequence notificationTitle = "【" + extras.getCharSequence(Notification.EXTRA_TITLE) +"】 ";//获取通知标题
        Log.d(TAG,"notificationText =:" + notificationText +",notificationTitle =:" + notificationTitle);
        if(TextUtils.isEmpty(notificationText)){
            return null;
        }
        PackageManager packageManager = mContext.getPackageManager();
        danmaku.addDanmakuStateListener(this);
        try {
            danmaku.show(notificationTitle.toString(),notificationText.toString(),packageManager.getApplicationIcon(who));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PendingIntent intent = notification.contentIntent;
        if(intent != null){
            danmaku.setAction(intent);
        }
        return danmaku;
    }
    public void showDanmakuByConfigChanged() {
        try {
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            if(isUseRandomStyle()){
                return;
            }
            String testDanmaku = "示例弹幕 纸短情长";
            PackageManager packageManager = mContext.getPackageManager();
            Drawable icon = packageManager.getApplicationIcon(LocalControllerService.ME);
            buildNewDanmaku(testDanmaku,icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean appEnable(String who){
        return AppInfoDaoOpe.appEnable(mContext,who);
    }

    public void showDanmu(StatusBarNotification sbn) {
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
                buildNewDanmaku(sbn);
            }
        } catch (Exception e) {
            Log.e(TAG, "who ccc  =:" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void removeAllDanmakus() {
        for(DanmakuNotificationView danmaku : mDanmuList){
            danmaku.destroy();
        }
    }
    public void showOrHideDanmaku() {
        if (isEnhanceNotificationEnable()) {
            showDanmakuByConfigChanged();
        } else {
            removeAllDanmakus();
        }
    }

    private boolean isEnhanceNotificationEnable() {
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }
    private boolean isUseRandomStyle(){
        return SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.DANMU_USE_RANDOM_STYlE_ENABLE);
    }
    public static NotificationDanmuManager getInstance(Context context){
        if(sInstance == null){
            synchronized (NotificationLineManager.class){
                if(sInstance == null){
                    sInstance = new NotificationDanmuManager(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onStartShowDanmu(DanmakuNotificationView view) {
        if(!mDanmuList.contains(view)){
            mDanmuList.add(view);
        }
    }

    @Override
    public void onStopShowDanmu(DanmakuNotificationView view) {
        Log.d(TAG,"onStopShowDanmu :: view =:" + view.getDanmakuText());
        if(mDanmuList.contains(view)){
            mDanmuList.remove(view);
        }
        view.destroy();
        view = null;
        Log.d(TAG,"onStopShowDanmu :: mDanmuList =:" + mDanmuList.size());
    }
}
