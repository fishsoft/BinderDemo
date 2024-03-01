package com.morse.ipc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 接收数据
 */
public class BBBinder {

    public Object onTransact(Object o, Method method, Object[] objects) {
        // 服务器短接受到数据，调用服务器段的接口方法，并返回响应信息
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
