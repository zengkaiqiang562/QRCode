package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public class GeoResultBean extends ResultBean {

    private double latitude;
    private double longitude;
    private double altitude;
    private String query;

    public GeoResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull GeoParsedResult parsedResult) {
        latitude = parsedResult.getLatitude();
        longitude = parsedResult.getLongitude();
        altitude = parsedResult.getAltitude();
        query = parsedResult.getQuery();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        //"geo:([\\-0-9.]+),([\\-0-9.]+)(?:,([\\-0-9.]+))?(?:\\?(.*))?"
        /*
        geo:
        ([\\-0-9.]+),
        ([\\-0-9.]+)
        (?:,([\\-0-9.]+))?
        (?:\\?(.*))?
         */
        return "geo:" + latitude + "," + longitude + "," + altitude + (TextUtils.isEmpty(query) ? "" : ("?" + query));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getQuery() {
        return query;
    }
}
