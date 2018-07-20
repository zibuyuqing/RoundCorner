package com.zibuyuqing.roundcorner;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.umeng.commonsdk.UMConfigure;
import com.zibuyuqing.roundcorner.log.CrashHandler;
import com.zibuyuqing.roundcorner.service.NotificationListener;
import com.zibuyuqing.roundcorner.ui.activity.HomeActivity;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;

/**
 * Created by Xijun.Wang on 2017/11/2.
 */

public class MyApp extends Application {
    private static MyApp sInstance;
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.instance();
        crashHandler.init(getApplicationContext());
        if(SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE)) {
            NotificationListener.requestRebind(mContext);
        }
        UMConfigure.init(getApplicationContext(), "5ac1cd08f29d98595600002a", "KUAN",UMConfigure.DEVICE_TYPE_PHONE, "");
    }
    public static MyApp getInstanceNoCreate() {
        return sInstance;
    }
    public Context getContext() {
        return mContext;
    }
}
