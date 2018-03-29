package com.zibuyuqing.roundcorner.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.base.BaseAppListFragment;
import com.zibuyuqing.roundcorner.ui.fragment.SystemAppListFragment;
import com.zibuyuqing.roundcorner.ui.fragment.UserAppListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AppsManageActivity extends BaseActivity {
    @BindView(R.id.vp_app_fragment_container)
    ViewPager mVpAppFragmentContainer;
    BaseAppListFragment mUserAppListFragment;
    BaseAppListFragment mSystemAppListFragment;
    List<BaseAppListFragment> mAppListFragments;
    FragmentManager mFragmentManager;
    PageAdapter mAdapter;
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
        mUserAppListFragment = new UserAppListFragment();
        mSystemAppListFragment = new SystemAppListFragment();
        mAppListFragments = new ArrayList<>(2);
        mAppListFragments.add(mUserAppListFragment);
        mAppListFragments.add(mSystemAppListFragment);
        mFragmentManager = getSupportFragmentManager();
        mAdapter = new PageAdapter(mFragmentManager);
        mVpAppFragmentContainer.setAdapter(mAdapter);
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
