package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;

/**
 * uri 的匹配规则见 {@link com.google.zxing.client.result.URIResultParser#parse(Result)} 中的 isBasicallyValidURI(uri) 方法
 */
public class UriResultBean extends ResultBean<URIParsedResult> {

    private String uri;
    private String title;

    public UriResultBean() {}

    public UriResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((URIParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull URIParsedResult parsedResult) {
        uri = parsedResult.getURI();
        title = parsedResult.getTitle();
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return uri;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "UriResultBean{" +
                "uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
