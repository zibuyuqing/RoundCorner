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
    private ArrayList<Integer> mixedColorList;
    private int strokeSize;
    private int cornerSize;
    private int duration;

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public ArrayList<Integer> getMixedColorList() {
        return mixedColorList;
    }

    public void setMixedColorList(ArrayList<Integer> mixedColorList) {
        this.mixedColorList = mixedColorList;
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
}
