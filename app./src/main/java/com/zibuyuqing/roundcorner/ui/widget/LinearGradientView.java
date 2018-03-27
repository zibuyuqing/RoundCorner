package com.zibuyuqing.roundcorner.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zibuyuqing.roundcorner.utils.ViewUtil;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LinearGradientView extends View {
    private Paint mMixedPaint; // 混合颜色画笔
    private LinearGradient mColorShader; // 渐变色shader
    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext;
    private int[] mMixedColorArray;
    private boolean isDrawAble = false;
    public LinearGradientView(Context context) {
        this(context,null);
    }

    public LinearGradientView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LinearGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mMixedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMixedPaint.setStyle(Paint.Style.FILL);
    }
    public void setMixedColors(int[] colors){
        if(colors == null || colors.length < 2){
            return;
        }
        mMixedColorArray = colors;
        mColorShader = new LinearGradient(0,0,
                mScreenWidth,getMeasuredHeight(),mMixedColorArray,null, Shader.TileMode.MIRROR);
        mMixedPaint.setShader(mColorShader);
        isDrawAble = true;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLinearLine(canvas);
    }

    private void drawLinearLine(Canvas canvas) {
        if(isDrawAble){
            canvas.drawRect(new RectF(0,0,getMeasuredWidth(),getMeasuredHeight()),mMixedPaint);
        }
    }
}
