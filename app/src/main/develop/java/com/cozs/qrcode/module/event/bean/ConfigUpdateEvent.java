package com.cozs.qrcode.module.event.bean;

import androidx.annotation.Nullable;

import com.cozs.qrcode.module.bean.ConfigInfoBean;

public class ConfigUpdateEvent extends BaseEvent<ConfigInfoBean> {

    public ConfigUpdateEvent(@Nullable ConfigInfoBean bean) {
        super(bean);
    }
}
