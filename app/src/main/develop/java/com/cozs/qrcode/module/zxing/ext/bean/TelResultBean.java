package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;

public class TelResultBean implements IResultBean {

    private final String barcodeFormat;
    private final String parsedResultType;
    private final long createTime;
    private final String rawText;
    private final String displayContents;
    private boolean favorite;

    private String number;
    private String telURI;
    private String title;

    public TelResultBean(String barcodeFormat, String parsedResultType, long createTime, String rawText, String displayContents) {
        this.barcodeFormat = barcodeFormat;
        this.parsedResultType = parsedResultType;
        this.createTime = createTime;
        this.rawText = rawText;
        this.displayContents = displayContents;
    }

    public void buildField(@NonNull TelParsedResult parsedResult) {
        number = parsedResult.getNumber();
        telURI = parsedResult.getTelURI();
        title = parsedResult.getTitle();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        return "tel:" + number;
    }

    @Override
    public BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.valueOf(barcodeFormat);
    }

    @Override
    public ParsedResultType getParsedResultType() {
        return ParsedResultType.valueOf(parsedResultType);
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String getRawText() {
        return rawText;
    }

    @Override
    public String getDisplayContents() {
        return displayContents;
    }

    @Override
    public boolean isFavorite() {
        return favorite;
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
}
