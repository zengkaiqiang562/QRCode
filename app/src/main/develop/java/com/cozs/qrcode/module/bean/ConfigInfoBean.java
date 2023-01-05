package com.cozs.qrcode.module.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigInfoBean {
    @SerializedName("configInterval")
    private int configInterval;

    @SerializedName("splashDuration")
    private int splashDuration;

    @SerializedName("enableGuide")
    private boolean enableGuide;

    @SerializedName("adList")
    private List<AdPlaceInfoBean> adPlaceInfos;

    @SerializedName("updateMessage")
    private UpgradeInfoBean upgradeInfoBean;

    public int getConfigInterval() {
        return configInterval;
    }

    public int getSplashDuration() {
        return splashDuration;
    }

    public boolean isEnableGuide() {
        return enableGuide;
    }

    public List<AdPlaceInfoBean> getAdPlaceInfos() {
        return adPlaceInfos;
    }

    public UpgradeInfoBean getUpgradeInfoBean() {
        return upgradeInfoBean;
    }

    @Override
    public String toString() {
        return "ConfigInfoBean{" +
                "configInterval=" + configInterval +
                ", splashDuration=" + splashDuration +
                ", enableGuide=" + enableGuide +
                ", adPlaceInfos=" + adPlaceInfos +
                ", upgradeInfoBean=" + upgradeInfoBean +
                '}';
    }
}
