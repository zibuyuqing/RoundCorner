package com.zibuyuqing.roundcorner.model.bean;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/05/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class IconConfig {
    private int size;
    private int duration;
    private int bgColor;
    private int shape;
    private int positionX;
    private int positionY;
    private boolean collect;

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getBgColor() {
        return bgColor;

    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }
}
