package com.cozs.qrcode.module.constant;

public enum AdvertType {
    START("start"),
    INT("int"),
    NAV("nav"),
    BAN("ban");

    public final String type;

    AdvertType(String type) {
        this.type = type;
    }

    public static AdvertType convert(String type) {
        for (AdvertType advertType : values()) {
            if (advertType.type.equals(type)) {
                return advertType;
            }
        }
        return null;
    }
}
