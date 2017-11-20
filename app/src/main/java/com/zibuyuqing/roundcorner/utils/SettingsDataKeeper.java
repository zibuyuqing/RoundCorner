package com.zibuyuqing.roundcorner.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zibuyuqing.roundcorner.R;

/**
 * Created by Xijun.Wang on 2017/11/2.
 */

public class SettingsDataKeeper {
    public static final String PREFERENCES_NAME = "com_zibuyuqing_round_corner";
    public static final String CORNER_ENABLE = "corner_enable";
    public static final String NOTIFICATION_ENABLE = "notification_enable";
    public static final String CORNER_COLOR = "corner_color";
    public static final String CORNER_LEFT_TOP_ENABLE = "corner_left_top_enable";
    public static final String CORNER_RIGHT_TOP_ENABLE = "corner_right_top_enable";
    public static final String CORNER_LEFT_BOTTOM_ENABLE = "corner_left_bottom_enable";
    public static final String CORNER_RIGHT_BOTTOM_ENABLE = "corner_right_bottom_enable";
    public static final String CORNER_SIZE = "corner_size";
    public static final String CORNER_OPACITY = "corner_opacity";

    public static void writteSettingsInt(Context context,String key,int value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static void writteSettingsBoolean(Context context,String key,boolean value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static int getSettingsInt(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        switch (key){
            case CORNER_COLOR:
                return preferences.getInt(CORNER_COLOR,
                        context.getColor(R.color.black));
            case CORNER_SIZE :
                return preferences.getInt(CORNER_SIZE,
                        context.getResources().getDimensionPixelSize(R.dimen.corner_size));
            case CORNER_OPACITY :
                return preferences.getInt(CORNER_OPACITY,
                        context.getResources().getInteger(R.integer.corner_opacity));
        }
        return 0;
    }
    public static boolean getSettingsBoolean(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        switch (key){
            case CORNER_ENABLE :
                return preferences.getBoolean(CORNER_ENABLE,false);
            case NOTIFICATION_ENABLE :
                return preferences.getBoolean(NOTIFICATION_ENABLE,false);
            case CORNER_LEFT_TOP_ENABLE :
                return preferences.getBoolean(CORNER_LEFT_TOP_ENABLE,true);
            case CORNER_RIGHT_TOP_ENABLE :
                return preferences.getBoolean(CORNER_RIGHT_TOP_ENABLE,true);
            case CORNER_LEFT_BOTTOM_ENABLE :
                return preferences.getBoolean(CORNER_LEFT_BOTTOM_ENABLE,true);
            case CORNER_RIGHT_BOTTOM_ENABLE :
                return preferences.getBoolean(CORNER_RIGHT_BOTTOM_ENABLE,true);
        }
        return true;
    }
}
