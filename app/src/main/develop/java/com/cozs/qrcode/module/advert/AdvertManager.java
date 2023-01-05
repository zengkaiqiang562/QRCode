package com.cozs.qrcode.module.advert;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.cozs.qrcode.module.library.ActivityStackManager;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;

import com.cozs.qrcode.module.bean.AdPlaceInfoBean;
import com.cozs.qrcode.module.bean.AdTypeInfoBean;
import com.cozs.qrcode.module.constant.AdvertPlace;
import com.cozs.qrcode.module.constant.AdvertState;
import com.cozs.qrcode.module.constant.AdvertType;
import com.cozs.qrcode.module.event.EventManager;
import com.cozs.qrcode.module.event.bean.AdvertEvent;
import com.cozs.qrcode.module.library.ConfigLibrary;
import com.cozs.qrcode.module.library.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdvertManager implements AdvertListener {

    private static final String TAG = "AdvertManager";

    private volatile static AdvertManager instance;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private AdvertManager() {}

    public static AdvertManager getInstance() {
        if (instance == null) {
            synchronized (AdvertManager.class) {
                if (instance == null) {
                    instance = new AdvertManager();
                }
            }
        }
        return instance;
    }

    public boolean checkAdvertPrepared() {
        boolean adActive = ConfigLibrary.getInstance().checkActive(AdvertPlace.LAUNCH);
        AdvertResource<?> successAdvertResourceInStart = AdvertMap.get(AdvertPlace.LAUNCH, AdvertState.LOAD_SUCCESS);
        AdvertResource<?> successAdvertResourceInInterval = AdvertMap.get(AdvertPlace.INTERVAL, AdvertState.LOAD_SUCCESS);
        // start 广告位广告 or 任意一个插页广告加载完成
        return !adActive || successAdvertResourceInStart != null || successAdvertResourceInInterval != null;
    }

    public static void destroyAdActivity() {
        ActivityStackManager.getInstance().finishActivity(AdActivity.class);
    }

    public static void addAdActivity(@NonNull Activity activity) {
        if (activity.getClass() == AdActivity.class) {
            ActivityStackManager.getInstance().addActivity(activity);
        }
    }

    public static void removeAdActivity(@NonNull Activity activity) {
        if (activity.getClass() == AdActivity.class) {
            ActivityStackManager.getInstance().removeActivity(activity);
        }
    }

    public void loadEveryAdvert() {
        List<AdPlaceInfoBean> adPlaceHolderBeans = ConfigLibrary.getInstance().getAdPlaceHolderBeans();
        if (adPlaceHolderBeans == null || adPlaceHolderBeans.size() <= 0) {
            return;
        }

        Logger.d("##### 准备拉取所有广告 #####");

        for (AdPlaceInfoBean adPlaceHolderBean : adPlaceHolderBeans) { // 遍历所有广告位
            if (!adPlaceHolderBean.isEnable()) { // 广告位关闭则不加载广告
                Logger.d("<" + adPlaceHolderBean.getPlace() + ">广告关闭");
                continue;
            }

            if (adPlaceHolderBean.getAdTypeInfo() == null) {
                continue;
            }
            loadAdvert(adPlaceHolderBean, adPlaceHolderBean.getAdTypeInfo()); // 遍历每个广告位上的广告
        }

        Logger.d("#######################");
    }

    public void loadAdvertInPlaceHolder(AdvertPlace adPlaceHolder) {
        AdPlaceInfoBean adPlaceHolderBean = ConfigLibrary.getInstance().getAdPlaceHolderBean(adPlaceHolder); // 如果指定广告位上关闭了广告，返回 null

        if (adPlaceHolderBean == null || adPlaceHolderBean.getAdTypeInfo() == null) {
            return;
        }

        loadAdvert(adPlaceHolderBean, adPlaceHolderBean.getAdTypeInfo()); // 遍历指定广告位上的广告
    }

    /**
     * 查找指定广告位上的有效广告（返回的广告可复用其他广告位上的同类型广告，特别地，对 start 位上的 start 类型广告，还可复用其他位上的 int 广告）
     * 一般在要显示广告时调用该方法查找可显示的广告，若没有则显示失败，此时，应该去加载一个新的广告
     *
     * @param adPlaceHolder 广告位
     * @return 返回广告位上的有效广告，or 其他广告位上的可共用广告
     */
    public synchronized AdvertResource<?> retrieveAdvertResource(@NonNull AdvertPlace adPlaceHolder) {
        Logger.e(TAG, "--> retrieveAdvertResource()  adPlaceHolder=" + adPlaceHolder);

        AdvertType adCategory = ConfigLibrary.getInstance().getAdCategory(adPlaceHolder);
        boolean adActive = ConfigLibrary.getInstance().checkActive(adPlaceHolder);

        if (!adActive) { // // 若 slot 广告位关闭，则不再展示广告（即使有缓存也不展示）
            return null;
        }

        if (/*adActive && */adCategory == AdvertType.BAN) {
            return createBanAdvertResource(adPlaceHolder);
        }

        AdvertResource<?> advertResource = AdvertMap.get(adPlaceHolder, AdvertState.LOAD_SUCCESS);
        if (advertResource != null) {
            Logger.e(TAG, "--> retrieveAdvertResource() success!!!  adPlaceHolder=" + adPlaceHolder + "  adPacket=" + advertResource);
            return advertResource;
        } else {
            loadAdvertInPlaceHolder(adPlaceHolder); // slot 广告位上无有效广告时，去加载新的广告
        }

//        if (!adActive) { // 若 slot 广告位关闭，则不再去共用其他广告位的同类型广告
//            return null;
//        }

        // 执行到这里说明在 slot 上没找到有效广告，此时去其他广告位上找可共用的同类型的有效广告
        AdvertResource<?> shareAdvertResource = retrieveShareAdvertResource(adCategory, adPlaceHolder);

        if (shareAdvertResource != null) {
            return shareAdvertResource;
        }

        // 当 start 位上的广告是 start 类型且无有效的 start 类型时，可以去其他广告位（如 finish 位）上找 int 类型广告
        if (adCategory == AdvertType.START && adPlaceHolder == AdvertPlace.LAUNCH) {
            return retrieveShareAdvertResource(AdvertType.INT, adPlaceHolder);
        }

        return null;
    }

    private void loadAdvert(@NonNull AdPlaceInfoBean adPlaceHolderBean, @NonNull AdTypeInfoBean adCategoryBean) {

        AdvertPlace adPlaceHolder = AdvertPlace.convert(adPlaceHolderBean.getPlace());
        AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());

        if (TextUtils.isEmpty(adCategoryBean.getId()) || adCategory == null) {
            Logger.e(TAG, "--> loadAdvert() with invalid params  adCategoryBean=" + adCategoryBean + "  adCategory=" + adCategory);
            return;
        }

        if (adCategory == AdvertType.BAN) {
            Logger.e(TAG, "--> loadAdvert() don't need to load Banner");
            return;
        }


        AdvertResource<?> readyAdvertResource = AdvertMap.get(adPlaceHolder, AdvertState.READY);
        AdvertResource<?> loadingAdvertResource = AdvertMap.get(adPlaceHolder, AdvertState.LOADING);
        AdvertResource<?> successAdvertResource = AdvertMap.get(adPlaceHolder, AdvertState.LOAD_SUCCESS);

        Logger.e(TAG, "--> loadAdvert()  adPlaceHolder=" + adPlaceHolder + " adCategory=" + adCategory
                + " readyAdvertResource=" + readyAdvertResource
                + " loadingAdvertResource=" + loadingAdvertResource
                + " successAdvertResource=" + successAdvertResource);

        if (readyAdvertResource != null || loadingAdvertResource != null || successAdvertResource != null) {
            // 若 广告位上有广告 正准备拉取 or 正在拉取 or 已缓存有拉取成功的广告，则不再拉取
            return;
        }

//        if (type == BillType.NAV && isArrivedNavMaxClickTimes()) { // 当天 nav 广告的点击次数超上限时，不加载 nav 广告
//            Slog.e(TAG, "--> loadBill() don't load nav because of max click times");
//            Slog.td("类型为<" + type.type + ">的广告的单日点击次数达到上限，不再拉取该广告");
//            return;
//        }

        AdvertResource<?> advertResource = null;
        switch (adCategory) {
            case START:
                advertResource = new AdvertResource<AppOpenAd>(adPlaceHolderBean, adCategoryBean, this);
                break;
            case INT:
                advertResource = new AdvertResource<InterstitialAd>(adPlaceHolderBean, adCategoryBean, this);
                break;
            case NAV:
                advertResource = new AdvertResource<NativeAd>(adPlaceHolderBean, adCategoryBean, this);
                break;
        }
        Logger.e(TAG, "--> loadAdvert()  advertResource=" + advertResource);
        if (advertResource != null) {
            Logger.d("开始拉取<" + adPlaceHolderBean.getPlace() + ">广告 ## " + adCategoryBean);
            executorService.execute(advertResource::load);
            AdvertMap.put(adPlaceHolder, advertResource);
        }
    }

    /**
     * 在其他广告位上查找可共用的有效广告
     * @param dstAdCategory 广告类型
     * @param excludeAdPlaceHolder 当前广告位
     * @return 其他广告位上可共用的广告
     */
    private synchronized AdvertResource<?> retrieveShareAdvertResource(AdvertType dstAdCategory, AdvertPlace excludeAdPlaceHolder) {
        Set<Map.Entry<AdvertPlace, List<AdvertResource<?>>>> entrySet = AdvertMap.entrySet();
        for (Map.Entry<AdvertPlace, List<AdvertResource<?>>> entry : entrySet) {
            AdvertPlace adPlaceHolder = entry.getKey();
            if (adPlaceHolder == excludeAdPlaceHolder) { // 排除当前广告位
                continue;
            }
            List<AdvertResource<?>> advertResources = entry.getValue();
            if (advertResources == null || advertResources.size() <= 0) {
                continue;
            }
            for (int i = advertResources.size() - 1; i >= 0; i--) {
                AdvertResource<?> advertResource = advertResources.get(i);
                AdvertType adCategory = AdvertType.convert(advertResource.getAdCategoryBean().getType());
                AdvertState adStatus = advertResource.getAdStatus();
                if (adCategory != dstAdCategory) {
                    continue; // 只有同类型的广告才可共用
                }
                if (adStatus == AdvertState.LOAD_SUCCESS) {
                    Logger.e(TAG, "--> retrieveShareAdvertResource success!!!  adPlaceHolder=" + adPlaceHolder + "  adPacket=" + advertResource);
                    Logger.d("<" + excludeAdPlaceHolder.place + ">上可共用<" + advertResource.getAdPlaceHolderBean().getPlace() + ">上的广告 ## " + advertResource.getAdCategoryBean());
                    return advertResource;
                } else { // slot 广告位上没有可显示的广告，加载一个新的
                    loadAdvertInPlaceHolder(adPlaceHolder);
                }
            }
        }
        return null;
    }

    @Override
    public void onAdLoadBefore(AdvertResource<?> advertResource) {
        // 准备加载前，先移除缓存中之前 加载失败的广告 和 已销毁的广告
        mainHandler.post(() -> {
            AdvertPlace slot = AdvertPlace.convert(advertResource.getAdPlaceHolderBean().getPlace());
            AdvertMap.remove(slot, AdvertState.LOAD_FAILED);
            AdvertMap.remove(slot, AdvertState.DESTROY);
        });
    }

    @Override
    public void onAdLoadFailed(AdvertResource<?> advertResource, int code, String msg) {
        Logger.e(TAG, "--> onAdLoadFailed()  advertResource=" + advertResource);
        Logger.d("拉取<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告失败 ## " + advertResource.getAdCategoryBean());
        mainHandler.post(this::notifyAdPrepared);
    }

    @Override
    public void onAdLoadSuccess(AdvertResource<?> advertResource) {
        Logger.e(TAG, "--> onAdLoadSuccess()  advertResource=" + advertResource);
        Logger.d("拉取<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告成功 ## " + advertResource.getAdCategoryBean());
        mainHandler.post(this::notifyAdPrepared);
    }

    @Override
    public void onAdShow(AdvertResource<?> advertResource) {
        // 一个广告位上的广告被展示后，再为该广告位加载一个新的广告进行缓存（按需求：一个广告位上只有一个广告），以便下次展示
        Logger.d("展示<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告成功 ## " + advertResource.getAdCategoryBean());
        mainHandler.post(() -> {
            AdvertPlace slot = AdvertPlace.convert(advertResource.getAdPlaceHolderBean().getPlace());
//            loadBill(slot); // 显示成功再另外缓存个新的
            loadEveryAdvert(); // 2022.10.21 fix bug #25581 ：在广告展示完之后，需要检查所有的广告位
            notifyAdShow(advertResource);
        });
    }

    @Override
    public void onAdDismiss(AdvertResource<?> advertResource) {
        // 广告显示完，从缓存中移除
        mainHandler.post(() -> {
            AdvertPlace slot = AdvertPlace.convert(advertResource.getAdPlaceHolderBean().getPlace());
            AdvertMap.remove(slot, AdvertState.DISMISS);
            notifyAdDismiss(advertResource);
        });
    }

    @Override
    public void onAdUnshow(AdvertResource<?> advertResource, int code, String msg) {
        // 广告显示失败，从缓存中移除
        Logger.d("展示<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告失败 ## " + advertResource.getAdCategoryBean());
        mainHandler.post(() -> {
            AdvertPlace slot = AdvertPlace.convert(advertResource.getAdPlaceHolderBean().getPlace());
            AdvertMap.remove(slot, AdvertState.UNSHOW);
            loadAdvertInPlaceHolder(slot); // 显示失败再另外缓存个新的
            notifyAdUnshow(advertResource, code, msg);
        });
    }

    @Override
    public void onAdExpired(AdvertResource<?> advertResource) {
        // 广告超时，从缓存中移除
        Logger.d("<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告有效期超过 1 小时 ## " + advertResource.getAdCategoryBean());
        mainHandler.post(() -> {
            AdvertPlace slot = AdvertPlace.convert(advertResource.getAdPlaceHolderBean().getPlace());
            AdvertMap.remove(slot, AdvertState.EXPIRED);
            loadAdvertInPlaceHolder(slot); // 缓存超时再另外缓存个新的
        });
    }

    @Override
    public void onAdClick(AdvertResource<?> advertResource) {
        Logger.d("<" + advertResource.getAdPlaceHolderBean().getPlace() + ">广告被点击 ## " + advertResource.getAdCategoryBean());
    }

    /*---------------------------------------------------------------------------------*/

    private void notifyAdPrepared() {
        boolean isAdPrepared = checkAdvertPrepared();

        Logger.e(TAG, "--> notifyAdPrepared()  isAdPrepared=" + isAdPrepared);
        if (isAdPrepared) {
            EventManager.post(new AdvertEvent(null, AdvertEvent.Type.EVENT_AD_PREPARED), false);
        }
    }

    private void notifyAdShow(AdvertResource<?> advertResource) {
        EventManager.post(new AdvertEvent(advertResource, AdvertEvent.Type.EVENT_AD_SHOW), false);
    }

    private void notifyAdDismiss(AdvertResource<?> advertResource) {
        EventManager.post(new AdvertEvent(advertResource, AdvertEvent.Type.EVENT_AD_DISMISS), false);
    }

    private void notifyAdUnshow(AdvertResource<?> advertResource, int code, String msg) {
        EventManager.post(new AdvertEvent(advertResource, AdvertEvent.Type.EVENT_AD_UNSHOW), false);
    }

    private AdvertResource<?> createBanAdvertResource(@NonNull AdvertPlace adPlaceHolder) {
        AdPlaceInfoBean adPlaceHolderBean = ConfigLibrary.getInstance().getAdPlaceHolderBean(adPlaceHolder);
        if (adPlaceHolderBean == null || adPlaceHolderBean.getAdTypeInfo() == null) {
            return null;
        }
        AdTypeInfoBean adCategoryBean = adPlaceHolderBean.getAdTypeInfo();
        if (adCategoryBean == null) {
            return null;
        }
        Logger.d("开始拉取<" + adPlaceHolderBean.getPlace() + ">广告 ## " + adCategoryBean);
        // ban 不放入缓存池，但走回调监听
        return new AdvertResource<AdView>(adPlaceHolderBean, adCategoryBean, this);
    }
}
