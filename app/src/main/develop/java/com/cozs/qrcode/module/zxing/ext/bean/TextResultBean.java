package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;

public class TextResultBean extends ResultBean<ParsedResult> {

    private String text;

    public TextResultBean() {}

    public TextResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField(parsedResult);
    }

    @Override
    public void buildField(@NonNull ParsedResult parsedResult) {
        text = rawText;
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextResultBean{" +
                "barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
