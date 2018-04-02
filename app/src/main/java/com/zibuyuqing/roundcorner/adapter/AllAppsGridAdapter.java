package com.zibuyuqing.roundcorner.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.AppInfo;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;
import com.zibuyuqing.roundcorner.service.LocalControllerService;
import com.zibuyuqing.roundcorner.ui.activity.AppConfigActivity;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AllAppsGridAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<AppInfoWithIcon> mAllApps = new ArrayList<>();
    private LayoutInflater mInflate;
    private static final String TAG = "AllAppsGridAdapter";
    private ArrayMap<AppInfo, Integer> mChangedInfos = new ArrayMap<>();
    private int mSelectedIndex = 0;

    public static final Comparator<AppInfoWithIcon> APPS_COMPARATOR = new Comparator<AppInfoWithIcon>() {
        @Override
        public int compare(AppInfoWithIcon one, AppInfoWithIcon other) {
            return other.getEnableState() - one.getEnableState();
        }
    };
    public AllAppsGridAdapter(Context context) {
        mContext = context;
        mInflate = LayoutInflater.from(context);
    }

    public void updateData(List<AppInfoWithIcon> infos) {
        Iterator iterator = infos.iterator();
        AppInfoWithIcon info;
        while (iterator.hasNext()) {
            info = (AppInfoWithIcon) iterator.next();
            mAllApps.add(info);
        }
        Collections.sort(mAllApps,APPS_COMPARATOR);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.layout_app_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        viewHolder.appIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        viewHolder.appName = (TextView) view.findViewById(R.id.tv_app_name);
        viewHolder.appSelectedFlag = (ImageView) view.findViewById(R.id.iv_app_selected);
        viewHolder.appSelectedFlag.setVisibility(View.VISIBLE);
        viewHolder.mixedColorOne = (ImageView) view.findViewById(R.id.iv_mixed_color_1);
        viewHolder.mixedColorTwo = (ImageView) view.findViewById(R.id.iv_mixed_color_2);
        viewHolder.mixedColorThree = (ImageView) view.findViewById(R.id.iv_mixed_color_3);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final AppInfoWithIcon info = mAllApps.get(position);

        if (mChangedInfos.containsKey(info)) {
            viewHolder.verifySelectState(mChangedInfos.get(info));
        } else {
            viewHolder.verifySelectState(info.enableState);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.select == 1) {
                    viewHolder.select(0);
                } else {
                    viewHolder.select(1);
                }
                onItemChanged(info, viewHolder.select, info.enableState);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSelectedIndex = position;
                AppConfigActivity.start(mContext,info.getAppInfo());
                return true;
            }
        });
        viewHolder.appIcon.setImageBitmap(info.getIcon());
        viewHolder.appName.setText(info.title);
        Log.e(TAG,"onBindViewHolder = :" + info.toString());
        updateMixedColor(viewHolder.mixedColorOne,info.getMixedColorOne());
        updateMixedColor(viewHolder.mixedColorTwo,info.getMixedColorTwo());
        updateMixedColor(viewHolder.mixedColorThree,info.getMixedColorThree());
    }
    private void updateMixedColor(ImageView imageView,int color){
        Drawable drawable = imageView.getDrawable();
        drawable.setTint(color);
        imageView.setImageDrawable(drawable);
    }
    private void onItemChanged(AppInfoWithIcon appInfo, int enable, int originEnableState) {
        if (enable != originEnableState) {
            mChangedInfos.put(appInfo, enable);
        } else {
            if (!mChangedInfos.isEmpty() && mChangedInfos.containsKey(appInfo)) {
                mChangedInfos.remove(appInfo);
                Log.i(TAG, "onItemChanged hidden state is not changed,should not update ,info =:" + appInfo);
            }
        }
    }

    public void commitChanges() {
        if (mChangedInfos.size() > 0) {
            Set<AppInfo> infos = mChangedInfos.keySet();
//            ArrayList<AppInfo> items2Enable = new ArrayList<>();
//            ArrayList<AppInfo> items2Disable = new ArrayList<>();
            for (AppInfo info : infos) {
                info.enableState = mChangedInfos.get(info);
            }
            AppInfoDaoOpe.updateAppInfos(mContext,infos);
            Intent intent = new Intent(LocalControllerService.ACTION_APP_ENABLE_STATE_CHANGED);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    public void cancel() {
        mChangedInfos.clear();
    }

    @Override
    public int getItemCount() {
        return mAllApps.size();
    }
    public List<AppInfoWithIcon> getAllApps(){
        return mAllApps;
    }
    public void notifyItemChanged(AppInfo info) {
        Log.e(TAG,"mSelectedIndex =:" + mSelectedIndex);
        if(mAllApps.size() <= 0){
            return;
        }
        if(mSelectedIndex < 0 && mSelectedIndex > mAllApps.size()){
            return;
        }
        AppInfoWithIcon infoWithIcon = mAllApps.get(mSelectedIndex);
        infoWithIcon.setMixedColorOne(info.mixedColorOne);
        infoWithIcon.setMixedColorTwo(info.mixedColorTwo);
        infoWithIcon.setMixedColorThree(info.mixedColorThree);
        notifyItemChanged(mSelectedIndex);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon, appSelectedFlag,mixedColorOne,mixedColorTwo,mixedColorThree;
        private TextView appName;
        private int select = 0;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public void verifySelectState(int select) {
            select(select);
        }

        public void select(int select) {
            this.select = select;
            if (select == 1) {
                appSelectedFlag.setVisibility(View.VISIBLE);
                appSelectedFlag.setImageResource(R.drawable.app_state_selected);
            } else {
                appSelectedFlag.setVisibility(View.GONE);
            }
        }
    }
}
