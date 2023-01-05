package com.cozs.qrcode.module.constant;

public enum AdvertPlace {
    LAUNCH("Smarterstart"),
    HOME("Smarterhome"),
    REPORT("Smarterreport"),
    INTERVAL("Smarterconnect");

    public final String place;

    AdvertPlace(String place) {
        this.place = place;
    }

    public static AdvertPlace convert(String place) {
        for (AdvertPlace advertPlace : values()) {
            if (advertPlace.place.equals(place)) {
                return advertPlace;
            }
        }
        return null;
    }
}
