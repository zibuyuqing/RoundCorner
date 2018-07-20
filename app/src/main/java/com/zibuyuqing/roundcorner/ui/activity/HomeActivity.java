package com.zibuyuqing.roundcorner.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.RemoteService;
import com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment;
import com.zibuyuqing.roundcorner.ui.fragment.MeInfoFragment;
import com.zibuyuqing.roundcorner.ui.fragment.ScreenCornerFragment;
import com.zibuyuqing.roundcorner.ui.widget.DynamicBgView;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;

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
        View view = window.getDecorView();
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        Utilities.checkPermissions(this);
        initViews();
    }

    private void initViews() {
        if(Utilities.isBeforeAndroidM()){
            mThemeColor = getResources().getColor(R.color.colorPrimary);
        } else {
            mThemeColor = getResources().getColor(R.color.colorPrimary,null);
        }
        mIvAction.setImageResource(R.drawable.ic_share);
        mScreenCornerFragment = ScreenCornerFragment.newInstance();
        mNotificationFragment = EnhanceNotificationFragment.newInstance();
        mMeInfoFragment = MeInfoFragment.newInstance();
        mCurrentFragment = mScreenCornerFragment;
        mCurrentTab = mTvScreenCorner;
        updateTab(mTvScreenCorner);
        if(!mCurrentFragment.isAdded()) {
            getFragmentManager().beginTransaction().add(R.id.content, mCurrentFragment).commit();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
      //  mDynamicBgView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  mDynamicBgView.stop();
    }

    @Override
    protected void onDestroy() {
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
        if(SettingsDataKeeper.getSettingsBoolean(this,SettingsDataKeeper.HIDE_RECENT)) {
            Intent intent = new Intent(HomeActivity.this, NoDisplayActivity.class);
            intent.addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
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
    @OnClick(R.id.iv_action) void share(){
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "我发现了一个好玩的应用，点击下载：https://www.coolapk.com/apk/180019");
        startActivity(Intent.createChooser(textIntent, "分享"));
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
    private void hideFragment(BaseFragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(fragment.isAdded()){
            transaction.hide(fragment);
        }
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
        starter.addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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
