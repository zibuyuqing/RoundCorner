package com.zibuyuqing.roundcorner;

import android.app.Application;
/**
 * Created by Xijun.Wang on 2017/11/2.
 */

public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static MyApp getInstance(){
        return instance;
    }
}
