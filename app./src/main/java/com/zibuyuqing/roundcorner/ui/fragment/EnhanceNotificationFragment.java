package com.zibuyuqing.roundcorner.ui.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.ui.activity.AppsManageActivity;
import com.zibuyuqing.roundcorner.ui.activity.LabActivity;
import com.zibuyuqing.roundcorner.ui.widget.LinearGradientView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.zibuyuqing.roundcorner.ui.widget.IconNotificationView.SHAPE_CIRCLE;
import static com.zibuyuqing.roundcorner.ui.widget.IconNotificationView.SHAPE_NONE;
import static com.zibuyuqing.roundcorner.ui.widget.IconNotificationView.SHAPE_RECTANGLE;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EnhanceNotificationFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = EnhanceNotificationFragment.class.getSimpleName();
    public static final int NOTIFICATION_STYLE_LINE = 0;
    public static final int NOTIFICATION_STYLE_DANMAKU = 1;
    public static final int NOTIFICATION_STYLE_ICON = 2;
    private static final int NOTIFICATION_LISTENER_SETTINGS_REQUEST_CODE = 3333;
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 2222;
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private int[] mMixedColorsArray = new int[3];
    private String[] mDisplayConfigArray = new String[2];
    private String[] mAnimationStyleArray = new String[5];
    private String[] mDanmakuRepeatCount = new String[5];
    private String[] mDanmuSkinStyleArray = new String[3];
    private String[] mIconShowDuration = new String[5];

    // general
    private boolean isEnhanceNotificationEnable;
    private boolean isFullScreenEnable;
    private int mCurrentNotificationStyle;

    // line
    private int mCurrentDisplayConfig;
    private int mCurrentNotificationLineSize;
    private int mCurrentNotificationAnimationDuration;
    private int mCurrentNotificationAnimationStyle;

    // danmu
    private boolean isDanmuRandomStyleEnable;
    private boolean isDanmuBigStyleEnable;
    private int mCurrentDanmuBgColor;
    private int mCurrentDanmuTextColor;
    private int mCurrentDanmuOpacity;
    private int mCurrentDanmuSpeed;
    private int mCurrentDanmuRepeatCount;
    private int mCurrentDanmuSkinStyle;
    // icon
    private boolean isCollectNotificationEnable;
    private int mCurrentIconBgColor;
    private int mCurrentIconShape;
    private int mCurrentIconSize;
    private int mCurrentIconShowDuration;

    private View mTvCurrentIconSize;
    private View mTvCurrentDanmuSpeed;

    @BindView(R.id.layout_notification_line_settings)
    View mLineSettingsLayout;

    @BindView(R.id.layout_notification_danmaku_settings)
    View mDanmakuSettingsLayout;

    @BindView(R.id.layout_notification_icon_settings)
    View mIconSettingsLayout;

    @BindView(R.id.danmu_skin_layout)
    View mDanmuSkinLayout;
    @BindView(R.id.rg_notification_style)
    RadioGroup mRgNotificationStyle;

    @BindView(R.id.sw_enhance_notification_enable)
    Switch mSwEnhanceNotificationEnable;

    @BindView(R.id.tv_display_config_summary)
    TextView mTvDisplayConfigSummary;

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

    @BindView(R.id.iv_current_danmu_bg_color)
    ImageView mIvCurrentDanmuBgColor;
    @BindView(R.id.sb_change_danmu_opacity)
    SeekBar mSbChangeDanmuBgOpacity;
    @BindView(R.id.tv_opacity)
    TextView mTvDanmuOpacity;
    @BindView(R.id.tv_danmu_skin_style)
    TextView mTvDanmuSkinStyle;
    @BindView(R.id.tv_danmaku_text_color)
    TextView mTvDanmuTextColor;
    @BindView(R.id.ll_speed)
    LinearLayout mLlDanmuSpeed;
    @BindView(R.id.tv_danmu_repeat_count)
    TextView mTvDanmuRepeatCount;
    @BindView(R.id.sw_danmu_random_style_enable)
    Switch mSwDanmuRandowStyleEnable;

    @BindView(R.id.iv_current_icon_bg_color)
    ImageView mIvCurrentIconBgColor;
    @BindView(R.id.tv_icon_show_duration)
    TextView mTvIconShowDuration;
    @BindView(R.id.ll_icon_size)
    LinearLayout mLlIconSize;
    @BindView(R.id.sw_collect_enable)
    Switch mSwCollectNotificationEnable;

    @BindView(R.id.rg_icon_shape_style)
    RadioGroup mRgIconShapeStyle;

    @BindView(R.id.sw_full_screen_enable)
    Switch mSwFullScreenEnable;

    @BindView(R.id.sw_big_danmu)
    Switch mSwUseBigDanmu;

    private HashMap<Integer,Boolean> mInitMap = new HashMap<>(3);
    public static EnhanceNotificationFragment newInstance() {
        Bundle args = new Bundle();
        EnhanceNotificationFragment fragment = new EnhanceNotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void initData() {

        // general
        isEnhanceNotificationEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
        isFullScreenEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity, SettingsDataKeeper.FULL_SCREEN_ENABLE);
        mCurrentNotificationStyle = SettingsDataKeeper.
                getSettingsInt(mActivity, SettingsDataKeeper.ENHANCE_NOTIFICATION_STYLE);
        initDataByType(mCurrentNotificationStyle);
    }

    private void initDataByType(int type){
        if(mInitMap.containsKey(type)){
            if(mInitMap.get(type)){
                return;
            }
        }
        switch (type){
            case 0:
                // line
                mDisplayConfigArray = mActivity.getResources().getStringArray(R.array.display_config);
                mAnimationStyleArray = mActivity.getResources().getStringArray(R.array.notification_animation_style);

                mCurrentDisplayConfig = SettingsDataKeeper.
                        getSettingsInt(mActivity, SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG);
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
                break;
            case 1:

                // danmu

                mDanmakuRepeatCount = mActivity.getResources().getStringArray(R.array.danmaku_repeat_count);
                mDanmuSkinStyleArray = mActivity.getResources().getStringArray(R.array.danmaku_style);
                isDanmuRandomStyleEnable = SettingsDataKeeper.
                        getSettingsBoolean(mActivity,SettingsDataKeeper.DANMU_USE_RANDOM_STYlE_ENABLE);
                isDanmuBigStyleEnable = SettingsDataKeeper.
                        getSettingsBoolean(mActivity,SettingsDataKeeper.DANMU_USE_BIG_STYLE);
                mCurrentDanmuBgColor = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_PRIMARY_COLOR);
                mCurrentDanmuOpacity = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_BG_OPACITY);
                mCurrentDanmuTextColor = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_TEXT_COLOR);
                mCurrentDanmuSpeed = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_MOVE_SPEED);
                mCurrentDanmuRepeatCount = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_REPEAT_COUNT);
                mCurrentDanmuSkinStyle = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.DANMU_SKIN_STYLE);
                break;
            case 2:
                // icon
                mIconShowDuration = mActivity.getResources().getStringArray(R.array.icon_show_duration);
                mCurrentIconBgColor = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.ICON_BG_COLOR);
                mCurrentIconShape = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.ICON_SHAPE);
                mCurrentIconSize = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.ICON_SIZE);
                mCurrentIconShowDuration = SettingsDataKeeper.getSettingsInt(mActivity,SettingsDataKeeper.ICON_SHOW_DURATION);
                isCollectNotificationEnable = SettingsDataKeeper.
                        getSettingsBoolean(mActivity,SettingsDataKeeper.ICON_COLLECT_NOTIFICATION_ENABLE);

                Log.e(TAG,"initViewByType isCollectNotificationEnable =:" + isCollectNotificationEnable);
                break;
        }
        mInitMap.put(type,false);
        Log.e(TAG,"initDataByType type =:" + type);
    }
    @Override
    protected void initViews() {
        // general
        mSwEnhanceNotificationEnable.setChecked(isEnhanceNotificationEnable);
        mSwFullScreenEnable.setChecked(isFullScreenEnable);
        mRgNotificationStyle.setOnCheckedChangeListener(this);
        mTvAnimationStyleSummary.setText(mAnimationStyleArray[mCurrentNotificationAnimationStyle]);
        switch (mCurrentNotificationStyle){
            case NOTIFICATION_STYLE_LINE:
                mRgNotificationStyle.check(R.id.rb_line_style);
                break;
            case NOTIFICATION_STYLE_DANMAKU:
                mRgNotificationStyle.check(R.id.rb_danmaku_style);
                break;
            case NOTIFICATION_STYLE_ICON:
                mRgNotificationStyle.check(R.id.rb_icon_style);
                break;
        }
        initViewByType(mCurrentNotificationAnimationStyle);
    }
    private void initViewByType(int type){
        if(mInitMap.containsKey(type)){
            if(mInitMap.get(type).booleanValue()){
                return;
            }
        } else {
            return;
        }
        switch (type){
            case 0:
                // line
                mTvDisplayConfigSummary.setText(mDisplayConfigArray[mCurrentDisplayConfig]);
                updateMixedColor(mIvMixedColorOne, mMixedColorsArray[0]);
                updateMixedColor(mIvMixedColorTwo, mMixedColorsArray[1]);
                updateMixedColor(mIvMixedColorThree, mMixedColorsArray[2]);
                mLgvMixedColorsPreview.setMixedColors(mMixedColorsArray);
                mTvAnimationDuration.setText(mCurrentNotificationAnimationDuration + "s");
                mSbChangeAnimationDuration.setProgress(mCurrentNotificationAnimationDuration * 10);
                mSbChangeAnimationDuration.setOnSeekBarChangeListener(this);
                mTvLineSize.setText(mCurrentNotificationLineSize + "");
                mSbChangeLineSize.setProgress(mCurrentNotificationLineSize * 10);
                mSbChangeLineSize.setOnSeekBarChangeListener(this);
                break;
            case 1:
                // danmu
                mSbChangeDanmuBgOpacity.setProgress(mCurrentDanmuOpacity);
                mSbChangeDanmuBgOpacity.setOnSeekBarChangeListener(this);
                mSwDanmuRandowStyleEnable.setChecked(isDanmuRandomStyleEnable);
                mSwUseBigDanmu.setChecked(isDanmuBigStyleEnable);
                updateColorFlower(mIvCurrentDanmuBgColor,mCurrentDanmuBgColor);
                int speedLevel = mLlDanmuSpeed.getChildCount();
                mTvCurrentDanmuSpeed = (TextView) mLlDanmuSpeed.getChildAt(mCurrentDanmuSpeed);
                mTvCurrentDanmuSpeed.setBackgroundResource(R.drawable.dark_circle_bg_selected);
                for(int i = 0; i < speedLevel; i ++){
                    View child = mLlDanmuSpeed.getChildAt(i);
                    final int index = i;
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTvCurrentDanmuSpeed.setBackgroundResource(R.drawable.dark_circle_bg_normal);
                            mCurrentDanmuSpeed = index;
                            changeDanmuMoveSpeed();
                            mTvCurrentDanmuSpeed = v;
                            mTvCurrentDanmuSpeed.setBackgroundResource(R.drawable.dark_circle_bg_selected);
                        }
                    });
                }
                mTvDanmuOpacity.setText(mCurrentDanmuOpacity +"");
                mTvDanmuTextColor.setTextColor(mCurrentDanmuTextColor);
                mTvDanmuRepeatCount.setText(mDanmakuRepeatCount[mCurrentDanmuRepeatCount]);
                mTvDanmuSkinStyle.setText(mDanmuSkinStyleArray[mCurrentDanmuSkinStyle]);
                if(mCurrentDanmuSkinStyle == 2){
                    mDanmuSkinLayout.setVisibility(View.GONE);
                } else {
                    mDanmuSkinLayout.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                // icon
                mTvCurrentIconSize = (TextView)mLlIconSize.getChildAt(mCurrentIconSize);
                mTvCurrentIconSize.setBackgroundResource(R.drawable.dark_circle_bg_selected);
                int count = mLlIconSize.getChildCount();
                for(int i = 0; i < count; i ++){
                    View child = mLlIconSize.getChildAt(i);
                    final int index = i;
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTvCurrentIconSize.setBackgroundResource(R.drawable.dark_circle_bg_normal);
                            mCurrentIconSize = index;
                            changeIconSize();
                            mTvCurrentIconSize = v;
                            mTvCurrentIconSize.setBackgroundResource(R.drawable.dark_circle_bg_selected);
                        }
                    });
                }

                Log.e(TAG,"initViewByType isCollectNotificationEnable =:" + isCollectNotificationEnable);
                mSwCollectNotificationEnable.setChecked(isCollectNotificationEnable);
                mTvIconShowDuration.setText(mIconShowDuration[mCurrentIconShowDuration]);
                mRgIconShapeStyle.setOnCheckedChangeListener(this);
                switch (mCurrentIconShape){
                    case SHAPE_CIRCLE:
                        mRgIconShapeStyle.check(R.id.rb_icon_shape_circle_style);
                        break;
                    case SHAPE_RECTANGLE:
                        mRgIconShapeStyle.check(R.id.rb_icon_shape_rectangle_style);
                        break;
                    case SHAPE_NONE:
                        mRgIconShapeStyle.check(R.id.rb_icon_shape_none_style);
                        break;
                }
                updateColorFlower(mIvCurrentIconBgColor,mCurrentIconBgColor);
                break;
        }
        mInitMap.put(type,true);
        Log.e(TAG,"initViewByType type =:" + type);
    }
    @Override
    public void onResume() {
        super.onResume();
        boolean hasAlertWindowPermission = checkAlertWindowPermission();
        boolean hasNotificationListenPermission = checkNotificationListenPermission();
        boolean hasPermission = hasAlertWindowPermission && hasNotificationListenPermission;
        Log.e(TAG,"onResume isEnhanceNotificationEnable =：" + isEnhanceNotificationEnable +",hasPermission =:" + hasPermission) ;
        if(!hasPermission) {
            mSwEnhanceNotificationEnable.setChecked(false);
            isEnhanceNotificationEnable = false;
        } else {
            if(mSwEnhanceNotificationEnable.isChecked()){
                if(!isEnhanceNotificationEnable){
                    confirmEnhanceNotificationEnable(true);
                }
            }
        }
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
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            if (!Utilities.isBeforeAndroidM()) {
                if (Settings.canDrawOverlays(mActivity)) {
                    if (checkNotificationListenPermission()) {
                        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                                SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE, true);
                        updateSettingsWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
                        isEnhanceNotificationEnable = true;
                    } else {
                        requestNotificationListenPermission();
                        showTips(R.string.notification_listen_permission_required);
                    }
                }
            }
        }

    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
    }

    /**
     * 申请权限
     */
    private void requestNotificationListenPermission() {
        try {
            Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void confirmFullScreenEnable(boolean checked) {
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.FULL_SCREEN_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.FULL_SCREEN_ENABLE);
        isFullScreenEnable = checked;
    }
    private void confirmEnhanceNotificationEnable(boolean checked) {
        Log.e(TAG,"confirmEnhanceNotificationEnable check =: " + checked);
        if (checked) {
            boolean hasAlertWindowPermission = checkAlertWindowPermission();
            if (!hasAlertWindowPermission) {
                isEnhanceNotificationEnable = false;
                requestOverlayPermission();
                return;
            }
            boolean hasNotificationListenPermission = checkNotificationListenPermission();
            if (!hasNotificationListenPermission) {
                isEnhanceNotificationEnable = false;
                requestNotificationListenPermission();
                showTips(R.string.notification_listen_permission_required);
                return;
            }
        }
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
        isEnhanceNotificationEnable = checked;
    }


    @OnCheckedChanged(R.id.sw_full_screen_enable)
    void onFullScreenEnableChanged(){
        confirmFullScreenEnable(mSwFullScreenEnable.isChecked());
    }

    @OnCheckedChanged(R.id.sw_enhance_notification_enable)
    void onEnhanceNotificationEnableChanged(){
        confirmEnhanceNotificationEnable(mSwEnhanceNotificationEnable.isChecked());
    }

    @OnCheckedChanged(R.id.sw_danmu_random_style_enable)
    void onDanmuRandomStyleEnableChanged(){
        boolean checked = mSwDanmuRandowStyleEnable.isChecked();
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.DANMU_USE_RANDOM_STYlE_ENABLE,checked);
        isDanmuRandomStyleEnable = checked;
    }
    @OnCheckedChanged(R.id.sw_big_danmu)
    void onDanmuBigStyleEnableChanged(){
        boolean checked = mSwUseBigDanmu.isChecked();
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.DANMU_USE_BIG_STYLE,checked);
        isDanmuBigStyleEnable = checked;
    }
    @OnCheckedChanged(R.id.sw_collect_enable)
    void onCollectNotificationsEnableChanged(){
        confirmCollectNotificationsEnable(mSwCollectNotificationEnable.isChecked());
    }


    void confirmCollectNotificationsEnable(boolean checked){
        SettingsDataKeeper.writeSettingsBoolean(mActivity,
                SettingsDataKeeper.ICON_COLLECT_NOTIFICATION_ENABLE, checked);
        updateSettingsWithBool(SettingsDataKeeper.ICON_COLLECT_NOTIFICATION_ENABLE);
        isCollectNotificationEnable = checked;
    }
    @OnClick(R.id.rl_danmu_skin)
    void toLabActivity(){
        LabActivity.start(mActivity);
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
    @OnClick(R.id.rl_apps_manager_layout)
    void onClickAppsManagerLayout(){
        AppsManageActivity.start(mActivity);
    }

    @OnClick(R.id.rl_change_line_animation_style)
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

    @OnClick(R.id.change_danmaku_bg_color_layout)
    void clickDanmuBgColorLayout(){
       chooseColor(mIvCurrentDanmuBgColor,mCurrentDanmuBgColor);
    }
    @OnClick(R.id.change_icon_bg_color_layout)
    void clickIconBgColorLayout (){
        chooseColor(mIvCurrentIconBgColor,mCurrentIconBgColor);
    }
    private void chooseColor(final ImageView imageView,int color){
        android.support.v7.app.AlertDialog.Builder colorPickDialog = new android.support.v7.app.AlertDialog.Builder(mActivity);
        View colorPickLayout = View.inflate(mActivity,R.layout.layout_choose_color,null);
        if(Utilities.isBeforeAndroidM()){
            colorPickLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.window_bg_light));
        } else {
            colorPickLayout.setBackgroundColor(mActivity.getColor(R.color.window_bg_light));
        }

        colorPickDialog.setView(colorPickLayout);
        final ColorPicker picker = colorPickLayout.findViewById(R.id.cp_colors_panel);
        ValueBar valueBar = colorPickLayout.findViewById(R.id.cp_color_value);
        SaturationBar saturationBar = colorPickLayout.findViewById(R.id.cp_color_saturation);
        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
        picker.setColor(color);
        colorPickDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(imageView == mIvCurrentDanmuBgColor) {
                    mCurrentDanmuBgColor = picker.getColor();
                    SettingsDataKeeper.writeSettingsInt(mActivity, SettingsDataKeeper.DANMU_PRIMARY_COLOR, mCurrentDanmuBgColor);
                    updateSettingsWithInteger(SettingsDataKeeper.DANMU_PRIMARY_COLOR);
                    updateColorFlower(imageView, mCurrentDanmuBgColor);
                } else {
                    mCurrentIconBgColor = picker.getColor();
                    SettingsDataKeeper.writeSettingsInt(mActivity, SettingsDataKeeper.ICON_BG_COLOR, mCurrentIconBgColor);
                    updateSettingsWithInteger(SettingsDataKeeper.ICON_BG_COLOR);
                    updateColorFlower(imageView, mCurrentIconBgColor);
                }
            }
        });
        colorPickDialog.create();
        colorPickDialog.show();
    }

    @OnClick(R.id.change_danmaku_text_color_layout)
    void changeDanmuTextColor(){
        android.support.v7.app.AlertDialog.Builder colorPickDialog = new android.support.v7.app.AlertDialog.Builder(mActivity);
        View colorPickLayout = View.inflate(mActivity,R.layout.layout_choose_color,null);
        if(Utilities.isBeforeAndroidM()){
            colorPickLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.window_bg_light));
        } else {
            colorPickLayout.setBackgroundColor(mActivity.getColor(R.color.window_bg_light));
        }

        colorPickDialog.setView(colorPickLayout);
        final ColorPicker picker = colorPickLayout.findViewById(R.id.cp_colors_panel);
        ValueBar valueBar = colorPickLayout.findViewById(R.id.cp_color_value);
        SaturationBar saturationBar = colorPickLayout.findViewById(R.id.cp_color_saturation);
        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
        picker.setColor(mCurrentDanmuTextColor);
        colorPickDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCurrentDanmuTextColor = picker.getColor();
                SettingsDataKeeper.writeSettingsInt(mActivity,SettingsDataKeeper.DANMU_TEXT_COLOR, mCurrentDanmuTextColor);
                updateSettingsWithInteger(SettingsDataKeeper.DANMU_TEXT_COLOR);
                mTvDanmuTextColor.setTextColor(mCurrentDanmuTextColor);
            }
        });
        colorPickDialog.create();
        colorPickDialog.show();
    }

    @OnClick(R.id.rl_change_danmu_skin_style)
    void changeDanmuSkinStyle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mDanmuSkinStyleArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentDanmuSkinStyle = which;
                SettingsDataKeeper.writeSettingsInt(mActivity,SettingsDataKeeper.DANMU_SKIN_STYLE, mCurrentDanmuSkinStyle);
                mTvDanmuSkinStyle.setText(mDanmuSkinStyleArray[mCurrentDanmuSkinStyle]);
                if(mCurrentDanmuSkinStyle == 2){
                    mDanmuSkinLayout.setVisibility(View.GONE);
                    if(isDanmuRandomStyleEnable)
                    mSwDanmuRandowStyleEnable.setChecked(false);
                } else {
                    mDanmuSkinLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.create().show();
    }
    @OnClick(R.id.rl_change_danmu_repeat_count)

    void changeDanmuRepeatCount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mDanmakuRepeatCount, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentDanmuRepeatCount = which;
                SettingsDataKeeper.writeSettingsInt(mActivity,SettingsDataKeeper.DANMU_REPEAT_COUNT, mCurrentDanmuRepeatCount);
                updateSettingsWithInteger(SettingsDataKeeper.DANMU_REPEAT_COUNT);
                mTvDanmuRepeatCount.setText(mDanmakuRepeatCount[mCurrentDanmuRepeatCount]);
            }
        });
        builder.create().show();
    }
    @OnClick(R.id.rl_change_icon_show_duration)
    void changeIconShowDuration(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mIconShowDuration, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentIconShowDuration = which;
                SettingsDataKeeper.writeSettingsInt(mActivity,SettingsDataKeeper.ICON_SHOW_DURATION, mCurrentIconShowDuration);
                updateSettingsWithInteger(SettingsDataKeeper.ICON_SHOW_DURATION);
                mTvIconShowDuration.setText(mIconShowDuration[mCurrentIconShowDuration]);
            }
        });
        builder.create().show();
    }
    private void updateColorFlower(ImageView imageView,int color){
        Drawable drawable = mActivity.getDrawable(R.drawable.ic_color_selected);
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }

    void confirmAnimationStyle(int select) {
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
        if(Utilities.isBeforeAndroidM()){
            colorPickLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.window_bg_gray));
        } else {
            colorPickLayout.setBackgroundColor(mActivity.getColor(R.color.window_bg_gray));
        }

        colorPickDialog.setView(colorPickLayout);
        final ColorPicker picker = colorPickLayout.findViewById(R.id.cp_colors_panel);
        ValueBar valueBar = colorPickLayout.findViewById(R.id.cp_color_value);
        SaturationBar saturationBar = colorPickLayout.findViewById(R.id.cp_color_saturation);
        picker.addValueBar(valueBar);
        picker.addSaturationBar(saturationBar);
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

    private void changeDanmuBgOpacity() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity,SettingsDataKeeper.DANMU_BG_OPACITY, mCurrentDanmuOpacity);
        updateSettingsWithInteger(SettingsDataKeeper.DANMU_BG_OPACITY);
    }
    private void changeIconSize(){
        SettingsDataKeeper.writeSettingsInt(
                mActivity, SettingsDataKeeper.ICON_SIZE, mCurrentIconSize);
        updateSettingsWithInteger(SettingsDataKeeper.ICON_SIZE);
    }
    private void changeDanmuMoveSpeed() {
        SettingsDataKeeper.writeSettingsInt(
                mActivity, SettingsDataKeeper.DANMU_MOVE_SPEED, mCurrentDanmuSpeed);
        updateSettingsWithInteger(SettingsDataKeeper.DANMU_MOVE_SPEED);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_change_line_size:
                mCurrentNotificationLineSize = progress / 10;
                if(mCurrentNotificationLineSize == 0){
                    mCurrentNotificationLineSize = 1;
                }
                mTvLineSize.setText(mCurrentNotificationLineSize + "");
                break;
            case R.id.sb_change_animation_duration:
                mCurrentNotificationAnimationDuration = progress / 10;
                if(mCurrentNotificationAnimationDuration == 0){
                    mCurrentNotificationAnimationDuration = 1;
                }
                mTvAnimationDuration.setText(mCurrentNotificationAnimationDuration + "s");
                break;
            case R.id.sb_change_danmu_opacity:
                mCurrentDanmuOpacity = progress;
                mTvDanmuOpacity.setText(mCurrentDanmuOpacity +"");
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
            case R.id.sb_change_danmu_opacity:
                changeDanmuBgOpacity();
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == mRgNotificationStyle) {
            mLineSettingsLayout.setVisibility(View.GONE);
            mDanmakuSettingsLayout.setVisibility(View.GONE);
            mIconSettingsLayout.setVisibility(View.GONE);
            switch (checkedId) {
                case R.id.rb_line_style:
                    mCurrentNotificationStyle = NOTIFICATION_STYLE_LINE;
                    mLineSettingsLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_danmaku_style:
                    mCurrentNotificationStyle = NOTIFICATION_STYLE_DANMAKU;
                    mDanmakuSettingsLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_icon_style:
                    mCurrentNotificationStyle = NOTIFICATION_STYLE_ICON;
                    mIconSettingsLayout.setVisibility(View.VISIBLE);
                    break;
            }
            initDataByType(mCurrentNotificationStyle);
            initViewByType(mCurrentNotificationStyle);
            SettingsDataKeeper.writeSettingsInt(mActivity,
                    SettingsDataKeeper.ENHANCE_NOTIFICATION_STYLE, mCurrentNotificationStyle);
            updateSettingsWithInteger(SettingsDataKeeper.ENHANCE_NOTIFICATION_STYLE);
        } else if(group == mRgIconShapeStyle){
            Log.e(TAG,"----------------");
            switch (checkedId){
                case R.id.rb_icon_shape_circle_style:
                    mCurrentIconShape = SHAPE_CIRCLE;
                    break;
                case R.id.rb_icon_shape_rectangle_style:
                    mCurrentIconShape = SHAPE_RECTANGLE;
                    break;
                case R.id.rb_icon_shape_none_style:
                    mCurrentIconShape = SHAPE_NONE;
                    break;
            }
            SettingsDataKeeper.writeSettingsInt(mActivity,
                    SettingsDataKeeper.ICON_SHAPE, mCurrentIconShape);
            updateSettingsWithInteger(SettingsDataKeeper.ICON_SHAPE);
        }
    }
}
