package com.zibuyuqing.roundcorner.model.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.model.bean.EdgeLineConfig;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.ui.widget.EdgeLineView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.IllegalFormatCodePointException;
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
public class NotificationLineManager {
    private static final String TAG = NotificationLineManager.class.getSimpleName();
    private static NotificationLineManager sInstance;
    private Map<String, EdgeLineConfig> mNotificationsMap;
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
    }

    private int ensureWindowType() {
        boolean hasNav = ViewUtil.getNavigationBarHeight(mContext) > 0;
        if (hasNav) {
            mWindowParams.y = ViewUtil.getNavigationBarHeight(mContext) / 2;
        } else {
            mWindowParams.y = 0;
        }
        if (Utilities.isCanUseToastType()) {
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
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            Log.e(TAG, "showEdgeLineByConfigChanged :: " + mLineView.isAttachedToWindow());
            confirmBrightness();
            Log.e(TAG, "showEdgeLineByConfigChanged :: " + mLineView.isAnimatorRunning() +",mLineView.isAttachedToWindow() =:" + mLineView.isAttachedToWindow());
            if (!mLineView.isAttachedToWindow()) {
                mWindowManager.addView(mLineView, mWindowParams);
            }
            mLineView.setConfig(Utilities.getEdgeLineConfig(mContext));
            mLineView.startAnimator();
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
            if (!mLineView.isAttachedToWindow()) {
                mLineView.setConfig(Utilities.getEdgeLineConfig(mContext));
                mLineView.startAnimator();
                confirmBrightness();
                mWindowManager.addView(mLineView, mWindowParams);
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

    public void registerAnimationStateListener(EdgeLineView.AnimationStateListener listener) {
        if (mLineView != null) {
            mLineView.setAnimationStateListener(listener);
        }
    }

    public void showOrHideEdgeLine() {
        if (isEnhanceNotificationEnable()) {
            showEdgeLine(LocalControllerService.ME);
        } else {
            removeEdgeLine();
        }
    }
}
