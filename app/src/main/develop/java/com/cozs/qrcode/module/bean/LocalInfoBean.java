package com.cozs.qrcode.module.bean;

import com.google.gson.annotations.SerializedName;

public class LocalInfoBean {
    @SerializedName("query")
    private String ip;

    @SerializedName("country")
    private String country;

    @SerializedName("countryCode")
    private String countryCode; // 国家代码

    @SerializedName("regionName")
    private String province; // 省份

    @SerializedName("lat")
    private float latitude; // 维度

    @SerializedName("lon")
    private float longitude; // 经度

    public String getIp() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getProvince() {
        return province;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "LocalInfoBean{" +
                "ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", province='" + province + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
