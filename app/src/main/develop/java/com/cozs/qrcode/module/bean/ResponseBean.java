package com.cozs.qrcode.module.bean;

import java.io.Serializable;

public class ResponseBean<T> implements Serializable {
    /*
    statusCode	Integer	是	响应码	status
    respMessage	String	是	响应信息	msg
    returnData	Object	否	响应体	data
     */

    protected int statusCode;
    protected String respMessage;
    protected T returnData;

    public int getStatusCode() {
        return statusCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public T getReturnData() {
        return returnData;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "statusCode=" + statusCode +
                ", respMessage='" + respMessage + '\'' +
                ", returnData=" + returnData +
                '}';
    }
}
