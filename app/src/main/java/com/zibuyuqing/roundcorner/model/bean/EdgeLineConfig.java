package com.zibuyuqing.roundcorner.model.bean;

import java.util.ArrayList;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EdgeLineConfig {
    private int primaryColor;
    private int[] mixedColorArr;
    private int strokeSize;
    private int cornerSize;
    private int duration;
    private int style;
    private boolean isCornersShown;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public int[] getMixedColorArr() {
        return mixedColorArr;
    }

    public void setMixedColorArr(int[] mixedColorArr) {
        this.mixedColorArr = mixedColorArr;
    }

    public int getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(int strokeSize) {
        this.strokeSize = strokeSize;
    }

    public int getCornerSize() {
        return cornerSize;
    }

    public void setCornerSize(int cornerSize) {
        this.cornerSize = cornerSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isCornersShown() {
        return isCornersShown;
    }

    public void setCornersShown(boolean cornersShown) {
        isCornersShown = cornersShown;
    }
}
