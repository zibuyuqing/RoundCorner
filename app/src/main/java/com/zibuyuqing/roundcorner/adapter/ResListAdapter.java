package com.zibuyuqing.roundcorner.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zibuyuqing.roundcorner.R;

import java.util.ArrayList;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/06/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ResListAdapter extends RecyclerView.Adapter{
    public static final int RES_TYPE_IMAGE = 0;
    public static final int RES_TYPE_COLOR = 1;
    private ArrayList<Integer> mResList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mListener;
    public ResListAdapter(int[] resArr,Context context){
        int length = resArr.length;
        for(int i = 0; i < length; i ++ ){
            mResList.add(resArr[i]);
        }
        mContext = context;
    }
    public ResListAdapter(ArrayList<Integer> resList, Context context) {
        mResList.addAll(resList);
        mContext = context;
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.layout_skin_preview_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        populateView(itemHolder,position);
    }

    private void populateView(final ViewHolder itemHolder, final int position) {
        itemHolder.ivPreview.setImageResource(mResList.get(position));
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClicked(itemHolder.itemView,mResList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResList.size();
    }
    private class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivPreview;
        public ViewHolder(View itemView) {
            super(itemView);
            ivPreview = (ImageView) itemView.findViewById(R.id.iv_preview);
        }
    }
    public abstract interface OnItemClickListener{
        void onItemClicked(View view,Integer res);
    }
}
