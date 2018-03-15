package com.zibuyuqing.roundcorner.service;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zibuyuqing.roundcorner.IProcessService;
import com.zibuyuqing.roundcorner.utils.Utilities;

import static com.zibuyuqing.roundcorner.service.LocalControllerService.NOTIFICATION_ID;

/**
 * Created by lingy on 2018-03-14.
 */

public class RemoteService extends Service {
    private static final String TAG = RemoteService.class.getSimpleName();
    private RemoteBinder mBinder;
    private RemoteConn mConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new RemoteBinder();
        Log.e(TAG,"remote service onCreate");
        if(mConnection == null){
            mConnection = new RemoteConn();
        }
        LocalControllerService.tryToAddCorners(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"remote service onStartCommand");
        bindService(new Intent(this,LocalControllerService.class),
                mConnection,Context.BIND_IMPORTANT);
        return START_STICKY;
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, RemoteService.class);
        context.startService(starter);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class RemoteBinder extends IProcessService.Stub{
        @Override
        public String getServiceName() throws RemoteException {
            return TAG;
        }
    }

    private class RemoteConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG,"Local service 连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG,"Local service 断开连接");
            startService(new Intent(RemoteService.this,LocalControllerService.class));
            bindService(new Intent(RemoteService.this,LocalControllerService.class),
                    mConnection,
                    Context.BIND_IMPORTANT);
        }
    }
}
