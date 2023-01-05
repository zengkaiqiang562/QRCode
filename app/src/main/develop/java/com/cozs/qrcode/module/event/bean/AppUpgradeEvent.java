package com.cozs.qrcode.module.event.bean;

import androidx.annotation.Nullable;

import com.cozs.qrcode.module.bean.UpgradeInfoBean;

public class AppUpgradeEvent extends BaseEvent<UpgradeInfoBean>{

    public AppUpgradeEvent(@Nullable UpgradeInfoBean bean) {
        super(bean);
    }
}
