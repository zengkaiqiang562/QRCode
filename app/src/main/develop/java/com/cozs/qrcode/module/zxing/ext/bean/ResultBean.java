package com.cozs.qrcode.module.zxing.ext.bean;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public abstract class ResultBean {
    protected final BarcodeFormat barcodeFormat;
    protected final ParsedResultType parsedResultType;
    protected final long createTime;
    protected final String rawText;
    protected final String display;

    public ResultBean(Result result, ParsedResult parsedResult) {
        this.barcodeFormat = result.getBarcodeFormat();
        this.createTime = result.getTimestamp();
        this.rawText = result.getText();
        this.parsedResultType = parsedResult.getType();
        this.display = parsedResult.getDisplayResult();
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    public ParsedResultType getParsedResultType() {
        return parsedResultType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getRawText() {
        return rawText;
    }

    public String getDisplay() {
        return display;
    }
}
