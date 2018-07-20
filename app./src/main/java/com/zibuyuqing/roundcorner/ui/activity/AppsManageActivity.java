package com.zibuyuqing.roundcorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.AllAppsGridAdapter;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.model.db.AppInfoLoadTask;
import com.zibuyuqing.roundcorner.ui.widget.XRecyclerView;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AppsManageActivity extends BaseActivity implements XRecyclerView.LoadStateListener{
    private static final String TAG = "AppsManageActivity";
    private AllAppsGridAdapter mAdapter;
    @BindView(R.id.pb_load_progress)
    ProgressBar mPbLoadProgress;
    @BindView(R.id.rv_app_list)
    XRecyclerView mRvAppList;
    private boolean isFirstLoad = true;
    private boolean isRefresh = false;
    @BindView(R.id.toolbar)
    View mToolbar;
    private int mCurrentPage = 0;
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
        if(Utilities.isBeforeAndroidM()){
            mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mToolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        }
        ((TextView)mToolbar.findViewById(R.id.tv_title)).setText(getString(R.string.application_manager));
        mAdapter = new AllAppsGridAdapter(this);
        mRvAppList.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvAppList.setLayoutManager(layoutManager);
        mRvAppList.setLoadStateListener(this);
        mRvAppList.loadData();
    }

    @Override
    protected void onDestroy() {
        mAdapter.clear();
        super.onDestroy();
    }

    @Override
    public void loadMore() {
        AppInfoLoadTask.execute(this,AppInfoLoadTask.QUERAY_ALL, new AppInfoLoadTask.AppInfoLoadStateListener() {
            @Override
            public void startLoad(int totalCount) {
                Log.e(TAG,"startLoad totalCount =:" + totalCount);
                if(isFirstLoad) {
                    mPbLoadProgress.setVisibility(View.VISIBLE);
                    mPbLoadProgress.setMax(totalCount);
                }
                mRvAppList.setIsLoadingData(true);
            }

            @Override
            public void onLoad(int process) {
                if(isFirstLoad) {
                    mPbLoadProgress.setProgress(process);
                }
            }

            @Override
            public void endLoad(List<AppInfoWithIcon> appInfoWithIconList) {
                if(isFirstLoad){
                    mRvAppList.setVisibility(View.VISIBLE);
                    mPbLoadProgress.setVisibility(View.GONE);
                    isFirstLoad = false;
                }
                mAdapter.updateData(appInfoWithIconList);
                mRvAppList.setIsLoadingData(false);
                mCurrentPage ++;
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG,"onError msg =:" + msg);
            }
        });
    }

}
