package com.zibuyuqing.roundcorner.model.bean;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/17
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DanmuConfig {
    private int textColor;
    private int primaryColor;
    private int repeatCount;
    private int moveSpeed;
    private boolean useRandomColor;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public boolean isUseRandomColor() {
        return useRandomColor;
    }

    public void setUseRandomColor(boolean useRandomColor) {
        this.useRandomColor = useRandomColor;
    }
}
