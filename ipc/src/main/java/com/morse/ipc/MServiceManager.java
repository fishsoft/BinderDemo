package com.morse.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.morse.ipc.ann.ClassId;
import com.morse.ipc.bean.RequestBean;
import com.morse.ipc.bean.RequestParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 模拟ServiceManager业务
 * <p>
 * 1、注册服务，将服务列表缓存在本地
 * 2、发现服务
 * 3、服务调用
 */
public class MServiceManager {

    private static final String TAG = "IPC：MServiceManager";

    public static final int REQUEST_GET = 1;
    public static final int REQUEST_INVOKE = 2;

    private static final MServiceManager instance = new MServiceManager();

    private ServiceCache mServiceCache = ServiceCache.getInstance();

    private ServiceConnection mServiceConnection;

    private MorseBinderInterface morseBinderInterface;

    private Gson gson = new Gson();

    private Context mContext;

    public static MServiceManager getDefault() {
        return instance;
    }

    public void open(Context context) {
        open(context, null);
        mContext = context.getApplicationContext();
    }

    /**
     * 链接服务
     *
     * @param context
     * @param packageName
     */
    private void open(Context context, String packageName) {
        bind(context.getApplicationContext(), packageName, MorseService.class);
    }

    /**
     * 绑定服务
     *
     * @param context     绑定对象的上下文
     * @param packageName 包名
     * @param service     接口
     */
    private void bind(Context context, String packageName, Class<? extends MorseService> service) {
        Log.d(TAG, "bind");
        Intent intent;
        mServiceConnection = new MServiceConnection();
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            ComponentName componentName = new ComponentName(packageName, service.getName());
            intent = new Intent();
            intent.setComponent(componentName);
            intent.setAction(service.getName());
        }
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 服务注册
     *
     * @param key   服务标识
     * @param clazz 服务类
     */
    public void addService(String key, Class<?> clazz) {
        Log.d(TAG, "addService");
        mServiceCache.register(key, clazz);
    }

    /**
     * 获取对象实例
     *
     * @param clazz 实例化的对象类
     * @param <T>
     * @return 实例化的对象
     */
    public <T> T getInstance(Class<T> clazz) {
        Log.d(TAG, "getInstance");
        sendRequest(clazz, null, new Object[0], REQUEST_GET);
        // 通过动态代理获取服务接口的实现对象
        return (T) Proxy.newProxyInstance(mContext.getClassLoader(), new Class[]{clazz},
                new BPBinder(clazz));
    }

    /**
     * 发送数据，并接收返回的响应信息
     *
     * @param clazz      调用的类
     * @param method     调用的方法
     * @param parameters 调用方法的参数
     * @param type       调用方法类型
     * @param <T>
     * @return 响应消息
     */
    public <T> String sendRequest(Class<T> clazz, Method method, Object[] parameters, int type) {
        String request = gson.toJson(getRequestData(clazz, method, parameters, type));
        Log.d(TAG, "sendRequest: request: " + request);
        try {
            return morseBinderInterface.transcat(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据类和方法信息，将参数转化成java请求类
     *
     * @param clazz      调用的类
     * @param method     调用的方法
     * @param parameters 调用方法的参数
     * @param type       方法类型
     * @param <T>
     * @return javabean
     */
    private <T> RequestBean getRequestData(Class<T> clazz, Method method, Object[] parameters,
                                           int type) {
        RequestBean requestBean = new RequestBean();
        String className = clazz.getAnnotation(ClassId.class).value();
        requestBean.setClassName(className);
        requestBean.setMethodName(method == null ? "getInstance" : method.getName());
        requestBean.setType(type);
        int length = parameters == null ? 0 : parameters.length;
        if (length == 0) {
            requestBean.setRequestParameters(new RequestParameter[0]);
            return requestBean;
        }
        RequestParameter[] requestParameters = new RequestParameter[length];
        for (int i = 0; i < length; i++) {
            Object o = parameters[i];
            String clazzName = o.getClass().getName();
            RequestParameter parameter = new RequestParameter();
            parameter.setParameterClassName(clazzName);
            parameter.setParameterValue(gson.toJson(o));
            requestParameters[i] = parameter;
        }
        requestBean.setRequestParameters(requestParameters);
        return requestBean;
    }

    /**
     * 链接服务
     */
    private class MServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            morseBinderInterface = MorseBinderInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    }

}
