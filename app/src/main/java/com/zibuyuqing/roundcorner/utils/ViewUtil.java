package com.zibuyuqing.roundcorner.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by Xijun.Wang on 2017/10/28.
 */

public class ViewUtil {
    private static final String TAG = "ViewUtil";

    public static Point getScreenSize (Context context){
        WindowManager manager = (WindowManager)
                context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getRealSize(point);
        Log.e(TAG,"getScreenSize =:" + getNavigationBarHeight(context));
        return point;
    }
    // #ifdef LAVA_EDIT
    // wangxijun. 2016/10/11, NavigationBar
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hasNavigationBar) {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return  height;
    }


    public static Drawable tintDrawable(Drawable drawable, int colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrappedDrawable, colors);
        return wrappedDrawable;
    }
    /*
    private static int getNavBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasNavBar = !hasMenuKey && !hasBackKey;// 通过判断是否有虚拟菜单键和返回键来确定是否有导航栏

        if (hasNavBar) {
            boolean isPortrait = context.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT;

            boolean isTablet = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;

            String key = isPortrait ? "navigation_bar_height"
                    : (isTablet ? "navigation_bar_height_landscape" : null);

            return key == null ? 0 : getDimenSize(context, key);
        } else {
            return 0;
        }
    }
    // 根据关键字获取对应的值
    private static int getDimenSize(Context context, String key) {
        int resourceId = context.getResources().getIdentifier(key, "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }
    */
}
