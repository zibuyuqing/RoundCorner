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

    public static final String ENHANCE_NOTIFICATION_ENABLE = "enhance_notification_enable";
    public static final String BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE = "brighten_screen_when_notify_enable";
    public static final String NOTIFICATION_DISPLAY_CONFIG = "notification_display_config";
    public static final String USE_MIXED_COLORS_ENABLE = "use_mixed_colors_enable";
    public static final String MIXED_COLOR_ONE = "mixed_color_one";
    public static final String MIXED_COLOR_TWO = "mixed_color_two";
    public static final String MIXED_COLOR_THREE = "mixed_color_three";
    public static final String NOTIFICATION_LINE_SIZE = "notification_line_size";
    public static final String NOTIFICATION_ANIMATION_DURATION = "notification_animation_duration";
    public static final String NOTIFICATION_ANIMATION_STYLE = "notification_style";


    public static void writeSettingsInt(Context context, String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static void writeSettingsBoolean(Context context, String key, boolean value){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static synchronized int getSettingsInt(Context context,String key){
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

            case NOTIFICATION_DISPLAY_CONFIG :
                return preferences.getInt(NOTIFICATION_DISPLAY_CONFIG,0);
            case MIXED_COLOR_ONE :
                return preferences.getInt(MIXED_COLOR_ONE,
                        context.getResources().getColor(R.color.color_purple,null));
            case MIXED_COLOR_TWO :
                return preferences.getInt(MIXED_COLOR_TWO,
                        context.getResources().getColor(R.color.color_yellow_light,null));
            case MIXED_COLOR_THREE :
                return preferences.getInt(MIXED_COLOR_THREE,
                        context.getResources().getColor(R.color.color_cyan_light,null));
            case NOTIFICATION_LINE_SIZE :
                return preferences.getInt(NOTIFICATION_LINE_SIZE,
                        context.getResources().getInteger(R.integer.notification_line_size));
            case NOTIFICATION_ANIMATION_DURATION :
                return preferences.getInt(NOTIFICATION_ANIMATION_DURATION,
                        context.getResources().getInteger(R.integer.notification_animation_duration));
            case NOTIFICATION_ANIMATION_STYLE :
                return preferences.getInt(NOTIFICATION_ANIMATION_STYLE, 0);
        }
        return 0;
    }
    public static synchronized boolean getSettingsBoolean(Context context,String key){
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
            case ENHANCE_NOTIFICATION_ENABLE :
                return preferences.getBoolean(ENHANCE_NOTIFICATION_ENABLE,false);
            case BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE:
                return preferences.getBoolean(BRIGHTEN_SCREEN_WHEN_NOTIFY_ENABLE,true);
            case USE_MIXED_COLORS_ENABLE :
                return preferences.getBoolean(CORNER_LEFT_TOP_ENABLE,true);
        }
        return true;
    }
}
