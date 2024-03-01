package com.morse.ipc;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 接收端数据解析
 */
public class BPBinder implements InvocationHandler {

    private Class<?> clazz;

    Gson gson = new Gson();

    public BPBinder(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String data = MServiceManager.getDefault().sendRequest(clazz, method, args, MServiceManager.REQUEST_INVOKE);
        return TextUtils.isEmpty(data) ? null : gson.fromJson(data, method.getReturnType());
    }
}
