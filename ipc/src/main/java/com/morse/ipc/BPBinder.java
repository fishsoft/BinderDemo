package com.morse.ipc;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 发送数据
 */
public class BPBinder implements InvocationHandler {

    private static final String TAG = "TAG:BPBinder";

    private Class<?> clazz;

    Gson gson = new Gson();

    public BPBinder(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke");
        // 发送客户端参数，并接受服务器端返回的数据
        String data = MServiceManager.getDefault().sendRequest(clazz, method, args,
                MServiceManager.REQUEST_INVOKE);
        return TextUtils.isEmpty(data) ? null : gson.fromJson(data, method.getReturnType());
    }
}
