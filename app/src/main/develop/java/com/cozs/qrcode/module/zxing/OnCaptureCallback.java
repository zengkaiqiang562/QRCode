package com.cozs.qrcode.module.zxing;


import com.cozs.qrcode.module.zxing.result.ResultHandler;
import com.google.zxing.Result;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface OnCaptureCallback {

    /**
     * 接收扫码结果回调
     */
    void onResultCallback(Result result, ResultHandler resultHandler);
}
