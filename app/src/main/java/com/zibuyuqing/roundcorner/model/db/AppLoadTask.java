package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppLoadTask extends AsyncTask<Void, Void, Void> {
    private final static String TAG = AppLoadTask.class.getSimpleName();
    private Context mContext;

    public AppLoadTask(Context context) {
        mContext = context;
    }

    public static void execute(Context context) {
        new AppLoadTask(context).execute();
    }

    private boolean contains(ApplicationInfo info) {
        return AppInfoDaoOpe.isExist(mContext,info.packageName);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
        List<AppInfo> newApps = new ArrayList<AppInfo>();
        List<ApplicationInfo> appInfos =
                packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<ApplicationInfo> applicationInfos = new ArrayList<>();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = packageManager
                .queryIntentActivities(resolveIntent,0);
        Set<String> allowPackages = new HashSet<>();
        for(ResolveInfo info : resolveInfoList){
            allowPackages.add(info.activityInfo.packageName);
        }
        for(ApplicationInfo app : appInfos){
            if(allowPackages.contains(app.packageName)){
                applicationInfos.add(app);
            }
        }
        for (ApplicationInfo info : applicationInfos) {
            if (contains(info)) {
                continue;
            } else {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppType(Utilities.getAppType(info));
                appInfo.setEnableState(appInfo.appType);
                appInfo.setTitle(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(info.packageName);
                newApps.add(appInfo);
            }
        }
        if (newApps.size() > 0) {
            AppInfoDaoOpe.insertAppInfos(mContext, newApps);
        }
        newApps.clear();
        newApps = null;
        applicationInfos.clear();
        applicationInfos = null;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
