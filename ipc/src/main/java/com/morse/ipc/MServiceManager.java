package com.morse.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * 模拟ServiceManager业务
 * <p>
 * 1、注册服务，将服务列表缓存在本地
 * 2、发现服务
 * 3、服务调用
 */
public class MServiceManager {

    private static final String TAG = "IPC";

    private static final MServiceManager instance = new MServiceManager();

    private ServiceCache mServiceCache = ServiceCache.getInstance();

    private ServiceConnection mServiceConnection;

    public static MServiceManager getDefault() {
        return instance;
    }

    public void open(Context context) {
        open(context, null);
    }

    private void open(Context context, String packageName) {
        bind(context.getApplicationContext(), packageName, MServiceManager.class);
    }

    private void bind(Context context, String packageName,
                      Class<? extends MServiceManager> service) {
        mServiceConnection = new MServiceConnection();
        Intent intent = new Intent(context, service);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 服务注册
     *
     * @param key   服务标识
     * @param clazz 服务类
     */
    public void addService(String key, Class<?> clazz) {
        mServiceCache.register(key, clazz);
    }

    public <T> T getInstance(Class<T> clazz, Object ...params) {
        return mServiceCache.getObject(clazz, params);
    }

    private static class MServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    }

}
