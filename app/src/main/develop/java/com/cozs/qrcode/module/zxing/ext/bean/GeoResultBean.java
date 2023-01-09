package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ParsedResult;

public class GeoResultBean extends ResultBean<GeoParsedResult> {

    private double latitude;
    private double longitude;
    private double altitude;
    private String query;

    public GeoResultBean() {}

    public GeoResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((GeoParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull GeoParsedResult parsedResult) {
        latitude = parsedResult.getLatitude();
        longitude = parsedResult.getLongitude();
        altitude = parsedResult.getAltitude();
        query = parsedResult.getQuery();
    }

    @Override
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "GeoResultBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", query='" + query + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
