package com.zibuyuqing.roundcorner.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zibuyuqing.roundcorner.R;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DynamicBgView extends View {

    private Paint paint;
    //默认的基色
    private int baseColor = 0x7f0194;
    private Context context;
    private PointF startPoint1,startPoint2,startPoint3,startPoint4,startPoint5;
    private PointF endPoint1,endPoint2,endPoint3,endPoint4,endPoint5;
    private PointF controlPoint1,controlPoint2,controlPoint3,controlPoint4,controlPoint5;

    int animCount = 0;

    int resetAnimCount = 0;

    Handler handler = new Handler();

    private boolean isRunning = false;

    public DynamicBgView(Context context) {
        this(context,null);
    }

    public DynamicBgView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DynamicBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DynamicBgView,
                defStyleAttr, 0);
        baseColor = attributes.getColor(R.styleable.DynamicBgView_base_color, context.getResources().getColor(R.color.colorPrimary));
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setColor(baseColor);

        startPoint1 = new PointF(0, 0);
        endPoint1 = new PointF(0, 0);
        controlPoint1 = new PointF(0, 0);

        startPoint2 = new PointF(0, 0);
        endPoint2 = new PointF(0, 0);
        controlPoint2 = new PointF(0, 0);

        startPoint3 = new PointF(0, 0);
        endPoint3 = new PointF(0, 0);
        controlPoint3 = new PointF(0, 0);

        startPoint4 = new PointF(0, 0);
        endPoint4 = new PointF(0, 0);
        controlPoint4 = new PointF(0, 0);

        startPoint5 = new PointF(0, 0);
        endPoint5 = new PointF(0, 0);
        controlPoint5 = new PointF(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        startPoint1.x = w * 0.25f;
        startPoint1.y = 0;

        endPoint1.x = w * 4;
        endPoint1.y = 0;

        controlPoint1.x = w * 0.5f;
        controlPoint1.y = h * 0.8f;

        //-------------------------------
        startPoint2.x = w * 0.2f;
        startPoint2.y = 0;

        endPoint2.x = w * 2;
        endPoint2.y = 0;

        controlPoint2.x = w * 0.5f;
        controlPoint2.y = h * 0.6f;
        //--------------------------------
        startPoint3.x = 0;
        startPoint3.y = 0;

        endPoint3.x = w + w;
        endPoint3.y = 0;

        controlPoint3.x = w * 0.5f;
        controlPoint3.y = h * 0.6f;

        //--------------------------------
        startPoint4.x = -w;
        startPoint4.y = 0;

        endPoint4.x = w * 0.8f;
        endPoint4.y = - h * 0.1f;

        controlPoint4.x = w * 0.5f;
        controlPoint4.y =  h * 0.6f;

        //--------------------------------
        startPoint5.x = -w;
        startPoint5.y = 0;

        endPoint5.x = w * 0.5f;
        endPoint5.y = - h * 0.1f;

        controlPoint5.x = w * 0.3f;
        controlPoint5.y = h * 0.5f;
    }
    public void setBaseColor(int color){
        baseColor = color;
        Log.e("hahahha","setBaseColor  color =:" + color);
        paint.setColor(baseColor);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path1 = new Path();
        paint.setAlpha(68);
        path1.moveTo(startPoint1.x, startPoint1.y);
        path1.quadTo(controlPoint1.x, controlPoint1.y, endPoint1.x, endPoint1.y);
        canvas.drawPath(path1, paint);

        Path path2 = new Path();
        paint.setAlpha(102);
        path2.moveTo(startPoint2.x, startPoint2.y);
        path2.quadTo(controlPoint2.x, controlPoint2.y, endPoint2.x, endPoint2.y);
        canvas.drawPath(path2, paint);

        Path path4 = new Path();
        paint.setAlpha(85);
        path4.moveTo(startPoint4.x, startPoint4.y);
        path4.quadTo(controlPoint4.x, controlPoint4.y, endPoint4.x, endPoint4.y);
        canvas.drawPath(path4, paint);

        Path path3 = new Path();
        paint.setAlpha(136);
        path3.moveTo(startPoint3.x, startPoint3.y);
        path3.quadTo(controlPoint3.x, controlPoint3.y, endPoint3.x, endPoint3.y);
        canvas.drawPath(path3, paint);

        Path path5 = new Path();
        paint.setAlpha(119);
        path5.moveTo(startPoint5.x, startPoint5.y);
        path5.quadTo(controlPoint5.x, controlPoint5.y, endPoint5.x, endPoint5.y);
        canvas.drawPath(path5, paint);
        paint.setAlpha(255);

    }

    /**
     * 部分控制点的Y坐标是加部分控制点的是减
     */
    private void anim(){
        controlPoint1.y += 2;
        controlPoint2.y -= 2;
        controlPoint3.y += 2;
        controlPoint4.y -= 2;
        controlPoint5.y += 2;
        postInvalidate();
    }
    private void resetAnim(){
        controlPoint1.y -= 2;
        controlPoint2.y += 2;
        controlPoint3.y -= 2;
        controlPoint4.y += 2;
        controlPoint5.y -= 2;
        postInvalidate();
    }

    /**
     * 不断刷新界面
     * 不断来回改变控制点的Y坐标不断重绘界面。
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(animCount < 40){
                anim();
                animCount++;
                if(animCount == 40){
                    resetAnimCount = 0;
                }
            }else{
                resetAnim();
                resetAnimCount++;
                if(resetAnimCount == 40){
                    animCount = 0;
                }
            }
            handler.postDelayed(this, 100);
        }
    };

    public void start(){
        Log.e("hahah"," isrunnig =:" + isRunning);
        if(isRunning){
            return;
        }
        handler.post(runnable);
        isRunning = true;
    }

    public void stop(){
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
    }
}
