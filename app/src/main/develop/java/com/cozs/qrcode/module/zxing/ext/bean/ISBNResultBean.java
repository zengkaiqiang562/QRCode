package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;

public class ISBNResultBean extends ResultBean<ISBNParsedResult> {

    private String isbn;

    public ISBNResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((ISBNParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull ISBNParsedResult parsedResult) {
        isbn = parsedResult.getISBN();
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        /*
        BarcodeFormat format = result.getBarcodeFormat();
        if (format != BarcodeFormat.EAN_13) {
          return null;
        }
        String rawText = getMassagedText(result);
        int length = rawText.length();
        if (length != 13) {
          return null;
        }
        if (!rawText.startsWith("978") && !rawText.startsWith("979")) {
          return null;
        }
         */
        return isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "ISBNResultBean{" +
                "isbn='" + isbn + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
