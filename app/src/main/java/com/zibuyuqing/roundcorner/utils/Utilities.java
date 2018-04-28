package com.zibuyuqing.roundcorner.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.DanmuConfig;
import com.zibuyuqing.roundcorner.model.bean.EdgeLineConfig;
import com.zibuyuqing.roundcorner.ui.activity.HomeActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Xijun.Wang on 2017/11/3.
 */

public class Utilities {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final int NOTIFY_ID = 1111;

    public static boolean isBeforeAndroidN() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }

    public static boolean isCanUseApplicationOverlayType() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 检查是否有权限
     * @param context
     * @return
     */
    public static boolean checkNotificationListenPermission(Context context) {
        // 获取允许监听通知的包
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e("", Log.getStackTraceString(e));
            }
        } else {
            Log.e("Utilities", "Below API 19 cannot invoke!");
        }
        return false;
    }


    public static Notification buildNotification(Context context) {
        Notification notification;
        // bigView.setOnClickPendingIntent() etc..
        Notification.Builder notifyBuilder = new Notification.Builder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);
        notification = notifyBuilder.setSmallIcon(R.drawable.ic_change_number)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.click_to_set_corner))
                .build();
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(context.getColor(R.color.transparent));
        return notification;
    }

    public static EdgeLineConfig getEdgeLineConfig(Context context) {
        EdgeLineConfig config = new EdgeLineConfig();
        config.setStyle(SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.NOTIFICATION_ANIMATION_STYLE));
        config.setAlwaysOnAble(SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.NOTIFICATION_DISPLAY_CONFIG) == 1);
        int animationDuration = SettingsDataKeeper.
                getSettingsInt(context, SettingsDataKeeper.NOTIFICATION_ANIMATION_DURATION) * 1000;
        config.setDuration(animationDuration);
        config.setStrokeSize(SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.NOTIFICATION_LINE_SIZE));
        config.setMixedColorArr(new int[]{
                SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.MIXED_COLOR_ONE),
                SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.MIXED_COLOR_TWO),
                SettingsDataKeeper.getSettingsInt(context, SettingsDataKeeper.MIXED_COLOR_THREE)
        });
        return config;
    }
    public static DanmuConfig getDanmuConfig(Context context) {
        DanmuConfig config = new DanmuConfig();
        config.setMoveSpeed(SettingsDataKeeper.getSettingsInt(context,SettingsDataKeeper.DANMU_MOVE_SPEED));
        config.setPrimaryColor(SettingsDataKeeper.getSettingsInt(context,SettingsDataKeeper.DANMU_PRIMARY_COLOR));
        config.setRepeatCount(SettingsDataKeeper.getSettingsInt(context,SettingsDataKeeper.DANMU_REPEAT_COUNT));
        config.setTextColor(SettingsDataKeeper.getSettingsInt(context,SettingsDataKeeper.DANMU_TEXT_COLOR));
        config.setUseRandomColor(SettingsDataKeeper.getSettingsBoolean(context,SettingsDataKeeper.DANMU_USE_RANDOM_COLOR_ENABLE));
        return config;
    }
    public static int getAppType(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return AppInfo.SYSTEM_APP;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {//判断是不是系统应用
            return AppInfo.SYSTEM_APP;
        }
        return AppInfo.USER_APP;
    }
    public static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!activity.shouldShowRequestPermissionRationale(permission)){
                return false;
            }
        }
        return true;
    }
    public static boolean checkStoragePermission(Activity activity) {
        final List<String> permissionsList = new ArrayList<String>();
        addPermission(activity,permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addPermission(activity,permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionsList.size() > 0) {
            activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
