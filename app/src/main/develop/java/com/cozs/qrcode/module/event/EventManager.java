package com.cozs.qrcode.module.event;

import com.cozs.qrcode.module.event.bean.BaseEvent;

import org.greenrobot.eventbus.EventBus;

public class EventManager {
    public static <T> void post(BaseEvent<T> eventBean, boolean sticky) {
        if (sticky) {
            EventBus.getDefault().postSticky(eventBean);
        } else {
            EventBus.getDefault().post(eventBean);
        }
    }

    public static void register(IEventListener<?> listener) {
        if (!EventBus.getDefault().isRegistered(listener)) {
            EventBus.getDefault().register(listener);
        }
    }

    public static void unregister(IEventListener<?> listener) {
        if (EventBus.getDefault().isRegistered(listener)) {
            EventBus.getDefault().unregister(listener);
        }
    }
}
