package com.zibuyuqing.roundcorner.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.DanmuConfig;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.Random;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DanmakuNotificationView extends FrameLayout {
    private static final String TAG = DanmakuNotificationView.class.getSimpleName();
    public static final int DANMU_MOVE_SPEED_LOW = -1;
    public static final int DANMU_MOVE_SPEED_MIDDLE = 0;
    public static final int DANMU_MOVE_SPEED_HIGH = 1;
    private Context mContext;
    private int mDanmuBgColor;
    private int mMoveSpeed;
    private int mRepeatCount;
    private int mDanmuTextColor;
    private float mProgress;
    private int mScreenHeight;
    private int mScreenWidth;
    private View mDanmuView;
    private ImageView mDanmuIconView;
    private TextView mDanmuTextView;
    private AnimationStateListener mStateListener;
    private ValueAnimator.AnimatorUpdateListener mValueUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator mValueAnimator;
    private Paint mBgPaint;
    private int mHeight;
    private int mWidth;
    private int mPaddingLeft;
    private int mDistance = 0;
    private int mShowPositionY = 0;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    public DanmakuNotificationView(Context context) {
        this(context, null);
    }

    public DanmakuNotificationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmakuNotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void addDanmakuStateListener(AnimationStateListener stateListener) {
        mStateListener = stateListener;
    }

    public void setConfig(DanmuConfig config) {
        mDanmuBgColor = config.getPrimaryColor();
        mDanmuTextColor = config.getTextColor();
        mRepeatCount = config.getRepeatCount();
        mMoveSpeed = config.getMoveSpeed();
        mDanmuTextView.setTextColor(mDanmuTextColor);
        mBgPaint.setColor(mDanmuBgColor);
        mValueAnimator.setDuration(getAnimationDuration(mMoveSpeed));
        mValueAnimator.setRepeatCount(0);
        Log.e(TAG,"setConfig getAnimationDuration(mMoveSpeed) =:" + getAnimationDuration(mMoveSpeed)+",mRepeatCount =:" + mRepeatCount);
    }

    private void init() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager)
                    mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (mWindowParams == null) {
            mWindowParams = new WindowManager.LayoutParams();
            mWindowParams.format = PixelFormat.RGBA_8888;
            mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowParams.alpha = 1;
            mWindowParams.type = ensureWindowType();
            mWindowParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            mWindowParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        }

        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
        mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_36_dp);
        mPaddingLeft = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_16_dp);
        //动画进度监听
        mValueUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                flush();
            }
        };
        mAnimatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStateListener != null) {
                    mStateListener.onStopShowDanmu(DanmakuNotificationView.this);
                    Toast.makeText(mContext, "停止", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mStateListener != null) {
                    if(isAttachedToWindow()){
                        mWindowManager.removeView(DanmakuNotificationView.this);
                    }
                    mStateListener.onStartShowDanmu(DanmakuNotificationView.this);
                    Toast.makeText(mContext, "开始", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.addUpdateListener(mValueUpdateListener);
        mValueAnimator.addListener(mAnimatorListener);
        mDanmuView = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_notification, this);
        mDanmuIconView = (ImageView) mDanmuView.findViewById(R.id.civ_danmu_icon);
        mDanmuTextView = (TextView) mDanmuView.findViewById(R.id.tv_danmu_text);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    private void updateWindow(){
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
    }
    public void show(String text, Drawable icon) {
        mDanmuTextView.setText(text);
        mDanmuIconView.setImageDrawable(icon);
        Log.e(TAG,"show :: text = :" + text +",mValueAnimator =:" + mValueAnimator +",mValueAnimator.isRunning() + ;" + mValueAnimator.isRunning());
        if (mValueAnimator != null && !mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
        Paint paint = new Paint();
        paint.setTextSize(mDanmuTextView.getTextSize());
        mWidth = (int) (mHeight + paint.measureText(text)) + 2 * mPaddingLeft;
        mDistance = (int) (mWidth * 1.25f);
        Random random = new Random();
        if(mScreenWidth > mScreenHeight){
            mShowPositionY = (int) (mScreenHeight * 0.2f);
        } else {
            mShowPositionY = random.nextInt((int) (mScreenHeight * 0.8f));
        }
        mWindowParams.width = mWidth;
        mWindowParams.height = mHeight;
        mWindowParams.y = mShowPositionY;
        mWindowParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        setLayoutParams(mWindowParams);
        requestLayout();
        if (!isAttachedToWindow()) {
            mWindowManager.addView(this, mWindowParams);
        }
    }
    private void flush() {
        setTranslationX(- mDistance + (mScreenWidth + mDistance) *(1.0f - mProgress));
    }

    public void destroy(){
        if(isAttachedToWindow()){
            mWindowManager.removeView(this);
        }
    }
    private int ensureWindowType() {
        mWindowParams.width = ViewUtil.getScreenWidth(mContext);
        mWindowParams.height = ViewUtil.getScreenHeight(mContext);
        boolean hasNav = ViewUtil.getNavigationBarHeight(mContext) > 0;
        if (Utilities.isBeforeAndroidN()) {
            return WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Utilities.isCanUseApplicationOverlayType()) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            if (hasNav) {
                return WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        updateWindow();
        setMeasuredDimension(mScreenWidth,mHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRoundRect(new RectF(0,0,mWidth,mHeight),mHeight,mHeight,mBgPaint);
    }


    public void setAction(final PendingIntent intent) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"我被点击了",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface AnimationStateListener {
        void onStartShowDanmu(DanmakuNotificationView view);

        void onStopShowDanmu(DanmakuNotificationView view);
    }

    private int getAnimationDuration(int speed) {
        switch (speed) {
            case DANMU_MOVE_SPEED_LOW:
                return 12000;
            case DANMU_MOVE_SPEED_MIDDLE:
                return 6000;
            case DANMU_MOVE_SPEED_HIGH:
                return 4000;
            default:
                return 6000;
        }
    }
}
