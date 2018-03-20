package com.zibuyuqing.roundcorner.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.service.LocalControllerService;

import butterknife.ButterKnife;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class BaseFragment extends Fragment{
    protected Activity mActivity;
    protected View mContains;
    protected boolean isResumed = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContains = inflater.inflate(providedLayoutId(),container,false);
        ButterKnife.bind(this,mContains);
        return mContains;
    }
    public void showTips(String msg){
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }
    public void showTips(int resId){
        Toast.makeText(mActivity, getString(resId), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initData();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }

    public void updateSettingsWithInteger(String key){
        if(isResumed) {
            Intent intent = new Intent(mActivity, LocalControllerService.class);
            intent.setAction(key);
            mActivity.startService(intent);
        }
    }
    public void updateSettingsWithBool(String key){
        if(isResumed) {
            Intent intent = new Intent(mActivity, LocalControllerService.class);
            intent.setAction(key);
            mActivity.startService(intent);
        }
    }

    protected abstract void initData();
    protected abstract void initViews();
    protected abstract int providedLayoutId();
    public abstract String getIdentifyTag();
}
