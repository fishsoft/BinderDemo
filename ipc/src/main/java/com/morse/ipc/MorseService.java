package com.morse.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.morse.ipc.bean.RequestBean;
import com.morse.ipc.bean.RequestParameter;

import java.lang.reflect.Method;

/**
 * 服务的具体实现
 */
public class MorseService extends Service {

    private Gson gson = new Gson();

    private BBBinder bbBinder = new BBBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MorseBinderInterface.Stub() {
            @Override
            public String transcat(String request) throws RemoteException {
                // 接受到数据，对数据进行解析
                if (TextUtils.isEmpty(request)) {
                    return null;
                }
                RequestBean requestBean = gson.fromJson(request, RequestBean.class);
                String className = requestBean.getClassName();
                String methodName = requestBean.getMethodName();
                int type = requestBean.getType();
                switch (type) {
                    case MServiceManager.REQUEST_GET:
                        // 调用实例化接口，从json数据中获取调用方法的对应方法，方法参数
                        Object[] objects = makeParameterObject(requestBean.getRequestParameters());
                        Method method = ServiceCache.getInstance().getMethod(className, makeMethodKey(methodName,
                                requestBean.getRequestParameters()));
                        // 接受对应实例化方法的处理结果
                        Object o = bbBinder.onTransact(null, method, objects);
                        // 保存对象
                        ServiceCache.getInstance().putObject(className, o);
                        break;
                    case MServiceManager.REQUEST_INVOKE:
                        // 调用实例化的对象的具体方法，从缓存中找到对应的对象和方法以及参数
                        Object object = ServiceCache.getInstance().getObject(className);
                        Method method1 = ServiceCache.getInstance().getMethod(className, makeMethodKey(methodName,
                                requestBean.getRequestParameters()));
                        Object[] objects1 = makeParameterObject(requestBean.getRequestParameters());
                        // 接收调用的方法的执行结果
                        Object data = bbBinder.onTransact(object, method1, objects1);
                        return gson.toJson(data);
                    default:
                        break;
                }
                return null;
            }
        };
    }

    private Object[] makeParameterObject(RequestParameter[] parameters) {
        int length = parameters.length;
        if (length == 0) {
            return new Object[0];
        }
        Object[] objects = new Object[length];
        for (int i = 0; i < length; i++) {
            RequestParameter parameter = parameters[i];
            Class<?> clazz = getClassType(parameter.getParameterClassName());
            objects[i] = gson.fromJson(parameter.getParameterValue(), clazz);
        }
        return objects;
    }

    private String makeMethodKey(String methodName, RequestParameter[] parameters) {
        StringBuilder builder = new StringBuilder(methodName);
        int length = parameters.length;
        if (length == 0) {
            return builder.toString();
        }
        for (int i = 0; i < length; i++) {
            RequestParameter parameter = parameters[i];
            Class<?> clazz = getClassType(parameter.getParameterClassName());
            builder.append("-").append(clazz.getName());
        }
        return builder.toString();
    }

    private Class<?> getClassType(String parameterClassName) {
        try {
            return Class.forName(parameterClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
