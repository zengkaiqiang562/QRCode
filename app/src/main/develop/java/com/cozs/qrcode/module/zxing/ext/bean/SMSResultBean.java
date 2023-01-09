package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.SMSParsedResult;

public class SMSResultBean extends ResultBean {

    private String[] numbers;
    private String[] vias;
    private String subject;
    private String body;

    public SMSResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull SMSParsedResult parsedResult) {
        numbers = parsedResult.getNumbers();
        vias = parsedResult.getVias();
        subject = parsedResult.getSubject();
        body = parsedResult.getBody();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        if (numbers == null || numbers.length == 0) {
            return null;
        }
        return "smsto:" + numbers[0] + ":" + body;
    }

    public String[] getNumbers() {
        return numbers;
    }

    public String[] getVias() {
        return vias;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
