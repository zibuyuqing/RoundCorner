package com.zibuyuqing.roundcorner.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.IconConfig;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class IconNotificationView extends FrameLayout{
    public final static int DEFAULT_ICON_SHOW_DURATION = 0;
    public final static int SHAPE_CIRCLE = 0;
    public final static int SHAPE_RECTANGLE = 1;
    public final static int SHAPE_NONE = 2;
    private static final long FADE_OUT_DELAY = 100;
    private Context mContext;
    private int mDuration;
    private int mBgColor;
    private int mShape;
    private int mSize;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mPositionX;
    private int mPositionY;
    private int mTouchSlop;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private float mLastMotionY;
    private float animateProgress;
    private View mNotificationView;
    private ImageView mIvIcon;
    private Paint mBgPaint;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private boolean mIsMoving = false;
    private boolean mIsIconViewTouched = false;
    private boolean mIsCollectEnanle;
    private boolean mIsIconViewReset = false;
    private List<ObjectAnimator> mRunningAnimatorList = new ArrayList<>();
    private OnActionListener mListener;
    private String mWho;
    private Handler mHandler;
    private final Runnable mRemoveViewCallBack = new Runnable() {
        @Override
        public void run() {
            mWho = "";
            if(isAttachedToWindow()){
                mWindowManager.removeView(IconNotificationView.this);
            }
        }
    };
    private final Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if(IconNotificationView.this.isAttachedToWindow()){
                mWindowManager.removeView(IconNotificationView.this);
                if(mListener != null){
                    mListener.onIconDismiss();
                }
            }
        }
    };
    private final Runnable mFadeOutRunnable = new Runnable() {
        @Override
        public void run() {
            mIsIconViewReset = false;
            int halfScreen = mScreenWidth >> 1;
            final boolean toLeft = Math.abs(mLastMotionX) < halfScreen;
            final int toX = toLeft ? 0 : mScreenWidth;
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",1.0f,0.3f);
            final ObjectAnimator windowAnimator;
            windowAnimator = ObjectAnimator.ofPropertyValuesHolder(IconNotificationView.this,alpha);
            windowAnimator.setStartDelay(500);
            windowAnimator.setDuration(500);
            windowAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(!mIsIconViewReset) {
                        if (!mRunningAnimatorList.contains(windowAnimator)) {
                            mRunningAnimatorList.add(windowAnimator);
                        }
                    } else {
                        if(windowAnimator.isRunning()) {
                            windowAnimator.cancel();
                        }
                    }
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRunningAnimatorList.remove(windowAnimator);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mRunningAnimatorList.remove(windowAnimator);
                }
            });
            // 将悬浮窗靠边
            PropertyValuesHolder animateProgress = PropertyValuesHolder.ofFloat("animateProgress", 1.0f, 0.0f);
            final ObjectAnimator viewAnimator = ObjectAnimator.ofPropertyValuesHolder(IconNotificationView.this, animateProgress);
            viewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = ((Float) animation.getAnimatedValue()).floatValue();
                    if (toLeft) {
                        mWindowParams.x = (int) (toX + Math.abs(toX - mLastMotionX) * rate);
                    } else {
                        mWindowParams.x = (int) (mLastMotionX + Math.abs(mScreenWidth -  mLastMotionX - mSize) *(1.0f - rate));
                    }
                    if(mListener != null){
                        mListener.onIconMoved(mWindowParams.x,mWindowParams.y);
                    }
                    if (isAttachedToWindow()) {
                        mWindowManager.updateViewLayout(IconNotificationView.this, mWindowParams);
                    }
                }
            });
            viewAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (!mRunningAnimatorList.contains(viewAnimator)) {
                        mRunningAnimatorList.add(viewAnimator);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!mIsIconViewReset) {
                        windowAnimator.start();
                    }
                    mLastMotionX = mWindowParams.x;
                    mLastMotionY = mWindowParams.y;
                    updateIconPosition();
                    mRunningAnimatorList.remove(viewAnimator);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mRunningAnimatorList.remove(viewAnimator);
                    mLastMotionX = mWindowParams.x;
                    mLastMotionY = mWindowParams.y;
                    updateIconPosition();
                }
            });
            viewAnimator.setDuration(500);
            viewAnimator.start();
        }
    };

    public IconNotificationView(@NonNull Context context) {
        this(context, null);
    }

    public IconNotificationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconNotificationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }
    public void setOnActionListener(OnActionListener listener){
        mListener = listener;
    }
    private void init() {
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
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

                    // 以下属性设置加载我们圆角window 不抢焦点,不拦截事件
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            mWindowParams.type = ensureWindowType();
        }
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop() / 2;
        mHandler = new Handler(Looper.getMainLooper());
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mNotificationView = LayoutInflater.from(mContext).inflate(R.layout.layout_icon_notification,this);
        mIvIcon = mNotificationView.findViewById(R.id.iv_icon);
    }
    public void setConfig(IconConfig config){
        Log.e("hahah","setConfig =:" + config.toString());
        mBgColor = config.getBgColor();
        mDuration = getDuration(config.getDuration());
        mShape = config.getShape();
        mIsCollectEnanle = config.isCollect();
        mSize = config.getSize();
        mPositionX = config.getPositionX();
        mPositionY = config.getPositionY();
        mLastMotionX = mPositionX;
        mLastMotionY = mPositionY;
        mWindowParams.width = mSize ;
        mWindowParams.height = mSize;
        mWindowParams.x = mPositionX;
        mWindowParams.y = mPositionY;
        mBgPaint.setColor(mBgColor);
        if(mShape !=  SHAPE_NONE) {
            mBgPaint.setAlpha(255);
        } else {
            mBgPaint.setAlpha(0);
        }
    }
    
    private void updateIconPosition(){
        SettingsDataKeeper.writeSettingsInt(mContext,SettingsDataKeeper.ICON_NOTIFICATION_POSITION_X, (int) mLastMotionX);
        SettingsDataKeeper.writeSettingsInt(mContext,SettingsDataKeeper.ICON_NOTIFICATION_POSITION_Y, (int) mLastMotionY);
    }
    private void setAnimateProgress(float progress){
        animateProgress = progress;
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mShape == SHAPE_CIRCLE){
            canvas.drawOval(new RectF(0,0,mSize,mSize),mBgPaint);
        } else if(mShape == SHAPE_RECTANGLE){
            canvas.drawRoundRect(new RectF(0,0,mSize,mSize),10,10,mBgPaint);
        }
        super.dispatchDraw(canvas);
    }
    private void resetTouchState(){
        mIsMoving = false;
        mIsIconViewTouched = false;
    }
    private void resetController(){
        mIsIconViewReset = true;
        clearViewAnimation();
        setAlpha(1.0f);
        setTranslationX(0);
    }
    private void clearViewAnimation(){
        for(ObjectAnimator animator : mRunningAnimatorList){
            animator.cancel();
        }
    }
    private void fadeOutController(){
        mHandler.postDelayed(mFadeOutRunnable,FADE_OUT_DELAY);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                mInitialMotionX  = event.getX();
                mInitialMotionY  = event.getY();
                mIsMoving = false;
                Rect iconViewRect = new Rect();
                IconNotificationView.this.getHitRect(iconViewRect);
                if(iconViewRect.contains((int)mInitialMotionX,(int)mInitialMotionY)){
                    mIsIconViewTouched = true;
                }
                mHandler.removeCallbacks(mFadeOutRunnable);
                resetController();
                break;
            case MotionEvent.ACTION_MOVE:
                int xMove = (int) Math.abs(x - mLastMotionX - mInitialMotionX);
                int yMove = (int)Math.abs(y - mLastMotionY - mInitialMotionY);
                if(xMove > mTouchSlop || yMove > mTouchSlop){
                    mIsMoving = true;
                    mWindowParams.x = (int) Math.abs(x - mInitialMotionX);
                    mWindowParams.y = (int) Math.abs(y - mInitialMotionY);
                    mWindowManager.updateViewLayout(this,mWindowParams);
                    mLastMotionX = mWindowParams.x;
                    mLastMotionY = mWindowParams.y;
                    if(mListener != null){
                        mListener.onIconMoved(mWindowParams.x, mWindowParams.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mInitialMotionX = mInitialMotionY = 0;
                mWindowManager.updateViewLayout(this,mWindowParams);
                if(!mIsMoving){
                    if(mIsIconViewTouched) {
                        if(mListener != null){
                            mListener.onIconClicked();
                        }
                    }
                }
                fadeOutController();
                resetTouchState();
                updateIconPosition();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,widthMeasureSpec);
        mSize = ViewUtil.getIconSize(mContext);
        updateWindow();
        setMeasuredDimension(mSize,mSize);
    }
    private void updateWindow(){
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
        if(isAttachedToWindow()){
            if(mWindowManager != null && mWindowParams != null) {
                mWindowParams.width = mSize;
                mWindowParams.height = mSize;
                if(mScreenWidth > mScreenHeight) {
                    if(mWindowParams.y + mSize > mScreenHeight){
                        mWindowParams.y = mScreenHeight / 2;
                    }
                }
                if(mWindowParams.x + mSize > mScreenWidth){
                    mWindowParams.x = mScreenWidth - mSize;
                }
                mWindowManager.updateViewLayout(IconNotificationView.this, mWindowParams);
                if(mListener!= null){
                    mListener.onIconSizeChanged();
                    mListener.onIconMoved(mWindowParams.x,mWindowParams.y);
                }
            }
        }
    }
    private int ensureWindowType() {
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
    public void updateIcon(Drawable icon,String who){
        mWho = who;
        mIvIcon.setImageBitmap(getFixedIcon(icon));
    }
    private Bitmap getFixedIcon(Drawable icon){
        Bitmap iconBmp;
        if(mShape != SHAPE_NONE) {
            iconBmp = ViewUtil.createIconBitmap(icon, (int) (mSize * 0.6f));
        } else {
            iconBmp = ViewUtil.createIconBitmap(icon, (int) (mSize));
        }
        return iconBmp;
    }
    public void animateShow(String who,Drawable icon, final boolean isDemo) {
        if(!isAttachedToWindow()){
            mWindowManager.addView(IconNotificationView.this,mWindowParams);
        }
        mHandler.removeCallbacks(mRemoveViewCallBack);
        setAlpha(1);
        mWho = who;
        Bitmap iconBmp = getFixedIcon(icon);
        mIvIcon.setImageBitmap(iconBmp);
        int halfScreen = mScreenWidth >> 1;
        final boolean onLeft = Math.abs(mLastMotionX) < halfScreen;
        float startX = onLeft ? - mSize : 0;
        float endX = onLeft ? 0 : 0;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,"translationX",startX,endX);
        animator.setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(isDemo){
                    mHandler.postDelayed(mDismissRunnable,2000);
                }
                if(mDuration != DEFAULT_ICON_SHOW_DURATION){
                    mHandler.postDelayed(mDismissRunnable,mDuration);
                } else {
                    mHandler.postDelayed(mFadeOutRunnable,3000);
                }
            }
        });
        animator.start();
    }
    public float getPositionX(){
        return mWindowParams.x;
    }
    public float getPositionY(){
        return mWindowParams.y;
    }
    public String getIdentify(){
        return mWho;
    }

    public void dismiss() {
        mHandler.postDelayed(mRemoveViewCallBack,100);
    }

    public interface OnActionListener{
        void onIconClicked();
        void onIconMoved(float x,float y);
        void onIconSizeChanged();
        void onIconDismiss();
    }
    private int getDuration(int speed) {
        switch (speed) {
            case 0:
                return DEFAULT_ICON_SHOW_DURATION;
            case 1:
                return 2000;
            case 2:
                return 4000;
            case 3:
                return 6000;
            case 4:
                return 8000;
            default:
                return DEFAULT_ICON_SHOW_DURATION;
        }
    }
}
