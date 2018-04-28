package com.zibuyuqing.roundcorner.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.model.db.AppLoadTask;
import com.zibuyuqing.roundcorner.model.db.DbManager;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.RemoteService;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.ArrayList;


/**
 * Created by Xijun.Wang on 2017/10/31.
 */

public class SplashActivity  extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected int providedLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        View view = window.getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        Utilities.checkStoragePermission(this);
        startServices();
        toMain();
    }

    private void startServices() {
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
        startLoadApps();
    }

    private void startLoadApps() {
        AppLoadTask.execute(this);
    }

    private void toMain() {
        HomeActivity.start(SplashActivity.this);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utilities.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if(grantResults == null || grantResults.length <=0){
                    return;
                }
                for(int i = 0; i<grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        showTips(R.string.storage_permission_denied);
                        return;
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
