package com.cozs.qrcode.module.event.bean;

import androidx.annotation.Nullable;

import com.cozs.qrcode.module.advert.AdvertResource;

public class AdvertEvent extends BaseEvent<AdvertResource<?>>{

    private final Type type;

    public AdvertEvent(@Nullable AdvertResource<?> bean, Type type) {
        super(bean);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        EVENT_AD_PREPARED,
        EVENT_AD_SHOW,
        EVENT_AD_DISMISS,
        EVENT_AD_UNSHOW
    }
}
