package com.zibuyuqing.roundcorner.activity;

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
import com.zibuyuqing.roundcorner.service.KeepCornerLiveService;
import com.zibuyuqing.roundcorner.service.RemoteService;
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
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.iv_action)
    ImageView ivAction;
    @BindView(R.id.rl_corner_enable_layout)
    RelativeLayout rlCornerEnable;
    @BindView(R.id.sw_corner_enable)
    Switch swCornerEnable;
    @BindView(R.id.rl_notify_enable_layout)
    RelativeLayout rlNotifyEnable;
    @BindView(R.id.sw_notify_enable)
    Switch swNotifyEnable;

    @BindView(R.id.sb_change_corner_size)
    SeekBar sbChangeCornerSize;
    @BindView(R.id.sb_change_opacity)
    SeekBar sbChangeOpacity;

    @BindView(R.id.tv_corner_size)
    TextView tvCornerSize;
    @BindView(R.id.tv_opacity)
    TextView tvOpacity;
    @BindView(R.id.iv_current_color)
    ImageView ivCurrentColor;

    @BindView(R.id.iv_corner_left_top)
    ImageView ivCornerLeftTop;

    @BindView(R.id.iv_corner_left_bottom)
    ImageView ivCornerLeftBottom;

    @BindView(R.id.iv_corner_right_top)
    ImageView ivCornerRightTop;

    @BindView(R.id.iv_corner_right_bottom)
    ImageView ivCornerRightBottom;
    private boolean cornerEnable;
    private boolean notifyEnable;
    private int currentOpacity;
    private int currentCornerSize;
    private int currentColor;
    private boolean leftTopEnable;
    private boolean leftBottomEnable;
    private boolean rightTopEnable;
    private boolean rightBottomEnable;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        // settings data
        cornerEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE);
        notifyEnable = SettingsDataKeeper.
                getSettingsBoolean(this, SettingsDataKeeper.NOTIFICATION_ENABLE);
        currentCornerSize = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_SIZE);
        currentOpacity = SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_OPACITY);
        currentColor =  SettingsDataKeeper.
                getSettingsInt(this, SettingsDataKeeper.CORNER_COLOR);

        leftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        leftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        rightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        rightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(this,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
        initViews();
        startService(new Intent(this, RemoteService.class));
    }

    @OnCheckedChanged(R.id.sw_corner_enable)
    void onCornerEnableChanged() {
        confirmCornerEnable(swCornerEnable.isChecked());
    }

    @OnCheckedChanged(R.id.sw_notify_enable)
    void onNotifyEnableChanged() {
        confirmNotifyEnable(swNotifyEnable.isChecked());
    }

    @OnClick(R.id.rl_corner_enable_layout)
    void onClickCornerLayout() {
        if (cornerEnable) {
            swCornerEnable.setChecked(false);
        } else {
            swCornerEnable.setChecked(true);
        }
    }

    @OnClick(R.id.rl_notify_enable_layout)
    void onClickNotifyLayout() {
        if (notifyEnable) {
            swNotifyEnable.setChecked(false);
        } else {
            swNotifyEnable.setChecked(true);
        }
    }

    @OnClick(R.id.iv_corner_left_top)
    void showOrHideLeftTopCorner() {
        leftTopEnable = !leftTopEnable;
        updateLocationFlag(ivCornerLeftTop,leftTopEnable);
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE,leftTopEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE,leftTopEnable);
    }

    @OnClick(R.id.iv_corner_right_top)
    void showOrHideRightTopCorner() {
        rightTopEnable = !rightTopEnable;
        updateLocationFlag(ivCornerRightTop,rightTopEnable);
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE,rightTopEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE,rightTopEnable);
    }

    @OnClick(R.id.iv_corner_left_bottom)
    void showOrHideLeftBottomCorner() {
        leftBottomEnable = !leftBottomEnable;
        updateLocationFlag(ivCornerLeftBottom,leftBottomEnable);
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE,leftBottomEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE,leftBottomEnable);
    }

    @OnClick(R.id.iv_corner_right_bottom)
    void showOrHideRightBottomCorner() {
        rightBottomEnable = !rightBottomEnable;
        updateLocationFlag(ivCornerRightBottom,rightBottomEnable);
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE,rightBottomEnable);
        updateCornersWithBool(SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE,rightBottomEnable);
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
        View colorPickLayout = View.inflate(this,R.layout.choose_color_layout,null);
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
                currentColor = picker.getColor();
                changeCornerColor(currentColor);
            }
        });
        colorPickDialog.create();
        colorPickDialog.show();
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
    private void initViews() {
        title.setText(getString(R.string.app_name));
        ivAction.setImageResource(R.drawable.ic_share);
        swCornerEnable.setChecked(cornerEnable);
        swNotifyEnable.setChecked(notifyEnable);
        sbChangeCornerSize.setProgress(currentCornerSize);
        sbChangeCornerSize.setMax(100);
        sbChangeCornerSize.setOnSeekBarChangeListener(this);

        sbChangeOpacity.setMax(255);
        sbChangeOpacity.setProgress(currentOpacity);
        sbChangeOpacity.setOnSeekBarChangeListener(this);
        tvCornerSize.setText(currentCornerSize + "");
        tvOpacity.setText(getOpacity(currentOpacity));
        updateColorFlower(currentColor);
        updateLocationFlag(ivCornerLeftTop,leftTopEnable);
        updateLocationFlag(ivCornerLeftBottom,leftBottomEnable);
        updateLocationFlag(ivCornerRightTop,rightTopEnable);
        updateLocationFlag(ivCornerRightBottom,rightBottomEnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cornerEnable = checkPermission();
        swCornerEnable.setChecked(cornerEnable);
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

    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE,true);
            }
        }
    }
    private void confirmNotifyEnable(boolean checked) {
        SettingsDataKeeper.writteSettingsBoolean(this,
                SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
        updateCornersWithBool(SettingsDataKeeper.NOTIFICATION_ENABLE,checked);
    }

    private void confirmCornerEnable(boolean checked) {
        Log.e(TAG,"confirmCornerEnable :: checked =:" + checked);
        if (checked) {
            if (checkPermission()) {
                SettingsDataKeeper.writteSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, true);
            } else {
                requestPermission();
                showTips(getString(R.string.permission_required));
                return;
            }
        } else {
            SettingsDataKeeper.writteSettingsBoolean(this, SettingsDataKeeper.CORNER_ENABLE, false);
        }
        updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE,checked);
    }

    @Override
    protected void onDestroy() {
        RemoteService.start(this);
        KeepCornerLiveService.tryToAddCorner(this);
        super.onDestroy();
        Log.e(TAG, "MainActivity killed--------");
    }

    @Override
    protected void onStop() {
        Log.e(TAG,"onStop  ********************");
        super.onStop();
    }

    private void updateCornersWithBool(String key, boolean value){
        Intent intent = new Intent(this, KeepCornerLiveService.class);
        intent.putExtra(key,value);
        intent.setAction(key);
        startService(intent);
    }
    private void updateCornersWithInteger(String key,int value){
        Intent intent = new Intent(this, KeepCornerLiveService.class);
        intent.setAction(key);
        intent.putExtra(key,value);
        startService(intent);
    }
    private void changeOpacity(int opacity) {
        Log.e(TAG, "changeOpacity :: opacity =:" + opacity);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_OPACITY,opacity);
        SettingsDataKeeper.writteSettingsInt(
                MainActivity.this,SettingsDataKeeper.CORNER_OPACITY,currentOpacity);
    }

    private void changeCornerSize(int cornerSize) {
        Log.e(TAG, "changeCornerSize :: cornerSize =:" + cornerSize);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_SIZE,cornerSize);
        SettingsDataKeeper.writteSettingsInt(
                MainActivity.this,SettingsDataKeeper.CORNER_SIZE,currentCornerSize);
    }
    private void changeCornerColor(int color) {
        updateColorFlower(color);
        updateCornersWithInteger(SettingsDataKeeper.CORNER_COLOR,color);
        SettingsDataKeeper.writteSettingsInt(MainActivity.this,SettingsDataKeeper.CORNER_COLOR,currentColor);
    }
    private void updateColorFlower(int color){
        Drawable drawable = getDrawable(R.drawable.ic_color_selected);
        drawable.setTint(color);
        ivCurrentColor.setImageDrawable(drawable);
    }

    private String getOpacity(float opacity) {
        return (int) ((opacity / 255) * 100) + "%";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                currentOpacity = i;
                tvOpacity.setText(getOpacity(currentOpacity));
                break;
            case R.id.sb_change_corner_size:
                currentCornerSize = i;
                tvCornerSize.setText(currentCornerSize + "");
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_change_opacity:
                changeOpacity(currentOpacity);
                break;
            case R.id.sb_change_corner_size:
                changeCornerSize(currentCornerSize);
        }
    }
}
