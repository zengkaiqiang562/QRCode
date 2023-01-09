package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.TelParsedResult;

public class TelResultBean extends ResultBean<TelParsedResult> {

    private String number;
    private String telURI;
    private String title;

    public TelResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((TelParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull TelParsedResult parsedResult) {
        number = parsedResult.getNumber();
        telURI = parsedResult.getTelURI();
        title = parsedResult.getTitle();
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        return "tel:" + number;
    }

    public String getNumber() {
        return number;
    }

    public String getTelURI() {
        return telURI;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "TelResultBean{" +
                "number='" + number + '\'' +
                ", telURI='" + telURI + '\'' +
                ", title='" + title + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
