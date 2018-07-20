package com.zibuyuqing.roundcorner.model.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;
import com.zibuyuqing.roundcorner.ui.widget.CornerView;

import java.util.HashMap;
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
public class CornerManager {
    private static final String TAG = CornerManager.class.getSimpleName();
    private static final String LEFT_TOP = "left_top";
    private static final String LEFT_BOTTOM = "left_bottom";
    private static final String RIGHT_TOP = "right_top";
    private static final String RIGHT_BOTTOM = "right_bottom";
    private static final String[] POSITION_TAGS = {
            LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    };
    private static CornerManager sInstance;
    private Map<String,CornerView> mCornersMap = new HashMap<>();
    private WindowManager mWindowManager;
    private boolean isCornerAdded;
    private int mCurrentOpacity;
    private int mCurrentCornerSize;
    private int mCurrentColor;
    private boolean isLeftTopEnable;
    private boolean isLeftBottomEnable;
    private boolean isRightTopEnable;
    private boolean isRightBottomEnable;
    private WindowManager.LayoutParams mWindowParams;
    private Context mContext;
    private CornerManager(Context context){
        mContext = context;
        init();
    }
    public static CornerManager getInstance(Context context) {
        if(sInstance == null){
            synchronized (CornerManager.class){
                if(sInstance == null){
                    sInstance = new CornerManager(context);
                }
            }
        }
        return sInstance;
    }
    private void init(){
        refreshSettings();
        if(mWindowManager == null) {
            mWindowManager = (WindowManager)
                    mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if(mWindowParams == null){
            mWindowParams = new WindowManager.LayoutParams();
            mWindowParams.format = PixelFormat.RGBA_8888;
            mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowParams.alpha = 1;
            mWindowParams.width = ViewUtil.getScreenWidth(mContext);
            mWindowParams.height = ViewUtil.getScreenHeight(mContext);
        }
    }
    private void ensureWindowType(String position){
        // 系统提示类型,重要
        boolean hasNav =  ViewUtil.getNavigationBarHeight(mContext) > 0;
        // 系统提示类型,重要
        if(hasNav){
            if(isFullScreenEnable()) {
                mWindowParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                return;
            }
        }
        if (Utilities.isBeforeAndroidN()) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if(Utilities.isCanUseApplicationOverlayType()){
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            if(hasNav) {
                switch (position) {
                    case LEFT_TOP:
                        mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                        break;
                    case RIGHT_TOP:
                        mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                        break;
                    case LEFT_BOTTOM:
                        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                        break;
                    case RIGHT_BOTTOM:
                        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                        break;
                }
            } else {
                mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
        }
    }
    private void refreshSettings(){
        // settings data

        mCurrentCornerSize = SettingsDataKeeper.
                getSettingsInt(mContext, SettingsDataKeeper.CORNER_SIZE);
        mCurrentOpacity = SettingsDataKeeper.
                getSettingsInt(mContext, SettingsDataKeeper.CORNER_OPACITY);
        mCurrentColor =  SettingsDataKeeper.
                getSettingsInt(mContext, SettingsDataKeeper.CORNER_COLOR);

        isLeftTopEnable = SettingsDataKeeper.
                getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        isLeftBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        isRightTopEnable = SettingsDataKeeper.
                getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        isRightBottomEnable = SettingsDataKeeper.
                getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }
    private CornerView buildCorner(boolean enable,int position){
        CornerView corner = new CornerView(mContext);
        corner.setColor(mCurrentColor);
        corner.setCornerOpacity(mCurrentOpacity);
        corner.setCornerSize(mCurrentCornerSize);
        corner.setLocation(position);
        if(enable){
            corner.show();
        } else {
            corner.hide();
        }
        return corner;
    }
    public void removeCorners(){
        try {
            for (CornerView corner : mCornersMap.values()) {
                mWindowManager.removeView(corner);
            }
            mCornersMap.clear();
            isCornerAdded = false;
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void addCorners(){
        Log.e(TAG, "addCornerViews --");
        refreshSettings();
        if(!mCornersMap.isEmpty()){
            removeCorners();
        }
        try {
            for (String position : POSITION_TAGS) {
                addCornerViewByPosition(position);
            }
            isCornerAdded = true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addCornerViewByPosition(String position){
        boolean enable = true;
        switch (position) {
            case LEFT_TOP:
                enable = isLeftTopEnable;
                mWindowParams.x = mWindowParams.y = 0;
                mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case RIGHT_TOP:
                enable = isRightTopEnable;
                mWindowParams.x = mWindowParams.y = 0;
                mWindowParams.gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case LEFT_BOTTOM:
                enable = isLeftBottomEnable;
                mWindowParams.x = 0;
                mWindowParams.y = - ViewUtil.getNavigationBarHeight(mContext);
                mWindowParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case RIGHT_BOTTOM:
                enable = isRightBottomEnable;
                mWindowParams.x = 0;
                mWindowParams.y = - ViewUtil.getNavigationBarHeight(mContext);
                mWindowParams.height = ViewUtil.getScreenHeight(mContext);
                mWindowParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
        }
        ensureWindowType(position);
        CornerView corner = buildCorner(enable,mWindowParams.gravity);
        if(!mCornersMap.containsValue(corner)) {
            mCornersMap.put(position, corner);
            mWindowManager.addView(corner, mWindowParams);
        }
    }
    public void hideCornerByPosition(String position) {
        CornerView corner = mCornersMap.get(position);
        corner.hide();
    }
    public void changeCornersOpacity() {
        int opacity = SettingsDataKeeper.getSettingsInt(mContext,SettingsDataKeeper.CORNER_OPACITY);
        Log.e(TAG, "changeOpacity :: opacity =:" + opacity);
        for (CornerView cornerView : mCornersMap.values()) {
            cornerView.setCornerOpacity(opacity);
        }
    }
    public void changeCornersSize() {
        int cornerSize = SettingsDataKeeper.getSettingsInt(mContext,SettingsDataKeeper.CORNER_SIZE);
        for (CornerView cornerView : mCornersMap.values()) {
            cornerView.setCornerSize(cornerSize);
        }
    }
    public void changeCornersColor() {
        int color = SettingsDataKeeper.getSettingsInt(mContext,SettingsDataKeeper.CORNER_COLOR);
        for (CornerView cornerView : mCornersMap.values()) {
            cornerView.setColor(color);
        }
    }
    public void showOrHideCorners(){
        if(isCornerEnable()){
            if(!isCornerAdded) {
                addCorners();
            }
        } else {
            if(isCornerAdded){
                removeCorners();
            }
        }
    }
    public boolean isCornerShown(){
        return isCornerAdded && ! mCornersMap.isEmpty();
    }
    private boolean isCornerEnable(){
        return SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_ENABLE);
    }
    public void reAddCorners(){
        if(isCornerEnable()){
            removeCorners();
            addCorners();
        }
    }
    public void tryToAddCorners(){
        Log.e(TAG,"tryToAddCorners :: isCornerAdded =:" + isCornerAdded);
        if(isCornerEnable()) {
            try {
                if (mCornersMap.isEmpty()) {
                    addCorners();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void showOrHideLeftTopCorner() {
        if(!isCornerEnable()){
            return;
        }
        boolean leftTopEnable = SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_LEFT_TOP_ENABLE);
        if(!leftTopEnable){
            hideCornerByPosition(LEFT_TOP);
        } else {
            mCornersMap.get(LEFT_TOP).show();
        }
    }

    public void showOrHideLeftBottomCorner() {
        if(!isCornerEnable()){
            return;
        }
        boolean leftBottomEnable = SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_LEFT_BOTTOM_ENABLE);
        if(!leftBottomEnable){
            hideCornerByPosition(LEFT_BOTTOM);
        } else {
            mCornersMap.get(LEFT_BOTTOM).show();
        }
    }

    public void showOrHideRightTopCorner() {
        if(!isCornerEnable()){
            return;
        }
        boolean rightTopEnable = SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_RIGHT_TOP_ENABLE);
        if(!rightTopEnable){
            hideCornerByPosition(RIGHT_TOP);
        } else {
            mCornersMap.get(RIGHT_TOP).show();
        }
    }

    public void showOrHideRightBottomCorner() {
        if(!isCornerEnable()){
            return;
        }
        boolean rightBottomEnable = SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
        if(!rightBottomEnable){
            hideCornerByPosition(RIGHT_BOTTOM);
        } else {
            mCornersMap.get(RIGHT_BOTTOM).show();
        }

    }
    private boolean isFullScreenEnable(){
        return SettingsDataKeeper.getSettingsBoolean(mContext, SettingsDataKeeper.FULL_SCREEN_ENABLE);
    }
    public void updateWindowForFullScreen() {
        reAddCorners();
    }
}
