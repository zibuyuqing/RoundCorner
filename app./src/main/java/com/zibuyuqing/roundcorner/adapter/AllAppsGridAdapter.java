package com.zibuyuqing.roundcorner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.AppInfoWithIcon;
import com.zibuyuqing.roundcorner.model.db.AppInfoDaoOpe;

/**
 * Created by xijun.wang on 2017/6/22.
 */

public class AllAppsGridAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<AppInfoWithIcon> mAllApps = new ArrayList<>();
    private SparseBooleanArray mEnablePositions = new SparseBooleanArray();
    private LayoutInflater mInflate;
    private static final String TAG = "AllAppsGridAdapter";
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
        mAllApps.clear();
        mEnablePositions.clear();
        while (iterator.hasNext()) {
            info = (AppInfoWithIcon) iterator.next();
            mAllApps.add(info);
        }
        Collections.sort(mAllApps,APPS_COMPARATOR);
        int N = mAllApps.size();
        AppInfoWithIcon infoWithIcon;
        for(int i = 0;i < N ; i++){
            infoWithIcon = mAllApps.get(i);
            mEnablePositions.put(i,infoWithIcon.enableState == 1);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.layout_app_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final AppInfoWithIcon info = mAllApps.get(position);
        viewHolder.verifySelectState(isItemChecked(position));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.enable) {
                    viewHolder.select(false);
                } else {
                    viewHolder.select(true);
                }
                onItemChanged(info,position,viewHolder.enable);
            }
        });
        viewHolder.appEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.enable) {
                    viewHolder.select(false);
                } else {
                    viewHolder.select(true);
                }
                onItemChanged(info,position,viewHolder.enable);
            }
        });
        viewHolder.appIcon.setImageBitmap(info.getIcon());
        viewHolder.appName.setText(info.title);
    }

    private void onItemChanged(AppInfoWithIcon appInfo,int position, boolean enable) {
        appInfo.enableState = enable ? 1:0;
        AppInfoDaoOpe.updateAppInfo(mContext,appInfo);
        mEnablePositions.put(position, enable);
    }

    private boolean isItemChecked(int position) {
        return mEnablePositions.get(position);
    }

    @Override
    public int getItemCount() {
        return mAllApps.size();
    }

    public void clear() {
        mAllApps.clear();
        mAllApps = null;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private Switch appEnable;
        private TextView appName;
        private boolean enable = false;

        public ItemViewHolder(View itemView) {
            super(itemView);
            appIcon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            appName = (TextView) itemView.findViewById(R.id.tv_app_name);
            appEnable = (Switch) itemView.findViewById(R.id.sw_enable);
        }

        public void verifySelectState(boolean enable) {
            select(enable);
        }

        public void select(boolean enable) {
            this.enable = enable;
            appEnable.setChecked(enable);
        }
    }
}
