package com.zibuyuqing.roundcorner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.ui.widget.IconNotificationView;

import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by Xijun.Wang on 2017/11/2.
 */

public class SettingsDataKeeper {

    public static final String PREFERENCES_NAME = "com_zibuyuqing_round_corner";
    public static final String DB_VERSION = "db_version";
    public static final String IS_FIRST_LOAD = "is_first_load";
    public static final String FULL_SCREEN_ENABLE = "full_screen_enable";
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
    public static final String ENHANCE_NOTIFICATION_STYLE = "enhance_notification_style";
    public static final String NOTIFICATION_DISPLAY_CONFIG = "notification_display_config";
    public static final String MIXED_COLOR_ONE = "mixed_color_one";
    public static final String MIXED_COLOR_TWO = "mixed_color_two";
    public static final String MIXED_COLOR_THREE = "mixed_color_three";
    public static final String NOTIFICATION_LINE_SIZE = "notification_line_size";
    public static final String NOTIFICATION_ANIMATION_DURATION = "notification_animation_duration";
    public static final String NOTIFICATION_ANIMATION_STYLE = "notification_style";

    public static final String DANMU_USE_RANDOM_STYlE_ENABLE = "danmu_use_random_style_enable";
    public static final String DANMU_MOVE_SPEED = "danmu_move_speed";
    public static final String DANMU_REPEAT_COUNT = "danmu_repeat_count";
    public static final String DANMU_PRIMARY_COLOR = "danmu_primary_color";
    public static final String DANMU_BG_OPACITY = "danmu_bg_opacity";
    public static final String DANMU_TEXT_COLOR = "danmu_text_color";
    public static final String DANMU_USE_BIG_STYLE = "danmu_use_big_style";

    public static final String DANMU_SKIN_RAIL_RES_ID = "danmu_skin_rail_res_id";
    public static final String DANMU_SKIN_EDGE_COLOR = "danmu_skin_edge_color";
    public static final String DANMU_SKIN_STYLE = "danmu_skin_style";

    public static final String HIDE_RECENT = "hide_recent";

    public static final String ICON_NOTIFICATION_POSITION_X = "icon_notification_position_x";
    public static final String ICON_NOTIFICATION_POSITION_Y = "icon_notification_position_y";
    public static final String ICON_SIZE = "icon_size";
    public static final String ICON_BG_COLOR = "icon_bg_color";
    public static final String ICON_SHAPE = "icon_shape";
    public static final String ICON_SHOW_DURATION = "icon_show_duration";
    public static final String ICON_COLLECT_NOTIFICATION_ENABLE = "icon_collect_notification_enable";

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
            case DB_VERSION:
                return preferences.getInt(DB_VERSION,0);
            case CORNER_COLOR: {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.black);
                } else {
                    color = context.getColor(R.color.black);
                }
                return preferences.getInt(CORNER_COLOR, color);
            }
            case CORNER_SIZE :
                return preferences.getInt(CORNER_SIZE,
                        context.getResources().getDimensionPixelSize(R.dimen.corner_size));
            case CORNER_OPACITY :
                return preferences.getInt(CORNER_OPACITY,
                        context.getResources().getInteger(R.integer.corner_opacity));

            case ENHANCE_NOTIFICATION_STYLE :
                return preferences.getInt(ENHANCE_NOTIFICATION_STYLE,0);

            case NOTIFICATION_DISPLAY_CONFIG :
                return preferences.getInt(NOTIFICATION_DISPLAY_CONFIG,0);
            case MIXED_COLOR_ONE : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.color_purple);
                } else {
                    color = context.getColor(R.color.color_purple);
                }
                return preferences.getInt(MIXED_COLOR_ONE,color);
            }
            case MIXED_COLOR_TWO : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.color_yellow_light);
                } else {
                    color = context.getColor(R.color.color_yellow_light);
                }
                return preferences.getInt(MIXED_COLOR_TWO,color);
            }
            case MIXED_COLOR_THREE : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.color_cyan_light);
                } else {
                    color = context.getColor(R.color.color_cyan_light);
                }
                return preferences.getInt(MIXED_COLOR_THREE, color);
            }
            case NOTIFICATION_LINE_SIZE :
                return preferences.getInt(NOTIFICATION_LINE_SIZE,
                        context.getResources().getInteger(R.integer.notification_line_size));
            case NOTIFICATION_ANIMATION_DURATION :
                return preferences.getInt(NOTIFICATION_ANIMATION_DURATION,
                        context.getResources().getInteger(R.integer.notification_animation_duration));

            case NOTIFICATION_ANIMATION_STYLE :
                return preferences.getInt(NOTIFICATION_ANIMATION_STYLE, 0);

            case DANMU_BG_OPACITY :
                return preferences.getInt(DANMU_BG_OPACITY,
                        context.getResources().getInteger(R.integer.danmu_bg_opacity));
            case DANMU_PRIMARY_COLOR : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.white);
                } else {
                    color = context.getColor(R.color.white);
                }
                return preferences.getInt(DANMU_PRIMARY_COLOR,color);
            }
            case DANMU_REPEAT_COUNT :
                return preferences.getInt(DANMU_REPEAT_COUNT,
                        context.getResources().getInteger(R.integer.danmu_repeat_count));
            case DANMU_TEXT_COLOR : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.black);
                } else {
                    color = context.getColor(R.color.black);
                }
                return preferences.getInt(DANMU_TEXT_COLOR,color);
            }
            case DANMU_MOVE_SPEED :
                return preferences.getInt(DANMU_MOVE_SPEED,
                        context.getResources().getInteger(R.integer.danmu_move_speed));
            case ICON_BG_COLOR : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.icon_notification_default_bg);
                } else {
                    color = context.getColor(R.color.icon_notification_default_bg);
                }
                return preferences.getInt(ICON_BG_COLOR, color);
            }
            case ICON_SHAPE:
                return preferences.getInt(ICON_SHAPE, IconNotificationView.SHAPE_CIRCLE);
            case ICON_SHOW_DURATION:
                return preferences.getInt(ICON_SHOW_DURATION,IconNotificationView.DEFAULT_ICON_SHOW_DURATION);
            case ICON_SIZE :
                return preferences.getInt(ICON_SIZE,1);
            case ICON_NOTIFICATION_POSITION_X :
                return preferences.getInt(ICON_NOTIFICATION_POSITION_X,
                        ViewUtil.getScreenWidth(context) -
                        context.getResources().getDimensionPixelSize(R.dimen.icon_size));
            case ICON_NOTIFICATION_POSITION_Y :
                return preferences.getInt(ICON_NOTIFICATION_POSITION_Y, (int) (ViewUtil.getScreenHeight(context) * 0.5f));
            case DANMU_SKIN_RAIL_RES_ID :
                return preferences.getInt(DANMU_SKIN_RAIL_RES_ID,R.drawable.hentai_07);
            case DANMU_SKIN_EDGE_COLOR : {
                int color;
                if (Utilities.isBeforeAndroidM()) {
                    color = context.getResources().getColor(R.color.color_red);
                } else {
                    color = context.getColor(R.color.color_red);
                }
                return preferences.getInt(DANMU_SKIN_EDGE_COLOR, color);
            }
            case DANMU_SKIN_STYLE : {
                return preferences.getInt(DANMU_SKIN_STYLE, 1);
            }
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
            case IS_FIRST_LOAD :
                return preferences.getBoolean(IS_FIRST_LOAD,false);
            case FULL_SCREEN_ENABLE :
                return preferences.getBoolean(FULL_SCREEN_ENABLE,false);
            case DANMU_USE_RANDOM_STYlE_ENABLE :
                return preferences.getBoolean(DANMU_USE_RANDOM_STYlE_ENABLE,false);
            case HIDE_RECENT :
                return preferences.getBoolean(HIDE_RECENT,false);

            case ICON_COLLECT_NOTIFICATION_ENABLE :
                return preferences.getBoolean(ICON_COLLECT_NOTIFICATION_ENABLE,true);

            case DANMU_USE_BIG_STYLE :
                return preferences.getBoolean(DANMU_USE_BIG_STYLE,false);
        }
        return true;
    }
}
