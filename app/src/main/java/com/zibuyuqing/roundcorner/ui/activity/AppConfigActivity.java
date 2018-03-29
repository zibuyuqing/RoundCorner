package com.zibuyuqing.roundcorner.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;
import com.zibuyuqing.roundcorner.ui.widget.LinearGradientView;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

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
    private final String TAG = AppConfigActivity.class.getSimpleName();
    private final static String EXTRA_KEY = "app_info";
    private AppInfo mAppInfo;
    private PackageManager mPackageManager;
    @BindView(R.id.iv_mixed_color_1)
    ImageView mIvMixedColorOne;
    @BindView(R.id.iv_mixed_color_2)
    ImageView mIvMixedColorTwo;
    @BindView(R.id.iv_mixed_color_3)
    ImageView mIvMixedColorThree;
    @BindView(R.id.iv_app_icon)
    ImageView mIvAppIcon;
    @BindView(R.id.tv_app_name)
    TextView mTvAppName;
//    @BindView(R.id.sw_listen_notification_enable)
//    Switch mSwListenNotification;
    @BindView(R.id.lgv_mixed_colors_preview)
    LinearGradientView mGradientView;
    @BindView(R.id.ll_app_icon_head)
    LinearLayout mLlAppIconHead;
    @BindView(R.id.toolbar)
    View mToolbar;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_app_config;
    }

    @Override
    protected void init() {
        ((TextView)mToolbar.findViewById(R.id.tv_title)).setText(getString(R.string.application_config));
        Intent intent = getIntent();
        if(intent != null){
            mPackageManager = this.getApplicationContext().getPackageManager();
            mAppInfo = intent.getParcelableExtra(EXTRA_KEY);
            initView(mAppInfo);
        } else {
            finish();
        }
    }
    void initView(AppInfo appInfo){
//        mSwListenNotification.setChecked(appInfo.getEnableState() == AppInfo.APP_ENABLE);
        try {
            Bitmap icon = ViewUtil.createIconBitmap(this,
                    mPackageManager.getApplicationIcon(appInfo.packageName));
            if(icon != null) {
                mIvAppIcon.setImageBitmap(icon);
                int color = ViewUtil.getMainColorFromBitmap(icon);
                mLlAppIconHead.setBackgroundColor(color);
                getWindow().setStatusBarColor(color);
            }
            mTvAppName.setText(appInfo.getTitle());
            mGradientView.setMixedColors(new int[]{appInfo.mixedColorOne,appInfo.mixedColorTwo,appInfo.mixedColorThree});
            updateMixedColor(mIvMixedColorOne,appInfo.getMixedColorOne());
            updateMixedColor(mIvMixedColorTwo,appInfo.getMixedColorTwo());
            updateMixedColor(mIvMixedColorThree,appInfo.getMixedColorThree());
            Log.e(TAG,appInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cancel)
    void cancel(){
        finish();
    }
    @OnClick(R.id.btn_ok)
    void confirm(){
        AppInfoDaoOpe.updateAppInfo(this,mAppInfo);
        finish();
    }
    private void chooseMixedColor(final ImageView imageView, int currentColor) {
        AlertDialog.Builder colorPickDialog = new AlertDialog.Builder(this);
        View colorPickLayout = View.inflate(this, R.layout.layout_choose_color, null);
        colorPickLayout.setBackgroundColor(this.getColor(R.color.window_bg_light));
        colorPickDialog.setView(colorPickLayout);
        final ColorPicker picker = colorPickLayout.findViewById(R.id.cp_colors_panel);
        ValueBar valueBar = colorPickLayout.findViewById(R.id.cp_color_value);
        OpacityBar opacityBar = colorPickLayout.findViewById(R.id.cp_color_opacity);
        picker.addValueBar(valueBar);
        picker.addOpacityBar(opacityBar);
        picker.setColor(currentColor);
        colorPickDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeMixedColor(imageView, picker.getColor());
            }
        });
        colorPickDialog.create();
        colorPickDialog.show();
    }
    @OnClick(R.id.iv_mixed_color_1)
    void clickMixedColorOne(){
        chooseMixedColor(mIvMixedColorOne,mAppInfo.getMixedColorOne());
    }
    @OnClick(R.id.iv_mixed_color_2)
    void clickMixedColorTwo(){
        chooseMixedColor(mIvMixedColorTwo,mAppInfo.getMixedColorTwo());
    }
    @OnClick(R.id.iv_mixed_color_3)
    void clickMixedColorThree(){
        chooseMixedColor(mIvMixedColorThree,mAppInfo.getMixedColorThree());
    }
//    @OnCheckedChanged(R.id.sw_listen_notification_enable)
//    void onListenNotificationEnableChanged(){
//        boolean checked = mSwListenNotification.isChecked();
//        mAppInfo.setEnableState(checked ? AppInfo.APP_ENABLE : AppInfo.APP_DISABLE);
//    }
    private void changeMixedColor(ImageView imageView, int color) {
        updateMixedColor(imageView,color);
        switch (imageView.getId()) {
            case R.id.iv_mixed_color_1:
                mAppInfo.setMixedColorOne(color);
                break;
            case R.id.iv_mixed_color_2:
                mAppInfo.setMixedColorTwo(color);
                break;
            case R.id.iv_mixed_color_3:
                mAppInfo.setMixedColorThree(color);
                break;
        }
        mGradientView.setMixedColors(new int[]{mAppInfo.mixedColorOne,mAppInfo.mixedColorTwo,mAppInfo.mixedColorThree});
    }

    private void updateMixedColor(ImageView imageView,int color){
        Drawable drawable = imageView.getDrawable();
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }
    public static void start(Context context, AppInfo appInfo) {
        Intent starter = new Intent(context, AppConfigActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_KEY,appInfo);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }
}
