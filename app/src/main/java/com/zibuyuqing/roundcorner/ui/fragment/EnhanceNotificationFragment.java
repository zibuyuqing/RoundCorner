package com.zibuyuqing.roundcorner.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.ui.widget.LinearGradientView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EnhanceNotificationFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = EnhanceNotificationFragment.class.getSimpleName();
    private static final int NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE = 3333;
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 2222;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private boolean isEnhanceNotificationEnable;
    private boolean isBrightenScreenEnable;
    private int mCurrentDisplayConfig;
    private boolean isUseMixedColorsEnable;
    private int[] mMixedColorsArray = new int[3];
    private int mCurrentNotificationLineSize;
    private int mCurrentNotificationAnimationDuration;
    private int mCurrentNotificationAnimationStyle;
    private String[] mDisplayConfigArray = new String[2];
    private String[] mAnimationStyleArray = new String[5];

    @BindView(R.id.sw_enhance_notification_enable)
    Switch mSwEnhanceNotificationEnable;
    @BindView(R.id.sw_lighting_enable)
    Switch mSwLightScreenEnable;

    @BindView(R.id.tv_display_config_summary)
    TextView mTvDisplayConfigSummary;

    @BindView(R.id.sw_use_mixed_color)
    Switch mSwUseMixedColorEnable;

    @BindView(R.id.lgv_mixed_colors_preview)
    LinearGradientView mLgvMixedColorsPreview;

    @BindView(R.id.iv_mixed_color_1)
    ImageView mIvMixedColorOne;
    @BindView(R.id.iv_mixed_color_2)
    ImageView mIvMixedColorTwo;
    @BindView(R.id.iv_mixed_color_3)
    ImageView mIvMixedColorThree;

    @BindView(R.id.sb_change_line_size)
    SeekBar mSbChangeLineSize;
    @BindView(R.id.tv_line_size)
    TextView mTvLineSize;
    @BindView(R.id.sb_change_animation_duration)
    SeekBar mSbChangeAnimationDuration;
    @BindView(R.id.tv_animation_duration)
    TextView mTvAnimationDuration;
    @BindView(R.id.tv_animation_style_summary)
    TextView mTvAnimationStyleSummary;
    @BindView(R.id.rl_mixed_colors_preview_layout)
    RelativeLayout mRlMixedColorsPreviewLayout;

    @Override
    protected void initData() {
        mDisplayConfigArray = mActivity.getResources().getStringArray(R.array.display_config);
        mAnimationStyleArray = mActivity.getResources().getStringArray(R.array.notification_animation_style);
        isEnhanceNotificationEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
        isBrightenScreenEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE);
        mCurrentDisplayConfig = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG);
        isUseMixedColorsEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.USE_MIXED_COLORS_ENABLE);
        mMixedColorsArray[0] = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.MIXED_COLOR_ONE);
        mMixedColorsArray[1] = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.MIXED_COLOR_TWO);
        mMixedColorsArray[2] = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.MIXED_COLOR_THREE);

        mCurrentNotificationLineSize = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.NOTIFICATION_LINE_SIZE);
        mCurrentNotificationAnimationDuration = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION);
        mCurrentNotificationAnimationStyle = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.NOTIFICATION_ANIMATION_STYLE);
    }

    @Override
    protected void initViews() {
        mSwEnhanceNotificationEnable.setChecked(isEnhanceNotificationEnable);
        mSwLightScreenEnable.setChecked(isBrightenScreenEnable);
        mTvDisplayConfigSummary.setText(mDisplayConfigArray[mCurrentDisplayConfig]);
        mSwUseMixedColorEnable.setChecked(isUseMixedColorsEnable);
        mRlMixedColorsPreviewLayout.setVisibility(isUseMixedColorsEnable ? View.VISIBLE : View.GONE);
        updateMixedColor(mIvMixedColorOne, mMixedColorsArray[0]);
        updateMixedColor(mIvMixedColorTwo, mMixedColorsArray[1]);
        updateMixedColor(mIvMixedColorThree, mMixedColorsArray[2]);
        mLgvMixedColorsPreview.setMixedColors(mMixedColorsArray);
        mTvLineSize.setText(mCurrentNotificationLineSize + "");
        mSbChangeAnimationDuration.setOnSeekBarChangeListener(this);
        mTvAnimationDuration.setText(mCurrentNotificationAnimationDuration + "s");
        mSbChangeLineSize.setOnSeekBarChangeListener(this);
        mTvAnimationStyleSummary.setText(mAnimationStyleArray[mCurrentNotificationAnimationStyle]);
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean hasAlertWindowPermission = checkAlertWindowPermission();
        boolean hasNotificationListenPermission = checkNotificationListenPermission();
        isEnhanceNotificationEnable = hasAlertWindowPermission && hasNotificationListenPermission;
        Log.e(TAG,"onResume isEnhanceNotificationEnable =：" + isEnhanceNotificationEnable);
        mSwEnhanceNotificationEnable.setChecked(isEnhanceNotificationEnable);
    }

    private boolean checkAlertWindowPermission() {
        return Utilities.checkFloatWindowPermission(mActivity);
    }

    public boolean checkNotificationListenPermission() {
        return Utilities.checkNotificationListenPermission(mActivity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult ;; requestCode =:" + requestCode +",resultCode =:" + resultCode +",Settings.canDrawOverlays(mActivity) =:" +
                Settings.canDrawOverlays(mActivity) +",checkNotificationListenPermission() =:" + checkNotificationListenPermission());
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (Settings.canDrawOverlays(mActivity)) {
                if (checkNotificationListenPermission()) {
                    SettingsDataKeeper.writeSettingsBoolean(mActivity,
                            SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE, true);
                    updateSettingsWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
                } else {
                    requestNotificationListenPermission();
                    showTips(R.string.notification_listen_permission_required);
                }
            }
        }

    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }

    private void requestNotificationListenPermission() {
        try {
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void confirmEnhanceNotificationEnable(boolean checked) {
        Log.e(TAG,"confirmEnhanceNotificationEnable check =: " + checked);
        if (checked) {
            boolean hasAlertWindowPermission = checkAlertWindowPermission();
            if (!hasAlertWindowPermission) {
                requestOverlayPermission();
                return;
            }
            boolean hasNotificationListenPermission = checkNotificationListenPermission();
            if (!hasNotificationListenPermission) {
                requestNotificationListenPermission();
                return;
            }
            if (!checkNotificationListenPermission()) {
                requestNotificationListenPermission();
                showTips(R.string.notification_listen_permission_required);
                return;
            }
        } else {

        }
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }

    @OnCheckedChanged(R.id.sw_enhance_notification_enable)
    void onEnhanceNotificationEnableChanged(){
        confirmEnhanceNotificationEnable(mSwEnhanceNotificationEnable.isChecked());
    }
    @OnClick(R.id.rl_enhance_notification_layout)
    void enhanceNotification() {
        if (isEnhanceNotificationEnable) {
            isEnhanceNotificationEnable = false;
            mSwEnhanceNotificationEnable.setChecked(false);
        } else {
            isEnhanceNotificationEnable = true;
            mSwEnhanceNotificationEnable.setChecked(true);
        }
    }

    @OnClick(R.id.rl_brighten_enable_layout)
    void clickLightEnableLayout() {
        if (isBrightenScreenEnable) {
            isBrightenScreenEnable = false;
            mSwLightScreenEnable.setChecked(false);
        } else {
            isBrightenScreenEnable = true;
            mSwLightScreenEnable.setChecked(true);
        }
    }

    @OnCheckedChanged(R.id.sw_lighting_enable)
    void onLightScreenEnableChanged() {
        confirmLightScreenEnable(mSwLightScreenEnable.isChecked());
    }

    private void confirmLightScreenEnable(boolean checked) {
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE);
    }

    @OnClick(R.id.rl_notification_display_config)
    void onClickDisplayConfigLayout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mDisplayConfigArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmDisplayConfig(which);
            }
        }).create();
        builder.show();
    }

    @OnClick(R.id.rl_use_mixed_color_layout)
    void clickUseMixedColorLayout() {
        if (isUseMixedColorsEnable) {
            isUseMixedColorsEnable = false;
            mSwUseMixedColorEnable.setChecked(false);
        } else {
            isUseMixedColorsEnable = true;
            mSwUseMixedColorEnable.setChecked(false);
        }
    }

    @OnCheckedChanged(R.id.sw_use_mixed_color)
    void onUseMixedColorEnableChanged() {
        confirmUseMixedColorEnable(mSwUseMixedColorEnable.isChecked());
    }

    private void confirmUseMixedColorEnable(boolean checked) {
        mRlMixedColorsPreviewLayout.setVisibility(isUseMixedColorsEnable ? View.VISIBLE : View.GONE);
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.USE_MIXED_COLORS_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.USE_MIXED_COLORS_ENABLE);
    }

    @OnClick(R.id.rl_change_animation_style)
    void clickAnimationStyleLayout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mAnimationStyleArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmAnimationStyle(which);
            }
        }).create();
        builder.show();
    }

    private void confirmAnimationStyle(int select) {
        mTvAnimationStyleSummary.setText(mAnimationStyleArray[select]);
        SettingsDataKeeper.writeSettingsInt(mActivity,
                SettingsDataKeeper.NOTIFICATION_ANIMATION_STYLE, select);
        updateSettingsWithBool(SettingsDataKeeper.NOTIFICATION_ANIMATION_STYLE);
    }

    private void confirmDisplayConfig(int select) {
        mTvDisplayConfigSummary.setText(mDisplayConfigArray[select]);
        SettingsDataKeeper.writeSettingsInt(mActivity,
                SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG, select);
        updateSettingsWithBool(SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG);
    }

    private void chooseMixedColor(final ImageView imageView, int currentColor) {
        AlertDialog.Builder colorPickDialog = new AlertDialog.Builder(mActivity);
        View colorPickLayout = View.inflate(mActivity, R.layout.layout_choose_color, null);
        colorPickLayout.setBackgroundColor(mActivity.getColor(R.color.window_bg_gray));
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
    void clickMixedColorOne() {
        chooseMixedColor(mIvMixedColorOne, mMixedColorsArray[0]);
    }

    @OnClick(R.id.iv_mixed_color_2)
    void clickMixedColorTwo() {
        chooseMixedColor(mIvMixedColorTwo, mMixedColorsArray[1]);
    }

    @OnClick(R.id.iv_mixed_color_3)
    void clickMixedColorThree() {
        chooseMixedColor(mIvMixedColorThree, mMixedColorsArray[2]);
    }

    private void updateMixedColor(ImageView imageView, int color) {
        Drawable drawable = imageView.getDrawable();
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }

    private void changeMixedColor(ImageView imageView, int color) {
        updateMixedColor(imageView, color);
        String key = "";
        switch (imageView.getId()) {
            case R.id.iv_mixed_color_1:
                key = SettingsDataKeeper.MIXED_COLOR_ONE;
                mMixedColorsArray[0] = color;
                break;
            case R.id.iv_mixed_color_2:
                key = SettingsDataKeeper.MIXED_COLOR_TWO;
                mMixedColorsArray[1] = color;
                break;
            case R.id.iv_mixed_color_3:
                key = SettingsDataKeeper.MIXED_COLOR_THREE;
                mMixedColorsArray[1] = color;
                break;
        }
        mLgvMixedColorsPreview.setMixedColors(mMixedColorsArray);
        Log.e(TAG,",changeMixedColor key =:" + key +",color =:" + color);
        SettingsDataKeeper.writeSettingsInt(mActivity, key, color);
        updateSettingsWithInteger(key);
    }

    private void changeNotificationLineSize() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity, SettingsDataKeeper.NOTIFICATION_LINE_SIZE, mCurrentNotificationLineSize);
        updateSettingsWithInteger(SettingsDataKeeper.NOTIFICATION_LINE_SIZE);

    }

    private void changeAnimationDuration() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity, SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION, mCurrentNotificationAnimationDuration);
        updateSettingsWithInteger(SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_change_line_size:
                mCurrentNotificationLineSize = progress;
                mTvLineSize.setText(mCurrentNotificationLineSize + "");
                break;
            case R.id.sb_change_animation_duration:
                mCurrentNotificationAnimationDuration = progress;
                mTvAnimationDuration.setText(mCurrentNotificationAnimationDuration + "s");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_change_line_size:
                changeNotificationLineSize();
                break;
            case R.id.sb_change_animation_duration:
                changeAnimationDuration();
                break;
        }
    }


    @Override
    protected int providedLayoutId() {
        return R.layout.fragment_enhance_notification_settings;
    }

    @Override
    public String getIdentifyTag() {
        return TAG;
    }
}
