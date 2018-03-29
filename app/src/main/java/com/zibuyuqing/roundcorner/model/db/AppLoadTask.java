package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.zibuyuqing.roundcorner.R;
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
    private final static String TAG = AppLoadTask.class.getSimpleName();
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
                appInfo.setAppType(Utilities.getAppType(info));
                appInfo.setEnableState(appInfo.appType);
                appInfo.setTitle(info.loadLabel(packageManager).toString());
                appInfo.setPackageName(info.packageName);
                appInfo.setMixedColorOne(mContext.getResources().getColor(R.color.color_purple,null));
                appInfo.setMixedColorTwo(mContext.getResources().getColor(R.color.color_yellow_light,null));
                appInfo.setMixedColorThree(mContext.getResources().getColor(R.color.color_cyan_light,null));
                newApps.add(appInfo);
                Log.e(TAG,"appInfo.appType = :" + appInfo.appType+",name =:" + appInfo.getTitle());
            }
        }
        if(newApps.size() > 0) {
            AppInfoDaoOpe.insertAppInfos(mContext, newApps);
        }
        return null;
    }

}
