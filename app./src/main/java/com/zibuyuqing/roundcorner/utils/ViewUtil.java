package com.zibuyuqing.roundcorner.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.view.WindowManager;

import com.zibuyuqing.roundcorner.R;

import java.lang.reflect.Method;

/**
 * Created by Xijun.Wang on 2017/10/28.
 */

public class ViewUtil {
    private static final String TAG = "ViewUtil";
    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();
    private static final int NUMBER_OF_PALETTE_COLORS = 24;
    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
    }
    public static Point getScreenSize (Context context){
        WindowManager manager = (WindowManager)
                context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getRealSize(point);
        return point;
    }
    public static int getScreenWidth(Context context){
        return getScreenSize(context).x;
    }
    public static int getScreenHeight(Context context){
        return getScreenSize(context).y;
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
    /**
     * Returns a bitmap suitable for the all apps view.
     */
    public static Bitmap createIconBitmap(Drawable icon,int size) {
        if(icon == null){
            return null;
        }
        synchronized (sCanvas) {
            // 取 drawable 的长宽
            int w = icon.getIntrinsicWidth();
            int h = icon.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            icon.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            icon.draw(canvas);
            Bitmap scaleBmp = Bitmap.createScaledBitmap(bitmap,size,size,true);
            if(bitmap != null){
                if (!bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
            return scaleBmp;
        }
    }
    public static Bitmap drawable2Bitmap(Drawable drawable){
        synchronized (sCanvas) {
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            Bitmap scaleBmp = Bitmap.createScaledBitmap(bitmap,w,h,true);
            if(bitmap != null){
                if (!bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
            return scaleBmp;
        }
    }
    public static int colorFromBitmap(Bitmap bitmap) {
        // Author of Palette recommends using 24 colors when analyzing profile photos.
        final Palette palette = Palette.from(bitmap).generate();
        if (palette != null && palette.getVibrantSwatch() != null) {
            return palette.getVibrantSwatch().getRgb();
        }
        return 0;
    }
    public static int getIconSize(Context context){
        int sizeSelect =  SettingsDataKeeper.getSettingsInt(context,SettingsDataKeeper.ICON_SIZE);
        switch (sizeSelect) {
            case 0:
                return context.getResources().getDimensionPixelSize(R.dimen.dimen_30_dp);
            case 1:
                return context.getResources().getDimensionPixelSize(R.dimen.dimen_36_dp);
            case 2:
                return context.getResources().getDimensionPixelSize(R.dimen.dimen_48_dp);
            default:
                return context.getResources().getDimensionPixelSize(R.dimen.dimen_36_dp);
        }
    }
}
