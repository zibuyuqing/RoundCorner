package com.zibuyuqing.roundcorner.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zibuyuqing.roundcorner.model.DaoMaster;
import com.zibuyuqing.roundcorner.model.DaoSession;

import java.nio.file.ReadOnlyFileSystemException;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DbManager {
    // 是否加密
    private static final boolean ENCRYPTED = false;
    private static final String DB_NAME = "apps.db";
    private static DbManager sInstance;
    private DaoMaster.DevOpenHelper mDevOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DbManager(Context context){
        mContext = context;
        mDevOpenHelper = new DaoMaster.DevOpenHelper(mContext,DB_NAME,null);
        mDatabase = mDevOpenHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDatabase);
        mDaoSession = mDaoMaster.newSession();
    }
    public static DbManager getInstance(Context context){
        if(sInstance == null){
            synchronized (DbManager.class){
                if(sInstance == null){
                    sInstance = new DbManager(context);
                }
            }
        }
        return sInstance;
    }
    public DaoMaster getDaoMaster(){
        return mDaoMaster;
    }
    public DaoSession getDaoSession(){
        return mDaoSession;
    }
}
