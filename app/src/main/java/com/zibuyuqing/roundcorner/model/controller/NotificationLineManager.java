package com.zibuyuqing.roundcorner.model.controller;

import android.app.Notification;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
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

import java.util.ArrayList;
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

        if(hasNav){
            if(isFullScreenEnable()) {
                return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
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
    public void updateWindowForFullScreen(){
        mWindowParams.type = ensureWindowType();
        if(mLineView != null && mLineView.isAttachedToWindow()){
            mWindowManager.removeView(mLineView);
            mWindowManager.addView(mLineView,mWindowParams);
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
            Log.d(TAG, "showEdgeLineByConfigChanged :: " + mLineView.isAttachedToWindow() +",isEnhanceNotificationEnable() =:" + isEnhanceNotificationEnable());
            if (!isEnhanceNotificationEnable()) {
                return;
            }
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
    private boolean appEnable(String who){
        return AppInfoDaoOpe.appEnable(mContext,who);
    }
    public void showEdgeLine(String who) {
        try {
            if (!isEnhanceNotificationEnable()) {
                return;
            }
            Log.d(TAG, "who =:" + who);
            if(appEnable(who)) {
                if (!mLineView.isAttachedToWindow()) {
                    mLineView.setConfig(Utilities.getEdgeLineConfig(mContext));
                    mLineView.startAnimator();
                    mWindowManager.addView(mLineView, mWindowParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeEdgeLine() {
        Log.d(TAG,"removeEdgeLine isNotificationLineAdded =:" + mLineView.isAttachedToWindow());
        if (mLineView != null && mLineView.isAttachedToWindow()) {
            if (mLineView.isAnimatorRunning()) {
                mLineView.cancelAnimator();
            }
            mWindowManager.removeView(mLineView);
        }
    }

    private boolean isEnhanceNotificationEnable() {
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }
    private boolean isFullScreenEnable(){
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.FULL_SCREEN_ENABLE);
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
