package com.cozs.qrcode.module.event;

import com.cozs.qrcode.module.event.bean.BaseEvent;

public interface IEventListener <E extends BaseEvent<?>> {
    void onEventReceived(E event);
}
