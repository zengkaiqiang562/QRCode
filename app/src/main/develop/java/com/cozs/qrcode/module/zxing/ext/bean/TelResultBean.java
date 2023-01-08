package com.cozs.qrcode.module.zxing.ext.bean;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.result.ParsedResultType;

public class TelResultBean implements IResultBean {

    private final String barcodeFormat;
    private final String parsedResultType;
    private final long createTime;
    private final String rawText;
    private final String displayContents;
    private boolean favorite;

    public TelResultBean(String barcodeFormat, String parsedResultType, long createTime, String rawText, String displayContents) {
        this.barcodeFormat = barcodeFormat;
        this.parsedResultType = parsedResultType;
        this.createTime = createTime;
        this.rawText = rawText;
        this.displayContents = displayContents;
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
}
