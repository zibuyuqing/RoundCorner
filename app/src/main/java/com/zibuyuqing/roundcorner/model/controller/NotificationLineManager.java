package com.zibuyuqing.roundcorner.model.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.EdgeLineConfig;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.ui.widget.EdgeLineView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationLineManager implements EdgeLineView.OnScreenConfigurationChangeListener, EdgeLineView.AnimationStateListener {

    private static final String TAG = NotificationLineManager.class.getSimpleName();
    private static NotificationLineManager sInstance;
    private static final Map<String, EdgeLineConfig> sNotificationsMap = new HashMap<>();
    private EdgeLineView mLineView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private Context mContext;
    private NotificationLineManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        if (mLineView == null) {
            mLineView = new EdgeLineView(mContext);
            mLineView.setOnScreenConfigurationChangeListener(this);
            mLineView.setAnimationStateListener(this);
        }
        if (mWindowManager == null) {
            mWindowManager = (WindowManager)
                    mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (mWindowParams == null) {
            mWindowParams = new WindowManager.LayoutParams();
            mWindowParams.format = PixelFormat.RGBA_8888;
            mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowParams.alpha = 1;
            mWindowParams.x = 0;
            mWindowParams.type = ensureWindowType();
            mWindowParams.width = ViewUtil.getScreenWidth(mContext);
            mWindowParams.height = ViewUtil.getScreenHeight(mContext);
        }
        updateEnableAppMap();
    }
    public void updateAppMap(AppInfo info){
        if(sNotificationsMap.containsKey(info.packageName)){
            EdgeLineConfig config = sNotificationsMap.get(info.packageName);
            config.setMixedColorArr(new int[]{
                    info.getMixedColorOne(),
                    info.getMixedColorTwo(),
                    info.getMixedColorThree()
            });
        }
    }
    public void updateEnableAppMap(){
        List<AppInfo> enableApps = AppInfoDaoOpe.queryEnableAppInfos(mContext);
        updateEnableAppMap(enableApps,true);
    }
    public synchronized void  updateEnableAppMap(List<AppInfo> apps,boolean isFirst){
        if(isFirst){
            sNotificationsMap.clear();
        }
        Log.e(TAG,"updateEnableAppMap :: isFirst =:" + isFirst +",enableApps =:" + apps.size());
        EdgeLineConfig config;
        String packageName;
        EdgeLineConfig defaultConfig = Utilities.getEdgeLineConfig(mContext);
        for(AppInfo info : apps){
            config = new EdgeLineConfig();
            packageName = info.packageName;
            Log.e(TAG,"updateEnableAppMap :: info =:" + info.getPackageName());
            if(sNotificationsMap.containsKey(packageName)){
                continue;
            }
            config.setMixedColorArr(new int[]{
                    info.getMixedColorOne(),
                    info.getMixedColorTwo(),
                    info.getMixedColorThree()
            });
            config.setStyle(defaultConfig.getStyle());
            config.setStrokeSize(defaultConfig.getStrokeSize());
            config.setDuration(defaultConfig.getDuration());
            config.setPrimaryColor(defaultConfig.getPrimaryColor());
            config.setAlwaysOnAble(defaultConfig.isAlwaysOnAble());
            sNotificationsMap.put(packageName,config);
        }
    }
    private int ensureWindowType() {
        mWindowParams.width = ViewUtil.getScreenWidth(mContext);
        mWindowParams.height = ViewUtil.getScreenHeight(mContext);
        boolean hasNav = ViewUtil.getNavigationBarHeight(mContext) > 0;
        if(hasNav) {
            if (mWindowParams.width > mWindowParams.height) {
                mWindowParams.y = 0;
                mWindowParams.x = ViewUtil.getNavigationBarHeight(mContext) / 2;
            } else {
                mWindowParams.y = ViewUtil.getNavigationBarHeight(mContext) / 2;
                mWindowParams.x = 0;
            }
        }
        if (Utilities.isBeforeAndroidN()) {
            return WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Utilities.isCanUseApplicationOverlayType()) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            if (hasNav) {
                return WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
        }
    }

    public static NotificationLineManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NotificationLineManager.class) {
                if (sInstance == null) {
                    sInstance = new NotificationLineManager(context);
                }
            }
        }
        return sInstance;
    }

    public void showEdgeLineByConfigChanged() {
        try {
            Log.e(TAG, "showEdgeLineByConfigChanged :: " + mLineView.isAttachedToWindow() +",isEnhanceNotificationEnable() =:" + isEnhanceNotificationEnable());
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            confirmBrightness();
            if (!mLineView.isAttachedToWindow()) {
                mWindowManager.addView(mLineView, mWindowParams);
            }
            mLineView.setConfig(Utilities.getEdgeLineConfig(mContext));
            mLineView.startAnimator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateWindow(){
        try {
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            Log.e(TAG,"updateWindow ----------------");
            if(mLineView != null){
                mWindowParams.width = ViewUtil.getScreenWidth(mContext);
                mWindowParams.height = ViewUtil.getScreenHeight(mContext);
                boolean hasNav =  ViewUtil.getNavigationBarHeight(mContext) > 0;
                if(hasNav) {
                    if (mWindowParams.width > mWindowParams.height) {
                        mWindowParams.y = 0;
                        mWindowParams.x = ViewUtil.getNavigationBarHeight(mContext) / 2;
                    } else {
                        mWindowParams.y = ViewUtil.getNavigationBarHeight(mContext) / 2;
                        mWindowParams.x = 0;
                    }
                }
                if(mLineView.isAttachedToWindow()){
                    mWindowManager.updateViewLayout(mLineView,mWindowParams);
                } else {
                    mWindowManager.addView(mLineView,mWindowParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showEdgeLine(String who) {
        try {
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            Log.e(TAG, "who =:" + who);
            if(sNotificationsMap.containsKey(who)) {
                if (!mLineView.isAttachedToWindow()) {
                    EdgeLineConfig config = sNotificationsMap.get(who);
                    mLineView.setConfig(config != null? config : Utilities.getEdgeLineConfig(mContext));
                    mLineView.startAnimator();
                    confirmBrightness();
                    mWindowManager.addView(mLineView, mWindowParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void confirmBrightness(){
        Log.e(TAG,"confirmBrightness  =;  " + isBrightenScreenEnable());
        if (isBrightenScreenEnable()) {
            mWindowParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            mWindowParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        } else {
            mWindowParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            mWindowParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }
    }

    public void removeEdgeLine() {
        Log.e(TAG,"removeEdgeLine isNotificationLineAdded =:" + mLineView.isAttachedToWindow());
        if (mLineView != null && mLineView.isAttachedToWindow()) {
            if (mLineView.isAnimatorRunning()) {
                mLineView.cancelAnimator();
            }
            mWindowManager.removeView(mLineView);
        }
    }

    private boolean isBrightenScreenEnable() {
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE);
    }

    private boolean isEnhanceNotificationEnable() {
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }

    public void showOrHideEdgeLine() {
        if (isEnhanceNotificationEnable()) {
            // showEdgeLine(LocalControllerService.ME);
        } else {
            removeEdgeLine();
        }
    }

    @Override
    public void onScreenConfigurationChanged() {
        updateWindow();
    }
    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationRunning(float progress) {

    }

    @Override
    public void onAnimationEnd() {
        removeEdgeLine();
    }
}
