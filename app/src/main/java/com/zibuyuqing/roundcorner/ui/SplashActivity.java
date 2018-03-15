package com.zibuyuqing.roundcorner.ui;

import android.os.Handler;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;


/**
 * Created by Xijun.Wang on 2017/10/31.
 */

public class SplashActivity  extends BaseActivity{
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },2000);
    }
}
