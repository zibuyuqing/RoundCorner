package com.zibuyuqing.roundcorner.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.AllAppsGridAdapter;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.model.db.AppInfoLoadTask;
import com.zibuyuqing.roundcorner.ui.widget.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class BaseAppListFragment extends Fragment implements XRecyclerView.LoadStateListener {
    private static final String TAG = "AppsManageActivity";
    private AllAppsGridAdapter mAdapter;
    @BindView(R.id.pb_load_progress)
    ProgressBar mPbLoadProgress;
    @BindView(R.id.rv_app_list)
    XRecyclerView mRvAppList;
    private int mCurrentPage = 0;
    private boolean isFirstLoad = true;
    private Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        init();
    }
    @OnClick(R.id.fab_cancel) void cancel(){
        mAdapter.cancel();
        mActivity.finish();
    }
    @OnClick(R.id.fab_confirm) void confirm(){
        mAdapter.commitChanges();
        mActivity.finish();
    }
    private void init(){
        mAdapter = new AllAppsGridAdapter(mActivity);
        mRvAppList.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity,3);
        mRvAppList.setLayoutManager(gridLayoutManager);
        mRvAppList.setLoadStateListener(this);
        if(getUserVisibleHint()){
            mRvAppList.loadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG,"startLoad totalCount =:" + isVisibleToUser);
        if(isVisibleToUser){
            if(mRvAppList != null && mAdapter != null) {
                mRvAppList.loadData();
            }
        }
    }

    @Override
    public void loadMore() {
        AppInfoLoadTask.execute(mActivity,getAppType(),mCurrentPage, new AppInfoLoadTask.AppInfoLoadStateListener() {
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
                Log.e(TAG,"onLoad process =:" + process);
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
    protected abstract int getAppType();
}
