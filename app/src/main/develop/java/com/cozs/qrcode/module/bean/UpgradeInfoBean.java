package com.cozs.qrcode.module.bean;

import com.google.gson.annotations.SerializedName;

public class UpgradeInfoBean {
    @SerializedName("mustUpdate")
    private boolean force; // 是否强制更新

    @SerializedName("firstApp")
    private int beginCode; // 开始的版本号

    @SerializedName("stopApp")
    private int endCode; // 结束的版本号

    @SerializedName("upgradePkg")
    private String pkgName; // 更新包名

    @SerializedName("title_upd")
    private String title; // 更新标题

    @SerializedName("updateContent")
    private String message; // 弹窗信息

    public UpgradeInfoBean() {}

    private UpgradeInfoBean(boolean force, int beginCode, int endCode, String pkgName, String title, String message) {
        this.force = force;
        this.beginCode = beginCode;
        this.endCode = endCode;
        this.pkgName = pkgName;
        this.title = title;
        this.message = message;
    }

    public boolean isForce() {
        return force;
    }

    public int getBeginCode() {
        return beginCode;
    }

    public int getEndCode() {
        return endCode;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UpgradeInfoBean{" +
                "force=" + force +
                ", beginCode=" + beginCode +
                ", endCode=" + endCode +
                ", pkgName='" + pkgName + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
