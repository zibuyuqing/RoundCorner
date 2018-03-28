package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppInfoLoadTask extends AsyncTask<Void,Void,List<AppInfoWithIcon>>{
    private AppInfoLoadStateListener mListener;
    private Context mContext;
    public AppInfoLoadTask(Context context,AppInfoLoadStateListener listener){
        mContext = context;
        mListener = listener;
    }
    public static void execute(Context context,AppInfoLoadStateListener listener){
        new AppInfoLoadTask(context,listener).execute();
    }
    @Override
    protected List<AppInfoWithIcon> doInBackground(Void... voids) {
        PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
        List<AppInfo> appInfos = AppInfoDaoOpe.queryAll(mContext);
        List<AppInfoWithIcon> appInfoWithIconList = new ArrayList<>();
        int count = appInfos.size();
        if(mListener != null){
            mListener.startLoad(count);
        }
        AppInfo appInfo;
        for(int i = 0; i < count; i++){
            appInfo = appInfos.get(i);
            try {
                AppInfoWithIcon infoWithIcon = new AppInfoWithIcon(appInfo,
                        ViewUtil.createIconBitmap(mContext,packageManager.getApplicationIcon(appInfo.packageName)));
                if(!appInfoWithIconList.contains(infoWithIcon)){
                    appInfoWithIconList.add(infoWithIcon);
                }
                if(mListener != null){
                    mListener.onLoad(appInfoWithIconList.size());
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                if(mListener != null){
                    mListener.onError(e.getMessage());
                }
            }
        }
        return appInfoWithIconList;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<AppInfoWithIcon> appInfoWithIcons) {
        super.onPostExecute(appInfoWithIcons);
        if(mListener != null){
            mListener.endLoad(appInfoWithIcons);
        }
    }
    public interface AppInfoLoadStateListener{
        void startLoad(int totalCount);
        void onLoad(int process);
        void endLoad(List<AppInfoWithIcon> appInfoWithIconList);
        void onError(String msg);
    }
}
