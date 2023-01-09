package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.SMSParsedResult;

public class SMSResultBean implements IResultBean {

    private final String barcodeFormat;
    private final String parsedResultType;
    private final long createTime;
    private final String rawText;
    private final String displayContents;
    private boolean favorite;

    private String[] numbers;
    private String[] vias;
    private String subject;
    private String body;

    public SMSResultBean(String barcodeFormat, String parsedResultType, long createTime, String rawText, String displayContents) {
        this.barcodeFormat = barcodeFormat;
        this.parsedResultType = parsedResultType;
        this.createTime = createTime;
        this.rawText = rawText;
        this.displayContents = displayContents;
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
