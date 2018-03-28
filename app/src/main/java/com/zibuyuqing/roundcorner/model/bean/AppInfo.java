package com.zibuyuqing.roundcorner.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Entity
public class AppInfo{
    public static final int APP_DISABLE = 0;
    public static final int APP_ENABLE = 1;
    public static final int SYSTEM_APP = 0;
    public static final int USER_APP = 1;
    @Id(autoincrement = true)
    private Long id;
    @Unique
    public String packageName;

    public int isSystemApp;
    public int enableState = APP_ENABLE;
    public String title;

    @Generated(hash = 1326212595)
    public AppInfo(Long id, String packageName, int isSystemApp, int enableState,String title) {
        this.id = id;
        this.packageName = packageName;
        this.isSystemApp = isSystemApp;
        this.enableState = enableState;
        this.title = title;
    }
    @Generated(hash = 1656151854)
    public AppInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public int getIsSystemApp() {
        return this.isSystemApp;
    }
    public void setIsSystemApp(int isSystemApp) {
        this.isSystemApp = isSystemApp;
    }
    public int getEnableState() {
        return this.enableState;
    }
    public void setEnableState(int enableState) {
        this.enableState = enableState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
