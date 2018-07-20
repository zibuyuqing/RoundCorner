package com.zibuyuqing.roundcorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.adapter.ResListAdapter;
import com.zibuyuqing.roundcorner.base.BaseActivity;
import com.zibuyuqing.roundcorner.utils.BitmapUtil;
import com.zibuyuqing.roundcorner.utils.ResUtil;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.ViewUtil;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : xijun.wang
 *     e-mail : zibuyuqing@gmail.com
 *     time   : 2018/06/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LabActivity extends BaseActivity implements ResListAdapter.OnItemClickListener {

    private static final String TAG = LabActivity.class.getSimpleName();
    @BindView(R.id.iv_skin_rail)
    ImageView mIvDanmuRail;

    @BindView(R.id.rv_skin_list)
    RecyclerView mRvSkinList;

    @BindView(R.id.danmu_layout)
    LinearLayout mDanmuLayout;

    @OnClick(R.id.iv_back) void back(){
        finish();
    }

    @Override
    protected int providedLayoutId() {
        return R.layout.activity_lab;
    }

    @Override
    protected void init() {
        int resId = SettingsDataKeeper.getSettingsInt(this,SettingsDataKeeper.DANMU_SKIN_RAIL_RES_ID);
        selectSkin(resId);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        mRvSkinList.setLayoutManager(manager);
        ResListAdapter adapter = new ResListAdapter(ResUtil.SKIN_RAIL_RES_ARR,this);
        adapter.setOnItemClickListener(this);
        mRvSkinList.setAdapter(adapter);
    }
    private void selectSkin(Integer resId){
        Drawable drawable = getDrawable(resId);
        if(drawable != null){
            Bitmap fixedBitmap = ViewUtil.drawable2Bitmap(drawable);
            Log.e(TAG,"drawable.getIntrinsicHeight() =:" + drawable.getIntrinsicHeight() +",fixedBitmap = :" + fixedBitmap.getHeight());
            Drawable bg = mDanmuLayout.getBackground();
            int bgColor = ViewUtil.colorFromBitmap(fixedBitmap);
            bg.setTint(bgColor);
            mIvDanmuRail.setImageBitmap(BitmapUtil.fillet(fixedBitmap, drawable.getIntrinsicHeight(),BitmapUtil.CORNER_RIGHT));
            SettingsDataKeeper.writeSettingsInt(this,SettingsDataKeeper.DANMU_SKIN_EDGE_COLOR,bgColor);
            SettingsDataKeeper.writeSettingsInt(this,SettingsDataKeeper.DANMU_SKIN_RAIL_RES_ID,resId);
        }
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, LabActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onItemClicked(View view, Integer res) {
        selectSkin(res);
    }
}
