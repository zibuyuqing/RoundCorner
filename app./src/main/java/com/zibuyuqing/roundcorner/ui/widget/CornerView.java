package com.zibuyuqing.roundcorner.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.zibuyuqing.roundcorner.R;

/**
 * Created by Xijun.Wang on 2017/10/28.
 */

public class CornerView extends View {
    public static final int DEFAULT_OPACITY = 255;
    private Context context;
    private Paint paint;
    private Path path;
    private int location = 0;
    private int cornerSize = 0;
    private int opacity = 0;

    public CornerView(Context context) {
        this(context, null);
    }

    public CornerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CornerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        init();
    }

    private void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.cornerSize = context.getResources().getDimensionPixelSize(R.dimen.corner_size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.path = new Path();
        switch (location) {
            // top left
            case Gravity.TOP | Gravity.LEFT:
                this.path.moveTo(0.0f, 0.0f);
                this.path.lineTo(0.0f, (float) h);
                this.path.arcTo(new RectF(0.0f, 0.0f, ((float) w) * 2.0f, ((float) h) * 2.0f), 180.0f, 90.0f, true);
                this.path.lineTo((float) w, 0.0f);
                this.path.lineTo(0.0f, 0.0f);
                this.path.close();
                break;
            // top right
            case Gravity.TOP | Gravity.RIGHT:
                this.path.moveTo((float)w, 0.0f);
                this.path.lineTo(0.0f, 0.0f);
                this.path.arcTo(new RectF(-((float) w) , 0.0f, w, ((float) h) * 2.0f), 270.0f, 90.0f, true);
                this.path.lineTo((float) w, (float)h);
                this.path.lineTo((float)w, 0.0f);
                this.path.close();
                break;
            // bottom left
            case Gravity.BOTTOM | Gravity.LEFT:
                this.path.moveTo(0.0f, (float) h);
                this.path.lineTo((float) w, (float)h);
                this.path.arcTo(new RectF(0.0f, -((float) h), ((float) w) * 2.0f, ((float) h)), 90.0f, 90.0f, true);
                this.path.lineTo(0.0f, 0.0f);
                this.path.lineTo(0.0f, (float)h);
                this.path.close();
                break;
            // bottom right
            case Gravity.BOTTOM | Gravity.RIGHT:
                this.path.moveTo((float) w, (float)h);
                this.path.lineTo(0.0f, (float) h);
                this.path.arcTo(new RectF(-((float) w), -((float) h), ((float) w), ((float) h)), 90.0f, - 90.0f, true);
                this.path.lineTo((float) w, 0.0f);
                this.path.lineTo((float) w, (float)h);
                this.path.close();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(cornerSize,cornerSize);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawPath(this.path, this.paint);
    }

    public void setColor(int i) {
        this.paint.setColor(i);
        invalidate();
    }
    public void setLocation(int location){
        this.location = location;
        invalidate();
    }
    public void setCornerSize(int size){
        this.cornerSize = size;
        requestLayout();
        invalidate();
    }
    public void setCornerOpacity(int opacity){
        this.opacity = opacity;
        this.paint.setAlpha(opacity);
        invalidate();
    }
    public void hide(){
        setVisibility(GONE);
    }
    public void show(){
        setVisibility(VISIBLE);
    }
}
