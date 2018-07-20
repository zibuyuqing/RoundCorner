package com.zibuyuqing.roundcorner.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.DanmuConfig;
import com.zibuyuqing.roundcorner.utils.BitmapUtil;
import com.zibuyuqing.roundcorner.utils.ResUtil;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public static final int DANMU_MOVE_SPEED_SUPER_LOW = 0;
    public static final int DANMU_MOVE_SPEED_LOW = 1;
    public static final int DANMU_MOVE_SPEED_MIDDLE = 2;
    public static final int DANMU_MOVE_SPEED_HIGH = 3;
    public static final int DANMU_MOVE_SPEED_SUPER_HIGH = 4;
    private final static int SKIN_STYLE_NONE = 0;
    private final static int SKIN_STYLE_RAIL = 1;
    private final static int SKIN_STYLE_HEAD = 2;
    private final static int SKIN_STYLE_FILL_BODY = 3;
    private Context mContext;
    private int mDanmuBgColor;
    private int mMoveSpeed;
    private int mRepeatCount;
    private int mDanmuTextColor;
    private int mDanmuBgAlpha;
    private int mDanmuDuration;
    private float mProgress;
    private int mScreenHeight;
    private int mScreenWidth;
    private View mDanmuView;
    private ImageView mDanmuIconView;
    private TextView mDanmuContentView;
    private TextView mDanmuTitleView;
    private ImageView mDanmuHeadView;
    private ImageView mDanmuRailView;
    private AnimationStateListener mStateListener;
    private ValueAnimator.AnimatorUpdateListener mValueUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator mValueAnimator;
    private Paint mBgPaint;
    private Paint mBgEdgePaint;
    private int mHeight;
    private int mWidth;
    private int mPaddingLeft;
    private int mDistance = 0;
    private int mShowPositionY = 0;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private int mHeadImageWidth = 0;
    private int mRailImageWidth = 0;
    private int mRoadLength = 0;
    private int mSkinMode = SKIN_STYLE_HEAD;
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

    private  boolean isBright(int color) {
        double grayLevel = (Color.red(color) * 30 + Color.green(color) * 59
                + Color.blue(color) * 11 ) / 100;
        return grayLevel >= 215;
    }
    public void configRandomStyle(){
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        mDanmuBgColor = Color.rgb(r,g,b);
        mDanmuBgAlpha = random.nextInt(100);
        mDanmuDuration = getAnimationDuration(random.nextInt(4));
        mDanmuTextColor = Color.rgb(255 - r,255 - g,255 - b);
        mDanmuContentView.setTextColor(mDanmuTextColor);
        mDanmuTitleView.setTextColor(mDanmuTextColor);
        mBgPaint.setColor(mDanmuBgColor);
        mBgPaint.setAlpha((int) (2.55f * mDanmuBgAlpha));
    }
    public void setConfig(DanmuConfig config) {
        mDanmuBgColor = config.getPrimaryColor();
        mDanmuTextColor = config.getTextColor();
        mRepeatCount = config.getRepeatCount();
        mMoveSpeed = config.getMoveSpeed();
        mDanmuBgAlpha = config.getBgAlpha();
        mDanmuContentView.setTextColor(mDanmuTextColor);
        mDanmuTitleView.setTextColor(mDanmuTextColor);
        mBgPaint.setColor(mDanmuBgColor);
        mBgPaint.setAlpha((int) (2.55f * mDanmuBgAlpha));
        mDanmuDuration = getAnimationDuration(mMoveSpeed);
        mValueAnimator.setRepeatCount(mRepeatCount);
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
        if(isUseBigDanmu()){
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_48_dp);
        } else {
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_36_dp);
        }
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
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
                }
                setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mStateListener != null) {
                    if(isAttachedToWindow()){
                        mWindowManager.removeView(DanmakuNotificationView.this);
                    }
                    mStateListener.onStartShowDanmu(DanmakuNotificationView.this);
                }
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        };
        mValueAnimator = ValueAnimator.ofFloat(1, 0);
        mValueAnimator.addUpdateListener(mValueUpdateListener);
        mValueAnimator.addListener(mAnimatorListener);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mRailImageWidth = mHeight;
        mHeadImageWidth = getResources().getDimensionPixelSize(R.dimen.dimen_96_dp);
        mSkinMode = getDanmuSkinStyle();
        Log.e(TAG,"init mSkinMode =:" + mSkinMode);
        switch (mSkinMode){
            case SKIN_STYLE_FILL_BODY:
            case SKIN_STYLE_NONE:
            case SKIN_STYLE_RAIL:
                if(isUseBigDanmu()) {
                    mDanmuView = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_detial_notification_big, this);
                } else {
                    mDanmuView = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_detial_notification, this);
                }
                break;
            case SKIN_STYLE_HEAD:
                mDanmuView = LayoutInflater.from(mContext).inflate(R.layout.layout_danmu_head_notification, this);
                break;
        }
        mDanmuTitleView = (TextView) mDanmuView.findViewById(R.id.tv_danmu_title);
        mDanmuContentView = (TextView) mDanmuView.findViewById(R.id.tv_danmu_content);
        mDanmuIconView = (ImageView) mDanmuView.findViewById(R.id.iv_danmu_icon);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgEdgePaint.setStyle(Paint.Style.STROKE);
        mBgEdgePaint.setStrokeWidth(2);
    }

    private void updateWindow(){
        mScreenWidth = ViewUtil.getScreenWidth(mContext);
        mScreenHeight = ViewUtil.getScreenHeight(mContext);
    }

    public void show(String title,String content, Drawable icon) {
        Paint paint = new Paint();
        int titleWidth = 0;
        int contentWidth = 0;
        if(TextUtils.isEmpty(title)){
            mDanmuTitleView.setVisibility(GONE);
        } else {
            mDanmuTitleView.setText(title);
            paint.setTextSize(mDanmuTitleView.getTextSize());
            titleWidth = (int) paint.measureText(title);
        }
        if(TextUtils.isEmpty(content)){
            mDanmuContentView.setVisibility(GONE);
        } else {
            mDanmuContentView.setText(content);
            paint.setTextSize(mDanmuContentView.getTextSize());
            contentWidth = (int) paint.measureText(content);
        }
        if(isUseBigDanmu()){
            mHeight = getContext().getResources().getDimensionPixelSize(R.dimen.dimen_48_dp);
        } else {
            mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_36_dp);
        }
        float adjust = 1.5f;
        if(isUseBigDanmu()){
            adjust = 1.8f;
        }
        mWidth = (int) (mHeight + titleWidth + contentWidth + adjust * mPaddingLeft);
        Log.e(TAG,"show mSkinMode =:" + mSkinMode);
        switch (mSkinMode){
            case SKIN_STYLE_FILL_BODY:
            case SKIN_STYLE_NONE:
                mDanmuRailView = (ImageView) mDanmuView.findViewById(R.id.iv_skin_rail);
                mDanmuRailView.setVisibility(GONE);
                break;
            case SKIN_STYLE_RAIL:
                mWidth = mWidth + mRailImageWidth;
                mDanmuRailView = (ImageView) mDanmuView.findViewById(R.id.iv_skin_rail);
                mDanmuRailView.setVisibility(VISIBLE);
                Drawable drawable = getDanmuSkinRail();
                if(drawable != null){
                    Bitmap fixedBitmap = ViewUtil.drawable2Bitmap(drawable);
                    mDanmuRailView.setImageBitmap(BitmapUtil.fillet(fixedBitmap, fixedBitmap.getHeight(),BitmapUtil.CORNER_RIGHT));
                }
                break;
            case SKIN_STYLE_HEAD:
                mWidth = mWidth + mHeadImageWidth;
                mDanmuHeadView = (ImageView) mDanmuView.findViewById(R.id.iv_skin_head);
                break;
        }
        mDanmuIconView.setImageDrawable(icon);
        mDistance = (int) (mWidth * 1.25f);
        Random random = new Random();
        if(mScreenWidth > mScreenHeight){
            mShowPositionY = (int) (mScreenHeight * 0.2f);
        } else {
            mShowPositionY = random.nextInt((int) (mScreenHeight * 0.5f));
        }
        mWindowParams.width = mWidth;
        mWindowParams.height = mHeight;
        mWindowParams.y = mShowPositionY;
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        setLayoutParams(mWindowParams);
        requestLayout();
        if (!isAttachedToWindow()) {
            mWindowManager.addView(this, mWindowParams);
        }
        if (mValueAnimator != null && !mValueAnimator.isRunning()) {
            float adjustDurationRate = (float) mDistance / mScreenWidth;
            if(adjustDurationRate < 1){
                adjustDurationRate = 1;
            }
            if(adjustDurationRate > 3){
                adjustDurationRate = 3;
            }
            Log.e(TAG,"adjustDurationRate =:" + adjustDurationRate);
            mDanmuDuration = (int) (adjustDurationRate * mDanmuDuration);
            mValueAnimator.setDuration(mDanmuDuration);
            mValueAnimator.start();
        }
        mRoadLength = mScreenWidth + mDistance;
        mBgEdgePaint.setColor(getDanmuSkinEdgeColor());
    }
    private boolean isUseRandomStyle(){
        return SettingsDataKeeper.getSettingsBoolean(mContext,SettingsDataKeeper.DANMU_USE_RANDOM_STYlE_ENABLE);
    }
    private Drawable getDanmuSkinRail(){
        if(isUseRandomStyle()){
            Random random = new Random();
            int skin = ResUtil.SKIN_RAIL_RES_ARR[random.nextInt(12)];
            return mContext.getDrawable(skin);
        }
        return getContext().getDrawable(SettingsDataKeeper.getSettingsInt(getContext(),SettingsDataKeeper.DANMU_SKIN_RAIL_RES_ID));
    }
    private int getDanmuSkinEdgeColor(){
        if(isUseRandomStyle()){
            Random random = new Random();
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);
            return Color.rgb(r,g,b);
        }
        return SettingsDataKeeper.getSettingsInt(getContext(),SettingsDataKeeper.DANMU_SKIN_EDGE_COLOR);
    }
    private int getDanmuSkinStyle(){
        return SettingsDataKeeper.getSettingsInt(getContext(),SettingsDataKeeper.DANMU_SKIN_STYLE);
    }
    private boolean isUseBigDanmu(){
        return SettingsDataKeeper.getSettingsBoolean(getContext(),SettingsDataKeeper.DANMU_USE_BIG_STYLE);
    }
    private void flush() {
        setTranslationX(- mDistance + mRoadLength * mProgress);
    }

    public void destroy(){
        setVisibility(GONE);
        if(isAttachedToWindow()){
            mWindowManager.removeView(this);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        updateWindow();
        setMeasuredDimension(mWidth < mScreenWidth ? mScreenWidth : mWidth,mHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mSkinMode != SKIN_STYLE_HEAD) {
            canvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), mHeight, mHeight, mBgPaint);
        }
        super.dispatchDraw(canvas);

        if(mSkinMode == SKIN_STYLE_RAIL) {
            canvas.drawRoundRect(new RectF(2, 2, mWidth - 2, mHeight - 2), mHeight, mHeight, mBgEdgePaint);
        }
    }

    public void setAction(final PendingIntent intent) {
        Log.e(TAG,"setAction intent =:" + intent);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"我被点击了",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getDanmakuText(){
        return mDanmuContentView.getText().toString();
    }
    public interface AnimationStateListener {
        void onStartShowDanmu(DanmakuNotificationView view);

        void onStopShowDanmu(DanmakuNotificationView view);
    }

    private int getAnimationDuration(int speed) {
        switch (speed) {
            case DANMU_MOVE_SPEED_SUPER_LOW:
                return 14000;
            case DANMU_MOVE_SPEED_LOW:
                return 12000;
            case DANMU_MOVE_SPEED_MIDDLE:
                return 10000;
            case DANMU_MOVE_SPEED_HIGH:
                return 8000;
            case DANMU_MOVE_SPEED_SUPER_HIGH:
                return 6000;
            default:
                return 10000;
        }
    }
}
