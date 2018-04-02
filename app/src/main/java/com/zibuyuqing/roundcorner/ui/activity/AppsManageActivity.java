package com.zibuyuqing.roundcorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.base.BaseAppListFragment;
import com.zibuyuqing.roundcorner.ui.fragment.SystemAppListFragment;
import com.zibuyuqing.roundcorner.ui.fragment.UserAppListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AppsManageActivity extends BaseActivity {
    @BindView(R.id.vp_app_fragment_container)
    ViewPager mVpAppFragmentContainer;
    @BindView(R.id.toolbar)
    View mToolbar;
    BaseAppListFragment mUserAppListFragment;
    BaseAppListFragment mSystemAppListFragment;
    List<BaseAppListFragment> mAppListFragments;
    FragmentManager mFragmentManager;
    PageAdapter mAdapter;
    @OnClick(R.id.iv_back) void back(){
        finish();
    }
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_apps_manager;
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, AppsManageActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void init() {
        mToolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        ((TextView)mToolbar.findViewById(R.id.tv_title)).setText(getString(R.string.application_manager));
        mUserAppListFragment = new UserAppListFragment();
        mSystemAppListFragment = new SystemAppListFragment();
        mAppListFragments = new ArrayList<>(2);
        mAppListFragments.add(mUserAppListFragment);
        mAppListFragments.add(mSystemAppListFragment);
        mFragmentManager = getSupportFragmentManager();
        mAdapter = new PageAdapter(mFragmentManager);
        mVpAppFragmentContainer.setAdapter(mAdapter);
        showTips(R.string.long_click_tip);
    }
    private class PageAdapter extends FragmentStatePagerAdapter{
        public PageAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mAppListFragments.get(position);
        }

        @Override
        public int getCount() {
            return mAppListFragments.size();
        }
    }
}
