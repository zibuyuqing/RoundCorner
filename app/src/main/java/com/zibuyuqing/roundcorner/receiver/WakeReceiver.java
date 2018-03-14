package com.zibuyuqing.roundcorner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zibuyuqing.roundcorner.service.KeepCornerLiveService;
import com.zibuyuqing.roundcorner.service.RemoteService;

/**
 * Created by lingy on 2018-03-14.
 */

public class WakeReceiver extends BroadcastReceiver{
    private final static String TAG = WakeReceiver.class.getSimpleName();
    private final static int WAKE_SERVICE_ID = -1111;
    /**
     * 灰色保活手段唤醒广播的action
     */
    public final static String BOOT_COMPLETE_ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"action =:" + action);
        RemoteService.start(context);
        KeepCornerLiveService.tryToAddCorner(context);
    }
}
