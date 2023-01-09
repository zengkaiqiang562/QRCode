package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;

public class ProductResultBean extends ResultBean<ProductParsedResult> {

    private String productID;
    private String normalizedProductID;

    public ProductResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((ProductParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull ProductParsedResult parsedResult) {
        productID = parsedResult.getProductID();
        normalizedProductID = parsedResult.getNormalizedProductID();
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return productID;
    }

    public String getProductID() {
        return productID;
    }

    public String getNormalizedProductID() {
        return normalizedProductID;
    }

    @Override
    public String toString() {
        return "ProductResultBean{" +
                "productID='" + productID + '\'' +
                ", normalizedProductID='" + normalizedProductID + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
