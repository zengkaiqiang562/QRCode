package com.cozs.qrcode.module.advert;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cozs.qrcode.module.activity.BaseActivity;
import com.cozs.qrcode.module.constant.AdvertPlace;
import com.cozs.qrcode.module.constant.AdvertType;
import com.cozs.qrcode.module.event.EventManager;
import com.cozs.qrcode.module.event.IEventListener;
import com.cozs.qrcode.module.event.bean.AdvertEvent;
import com.cozs.qrcode.module.event.bean.ConfigUpdateEvent;
import com.cozs.qrcode.module.library.ConfigLibrary;
import com.cozs.qrcode.module.library.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class AdvertActivity extends BaseActivity {

    protected AdvertResource<?> insetAdvertResource; // 嵌入在 View 树中的广告，如 nav 广告，ban 广告
    protected AdvertResource<?> intervalAdvertResource; // Connect 广告
    protected AdvertResource<?> extraAdvertResource; // Extra广告

    protected AdvertPlace adPlaceHolder;

    protected ViewGroup container;

    private AdEventBusReceiver adEventBusReceiver;
    private UpdateConfigEventBusReceiver updateConfigEventBusReceiver;

    // 如果广告显示失败（返回 false），跳下一页；如果显示成功（返回 true），则在广告关闭的回调中跳下一页
    protected boolean showIntervalAdvert() {
        Logger.e(TAG, "--> showIntervalAdvert()");
        if (!isResumed) {
            return false;
        }
        AdvertResource<?> advertResource = AdvertManager.getInstance().retrieveAdvertResource(AdvertPlace.INTERVAL);
        boolean adShowed = false;
        if (advertResource != null && advertResource.show(this, null)) {
            adShowed = true;
            intervalAdvertResource = advertResource;
        }
        return adShowed;
    }

    protected boolean showExtraAdvert() {
        Logger.e(TAG, "--> showExtraAdvert()");
        AdvertResource<?> advertResource = AdvertManager.getInstance().retrieveAdvertResource(AdvertPlace.INTERVAL);
        boolean adShowed = false;
        if (advertResource != null && advertResource.show(this, null)) {
            adShowed = true;
            extraAdvertResource = advertResource;
        }
        return adShowed;
    }

    protected void showInsetAdvert(@NonNull AdvertPlace adPlaceHolder, @NonNull ViewGroup container) { // onStart 中调用，保证界面每次从后台到前台都加载新的广告
        showInsetAdvert(adPlaceHolder, container, false);
    }

    protected void showInsetAdvert(@NonNull AdvertPlace adPlaceHolder, @NonNull ViewGroup container, boolean light) { // onStart 中调用，保证界面每次从后台到前台都加载新的广告
        this.adPlaceHolder = adPlaceHolder;
        this.container = container;

        Logger.e(TAG, "--> showInsetAdvert()  adPlaceHolder=" + adPlaceHolder + "  container=" + container + "  insetAdvertResource=" + insetAdvertResource);

        if (insetAdvertResource == null) {
            AdvertResource<?> advertResource = AdvertManager.getInstance().retrieveAdvertResource(adPlaceHolder);
            if (advertResource != null && advertResource.show(this, container, light)) {
                insetAdvertResource = advertResource;
                container.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void destroyInsetAdvert(AdvertPlace adPlaceHolder, ViewGroup container) {
        Logger.e(TAG, "--> destroyInsetAd()  adPlaceHolder=" + adPlaceHolder + "  container=" + container + "  insetAdvertResource=" + insetAdvertResource);

        if (insetAdvertResource != null) {
            insetAdvertResource.destroy();
            insetAdvertResource = null;
        }

        if (container != null) {
            container.removeAllViews();
            container.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBusReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (insetAdvertResource != null) {
            insetAdvertResource.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (insetAdvertResource != null) {
            insetAdvertResource.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBusReceivers();
    }


    protected void onAdEventReceived(AdvertEvent event) {
        switch (event.getType()) {
            case EVENT_AD_DISMISS:
            case EVENT_AD_UNSHOW:
                if (intervalAdvertResource != null && intervalAdvertResource == event.getBean()) {
                    intervalAdvertResource = null;
                }
                if (extraAdvertResource != null && extraAdvertResource == event.getBean()) {
                    extraAdvertResource = null;
                }
                break;
        }
    }

    protected void onUpdateConfigEventReceived(ConfigUpdateEvent event) {
        if (adPlaceHolder == null || container == null) {
            return;
        }
        AdvertType advertType = ConfigLibrary.getInstance().getAdCategory(adPlaceHolder);
        if (advertType == null) {
            return;
        }

        if (!AdvertType.BAN.type.equals(advertType.type)) {
            return;
        }

        // 处理 ban
        boolean adActive = ConfigLibrary.getInstance().checkActive(adPlaceHolder);
        Logger.e(TAG, "--> onUpdateConfigEventReceived()  adPlaceHolder=" + adPlaceHolder + "  adActive=" + adActive);
        if (!adActive) {
            handler.post(() -> {
                destroyInsetAdvert(adPlaceHolder, container);
            });
        } else if (insetAdvertResource != null && AdvertType.BAN.type.equals(insetAdvertResource.getAdCategoryBean().getType())){ // 避免 nav 切 ban 的情况，只考虑 ban 广告的开关
            handler.post(() -> {
                showInsetAdvert(adPlaceHolder, container);
            });
        }
    }

    private void registerEventBusReceivers() {
        if (adEventBusReceiver == null) {
            adEventBusReceiver = new AdEventBusReceiver();
        }
        adEventBusReceiver.register();

        if (updateConfigEventBusReceiver == null) {
            updateConfigEventBusReceiver = new UpdateConfigEventBusReceiver();
        }
        updateConfigEventBusReceiver.register();
    }

    private void unregisterEventBusReceivers() {
        if (adEventBusReceiver != null) {
            adEventBusReceiver.unregister();
            adEventBusReceiver = null;
        }

        if (updateConfigEventBusReceiver != null) {
            updateConfigEventBusReceiver.unregister();
            updateConfigEventBusReceiver = null;
        }
    }

    private class UpdateConfigEventBusReceiver implements IEventListener<ConfigUpdateEvent> {

        private void register() {
            EventManager.register(this);
        }

        private void unregister() {
            EventManager.unregister(this);
        }

        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventReceived(ConfigUpdateEvent event) {
            Logger.e(TAG, "--> onEventReceived()  event=" + event);
            onUpdateConfigEventReceived(event);
        }
    }

    private class AdEventBusReceiver implements IEventListener<AdvertEvent> {

        private void register() {
            EventManager.register(this);
        }

        private void unregister() {
            EventManager.unregister(this);
        }

        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventReceived(AdvertEvent event) {
            Logger.e(TAG, "--> onEventReceived()  event=" + event);
            onAdEventReceived(event);
        }
    }
}
