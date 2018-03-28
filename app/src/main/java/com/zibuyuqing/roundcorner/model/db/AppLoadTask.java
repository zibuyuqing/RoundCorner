package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppLoadTask extends AsyncTask<Void,Void,Void> {
    private Context mContext;
    public AppLoadTask(Context context){
        mContext = context;
    }
    public static void execute(Context context){
        new AppLoadTask(context).execute();
    }
    private boolean contains(ApplicationInfo info){
        List<AppInfo> oldApps = AppInfoDaoOpe.queryAll(mContext);
        for(AppInfo appInfo : oldApps){
            if(appInfo.getPackageName().equals(info.packageName)){
                return true;
            }
        }
        return false;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
        List<AppInfo> newApps = new ArrayList<AppInfo>();

        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
        for (ApplicationInfo info : applicationInfos) {
            if(contains(info)){
                continue;
            } else {
                AppInfo appInfo = new AppInfo();
                appInfo.setIsSystemApp(Utilities.isSystemApp(info));
                appInfo.setEnableState(appInfo.getIsSystemApp());
                appInfo.setTitle(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(info.packageName);
                newApps.add(appInfo);
            }
        }
        if(newApps.size() > 0) {
            AppInfoDaoOpe.insertAppInfos(mContext, newApps);
        }
        return null;
    }

}
