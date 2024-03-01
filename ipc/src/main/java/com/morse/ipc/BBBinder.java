package com.morse.ipc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 发送端数据解析
 */
public class BBBinder {

    public Object onTransact(Object o, Method method, Object[] objects) {
        Object object = null;
        try {
            object = method.invoke(o, objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

}
