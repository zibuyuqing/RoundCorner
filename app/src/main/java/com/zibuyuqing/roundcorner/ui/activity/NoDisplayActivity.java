package com.zibuyuqing.roundcorner.ui.activity;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/05/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NoDisplayActivity extends BaseActivity{
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = 1;
        layoutParams.width = 1;
        layoutParams.x = 0;
        layoutParams.y = 0;
        window.setAttributes(layoutParams);
        finish();
    }
}
