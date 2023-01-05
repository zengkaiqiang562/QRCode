package com.cozs.qrcode.module.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdPlaceInfoBean {
    /*
        advSwitch	Boolean	是	广告开关	adSwitch
        adPlace	String	是	广告位	adPlace
        adPreload	Boolean	否	广告预加载(无效参数)	adPreload
        innerAd	Object[]	是	广告内层	adSource
         */
    @SerializedName("advSwitch")
    private boolean enable;

    @SerializedName("adPlace")
    private String place;

    private boolean adPreload; // 广告预加载(无效参数)

    @SerializedName("innerAd")
    private List<AdTypeInfoBean> adTypeInfos;

    public boolean isEnable() {
        return enable;
    }

    public String getPlace() {
        return place;
    }

    public AdTypeInfoBean getAdTypeInfo() {
        if (adTypeInfos == null || adTypeInfos.isEmpty()) {
            return null;
        }
        return adTypeInfos.get(0);
    }

    @Override
    public String toString() {
        return "AdPlaceInfoBean{" +
                "enable=" + enable +
                ", place='" + place + '\'' +
                '}';
    }
}
