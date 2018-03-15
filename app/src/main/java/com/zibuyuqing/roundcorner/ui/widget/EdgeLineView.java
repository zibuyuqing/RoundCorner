package com.zibuyuqing.roundcorner.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zibuyuqing.roundcorner.model.bean.EdgeLineConfig;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

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
public class EdgeLineView extends View {
    private final int STYLE_OUTSPREAD_MIDDLE_OUT = 1;// 中间向外展开
    private final int STYLE_OUTSPREAD_IN_MIDDLE = 2; // 向中间聚拢
    private final int STYLE_OUTSPREAD_BOTTOM_TO_TOP = 3;//从下往上
    private final int STYLE_OUTSPREAD_TOP_TO_BOTTOM = 4;//从上往下
    private final int STYLE_WIND = 5;// 绕圈
    private final int STYLE_FADE_IN_OUT = 5; //渐隐渐显
    private ArrayList<Integer> mMixedColorList = new ArrayList<>(4);
    private Context mContext;
    private Path mPath;
    private Paint mPrimaryPaint;
    private int mCornerSize;
    private float mStrokeWidth;
    private int mPrimaryColor;
    private int mDuration;
    private int mStyle;
    private int progress;
    private int mScreenHeight;
    private int mScreenWidth;
    private EdgeLineConfig mConfig;
    private RectF mScreenRectF;
    private boolean mNeedReverse = false;//是否需要倒放

    public EdgeLineView(Context context) {
        this(context,null);
    }

    public EdgeLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EdgeLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
        mPath = new Path();
        mPrimaryPaint = new Paint();
        mPrimaryPaint.setAntiAlias(true);
        mScreenRectF = new RectF(0,0,mScreenWidth,mScreenHeight);
    }

    public void setConfig(EdgeLineConfig config){
        mConfig = config;
        mPrimaryColor = config.getPrimaryColor();
        mCornerSize = config.getCornerSize();
        mStrokeWidth = config.getStrokeSize();
        mMixedColorList = config.getMixedColorList();
        mDuration = config.getDuration();
        mPrimaryPaint.setStrokeWidth(mStrokeWidth);
        mPrimaryPaint.setColor(mPrimaryColor);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        configPath();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawEdgeLine(canvas);
    }

    private void drawEdgeLine(Canvas canvas) {
        canvas.drawPath(mPath,mPrimaryPaint);
    }

    private void configPath(){
        mPath.addRoundRect(mScreenRectF,mCornerSize,mCornerSize,Path.Direction.CCW);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenWidth,mScreenHeight);
    }

    public void setPrimaryColor(int color){
        mPrimaryColor = color;
        mPrimaryPaint.setColor(color);
        invalidate();
    }
    public void setMixedColors(ArrayList<Integer> colors){
        mMixedColorList = colors;
        invalidate();
    }
    public void setStrokeWidth(float strokeWidth){
        mStrokeWidth = strokeWidth;
        mPrimaryPaint.setStrokeWidth(strokeWidth);
        invalidate();

    }
    public void setCornerSize(int size){
        mCornerSize = size;
        configPath();
        invalidate();
    }
    public void setDuration(int duration){
        mDuration = duration;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
