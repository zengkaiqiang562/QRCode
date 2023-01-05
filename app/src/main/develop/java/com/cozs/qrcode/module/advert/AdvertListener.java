package com.cozs.qrcode.module.advert;

public interface AdvertListener {
    void onAdLoadBefore(AdvertResource<?> advertResource);

    void onAdLoadFailed(AdvertResource<?> advertResource, int code, String msg);

    void onAdLoadSuccess(AdvertResource<?> advertResource);

    void onAdShow(AdvertResource<?> advertResource); // 显示中

    void onAdDismiss(AdvertResource<?> advertResource); // 显示完并消失

    void onAdUnshow(AdvertResource<?> advertResource, int code, String msg); // 显示失败

    void onAdExpired(AdvertResource<?> advertResource); // 广告超过有效期

    void onAdClick(AdvertResource<?> advertResource); // 访问（点击）广告
}
