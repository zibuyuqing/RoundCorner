package com.zibuyuqing.roundcorner.model.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/05/31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationInfo {
    private int id;
    String packageName;
    String messageOwner;
    String messageContent;
    Drawable icon;
    long time;
    public NotificationInfo(int id,String packageName, String messageOwner, String messageContent, Drawable icon,long time) {
        this.id = id;
        this.packageName = packageName;
        this.messageOwner = messageOwner;
        this.messageContent = messageContent;
        this.icon = icon;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Notification Info [ id =: " +id +",packeage =:" + packageName +",messageOwner =:" + messageOwner +",messageContent =:" + messageContent +",icon =:" + icon +",]";
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMessageOwner() {
        return messageOwner;
    }

    public void setMessageOwner(String messageOwner) {
        this.messageOwner = messageOwner;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String indentify(){
        return packageName + messageOwner + messageContent;
    }
}
