package com.zibuyuqing.roundcorner.ui.activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.MyApp;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.service.RemoteService;
import com.zibuyuqing.roundcorner.ui.fragment.EnhanceNotificationFragment;
import com.zibuyuqing.roundcorner.ui.fragment.ScreenCornerFragment;
import com.zibuyuqing.roundcorner.utils.MobileInfoUtils;
import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static final String ME_PACKAGE_NAME = "com.zibuyuqing.roundcorner";
    private static final int INDEX_FRAGMENT_CORNER = 0;
    private static final int INDEX_FRAGMENT_NOTIFICATION = 1;
    private FragmentManager mFragmentManager;
    private ScreenCornerFragment mCornerFragment;
    private EnhanceNotificationFragment mNotificationFragment;
    private int mCurrentFragment = -1;
    @BindView(R.id.dl_main)
    DrawerLayout mDlMain;
    @BindView(R.id.nav_menu)
    NavigationView mNavMenu;

    @BindView(R.id.title)
    TextView mTVTitle;
    @BindView(R.id.iv_action)
    ImageView mIvAction;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_main;
    }
    @OnClick(R.id.iv_action) void share(){
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "https://www.coolapk.com/apk/180019");
        startActivity(Intent.createChooser(textIntent, "分享"));
    }

    @OnClick(R.id.iv_menu) void showMenu(){
        if(mDlMain.isDrawerOpen(mNavMenu)){
            mDlMain.closeDrawer(mNavMenu);
        } else {
            mDlMain.openDrawer(mNavMenu);
        }
    }
    @Override
    protected void init() {
        initData();
        initViews();
        MyApp app = MyApp.getInstance(this);
        startServices();
    }
    private void initData(){
        mFragmentManager  = getFragmentManager();
        showFragment(INDEX_FRAGMENT_CORNER);
    }
    private void showFragment(int index){
        Log.e(TAG,"showFragment :: mCurrentFragment =:" + mCurrentFragment + ",index =：" + index);
        if(mCurrentFragment != index){
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            hideFragments(transaction);
            if(index == INDEX_FRAGMENT_CORNER){
                if(mCornerFragment == null){
                    mCornerFragment = new ScreenCornerFragment();
                    transaction.add(R.id.content,mCornerFragment);
                } else {
                    transaction.show(mCornerFragment);
                }
                mTVTitle.setText(getString(R.string.app_name));
            } else if(index == INDEX_FRAGMENT_NOTIFICATION){
                if(mNotificationFragment == null){
                    mNotificationFragment = new EnhanceNotificationFragment();
                    transaction.add(R.id.content,mNotificationFragment);
                } else {
                    transaction.show(mNotificationFragment);
                }
                mTVTitle.setText(getString(R.string.fragment_enhance_notification_title));
            }
            mCurrentFragment = index;
            transaction.commit();
        }

    }

    private void hideFragments(FragmentTransaction transaction){
        if(mCornerFragment != null){
            transaction.hide(mCornerFragment);
        }
        if(mNotificationFragment != null){
            transaction.hide(mNotificationFragment);
        }
    }
    private void startServices(){
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
    }


    private void favorite(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse("https://www.coolapk.com/apk/180019");
        intent.setData(content_url);
        startActivity(intent);
    }
    private void aboutMe(){
        Log.e(TAG,"aboutMe");
        AboutMeActivity.toAboutMe(this);
    }
    private void requestAutoStartPermission(){
        MobileInfoUtils.jumpStartInterface(this);
    }
    private void initViews() {
        mTVTitle.setText(getString(R.string.app_name));
        mIvAction.setImageResource(R.drawable.ic_share);
        mNavMenu.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onDestroy() {
        RemoteService.start(this);
        LocalControllerService.tryToAddCorners(this);
        super.onDestroy();
        Log.e(TAG, "MainActivity killed--------");
    }

    @Override
    protected void onStop() {
        Log.e(TAG,"onStop  ********************");
        super.onStop();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_screen_corner:
                showFragment(INDEX_FRAGMENT_CORNER);
                break;
            case R.id.menu_notification:
                showFragment(INDEX_FRAGMENT_NOTIFICATION);
                break;
            case R.id.menu_auto_start:
                requestAutoStartPermission();
                break;
            case R.id.menu_favorite:
                favorite();
                break;
            case R.id.menu_about_me:
                aboutMe();
                break;
        }
        mDlMain.closeDrawer(mNavMenu);
        return true;
    }
}
