package com.cozs.qrcode.module.zxing.ext.bean;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.result.ParsedResultType;

public interface IResultBean {
    BarcodeFormat getBarcodeFormat();
    ParsedResultType getParsedResultType();
    long getCreateTime();
    String getRawText();
    String getDisplayContents();
    boolean isFavorite();
}
