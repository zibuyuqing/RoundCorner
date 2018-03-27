package com.zibuyuqing.roundcorner;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zibuyuqing.roundcorner.model.DaoMaster;
import com.zibuyuqing.roundcorner.model.DaoSession;
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
    }
    public static MyApp getInstance(final Context context){
        if(sInstance == null){
            sInstance = new MyApp(context.getApplicationContext());
        }
        return sInstance;
    }
    private MyApp(Context context){
        mContext = context;
        if(SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE)) {
            NotificationListener.requestRebind(new ComponentName(
                    mContext, NotificationListener.class));
        }
    }
    public static MyApp getInstanceNoCreate() {
        return sInstance;
    }
    public Context getContext() {
        return mContext;
    }
}
