package com.zibuyuqing.roundcorner.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zibuyuqing.roundcorner.model.bean.EdgeLineConfig;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

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
    private static final String TAG = EdgeLineView.class.getSimpleName();
    public static final int STYLE_OUTSPREAD_MIDDLE_OUT = 1;// 中间向外展开
    public static final int STYLE_OUTSPREAD_IN_MIDDLE = 2; // 向中间聚拢
    public static final int STYLE_OUTSPREAD_BOTTOM_TO_TOP = 3;//从下往上
    public static final int STYLE_OUTSPREAD_TOP_TO_BOTTOM = 4;//从上往下
    public static final int STYLE_WIND = 5;// 绕圈
    public static final int STYLE_FADE_IN_OUT = 6; //渐隐渐显
    public static final int DEFAULT_REPEAT_COUNT = 4;
    private int[] mMixedColorArr;
    private Context mContext;
    private Path mPath;
    private Paint mPrimaryPaint;
    private Paint mMixedPaint;
    private int mCornerSize;
    private float mStrokeWidth;
    private int mPrimaryColor;
    private int mDuration;
    private int mStyle;
    private float mProgress;
    private int mScreenHeight;
    private int mScreenWidth;
    private EdgeLineConfig mConfig;
    private RectF mScreenRectF;
    private boolean needReverse = true;//是否需要倒放
    private ValueAnimator.AnimatorUpdateListener mValueUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator mValueAnimator;
    private PathMeasure mPathMeasure;
    private float mPathLength;
    private boolean isCornersShown = false;
    private boolean isAnimatorRunning = false;
    private AnimationStateListener mStateListener;
    private LinearGradient mColorShader;
    private Matrix mGradientMatrix;
    private float mTranslationX;
    private float mTranslationY;
    private boolean needChangeAlpha = false;
    private Path mDst;
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
        mDst = new Path();
        mPathMeasure = new PathMeasure();
        mPrimaryPaint = new Paint();
        mPrimaryPaint.setAntiAlias(true);
        mMixedPaint = new Paint();
        mMixedPaint.setAntiAlias(true);
        mGradientMatrix = new Matrix();
        mAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animateHide();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animateHide();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        };
        mValueUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                if(mStateListener != null){
                    mStateListener.onAnimationRunning(mProgress);
                }
                flush();
            }
        };
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.addUpdateListener(mValueUpdateListener);
        mValueAnimator.addListener(mAnimatorListener);
    }
    private void flush(){
        if(needChangeAlpha) {
            mMixedPaint.setAlpha((int) (mProgress * 255));
        }
        invalidate();
    }
    public void setConfig(EdgeLineConfig config){
        mConfig = config;
        mPrimaryColor = config.getPrimaryColor();
        mCornerSize = config.getCornerSize();
        mStrokeWidth = config.getStrokeSize();
        mMixedColorArr = config.getMixedColorArr();
        mDuration = config.getDuration();
        mStyle = config.getStyle();
        isCornersShown = config.isCornersShown();

        mPrimaryPaint.setStrokeWidth(mStrokeWidth);
        mPrimaryPaint.setColor(mPrimaryColor);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);

        mColorShader = new LinearGradient(0,0,mScreenWidth,mScreenHeight,mMixedColorArr,null, Shader.TileMode.MIRROR);
        mMixedPaint.setShader(mColorShader);
        mMixedPaint.setStyle(Paint.Style.STROKE);
        mMixedPaint.setStrokeWidth(mStrokeWidth);
        switch (mStyle){
            case STYLE_FADE_IN_OUT:
                needReverse = true;
                needChangeAlpha = true;
                break;
            case STYLE_WIND:
                needChangeAlpha = false;
                needReverse = false;
            case STYLE_OUTSPREAD_IN_MIDDLE:
                break;
            case STYLE_OUTSPREAD_MIDDLE_OUT:
                break;
        }
        if(needReverse) {
            mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        } else {
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
        int mixedColorNum = mMixedColorArr.length;
        if(mixedColorNum > 0) {
            int duration = mDuration / DEFAULT_REPEAT_COUNT;
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setDuration(duration);
        }
        configPath();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isAnimatorRunning) {
            drawEdgeLine(canvas);
        }
    }

    private void drawEdgeLine(Canvas canvas) {
        switch (mStyle){
            case STYLE_WIND:
                drawWindStyle(canvas);
                break;
            case STYLE_FADE_IN_OUT:
                drawFadeInOutStyle(canvas);
                break;
        }
    }
    private void drawFadeInOutStyle(Canvas canvas){
        mTranslationX = mProgress * mScreenWidth;
        mTranslationY = mProgress * mScreenHeight;
        mGradientMatrix.setTranslate(mTranslationX,mTranslationY);
        mColorShader.setLocalMatrix(mGradientMatrix);
        canvas.drawPath(mPath,mMixedPaint);
    }
    private void drawWindStyle(Canvas canvas){
        mDst.reset();
        mDst.close();
        float distance = mScreenHeight / 3;
        float start = mPathLength * mProgress;
        float end = (float) (mPathLength * mProgress  + distance * Math.pow((1.5f - Math.abs(mProgress - 0.5f)),3));
        if(end >= mPathLength){
            float offsetProgress = (end - mPathLength) / mPathLength;
            Path path = new Path();
            mPathMeasure.getSegment(0,mPathLength * offsetProgress,path,true);
            canvas.drawPath(path,mMixedPaint);
        }
        mPathMeasure.getSegment(start ,end,mDst,true);
        canvas.drawPath(mDst,mMixedPaint);
    }

    private void configPath(){
        float offset = 2 * mStrokeWidth / 5;
        mScreenRectF = new RectF(offset,offset,mScreenWidth - offset,mScreenHeight - offset);
        if(isCornersShown) {
            mPath.addRoundRect(mScreenRectF, mCornerSize, mCornerSize, Path.Direction.CCW);
        } else {
            mPath.addRect(mScreenRectF, Path.Direction.CW);
        }
        mPathMeasure.setPath(mPath,false);
        mPathLength = mPathMeasure.getLength();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenWidth,mScreenHeight);
    }
    public void setAnimationStateListener(AnimationStateListener stateListener){
        mStateListener = stateListener;
    }
    public void setPrimaryColor(int color){
        mPrimaryColor = color;
        mPrimaryPaint.setColor(color);
        invalidate();
    }
    public void setMixedColors(int[] colors){
        mMixedColorArr = colors;
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
        mProgress = progress;
    }
    public void startAnimator(){
        isAnimatorRunning = true;
        if(needChangeAlpha) {
            mPrimaryPaint.setAlpha(0);
            mMixedPaint.setAlpha(0);
        } else {
            mPrimaryPaint.setAlpha(255);
            mMixedPaint.setAlpha(255);
        }
        animateShow();
    }
    private void animateShow(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"alpha",0,1.0f);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mValueAnimator != null && !mValueAnimator.isRunning()){
                    mValueAnimator.start();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimatorRunning = true;
                if(mStateListener != null){
                    mStateListener.onAnimationStart();
                }
            }
        });
        animator.start();
    }
    private void animateHide(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"alpha",1.0f,0.0f);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimatorRunning = false;
                if(mStateListener != null){
                    mStateListener.onAnimationEnd();
                }
            }
        });
        animator.start();
    }
    public void setCornersShown(boolean shown){
        isCornersShown = shown;
    }
    public void stopAnimator(){
        setVisibility(INVISIBLE);
    }
    public interface AnimationStateListener{
        void onAnimationStart();
        void onAnimationRunning(float progress);
        void onAnimationEnd();
    }
}
