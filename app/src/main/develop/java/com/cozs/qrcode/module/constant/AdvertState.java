package com.cozs.qrcode.module.constant;

public enum AdvertState {
    READY, // 准备加载
    LOADING, // 正在加载
    LOAD_SUCCESS, // 加载成功
    LOAD_FAILED, // 加载失败
    SHOW, // 显示成功（并显示出来了）
    DISMISS, // 显示完进入消失状态
    UNSHOW, // 显示失败（没有显示出来）
    DESTROY, // 已释放资源（销毁）
    EXPIRED // 抓取后在有效期限内
}
