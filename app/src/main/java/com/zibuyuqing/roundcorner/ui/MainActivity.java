package com.zibuyuqing.roundcorner.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.SwitchPreference;
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
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.RemoteService;
import com.zibuyuqing.roundcorner.utils.MobileInfoUtils;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MainActivity";
    private static final String LEFT_TOP = "left_top";
    private static final String LEFT_BOTTOM = "left_bottom";
    private static final String RIGHT_TOP = "right_top";
    private static final String RIGHT_BOTTOM = "right_bottom";
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 2222;
    private static final int AUTO_START_REQUEST_CODE = 3333;
    public static final String ME_PACKAGE_NAME = "com.zibuyuqing.roundcorner";
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    @BindView(R.id.title)
    TextView mTVTitle;
    @BindView(R.id.iv_action)
    ImageView mIvAction;
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

    @BindView(R.id.sw_enhance_notification_enable)
    Switch mSwEnhanceNotificationEnable;

    private boolean isCornerEnable;
    private boolean isNotifyEnable;
    private int mCurrentOpacity;
    private int mCurrentCornerSize;
    private int mCurrentColor;
    private boolean isLeftTopEnable;
    private boolean isLeftBottomEnable;
    private boolean isRightTopEnable;
    private boolean isRightBottomEnable;
    private boolean isEnhanceNotificationEnable;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        // settings data
        isCornerEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE);
        isNotifyEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.NOTIFICATION_ENABLE);
        mCurrentCornerSize = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_SIZE);
        mCurrentOpacity = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_OPACITY);
        mCurrentColor =  SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_COLOR);

        isLeftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        isLeftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        isRightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        isRightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
        isEnhanceNotificationEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);

        initViews();
        startServices();
    }
    private void startServices(){
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
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

    @OnCheckedChanged(R.id.sw_enhance_notification_enable)
    void onEnhanceNotificationChanged(){
        confirmEnhanceNotificationEnable(mSwEnhanceNotificationEnable.isChecked());
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

    @OnClick(R.id.rl_auto_start_enable_layout)
    void onClickAutoStartLayout(){
        requestAutoStartPermission();
    }


    @OnClick(R.id.iv_corner_left_top)
    void showOrHideLeftTopCorner() {
        isLeftTopEnable = !isLeftTopEnable;
        updateLocationFlag(mIvCornerLeftTop, isLeftTopEnable);
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE, isLeftTopEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
    }

    @OnClick(R.id.iv_corner_right_top)
    void showOrHideRightTopCorner() {
        isRightTopEnable = !isRightTopEnable;
        updateLocationFlag(mIvCornerRightTop, isRightTopEnable);
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE, isRightTopEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
    }

    @OnClick(R.id.iv_corner_left_bottom)
    void showOrHideLeftBottomCorner() {
        isLeftBottomEnable = !isLeftBottomEnable;
        updateLocationFlag(mIvCornerLeftBottom, isLeftBottomEnable);
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE, isLeftBottomEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
    }

    @OnClick(R.id.iv_corner_right_bottom)
    void showOrHideRightBottomCorner() {
        isRightBottomEnable = !isRightBottomEnable;
        updateLocationFlag(mIvCornerRightBottom, isRightBottomEnable);
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE, isRightBottomEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }

    @OnClick(R.id.iv_action)
    void share() {
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "我发现了一个好玩的应用，点击下载：https://pan.baidu.com/s/1-Ruu88ThKaUdeMtQiu1ROA");
        startActivity(Intent.createChooser(textIntent, "分享"));
    }

    @OnClick(R.id.change_color_layout) void chooseColor(){
        AlertDialog.Builder colorPickDialog = new AlertDialog.Builder(this);
        View colorPickLayout = View.inflate(this,R.layout.layout_choose_color,null);
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

    @OnClick(R.id.rl_enhance_notification_layout)
    void enhanceNotification(){
        if(isEnhanceNotificationEnable){
            isEnhanceNotificationEnable = false;
            mSwEnhanceNotificationEnable.setChecked(false);
        } else {
            isEnhanceNotificationEnable = true;
            mSwEnhanceNotificationEnable.setChecked(true);
        }

    }

    @OnClick(R.id.favorite) void favorite(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse("https://www.jianshu.com/p/67b5b2072c15");
        intent.setData(content_url);
        startActivity(intent);
    }
    @OnClick(R.id.about_me) void aboutMe(){
        AboutMeActivity.toAboutMe(this);
    }
    private void requestAutoStartPermission(){
        MobileInfoUtils.jumpStartInterface(this);
    }
    private void initViews() {
        mTVTitle.setText(getString(R.string.app_name));
        mIvAction.setImageResource(R.drawable.ic_share);
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
    protected void onResume() {
        super.onResume();
        boolean hasPermission = checkPermission();
        if(!hasPermission){
            isCornerEnable = hasPermission;
        }
        mSwCornerEnable.setChecked(isCornerEnable);
    }
    private void updateLocationFlag(ImageView imageView,boolean enable){
        Drawable drawable = imageView.getDrawable();
        int color = enable? getColor(R.color.position_selected_color) : getColor(R.color.black);
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }

    private boolean checkPermission() {
        return Utilities.checkFloatWindowPermission(this);
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                SettingsDataKeeper.writeSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, true);
                updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE);
            }
        } else if(requestCode == AUTO_START_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                showTips(R.string.start_when_boot_complete_tip);
            }
        }
    }

    private void confirmEnhanceNotificationEnable(boolean checked) {
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE,checked);
        updateCornersWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }

    private void confirmNotifyEnable(boolean checked) {
        SettingsDataKeeper.writeSettingsBoolean(this,
                SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
        updateCornersWithBool(SettingsDataKeeper.NOTIFICATION_ENABLE);
    }

    private void confirmCornerEnable(boolean checked) {
        Log.e(TAG,"confirmCornerEnable :: checked =:" + checked);
        if (checked) {
            if (checkPermission()) {
                SettingsDataKeeper.writeSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, true);
            } else {
                requestOverlayPermission();
                showTips(getString(R.string.permission_required));
                return;
            }
        } else {
            SettingsDataKeeper.writeSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, false);
        }
        updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE);
    }

    @Override
    protected void onDestroy() {
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
        super.onDestroy();
        Log.e(TAG, "MainActivity killed--------");
    }

    @Override
    protected void onStop() {
        Log.e(TAG,"onStop  ********************");
        super.onStop();
    }

    private void updateCornersWithBool(String key){
        Intent intent = new Intent(this, LocalControllerService.class);
        intent.setAction(key);
        startService(intent);
    }

    private void updateCornersWithInteger(String key){
        Intent intent = new Intent(this, LocalControllerService.class);
        intent.setAction(key);
        startService(intent);
    }
    private void changeOpacity() {
        SettingsDataKeeper.writeSettingsInt(
                MainActivity.this,SettingsDataKeeper.CORNER_OPACITY, mCurrentOpacity);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_OPACITY);
    }

    private void changeCornerSize() {
        SettingsDataKeeper.writeSettingsInt(
                MainActivity.this,SettingsDataKeeper.CORNER_SIZE, mCurrentCornerSize);
        Log.e(TAG,"changeCornerSize ;;;; mCurrentCornerSize =:" + mCurrentCornerSize);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_SIZE);
    }
    private void changeCornerColor() {
        updateColorFlower(mCurrentColor);
        SettingsDataKeeper.writeSettingsInt(MainActivity.this,SettingsDataKeeper.CORNER_COLOR, mCurrentColor);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_COLOR);
    }
    private void updateColorFlower(int color){
        Drawable drawable = getDrawable(R.drawable.ic_color_selected);
        drawable.setTint(color);
        mIvCurrentColor.setImageDrawable(drawable);
    }

    private String getOpacity(float opacity) {
        return (int) ((opacity / 255) * 100) + "%";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                mCurrentOpacity = i;
                mTvOpacity.setText(getOpacity(mCurrentOpacity));
                break;
            case R.id.sb_change_corner_size:
                mCurrentCornerSize = i;
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
