package com.zibuyuqing.roundcorner.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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
public class ScreenCornerFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = ScreenCornerFragment.class.getSimpleName();
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 2222;
    private static final String LEFT_TOP = "left_top";
    private static final String LEFT_BOTTOM = "left_bottom";
    private static final String RIGHT_TOP = "right_top";
    private static final String RIGHT_BOTTOM = "right_bottom";
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    private boolean isCornerEnable;
    private boolean isNotifyEnable;
    private int mCurrentOpacity;
    private int mCurrentCornerSize;
    private int mCurrentColor;
    private boolean isLeftTopEnable;
    private boolean isLeftBottomEnable;
    private boolean isRightTopEnable;
    private boolean isRightBottomEnable;

    @BindView(R.id.rl_corner_enable_layout)
    RelativeLayout mRlCornerEnable;
    @BindView(R.id.sw_corner_enable)
    Switch mSwCornerEnable;
    @BindView(R.id.rl_notify_enable_layout)
    RelativeLayout mRlNotifyEnable;
    @BindView(R.id.sw_notify_enable)
    Switch mSwNotifyEnable;

    @BindView(R.id.sb_change_corner_size)
    SeekBar mSbChangeCornerSize;
    @BindView(R.id.sb_change_opacity)
    SeekBar mSbChangeOpacity;

    @BindView(R.id.tv_corner_size)
    TextView mTvCornerSize;
    @BindView(R.id.tv_opacity)
    TextView mTvOpacity;
    @BindView(R.id.iv_current_color)
    ImageView mIvCurrentColor;

    @BindView(R.id.iv_corner_left_top)
    ImageView mIvCornerLeftTop;

    @BindView(R.id.iv_corner_left_bottom)
    ImageView mIvCornerLeftBottom;

    @BindView(R.id.iv_corner_right_top)
    ImageView mIvCornerRightTop;

    @BindView(R.id.iv_corner_right_bottom)
    ImageView mIvCornerRightBottom;

    @Override
    protected void initData() {
        // settings data
        isCornerEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.CORNER_ENABLE);
        isNotifyEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.NOTIFICATION_ENABLE);
        mCurrentCornerSize = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.CORNER_SIZE);
        mCurrentOpacity = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.CORNER_OPACITY);
        mCurrentColor =  SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.CORNER_COLOR);

        isLeftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        isLeftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        isRightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        isRightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }
    @Override
    protected void initViews() {
        mSwCornerEnable.setChecked(isCornerEnable);
        mSwNotifyEnable.setChecked(isNotifyEnable);
        mSbChangeCornerSize.setProgress(mCurrentCornerSize);
        mSbChangeCornerSize.setMax(100);
        mSbChangeCornerSize.setOnSeekBarChangeListener(this);

        mSbChangeOpacity.setMax(255);
        mSbChangeOpacity.setProgress(mCurrentOpacity);
        mSbChangeOpacity.setOnSeekBarChangeListener(this);
        mTvCornerSize.setText(mCurrentCornerSize + "");
        mTvOpacity.setText(getOpacity(mCurrentOpacity));
        updateColorFlower(mCurrentColor);
        updateLocationFlag(mIvCornerLeftTop, isLeftTopEnable);
        updateLocationFlag(mIvCornerLeftBottom, isLeftBottomEnable);
        updateLocationFlag(mIvCornerRightTop, isRightTopEnable);
        updateLocationFlag(mIvCornerRightBottom, isRightBottomEnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean hasAlertWindowPermission = checkAlertWindowPermission();
        isCornerEnable = hasAlertWindowPermission;
        mSwCornerEnable.setChecked(isCornerEnable);
    }

    private void updateLocationFlag(ImageView imageView,boolean enable){
        Drawable drawable = imageView.getDrawable();
        int color = enable? mActivity.getColor(R.color.position_selected_color) : mActivity.getColor(R.color.black);
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }
    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" +  mActivity.getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }
    private boolean checkAlertWindowPermission() {
        return Utilities.checkFloatWindowPermission(mActivity);
    }
    @OnCheckedChanged(R.id.sw_corner_enable)
    void onCornerEnableChanged() {
        Log.e(TAG,"onCornerEnableChanged ;;;");
        confirmCornerEnable(mSwCornerEnable.isChecked());
    }

    @OnCheckedChanged(R.id.sw_notify_enable)
    void onNotifyEnableChanged() {
        confirmNotifyEnable(mSwNotifyEnable.isChecked());
    }

    @OnClick(R.id.rl_notify_enable_layout)
    void onClickNotifyLayout() {
        if (isNotifyEnable) {
            mSwNotifyEnable.setChecked(false);
            isNotifyEnable = false;
        } else {
            mSwNotifyEnable.setChecked(true);
            isNotifyEnable = true;
        }
    }
    @OnClick(R.id.rl_corner_enable_layout)
    void onClickCornerLayout() {
        if (isCornerEnable) {
            mSwCornerEnable.setChecked(false);
            isCornerEnable = false;
        } else {
            mSwCornerEnable.setChecked(true);
            isCornerEnable = true;
        }
    }
    @OnClick(R.id.iv_corner_left_top)
    void showOrHideLeftTopCorner() {
        isLeftTopEnable = !isLeftTopEnable;
        updateLocationFlag(mIvCornerLeftTop, isLeftTopEnable);
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE, isLeftTopEnable);
        updateSettingsWithBool(SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
    }

    @OnClick(R.id.iv_corner_right_top)
    void showOrHideRightTopCorner() {
        isRightTopEnable = !isRightTopEnable;
        updateLocationFlag(mIvCornerRightTop, isRightTopEnable);
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE, isRightTopEnable);
        updateSettingsWithBool(SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
    }

    @OnClick(R.id.iv_corner_left_bottom)
    void showOrHideLeftBottomCorner() {
        isLeftBottomEnable = !isLeftBottomEnable;
        updateLocationFlag(mIvCornerLeftBottom, isLeftBottomEnable);
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE, isLeftBottomEnable);
        updateSettingsWithBool(SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
    }

    @OnClick(R.id.iv_corner_right_bottom)
    void showOrHideRightBottomCorner() {
        isRightBottomEnable = !isRightBottomEnable;
        updateLocationFlag(mIvCornerRightBottom, isRightBottomEnable);
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE, isRightBottomEnable);
        updateSettingsWithBool(SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }
    @OnClick(R.id.change_color_layout) void chooseColor(){
        AlertDialog.Builder colorPickDialog = new AlertDialog.Builder(mActivity);
        View colorPickLayout = View.inflate(mActivity,R.layout.layout_choose_color,null);
        colorPickDialog.setView(colorPickLayout);
        final ColorPicker picker = colorPickLayout.findViewById(R.id.cp_colors_panel);
        ValueBar valueBar = colorPickLayout.findViewById(R.id.cp_color_value);
        OpacityBar opacityBar = colorPickLayout.findViewById(R.id.cp_color_opacity);
        picker.addValueBar(valueBar);
        picker.addOpacityBar(opacityBar);
        picker.setColor(mCurrentColor);
        colorPickDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCurrentColor = picker.getColor();
                changeCornerColor();
            }
        });
        colorPickDialog.create();
        colorPickDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (Settings.canDrawOverlays(mActivity)) {
                SettingsDataKeeper.writeSettingsBoolean(mActivity, SettingsDataKeeper.CORNER_ENABLE, true);
                updateSettingsWithBool(SettingsDataKeeper.CORNER_ENABLE);
            }
        }
    }
    private void confirmNotifyEnable(boolean checked) {
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
        updateSettingsWithBool(SettingsDataKeeper.NOTIFICATION_ENABLE);
    }
    private void confirmCornerEnable(boolean checked) {
        Log.e(TAG,"confirmCornerEnable :: checked =:" + checked);
        if (checked) {
            if (checkAlertWindowPermission()) {
                SettingsDataKeeper.writeSettingsBoolean(mActivity, SettingsDataKeeper.CORNER_ENABLE, true);
            } else {
                requestOverlayPermission();
                showTips(getString(R.string.alert_window_permission_required));
                return;
            }
        } else {
            SettingsDataKeeper.writeSettingsBoolean(mActivity, SettingsDataKeeper.CORNER_ENABLE, false);
        }
        updateSettingsWithBool(SettingsDataKeeper.CORNER_ENABLE);
    }
    private void changeOpacity() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity,SettingsDataKeeper.CORNER_OPACITY, mCurrentOpacity);
        updateSettingsWithInteger(SettingsDataKeeper.CORNER_OPACITY);
    }
    private void changeCornerSize() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity,SettingsDataKeeper.CORNER_SIZE, mCurrentCornerSize);
        Log.e(TAG,"changeCornerSize ;;;; mCurrentCornerSize =:" + mCurrentCornerSize);
        updateSettingsWithInteger(SettingsDataKeeper.CORNER_SIZE);
    }

    private String getOpacity(float opacity) {
        return (int) ((opacity / 255) * 100) + "%";
    }

    private void changeCornerColor() {
        updateColorFlower(mCurrentColor);
        SettingsDataKeeper.writeSettingsInt(mActivity,SettingsDataKeeper.CORNER_COLOR, mCurrentColor);
        updateSettingsWithInteger(SettingsDataKeeper.CORNER_COLOR);
    }
    private void updateColorFlower(int color){
        Drawable drawable = mActivity.getDrawable(R.drawable.ic_color_selected);
        drawable.setTint(color);
        mIvCurrentColor.setImageDrawable(drawable);
    }

    @Override
    protected int providedLayoutId() {
        return R.layout.fragment_screen_corner_settings;
    }

    @Override
    public String getIdentifyTag() {
        return TAG;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                mCurrentOpacity = progress;
                mTvOpacity.setText(getOpacity(mCurrentOpacity));
                break;
            case R.id.sb_change_corner_size:
                mCurrentCornerSize = progress;
                mTvCornerSize.setText(mCurrentCornerSize + "");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                changeOpacity();
                break;
            case R.id.sb_change_corner_size:
                changeCornerSize();
        }
    }
}
