package com.zibuyuqing.roundcorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppConfigActivity extends BaseActivity {
    private final static String EXTRA_KEY = "app_info";
    private Bundle mAppInfo;
    private PackageManager mPackageManager;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_app_config;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if(intent != null){
            mPackageManager = this.getApplicationContext().getPackageManager();
            mAppInfo = intent.getExtras().getBundle(EXTRA_KEY);
        } else {
            finish();
        }
    }
    public static void start(Context context, AppInfo appInfo) {
        Intent starter = new Intent(context, AppConfigActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_KEY,appInfo);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }
}
