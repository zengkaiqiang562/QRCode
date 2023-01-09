package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TextParsedResult;

public class TextResultBean extends ResultBean {

    private String text;

    public TextResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull TextParsedResult parsedResult) {
        text = parsedResult.getText();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return text;
    }

    public String getText() {
        return text;
    }
}
