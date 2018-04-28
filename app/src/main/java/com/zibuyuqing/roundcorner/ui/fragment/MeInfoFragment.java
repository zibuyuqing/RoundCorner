package com.zibuyuqing.roundcorner.ui.fragment;
import android.content.Intent;
import android.net.Uri;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseFragment;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/04/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MeInfoFragment extends BaseFragment {
    private static final String TAG = MeInfoFragment.class.getSimpleName();

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int providedLayoutId() {
        return R.layout.fragment_me_info;
    }

    @Override
    public String getIdentifyTag() {
        return null;
    }
    private void favorite(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse("https://www.coolapk.com/apk/180019");
        intent.setData(content_url);
        startActivity(intent);
    }
}
