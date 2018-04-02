package com.zibuyuqing.roundcorner.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.AllAppsGridAdapter;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.model.db.AppInfoLoadTask;
import com.zibuyuqing.roundcorner.ui.activity.AppConfigActivity;
import com.zibuyuqing.roundcorner.ui.widget.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zibuyuqing.roundcorner.service.LocalControllerService.ACTION_APP_ENABLE_STATE_CHANGED;

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
    private static final String TAG = "BaseAppListFragment";
    private AllAppsGridAdapter mAdapter;
    @BindView(R.id.pb_load_progress)
    ProgressBar mPbLoadProgress;
    @BindView(R.id.rv_app_list)
    XRecyclerView mRvAppList;
    @BindView(R.id.tv_settled_tip)
    TextView mTvSettledTip;
    private int mCurrentPage = 0;
    private boolean isFirstLoad = true;
    private boolean isRefresh = false;
    private Activity mActivity;
    private BroadcastReceiver mReceiver;
    private AppInfo mSelectedAppInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        init();
    }

    @Override
    public void onAttach(Context context) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_APP_ENABLE_STATE_CHANGED);
        Log.e(TAG,"onAttach -------------");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ACTION_APP_ENABLE_STATE_CHANGED.equals(intent.getAction())){
                    if(intent.getExtras() != null){
                        isRefresh = true;
                        mSelectedAppInfo = intent.getParcelableExtra(AppConfigActivity.EXTRA_KEY);
                        return;
                    }
                }
            }
        };
        manager.registerReceiver(mReceiver,filter);
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isRefresh){
            if(mSelectedAppInfo != null){
                mAdapter.notifyItemChanged(mSelectedAppInfo);
                isRefresh = false;
            }
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG,"onDetach -------------");
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
        mReceiver = null;
        super.onDetach();
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
        mTvSettledTip.setText(getAppType() == AppInfo.USER_APP ? mActivity.getString(R.string.user_app):mActivity.getString(R.string.system_app));
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
