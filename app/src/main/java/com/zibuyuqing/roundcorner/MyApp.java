package com.zibuyuqing.roundcorner;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import com.zibuyuqing.roundcorner.log.CrashHandler;
import com.zibuyuqing.roundcorner.service.NotificationListener;
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
    }
    public static MyApp getInstanceNoCreate() {
        return sInstance;
    }
    public Context getContext() {
        return mContext;
    }
}
