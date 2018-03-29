package com.zibuyuqing.roundcorner.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

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
public class AppInfo implements Parcelable{
    public static final int APP_DISABLE = 0;
    public static final int APP_ENABLE = 1;
    public static final int SYSTEM_APP = 0;
    public static final int USER_APP = 1;
    @Id(autoincrement = true)
    private Long id;
    @Unique
    public String packageName;

    public int appType;
    public int enableState = APP_ENABLE;
    public String title;
    public int mixedColorOne;
    public int mixedColorTwo;
    public int mixedColorThree;
    @Generated(hash = 79720016)
    public AppInfo(Long id, String packageName, int appType, int enableState, String title,
            int mixedColorOne, int mixedColorTwo, int mixedColorThree) {
        this.id = id;
        this.packageName = packageName;
        this.appType = appType;
        this.enableState = enableState;
        this.title = title;
        this.mixedColorOne = mixedColorOne;
        this.mixedColorTwo = mixedColorTwo;
        this.mixedColorThree = mixedColorThree;
    }
    @Generated(hash = 1656151854)
    public AppInfo() {
    }

    protected AppInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        packageName = in.readString();
        appType = in.readInt();
        enableState = in.readInt();
        title = in.readString();
        mixedColorOne = in.readInt();
        mixedColorTwo = in.readInt();
        mixedColorThree = in.readInt();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

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
    public int getAppType() {
        return this.appType;
    }
    public void setAppType(int appType) {
        this.appType = appType;
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
    public int getMixedColorOne() {
        return this.mixedColorOne;
    }
    public void setMixedColorOne(int mixedColorOne) {
        this.mixedColorOne = mixedColorOne;
    }
    public int getMixedColorTwo() {
        return this.mixedColorTwo;
    }
    public void setMixedColorTwo(int mixedColorTwo) {
        this.mixedColorTwo = mixedColorTwo;
    }
    public int getMixedColorThree() {
        return this.mixedColorThree;
    }
    public void setMixedColorThree(int mixedColorThree) {
        this.mixedColorThree = mixedColorThree;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(packageName);
        dest.writeInt(appType);
        dest.writeInt(enableState);
        dest.writeString(title);
        dest.writeInt(mixedColorOne);
        dest.writeInt(mixedColorTwo);
        dest.writeInt(mixedColorThree);
    }
}
