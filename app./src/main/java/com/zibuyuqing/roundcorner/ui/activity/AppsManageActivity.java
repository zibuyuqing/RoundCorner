package com.zibuyuqing.roundcorner.ui.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.AllAppsGridAdapter;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AppsManageActivity extends BaseActivity {
    private static final String TAG = "Launcher.HiddenAppsManageActivity";
    private AllAppsGridAdapter mAdapter;
    @BindView(R.id.rv_app_list)
    RecyclerView mRvAppList;

    @OnClick(R.id.tv_cancel) void cancel(){
        mAdapter.cancel();
        finish();
    }
    @OnClick(R.id.tv_confirm) void confirm(){
        mAdapter.commitChanges();
        finish();
    }
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_apps_manager;
    }

    @Override
    protected void init() {

    }
    private class AppLoadTask
//    private void bindAllApps(){
//        mAdapter = new AllAppsGridAdapter(this,infos);
//        mRvAppList.setAdapter(mAdapter);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
//        mRvAppList.setLayoutManager(gridLayoutManager);
//    }
}
