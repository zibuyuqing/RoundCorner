package com.zibuyuqing.roundcorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Xijun.Wang on 2017/11/6.
 */

public class AboutMeActivity extends BaseActivity {
    private static final int[] INFO_IMGS = {
            R.drawable.weixin,
            R.drawable.qq,
            R.drawable.weibo
    };
    private static final String MY_GITHUB_URL = "https://github.com/zibuyuqing/RoundCorner";
    private List<View> imageList = new ArrayList<>();
    @BindView(R.id.vp_me_info)
    ViewPager vpMeInfo;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.iv_action)
    ImageView ivAction;
    @Override
    protected int providedLayoutId() {
        return R.layout.activity_about_me;
    }
    public static void toAboutMe(Context context){
        context.startActivity(new Intent(context,AboutMeActivity.class));
    }
    @Override
    protected void init() {
        title.setText(getString(R.string.about_me));
        ivAction.setImageResource(R.drawable.github);
        for(int resId : INFO_IMGS){
            View imagePage = View.inflate(this,R.layout.layout_me_info_item,null);
            ((ImageView) imagePage.findViewById(R.id.iv_me_info)).setImageResource(resId);
            imageList.add(imagePage);
        }
        vpMeInfo.setAdapter(new MyViewPagerAdapter(imageList));
    }
    @OnClick(R.id.iv_action)
    void toMyGithub(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(MY_GITHUB_URL));
        startActivity(intent);
    }
    private class MyViewPagerAdapter extends PagerAdapter {
        List<View> images;
        public MyViewPagerAdapter(List<View> images){
            this.images = images;
        }
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position));
            return images.get(position);
        }
    }
}
