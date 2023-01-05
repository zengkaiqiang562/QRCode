package com.cozs.qrcode.module.bean;

import com.google.gson.annotations.SerializedName;

public class AdTypeInfoBean {
    /*
    ad_place	String	是	广告id	placeId
    adv_scale	Integer	是	广告权重	weight
    ad_type	String	是	广告类型	adtype
     */

    @SerializedName("ad_place")
    private String id;

    @SerializedName("adv_scale")
    private String weight;

    @SerializedName("ad_type")
    private String type;

    public String getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        String suffixId = id;
        if (id != null && id.startsWith("ca-app-pub-")) {
            suffixId = id.substring("ca-app-pub-".length());
        }
        return "{" +
                "id='" + suffixId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
