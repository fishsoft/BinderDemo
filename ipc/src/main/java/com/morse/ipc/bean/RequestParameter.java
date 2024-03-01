package com.morse.ipc.bean;

/**
 * 客户端请求参数
 */
public class RequestParameter {

    private String parameterClassName;
    private String parameterValue;

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName(String parameterClassName) {
        this.parameterClassName = parameterClassName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
}
