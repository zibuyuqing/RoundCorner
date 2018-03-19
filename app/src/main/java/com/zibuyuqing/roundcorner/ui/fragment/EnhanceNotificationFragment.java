package com.zibuyuqing.roundcorner.ui.fragment;

import android.content.Intent;
import android.widget.Switch;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * <pre>
 *     author : Xijun.Wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/03/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EnhanceNotificationFragment extends BaseFragment{
    private static final String TAG = EnhanceNotificationFragment.class.getSimpleName();
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private boolean isEnhanceNotificationEnable;
    @BindView(R.id.sw_enhance_notification_enable)
    Switch mSwEnhanceNotificationEnable;
    @Override
    protected void initData() {
        isEnhanceNotificationEnable = SettingsDataKeeper.
                getSettingsBoolean(mActivity,SettingsDataKeeper.CORNER_RIGHT_BOTTOM_ENABLE);
    }

    @Override
    protected void initViews() {
        mSwEnhanceNotificationEnable.setChecked(isEnhanceNotificationEnable);
    }
    @OnCheckedChanged(R.id.sw_enhance_notification_enable)
    void onEnhanceNotificationChanged(){
        confirmEnhanceNotificationEnable(mSwEnhanceNotificationEnable.isChecked());
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean hasNotificationListenPermission = checkNotificationListenPermission();
        isEnhanceNotificationEnable = hasNotificationListenPermission;
        mSwEnhanceNotificationEnable.setChecked(isEnhanceNotificationEnable);
    }
    public boolean checkNotificationListenPermission() {
        return Utilities.checkNotificationListenPermission(mActivity);
    }
    private void confirmEnhanceNotificationEnable(boolean checked) {
        if(checked){
            if(checkNotificationListenPermission()){
                SettingsDataKeeper.writeSettingsBoolean(mActivity,
                        SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE,checked);
            } else {
                requestNotificationListenPermission();
                showTips(R.string.notification_listen_permission_required);
            }
        }
        updateSettingsWithBool(SettingsDataKeeper.ENHANCE_NOTIFICATION_ENABLE);
    }

    private void requestNotificationListenPermission(){
        try {
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.rl_enhance_notification_layout)
    void enhanceNotification(){
        if(isEnhanceNotificationEnable){
            isEnhanceNotificationEnable = false;
            mSwEnhanceNotificationEnable.setChecked(false);
        } else {
            isEnhanceNotificationEnable = true;
            mSwEnhanceNotificationEnable.setChecked(true);
        }

    }
    @Override
    protected int providedLayoutId() {
        return R.layout.fragment_enhance_notification_settings;
    }

    @Override
    public String getIdentifyTag() {
        return TAG;
    }
}
