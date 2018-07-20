package com.zibuyuqing.roundcorner.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Switch;
import android.widget.Toast;

import com.zibuyuqing.roundcorner.R;
import com.zibuyuqing.roundcorner.base.BaseFragment;
import com.zibuyuqing.roundcorner.utils.MobileInfoUtils;
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper;
import com.zibuyuqing.roundcorner.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

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
    public static final String ALIPAY_PERSON_2_PAY = "HTTPS://QR.ALIPAY.COM/FFKX06233NKLSODKFRKFD04";//个人(支付宝里面我的二维码,然后提示让用的收款码)
    private static final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/";
    private static final String TAG = MeInfoFragment.class.getSimpleName();
    private String[] mPaySelectArr = new String[2];
    @BindView(R.id.sw_hide_recent_enable)
    Switch mSwHideRecent;
    private boolean mHideRecentEnable;

    public static MeInfoFragment newInstance() {

        Bundle args = new Bundle();

        MeInfoFragment fragment = new MeInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void initData() {
        mPaySelectArr = mActivity.getResources().getStringArray(R.array.donate_select);
        mHideRecentEnable = SettingsDataKeeper.getSettingsBoolean(mActivity,SettingsDataKeeper.HIDE_RECENT);
    }

    @Override
    protected void initViews() {
        mSwHideRecent.setChecked(mHideRecentEnable);
    }

    @Override
    protected int providedLayoutId() {
        return R.layout.fragment_me_info;
    }

    @OnClick(R.id.tv_auto_restart)
    void toRestart() {
        MobileInfoUtils.jumpStartInterface(mActivity);
    }

    @Override
    public String getIdentifyTag() {
        return "MeInfoFragment";
    }

    @OnClick(R.id.tv_favorite)
    void favorite() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse("http://mobile.baidu.com/item?docid=23989982&source=s1001");
        intent.setData(content_url);
        startActivity(intent);
    }

    @OnClick(R.id.tv_code)
    void toGithub() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse("https://github.com/zibuyuqing/RoundCorner");
        intent.setData(content_url);
        startActivity(intent);
    }

    @OnClick(R.id.tv_join_group)
    void joinQQGroup() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "XA6ZzD7l0btOuYjXpzv2j_L96JyExThQ"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            showTips("未安装手Q或安装的版本不支持");
        }
    }

    @OnCheckedChanged(R.id.sw_hide_recent_enable)
    void onHideRecentEnableChanged(){
        if(mSwHideRecent.isChecked() && !mHideRecentEnable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("小提示")
                    .setMessage("开启开关，点击back键（不是home键）后应用将不会在任务列表里显示，请确保将软件加入白名单之后再隐藏，以防被系统杀死造成功能异常o(*￣︶￣*)o")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSwHideRecent.setChecked(false);
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SettingsDataKeeper.writeSettingsBoolean(mActivity, SettingsDataKeeper.HIDE_RECENT, mSwHideRecent.isChecked());
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mSwHideRecent.setChecked(false);
                }
            }).create().show();
        } else {
            SettingsDataKeeper.writeSettingsBoolean(mActivity, SettingsDataKeeper.HIDE_RECENT, mSwHideRecent.isChecked());
        }
    }
    /*
    private boolean payByAlipay(Context context, String qrcode) {
        try {
            qrcode = URLEncoder.encode(qrcode, "utf-8");
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            openUri(context, alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            showTips("未安装支付宝或出现未知错误");
            e.printStackTrace();
        }
        return false;
    }


    private void payByWeiChat() {
        try {
            boolean hasStoragePermission = Utilities.checkPermissions(mActivity);
            if (hasStoragePermission) {
                Bitmap wxbm = BitmapFactory.decodeResource(getResources(), R.drawable.weichatpay);
                File file = new File(FILE_DIR + "weichatpay.jpg");
                FileOutputStream out = new FileOutputStream(file);
                wxbm.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
                Uri uri = Uri.fromFile(file);
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                Toast.makeText(mActivity, "微信二维码已保存至本地，请打开微信扫一扫进行捐赠", Toast.LENGTH_LONG).show();
                openWeixinToQECode(mActivity);
            } else {
                Toast.makeText(mActivity, "请允许存储权限", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openWeixinToQECode(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "未知错误", Toast.LENGTH_LONG).show();
        }
    }
    private  void openUri(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        context.startActivity(intent);
    }
    @OnClick(R.id.tv_donate)
    void donate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(mPaySelectArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    payByWeiChat();
                } else {
                    payByAlipay(mActivity,ALIPAY_PERSON_2_PAY);
                }
            }
        });
        builder.create().show();
    }
    */
    @OnClick(R.id.tv_about)
    void about(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setTitle("说明");
        dialog.setMessage("为了能保证软件能正常运行，请加入白名单，并且允许软件自启动，耗电问题大可放心，我测试过，只要你不是一直让动画显示，基本可以忽略，本软件初衷是为了好玩，希望大家一起探讨O(∩_∩)O");
        dialog.setCancelable(false);
        dialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

}
