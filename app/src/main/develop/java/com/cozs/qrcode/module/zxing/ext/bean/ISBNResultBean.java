package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public class ISBNResultBean extends ResultBean {

    private String isbn;

    public ISBNResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull ISBNParsedResult parsedResult) {
        isbn = parsedResult.getISBN();
    }

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
}
