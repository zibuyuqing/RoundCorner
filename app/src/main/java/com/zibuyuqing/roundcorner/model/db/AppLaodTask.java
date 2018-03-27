package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.zibuyuqing.roundcorner.model.bean.AppInfo;

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
public class AppLaodTask extends AsyncTask<Void,Void,List<AppInfo>> {
    private Context mContext;
    public AppLaodTask(Context context){
        mContext = context;
    }
    @Override
    protected List<AppInfo> doInBackground(Void... voids) {
        PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
        List<AppInfo> newApps = new ArrayList<AppInfo>();
        List<AppInfo> oldApps = AppInfoDaoOpe.queryAll(mContext);
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
        for (ApplicationInfo info : applicationInfos) {
        }
    }
}
