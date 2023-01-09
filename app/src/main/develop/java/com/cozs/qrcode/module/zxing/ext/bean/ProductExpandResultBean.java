package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;

import java.util.Map;

public class ProductExpandResultBean extends ResultBean {

    private String productID;
    private String sscc;
    private String lotNumber;
    private String productionDate;
    private String packagingDate;
    private String bestBeforeDate;
    private String expirationDate;
    private String weight;
    private String weightType;
    private String weightIncrement;
    private String price;
    private String priceIncrement;
    private String priceCurrency;
    // For AIS that not exist in this object
    private Map<String,String> uncommonAIs;

    public ProductExpandResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull ExpandedProductParsedResult parsedResult) {
        productID = parsedResult.getProductID();
        sscc = parsedResult.getSscc();
        lotNumber = parsedResult.getLotNumber();
        productionDate = parsedResult.getProductionDate();
        packagingDate = parsedResult.getPackagingDate();
        bestBeforeDate = parsedResult.getBestBeforeDate();
        expirationDate = parsedResult.getExpirationDate();
        weight = parsedResult.getWeight();
        weightType = parsedResult.getWeightType();
        weightIncrement = parsedResult.getWeightIncrement();
        price = parsedResult.getPrice();
        priceIncrement = parsedResult.getPriceIncrement();
        priceCurrency = parsedResult.getPriceCurrency();
        uncommonAIs = parsedResult.getUncommonAIs();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return null;
    }

    public String getProductID() {
        return productID;
    }

    public String getSscc() {
        return sscc;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public String getPackagingDate() {
        return packagingDate;
    }

    public String getBestBeforeDate() {
        return bestBeforeDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getWeight() {
        return weight;
    }

    public String getWeightType() {
        return weightType;
    }

    public String getWeightIncrement() {
        return weightIncrement;
    }

    public String getPrice() {
        return price;
    }

    public String getPriceIncrement() {
        return priceIncrement;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public Map<String, String> getUncommonAIs() {
        return uncommonAIs;
    }
}
