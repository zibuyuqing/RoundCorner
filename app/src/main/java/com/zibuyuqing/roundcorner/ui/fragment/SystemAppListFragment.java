package com.zibuyuqing.roundcorner.ui.fragment;

import com.zibuyuqing.roundcorner.base.BaseAppListFragment;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SystemAppListFragment extends BaseAppListFragment{
    @Override
    protected int getAppType() {
        return AppInfo.SYSTEM_APP;
    }
}
