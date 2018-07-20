package com.zibuyuqing.roundcorner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.model.bean.NotificationInfo;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/06/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationInfo> mInfoList;
    private Context mContext;
    private OnItemClickListener mListener;
    public NotificationListAdapter (Context context){
        mContext = context;
        mInfoList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public void clear(){
        mInfoList.clear();
        notifyDataSetChanged();
    }
    public void addNotificationInfo(NotificationInfo info){
        if(!mInfoList.contains(info)){
            mInfoList.add(info);
            Collections.sort(mInfoList, Utilities.TIME_COMPARATOR);
            notifyDataSetChanged();
        }
    }
    public void removeNotificationInfo(NotificationInfo info){
        if(mInfoList.contains(info)){
            int index = mInfoList.indexOf(info);
            mInfoList.remove(info);
            notifyItemChanged(index);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.layout_notification_item,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        populateView(itemHolder,position);
    }
    private void populateView(ViewHolder holder, final int position){
        final NotificationInfo info = mInfoList.get(position);
        holder.mIvIcon.setImageDrawable(info.getIcon());
        holder.mTvMessageOwer.setText(info.getMessageOwner());
        holder.mTvMessageContent.setText(info.getMessageContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClicked(position,info);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public void removeNotificationInfoByPackageName(String who) {
        Log.e("hahah","removeNotificationInfoByPackageName who =:" + who);
        List<NotificationInfo> infosCopy = new ArrayList<>(mInfoList);
        for(NotificationInfo info : infosCopy){
            if(info.getPackageName().equals(who)){
                mInfoList.remove(info);
                Log.e("hahah"," dd removeNotificationInfoByPackageName who =:" + who);
            }
        }
        notifyDataSetChanged();
    }

    public List<NotificationInfo> getNotificationInfos() {
        if(mInfoList.size() > 0) {
            return mInfoList;
        } else {
            return null;
        }
    }

    public boolean checkNotification(NotificationInfo info) {
        for(NotificationInfo notification : mInfoList){
            if(info.indentify().equals(notification.indentify())){
                return true;
            }
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTvMessageOwer;
        TextView mTvMessageContent;
        ImageView mIvIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mTvMessageOwer = (TextView) itemView.findViewById(R.id.tv_message_owner);
            mTvMessageContent = (TextView) itemView.findViewById(R.id.tv_message_content);
        }
    }
   public abstract interface OnItemClickListener{
        void onItemClicked(int position,NotificationInfo info);
    }
}
