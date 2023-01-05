package com.cozs.qrcode.module.library;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cozs.qrcode.module.bean.AdPlaceInfoBean;
import com.cozs.qrcode.module.bean.ConfigInfoBean;
import com.cozs.qrcode.module.bean.ResponseBean;
import com.cozs.qrcode.module.bean.UpgradeInfoBean;
import com.cozs.qrcode.module.constant.AdvertPlace;
import com.cozs.qrcode.module.constant.AdvertType;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.event.EventManager;
import com.cozs.qrcode.module.event.bean.AppUpgradeEvent;
import com.cozs.qrcode.module.event.bean.ConfigUpdateEvent;
import com.cozs.qrcode.module.net.NetService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigLibrary {
    private static final String TAG = "ConfigLibrary";

    private volatile static ConfigLibrary instance;

    private static final int MSG_UPDATE_APP_CONFIG = 10001; // 更新全局配置

    private ConfigInfoBean configInfoBean;

    private final AppConfigHandler handler = new AppConfigHandler();

    private ConfigLibrary() {}

    public static ConfigLibrary getInstance() {
        if (instance == null) {
            synchronized (ConfigLibrary.class) {
                if (instance == null) {
                    instance = new ConfigLibrary();
                }
            }
        }
        return instance;
    }

    /**
     * @return 返回指定广告位上的广告类型
     */
    public AdvertType getAdCategory(@NonNull AdvertPlace adPlaceHolder) {

        List<AdPlaceInfoBean> adPlaceHolderBeans = getAdPlaceHolderBeans();

        if (adPlaceHolderBeans == null || adPlaceHolderBeans.size() <= 0) {
            return null;
        }

        for (AdPlaceInfoBean adPlaceHolderBean : adPlaceHolderBeans) {
            if (!adPlaceHolder.place.equals(adPlaceHolderBean.getPlace()) || adPlaceHolderBean.getAdTypeInfo() == null) {
                continue;
            }
            return AdvertType.convert(adPlaceHolderBean.getAdTypeInfo().getType());
        }

        return null;
    }

    /**
     * @return 返回指定广告位实体类
     */
    public AdPlaceInfoBean getAdPlaceHolderBean(@NonNull AdvertPlace adPlaceHolder) {

        List<AdPlaceInfoBean> adPlaceHolderBeans = getAdPlaceHolderBeans();

        if (adPlaceHolderBeans == null || adPlaceHolderBeans.size() <= 0) {
            return null;
        }

        for (AdPlaceInfoBean adPlaceHolderBean : adPlaceHolderBeans) {
            if (!adPlaceHolder.place.equals(adPlaceHolderBean.getPlace())) {
                continue;
            }

            if (!adPlaceHolderBean.isEnable()) {
                Logger.d("<" + adPlaceHolderBean.getPlace() + ">广告关闭");
                continue;
            }

            return adPlaceHolderBean;
        }
        return null;
    }

    /**
     * @return 返回所有的广告位
     */
    public List<AdPlaceInfoBean> getAdPlaceHolderBeans() {
        ConfigInfoBean appConfigBean = getConfigBean();
        return appConfigBean == null ? null : appConfigBean.getAdPlaceInfos();
    }

    /**
     * @return 指定广告位是否已关闭（默认关闭）
     */
    public boolean checkActive(@NonNull AdvertPlace adPlaceHolder) {

        List<AdPlaceInfoBean> adPlaceHolderBeans = getAdPlaceHolderBeans();

        if (adPlaceHolderBeans == null || adPlaceHolderBeans.size() <= 0) {
            return false;
        }

        for (AdPlaceInfoBean adPlaceHolderBean : adPlaceHolderBeans) {
            if (!adPlaceHolder.place.equals(adPlaceHolderBean.getPlace())) {
                continue;
            }
            return adPlaceHolderBean.isEnable();
        }

        return false;
    }

    /**
     * @return 首次启动是否展示引导页 （默认展示）
     */
    public boolean isEnableGuide() {
        ConfigInfoBean appConfigBean = getConfigBean();
        return appConfigBean != null && appConfigBean.isEnableGuide();
    }

    /**
     * 冷热启动时，检查是否需要升级 app
     */
    public void updateApp() {
        ConfigInfoBean configInfoBean = getConfigBean();

        if (configInfoBean == null || configInfoBean.getUpgradeInfoBean() == null) {
            return;
        }

        UpgradeInfoBean upgradeInfoBean = configInfoBean.getUpgradeInfoBean();

        Logger.e(TAG, "--> updateApp()  upgradeInfoBean=" + upgradeInfoBean);

        String pkgName = upgradeInfoBean.getPkgName();
        String title = upgradeInfoBean.getTitle();
        String message = upgradeInfoBean.getMessage();
        int beginCode = upgradeInfoBean.getBeginCode();

        int appVersionCode = AppUtils.getAppVersionCode();
        String packageName = AppUtils.getAppPackageName();

        Logger.e(TAG, "--> updateApp()  appVersionCode=" + appVersionCode + " packageName=" + packageName);

        if (TextUtils.isEmpty(pkgName) || !pkgName.equals(packageName) // 与当前 app 包名不一样时 不更新
                || appVersionCode >= beginCode // 当前 app 版本号 >= 更新版本号时 不更新
                || TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
            return;
        }

        EventManager.post(new AppUpgradeEvent(upgradeInfoBean), true);
    }

    /**
     * 获取启动页时长上限（S）
     */
    public int getSplashDuration() {
        ConfigInfoBean configInfoBean = getConfigBean();

        if (configInfoBean == null) {
            return Constants.DEFAULT_MAX_LAUNCH_DURATION;
        }

        int splashDuration = configInfoBean.getSplashDuration();
        Logger.e(TAG, "--> getSplashDuration() splashDuration(s)=" + splashDuration);
        return splashDuration;
    }

    public void updateAppConfig(boolean fromUser) {
        long timeUpdateConfig = configInfoBean == null ? 0 : configInfoBean.getConfigInterval(); // unit: min

        if (timeUpdateConfig <= 0) timeUpdateConfig = Constants.TIME_UPDATE_APP_CONFIG;

        if (handler.hasMessages(MSG_UPDATE_APP_CONFIG)) {
            handler.removeMessages(MSG_UPDATE_APP_CONFIG);
        }
        handler.sendEmptyMessageDelayed(MSG_UPDATE_APP_CONFIG, fromUser ? 0 : (timeUpdateConfig * 60L * 1000L));
    }

    private void requestAppConfig() {
        NetService.getInstance().requestConfigInfo(new Callback<ResponseBean<ConfigInfoBean>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBean<ConfigInfoBean>> call, @NonNull Response<ResponseBean<ConfigInfoBean>> response) {
                ConfigInfoBean appConfigBean = response.body() == null ? null : response.body().getReturnData();
                Logger.e("请求<全局配置>成功 ## " + JsonUtils.toJson(appConfigBean));
                doAppConfig(appConfigBean);
//                doAppConfig(null); // TODO test 不从网络获取全局配置
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBean<ConfigInfoBean>> call, @NonNull Throwable t) {
                Logger.e("请求<全局配置>失败 ## " + t);
                doAppConfig(null);
            }
        });
    }

    private void doAppConfig(ConfigInfoBean appConfigBean) {
        if (appConfigBean == null) { // 网络请求全局配置失败，从本地缓存中获取
            this.configInfoBean = retrieveCache();
        } else {
            // 网络请求全局配置成功，更新到 sprefs 中的网络缓存
            cacheAppConfig(appConfigBean);
            this.configInfoBean = appConfigBean;
            // 通知配置发生变化
            EventManager.post(new ConfigUpdateEvent(appConfigBean), false);
        }
        updateAppConfig(false);
    }

    // cache profile to SP
    private void cacheAppConfig(ConfigInfoBean appConfigBean) {
        if (appConfigBean == null) {
            return;
        }
        String appConfigJson = JsonUtils.toJson(appConfigBean);
        if (TextUtils.isEmpty(appConfigJson)) {
            return;
        }
        appConfigJson = CryptoGuard.encrypt(appConfigJson);
        SPUtils.getInstance().put(Constants.SPREF_APP_CONFIG_CACHE, appConfigJson);
    }

    // get cache from sprefs or native
    private @Nullable ConfigInfoBean retrieveCache() {

        ConfigInfoBean appConfigBean = null;

        String appConfigCache = SPUtils.getInstance().getString(Constants.SPREF_APP_CONFIG_CACHE, "");

        if (!TextUtils.isEmpty(appConfigCache)) { // firstly, get from sprefs
            appConfigCache = CryptoGuard.decrypt(appConfigCache);
            Logger.e("从网络缓存中获取<全局配置> ## " + appConfigCache);
            appConfigBean = JsonUtils.fromJson(appConfigCache, ConfigInfoBean.class);
        }

        if (appConfigBean == null) {  // if no cache in sprefs, then get from native
            appConfigCache = CryptoGuard.nativeRetrieveConfig();
            Logger.e("从本地缓存中获取<全局配置> ## " + appConfigCache);
            appConfigBean = JsonUtils.fromJson(appConfigCache, ConfigInfoBean.class);
        }

        return appConfigBean;
    }

    private @Nullable ConfigInfoBean getConfigBean() {
        if (configInfoBean == null) {
            configInfoBean = retrieveCache();
        }
        return configInfoBean;
    }

    private class AppConfigHandler extends Handler {

        AppConfigHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_UPDATE_APP_CONFIG) {
                Logger.e(TAG, "--> MSG_UPDATE_APP_CONFIG");
                requestAppConfig();
            }
        }
    }
}
