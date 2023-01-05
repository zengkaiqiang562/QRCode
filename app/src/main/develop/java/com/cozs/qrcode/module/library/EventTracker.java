package com.cozs.qrcode.module.library;

public class EventTracker {
    private static final String TAG = "EventTracker";

    /**
     * 事件: 引导页展示
     * 事件名: jnrwnr
     * 是否去重: 是
     */
    public static void traceGuideViewShow() {
        SdkLibrary.traceEvent("jnrwnr", null, true);
    }

    /**
     * 事件: 启动页展示
     * 事件名: vsqnwk
     * 是否去重: 是
     */
    public static void traceLoadViewShow() {
        SdkLibrary.traceEvent("vsqnwk", null, true);
    }

    /**
     * 事件: 首页展示
     * 事件名: py4whu
     * 是否去重: 是
     */
    public static void traceHomeViewShow() {
        SdkLibrary.traceEvent("py4whu", null, true);
    }

    /**
     * 事件: 报告页展示
     * 事件名: h9rhf7
     * 是否去重: 是
     */
    public static void traceReportViewShow() {
        SdkLibrary.traceEvent("h9rhf7", null, true);
    }

    /**
     * 事件: 垃圾清理完成页展示
     * 事件名: kmlmoc
     * 是否去重: 是
     */
    public static void traceJunkCompletedViewShow() {
        SdkLibrary.traceEvent("kmlmoc", null, true);
    }

    /**
     * 事件: 手机加速完成页展示
     * 事件名:  vr28kk
     * 是否去重: 是
     */
    public static void traceBoostCompletedViewShow() {
        SdkLibrary.traceEvent("vr28kk", null, true);
    }

    /**
     * 事件: CPU降温完成页展示
     * 事件名:  j7l282
     * 是否去重: 是
     */
    public static void traceCpuCompletedViewShow() {
        SdkLibrary.traceEvent("j7l282", null, true);
    }

    /**
     * 事件: 图片清理完成页展示
     * 事件名:  j7r63j
     * 是否去重: 是
     */
    public static void tracePhotoCompletedViewShow() {
        SdkLibrary.traceEvent("j7r63j", null, true);
    }

    /**
     * 事件: 视频清理完成页展示
     * 事件名:  hoprfw
     * 是否去重: 是
     */
    public static void traceVideoCompletedViewShow() {
        SdkLibrary.traceEvent("hoprfw", null, true);
    }

    /**
     * 事件: 启动页插页广告展示成功
     * 事件名: epu17b
     * 是否去重: 否
     */
    public static void traceStartAdShow() {
        Logger.e(TAG, "--> traceStartAdShow()");
        SdkLibrary.traceEvent("epu17b", null, false);
    }

    /**
     * 事件: 完成页插页广告展示成功
     * 事件名: f0bdma
     * 是否去重: 否
     */
    public static void traceCompletedAdShow() {
        Logger.e(TAG, "--> traceCompletedAdShow()");
        SdkLibrary.traceEvent("f0bdma", null, false);
    }
}
