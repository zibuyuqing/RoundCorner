package com.zibuyuqing.roundcorner.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.R;

import butterknife.ButterKnife;

/**
 * Created by Xijun.Wang on 2017/11/1.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(providedLayoutId());
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary,null));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION );
        init();
    }

    public void showTips(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void showTips(int resId){
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }
    protected abstract int providedLayoutId();
    protected abstract void init();
}
