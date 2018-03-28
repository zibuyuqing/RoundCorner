package com.zibuyuqing.roundcorner.model.bean;

import android.graphics.Bitmap;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppInfoWithIcon extends AppInfo{
    private Bitmap icon;
    private AppInfo appInfo;
    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
    public AppInfoWithIcon(AppInfo info,Bitmap icon){
        this.appInfo = info;
        this.enableState = info.enableState;
        this.appType = info.appType;
        this.title = info.title;
        this.packageName = info.packageName;
        this.icon = icon;
    }
    public AppInfo getAppInfo(){
        return appInfo;
    }
}
