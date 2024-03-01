package com.morse.ipc;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {

    private static final String TAG = "IPC：ServiceCache";

    private static final ServiceCache instance = new ServiceCache();

    /**
     * 缓存服务类
     */
    private final Map<String, Class<?>> mClassMap;

    /**
     * 缓存服务对象
     */
    private final Map<String, Object> mInstanceObjectMap;

    /**
     * 缓存服务接口对外提供的方法
     */
    private final Map<String, ConcurrentHashMap<String, Method>> mAllMethodMap;

    private ServiceCache() {
        mClassMap = new ConcurrentHashMap<String, Class<?>>();
        mInstanceObjectMap = new ConcurrentHashMap<String, Object>();
        mAllMethodMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Method>>();
    }

    public static ServiceCache getInstance() {
        return instance;
    }

    public void register(String key, Class<?> clazz) {
        Log.d(TAG, "register");
        registerClass(key, clazz);
        registerMethod(clazz);
    }

    public Object getObject(String clazz) {
        Log.d(TAG, "getObject");
        // 将客户端需要调用的接口、方法参数发送出去
        return mInstanceObjectMap.get(clazz);
    }

    public void putObject(String className, Object o) {
        mInstanceObjectMap.put(className, o);
    }

    private void registerMethod(Class<?> clazz) {
        Log.d(TAG, "registerMethod");
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            ConcurrentHashMap<String, Method> map = mAllMethodMap.get(clazz.getName());
            if (map == null) {
                map = new ConcurrentHashMap<String, Method>();
                mAllMethodMap.put(clazz.getName(), map);
            }
            // java存在重载函数，在保存方法时，对方法进行签名
            String key = getMethodParameters(method);
            map.put(key, method);
        }
    }

    /**
     * 获取方法签名
     *
     * @param method 方法
     * @return 签名
     */
    private String getMethodParameters(Method method) {
        Log.d(TAG, "getMethodParameters");
        StringBuilder result = new StringBuilder();
        result.append(method.getName());
        Class<?>[] classess = method.getParameterTypes();
        int length = classess.length;
        if (length == 0) {
            return result.toString();
        }
        for (int i = 0; i < length; i++) {
            result.append("-").append(classess[i].getName());
        }
        return result.toString();
    }

    private void registerClass(String key, Class<?> clazz) {
        Log.d(TAG, "registerClass");
        mClassMap.put(key, clazz);
    }

    public Method getMethod(String className, String methodName) {
        ConcurrentHashMap<String, Method> methods = mAllMethodMap.get(className);
        if (methods == null) {
            return null;
        }
        return methods.get(methodName);
    }
}
