package com.cozs.qrcode.module.event.bean;

import androidx.annotation.Nullable;

public abstract class BaseEvent<T> {
    T bean;

    protected BaseEvent(@Nullable T bean) {
        this.bean = bean;
    }

    @Nullable
    public T getBean() {
        return bean;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "bean=" + bean +
                '}';
    }
}
