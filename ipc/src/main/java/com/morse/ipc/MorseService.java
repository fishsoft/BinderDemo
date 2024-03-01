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

public class MorseService extends Service {

    private Gson gson = new Gson();

    private ServiceCache cache = ServiceCache.getInstance();

    private BBBinder bbBinder = new BBBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MorseBinderInterface.Stub() {
            @Override
            public String transcat(String request) throws RemoteException {
                if (TextUtils.isEmpty(request)) {
                    return null;
                }
                RequestBean requestBean = gson.fromJson(request, RequestBean.class);
                String className = requestBean.getClassName();
                String methodName = requestBean.getMethodName();
                int type = requestBean.getType();
                switch (type) {
                    case MServiceManager.REQUEST_GET:
                        Object[] objects = makeParameterObject(requestBean.getRequestParameters());
                        Method method = cache.getMethod(className, makeMethodKey(methodName,
                                requestBean.getRequestParameters()));

                        Object o = bbBinder.onTransact(null, method, objects);
                        cache.putObject(className, o);
                        break;
                    case MServiceManager.REQUEST_INVOKE:
                        Object object = cache.getObject(className);
                        Method method1 = cache.getMethod(className, makeMethodKey(methodName,
                                requestBean.getRequestParameters()));
                        Object[] objects1 = makeParameterObject(requestBean.getRequestParameters());
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
