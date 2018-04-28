package com.zibuyuqing.roundcorner.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.model.db.AppLoadTask;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.RemoteService;
import com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment;
import com.zibuyuqing.roundcorner.ui.fragment.MeInfoFragment;
import com.zibuyuqing.roundcorner.ui.fragment.ScreenCornerFragment;
import com.zibuyuqing.roundcorner.ui.widget.DynamicBgView;
import com.zibuyuqing.roundcorner.utils.Utilities;
import com.zibuyuqing.roundcorner.utils.ViewUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HomeActivity extends BaseActivity {
    private BaseFragment mCurrentFragment;
    @BindView(R.id.main_root)
    View mRootView;
    @BindView(R.id.bg_view)
    DynamicBgView mDynamicBgView;
    @BindView(R.id.tv_screen_corner)
    TextView mTvScreenCorner;
    @BindView(R.id.tv_notification)
    TextView mTvNotification;
    @BindView(R.id.tv_me_info)
    TextView mTvMeInfo;
    @BindView(R.id.iv_action)
    ImageView mIvAction;
    private BaseFragment mScreenCornerFragment,mNotificationFragment,mMeInfoFragment;
    private TextView mCurrentTab;
    private int mThemeColor;
    @Override
    protected void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        View view = window.getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        Utilities.checkStoragePermission(this);
        initViews();
    }

    private void initViews() {
        mRootView = (RelativeLayout) findViewById(R.id.main_root);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
        int navigationBarHeight = ViewUtil.getNavigationBarHeight(this);
        layoutParams.bottomMargin = navigationBarHeight;
        mRootView.setLayoutParams(layoutParams);
        mThemeColor = getResources().getColor(R.color.colorPrimary,null);
        mIvAction.setImageResource(R.drawable.ic_share);
        mScreenCornerFragment = new ScreenCornerFragment();
        mNotificationFragment = new EnhanceNotificationFragment();
        mMeInfoFragment = new MeInfoFragment();
        mCurrentFragment = mScreenCornerFragment;
        mCurrentTab = mTvScreenCorner;
        updateTab(mTvScreenCorner);
        getFragmentManager().beginTransaction().add(R.id.content,mCurrentFragment).commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mDynamicBgView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDynamicBgView.stop();
    }

    @Override
    protected void onDestroy() {
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
        super.onDestroy();
    }

    @Override
    protected int providedLayoutId() {
        return R.layout.activity_home;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utilities.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if(grantResults == null || grantResults.length <=0){
                    return;
                }
                for(int i = 0; i<grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        showTips(R.string.storage_permission_denied);
                        return;
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnClick(R.id.tv_screen_corner) void toScreenCorner(){
        mCurrentTab = mTvScreenCorner;
        switchFragment(mScreenCornerFragment);
    }
    @OnClick(R.id.tv_notification) void toNotification(){
        mCurrentTab = mTvNotification;
        switchFragment(mNotificationFragment);
    }
    @OnClick(R.id.tv_me_info) void toMeInfo(){
        mCurrentTab = mTvMeInfo;
        switchFragment(mMeInfoFragment);
    }
    private void switchFragment(BaseFragment targetFragment) {
        updateTab(mCurrentTab);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.hide(mCurrentFragment).add(R.id.content, targetFragment).commit();
        } else {
            transaction.hide(mCurrentFragment).show(targetFragment).commit();
        }
        mCurrentFragment = targetFragment;
    }
    private void updateTab(TextView textView){
        resetTab();
        Drawable drawable = textView.getCompoundDrawables()[1];
        drawable.setTint(mThemeColor);
        textView.setTextColor(mThemeColor);
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        context.startActivity(starter);
    }
    private void resetTab(){
        int colorNormal = getResources().getColor(R.color.color_gray_light);
        Drawable drawable = mTvScreenCorner.getCompoundDrawables()[1];
        drawable.setTint(colorNormal);
        drawable = mTvNotification.getCompoundDrawables()[1];
        drawable.setTint(colorNormal);
        drawable = mTvMeInfo.getCompoundDrawables()[1];
        drawable.setTint(colorNormal);
        mTvScreenCorner.setTextColor(colorNormal);
        mTvNotification.setTextColor(colorNormal);
        mTvMeInfo.setTextColor(colorNormal);
    }
}
