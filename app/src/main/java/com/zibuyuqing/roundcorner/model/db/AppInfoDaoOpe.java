package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;

import com.zibuyuqing.roundcorner.model.AppInfoDao;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;

import org.greenrobot.greendao.query.QueryBuilder;

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
public class AppInfoDaoOpe {
    public static void insertAppInfo(Context context, AppInfo info){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().insert(info);
    }
    public static void insertAppInfos(Context context, List<AppInfo> infos){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().insertInTx(infos);
    }
    public static void deleteAppInfo(Context context,AppInfo info){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().delete(info);
    }
    public static void deleteAppInfoById(Context context,long id){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().deleteByKey(id);
    }
    public static void deleteAll(Context context){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().deleteAll();
    }
    public static void updateAppInfo(Context context,AppInfo info){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().update(info);
    }
    public static void updateAppInfos(Context context,Set<AppInfo> infos){
        DbManager.getInstance(context).getDaoSession().getAppInfoDao().updateInTx(infos);
    }
    public static List<AppInfo> queryAll(Context context){
        return DbManager.getInstance(context).getDaoSession().getAppInfoDao().queryBuilder().build().list();
    }
    public static List<AppInfo> queryEnableAppInfos(Context context){
        return DbManager.getInstance(context).getDaoSession().getAppInfoDao().
                queryBuilder().where(AppInfoDao.Properties.EnableState.eq(AppInfo.APP_ENABLE)).list();
    }
    public static List<AppInfo> querySystemAppInfos(Context context){
        return DbManager.getInstance(context).getDaoSession().getAppInfoDao().
                queryBuilder().where(AppInfoDao.Properties.AppType.eq(AppInfo.SYSTEM_APP)).list();
    }
    public static List<AppInfo> queryUserAppInfos(Context context){
        return DbManager.getInstance(context).getDaoSession().getAppInfoDao().
                queryBuilder().where(AppInfoDao.Properties.AppType.eq(AppInfo.USER_APP)).list();
    }
    public static List<AppInfo> queryAppInfosByTypeWithPage(Context context,int appType,int page){
        QueryBuilder queryBuilder = DbManager.getInstance(context).getDaoSession().getAppInfoDao().queryBuilder();
        return queryBuilder.where(AppInfoDao.Properties.AppType.eq(appType)).offset(page * 24).limit(24).build().list();
    }
    public static List<AppInfo> queryAppInfosByPage(Context context, int page){
        return DbManager.getInstance(context).getDaoSession().getAppInfoDao().
                queryBuilder().offset(page * 24).limit(24).build().list();
    }
}
