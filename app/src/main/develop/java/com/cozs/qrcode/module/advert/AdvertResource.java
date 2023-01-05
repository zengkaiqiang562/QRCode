package com.cozs.qrcode.module.advert;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.cozs.qrcode.module.QRApplication;
import com.cozs.qrcode.module.bean.AdPlaceInfoBean;
import com.cozs.qrcode.module.bean.AdTypeInfoBean;
import com.cozs.qrcode.module.constant.AdvertState;
import com.cozs.qrcode.module.constant.AdvertType;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.Logger;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import java.util.Locale;


public class AdvertResource<T> {

    private static final String TAG = "AdvertResource";

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final AdvertListener listener;
    private final AdPlaceInfoBean adPlaceHolderBean;
    private final AdTypeInfoBean adCategoryBean;
    private AdvertState adStatus;
    private T ad;
    private long timeLoaded; // timestamp when fetch successful


    public AdvertResource(@NonNull AdPlaceInfoBean adPlaceHolderBean, @NonNull AdTypeInfoBean adCategoryBean, AdvertListener listener) {
        adStatus =  AdvertState.READY;
        this.adPlaceHolderBean = adPlaceHolderBean;
        this.adCategoryBean = adCategoryBean;
        this.listener = listener;
    }

    public AdvertState getAdStatus() {
        return adStatus;
    }

    public AdPlaceInfoBean getAdPlaceHolderBean() {
        return adPlaceHolderBean;
    }

    public AdTypeInfoBean getAdCategoryBean() {
        return adCategoryBean;
    }

    public void load() {
        if (isNotReady()) {
            return;
        }

        final AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());
        if (adCategory == null) {
            Log.e(TAG, "-->  load()  faild !!! because of a null AdCategory");
            return;
        }

        if (listener != null && adCategory != AdvertType.BAN) {
            listener.onAdLoadBefore(this);
        }

        handler.post(() -> {
            switch (adCategory) {
                case START:
                    adStatus =  AdvertState.LOADING;
                    loadOpen();
                    break;
                case INT:
                    adStatus =  AdvertState.LOADING;
                    loadInt();
                    break;
                case NAV:
                    adStatus =  AdvertState.LOADING;
                    loadNav();
                    break;
//                case BAN:
//                    /* ban don't need load */
//                    break;
            }
        });
    }

    public boolean show(Activity activity, @Nullable ViewGroup container) {
        return show(activity, container, false);
    }

    /**
     * @param light 默认 false
     */
    public boolean show(Activity activity, @Nullable ViewGroup container, boolean light) {
        if (!ActivityUtils.checkContext(activity)) {
            return false;
        }

        if (isDirty()) {
            return false;
        }

        if (isExpired()) {
            adStatus =  AdvertState.EXPIRED;
            if (listener != null) {
                listener.onAdExpired(this);
            }
            return false;
        }

        final AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());
        if (adCategory == null) {
            Log.e(TAG, "-->  show()  faild !!! because of a null AdCategory");
            return false;
        }

        boolean result = false;
        switch (adCategory) {
            case START:
                result = showOpen(activity);
                break;
            case INT:
                result = showInt(activity);
                break;
            case NAV:
                result = showNav(activity, container, light);
                break;
            case BAN:
                result = showBan(activity, container);
                break;
        }
        return result;
    }

    public void resume() {
        final AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());
        if (adCategory == null) {
            Log.e(TAG, "-->  resume()  faild !!! because of a null AdCategory");
            return;
        }

        if (ad != null && adCategory == AdvertType.BAN) {
            ((AdView) ad).resume();
        }
    }

    public void pause() {
        final AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());
        if (adCategory == null) {
            Log.e(TAG, "-->  pause()  faild !!! because of a null AdCategory");
            return;
        }

        if (ad != null && adCategory == AdvertType.BAN) {
            ((AdView) ad).pause();
        }
    }

    public void destroy() {
        final AdvertType adCategory = AdvertType.convert(adCategoryBean.getType());
        if (adCategory == null) {
            Log.e(TAG, "-->  destroy()  faild !!! because of a null AdCategory");
            return;
        }

        if (ad != null) {
            switch (adCategory) {
                case NAV:
                    ((NativeAd) ad).destroy();
                    break;
                case BAN:
                    /* ban don't need load */
                    ((AdView) ad).destroy();
                    break;
            }
        }

        ad = null;
        adStatus =  AdvertState.DESTROY;
    }

    @Override
    public String toString() {
        return "AdPacket{" +
                "adPlaceHolderBean=" + adPlaceHolderBean +
                ", adCategoryBean=" + adCategoryBean +
                ", adStatus=" + adStatus +
                '}';
    }

    private boolean isExpired() {
        long timAwayLoaded = SystemClock.elapsedRealtime() - timeLoaded;
        Logger.e(TAG, "--> isExpired()  timAwayLoaded=" + timAwayLoaded);
        return timAwayLoaded > ConvertUtils.timeSpan2Millis(50, TimeConstants.MIN); // 50min 内的广告才有效
    }

    private boolean isNotReady() {
        Logger.e(TAG, "--> isNotReady()  adPlaceHolder=" + adPlaceHolderBean.getPlace() + "  adCategoryBean=" + adCategoryBean + "  adStatus=" + adStatus);
        return TextUtils.isEmpty(adCategoryBean.getId()) || adStatus != AdvertState.READY;
    }

    private boolean isDirty() {
        Logger.e(TAG, "--> isDirty()  ad=" + ad + "  adStatus=" + adStatus);
        return ad == null || adStatus != AdvertState.LOAD_SUCCESS;
    }

    private void loadOpen() {
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(QRApplication.getContext(), adCategoryBean.getId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        Logger.e(TAG, "--> onAdLoaded() appOpenAd=" + appOpenAd);
                        adStatus = AdvertState.LOAD_SUCCESS;
                        timeLoaded = SystemClock.elapsedRealtime();
                        ad = (T) appOpenAd;
                        if (listener != null) {
                            listener.onAdLoadSuccess(AdvertResource.this);
                        }
                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Logger.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);

                        adStatus = AdvertState.LOAD_FAILED;

                        String domain = loadAdError.getDomain();
                        int code = loadAdError.getCode();
                        String message = loadAdError.getMessage();
                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                        if (listener != null) {
                            listener.onAdLoadFailed(AdvertResource.this, code, errorMsg);
                        }
                    }
                });
    }

    private void loadInt() {
        AdRequest request = new AdRequest.Builder().build();
        InterstitialAd.load(QRApplication.getContext(), adCategoryBean.getId(), request,
                new InterstitialAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Logger.e(TAG, "--> onAdLoaded()  interstitialAd=" + interstitialAd);
                        adStatus = AdvertState.LOAD_SUCCESS;
                        timeLoaded = SystemClock.elapsedRealtime();
                        ad = (T) interstitialAd;
                        if (listener != null) {
                            listener.onAdLoadSuccess(AdvertResource.this);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Logger.e(TAG, "--> onAdFailedToLoad() loadAdError : " + loadAdError);

                        adStatus = AdvertState.LOAD_FAILED;

                        String domain = loadAdError.getDomain();
                        int code = loadAdError.getCode();
                        String message = loadAdError.getMessage();
                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                        if (listener != null) {
                            listener.onAdLoadFailed(AdvertResource.this, code, errorMsg);
                        }
                    }
                });
    }

    private void loadNav() {
        AdLoader.Builder adLoadBuilder = new AdLoader.Builder(QRApplication.getContext(), adCategoryBean.getId());

        adLoadBuilder.forNativeAd(nativeAd -> { // onNativeAdLoaded
            Logger.e(TAG, "--> onNativeAdLoaded()  nativeAd=" + nativeAd);
            adStatus = AdvertState.LOAD_SUCCESS;
            timeLoaded = SystemClock.elapsedRealtime();
            ad = (T) nativeAd;
            if (listener != null) {
                listener.onAdLoadSuccess(AdvertResource.this);
            }
        });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(true).build(); // 默认静音

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        adLoadBuilder.withNativeAdOptions(adOptions);
        AdLoader adLoader = adLoadBuilder.withAdListener(new com.google.android.gms.ads.AdListener() {

            @Override
            public void onAdClicked() { // 记录了广告获得的点击后，系统会调用 onAdClicked() 方法。
                Logger.e(TAG, "--> onAdClicked()");
//                BillService.getInstance().calcNavClickTimes();
                if (listener != null) {
                    listener.onAdClick(AdvertResource.this);
                }
            }

            @Override
            public void onAdClosed() { // 用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备。
                Logger.e(TAG, "--> onAdClosed()");
                adStatus = AdvertState.DISMISS;
                if (listener != null) {
                    listener.onAdDismiss(AdvertResource.this);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // onAdFailedToLoad() 是唯一包含参数的方法。LoadAdError 类型的错误参数描述了发生的错误。
                Logger.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);

                adStatus = AdvertState.LOAD_FAILED;

                String domain = loadAdError.getDomain();
                int code = loadAdError.getCode();
                String message = loadAdError.getMessage();
                String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                if (listener != null) {
                    listener.onAdLoadFailed(AdvertResource.this, code, errorMsg);
                }
            }

            @Override
            public void onAdImpression() { // 记录了广告获得的展示后，系统会调用 onAdImpression() 方法。
                Logger.e(TAG, "--> onAdImpression()");
                adStatus = AdvertState.SHOW;
                if (listener != null) {
                    listener.onAdShow(AdvertResource.this);
                }
            }

            public void onAdLoaded() { // 广告加载完成后，系统会执行 onAdLoaded() 方法。例如，如果您想将为 Activity 或 Fragment 添加 AdView 的操作推迟到您确定广告会加载时再执行，就可以通过此方法做到。
                Logger.e(TAG, "--> onAdLoaded()");
                // 对原生广告，触发 OnNativeAdLoadedListener.onNativeAdLoaded(NativeAd nativeAd)，不会回调 onAdLoaded()
            }

            public void onAdOpened() { // 广告打开覆盖屏幕的叠加层时，系统会调用 onAdOpened() 方法。
                Logger.e(TAG, "--> onAdOpened()");
            }

        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private boolean showOpen(Activity activity) {
        ((AppOpenAd) ad).setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {

                        Logger.e(TAG, "--> onAdDismissedFullScreenContent()");

                        adStatus = AdvertState.DISMISS;
                        if (listener != null) {
                            listener.onAdDismiss(AdvertResource.this);
                        }
                    }

                    /** Called when fullscreen content failed to show. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                        Logger.e(TAG, "--> onAdFailedToShowFullScreenContent() adError : " + adError);

                        adStatus = AdvertState.UNSHOW;

                        String domain = adError.getDomain();
                        int code = adError.getCode();
                        String message = adError.getMessage();
                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                        if (listener != null) {
                            listener.onAdUnshow(AdvertResource.this, code, errorMsg);
                        }
                    }

                    /** Called when fullscreen content is shown. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Logger.e(TAG, "--> onAdShowedFullScreenContent()");

                        adStatus = AdvertState.SHOW;
                        if (listener != null) {
                            listener.onAdShow(AdvertResource.this);
                        }
                    }
                });

        ((AppOpenAd) ad).show(activity);
        adStatus = AdvertState.SHOW;
        return true;
    }

    private boolean showInt(Activity activity) {
        ((InterstitialAd) ad).setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {

                        Logger.e(TAG, "--> onAdDismissedFullScreenContent()");

                        adStatus = AdvertState.DISMISS;
                        if (listener != null) {
                            listener.onAdDismiss(AdvertResource.this);
                        }
                    }

                    /** Called when fullscreen content failed to show. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                        Logger.e(TAG, "--> onAdFailedToShowFullScreenContent() adError=" + adError);

                        adStatus = AdvertState.UNSHOW;

                        String domain = adError.getDomain();
                        int code = adError.getCode();
                        String message = adError.getMessage();
                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                        if (listener != null) {
                            listener.onAdUnshow(AdvertResource.this, code, errorMsg);
                        }
                    }

                    /** Called when fullscreen content is shown. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Logger.e(TAG, "--> onAdShowedFullScreenContent()");

                        adStatus = AdvertState.SHOW;
                        if (listener != null) {
                            listener.onAdShow(AdvertResource.this);
                        }
                    }
                });

        ((InterstitialAd) ad).show(activity);
        adStatus = AdvertState.SHOW;
        return true;
    }

    private boolean showNav(Activity activity, @Nullable ViewGroup container, boolean light) {
        if (container == null) {
            return false;
        }

//        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(light ? R.layout.widget_nav_default_light : R.layout.widget_nav_default_dark, null);
//        populateNativeAdView((NativeAd) ad, adView, activity);
//        container.removeAllViews();
//        container.addView(adView);
//        adStatus = AdvertState.SHOW;
        return true;
    }

//    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, Activity activity) {
//        // Set the media view.
////        adView.setMediaView(adView.findViewById(R.id.ad_media));
//
//        // Set other ad assets.
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//
////        MediaView mediaView = adView.getMediaView();
//        View iconView = adView.getIconView();
//        View headlineView = adView.getHeadlineView();
//        View bodyView = adView.getBodyView();
//        View callToActionView = adView.getCallToActionView();
//
//        if (headlineView instanceof TextView) {
//            ((TextView) headlineView).setText(nativeAd.getHeadline());
//        }
//
//        String body = nativeAd.getBody();
//        if (bodyView instanceof TextView) {
//            boolean empty = TextUtils.isEmpty(body);
//            bodyView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                ((TextView) bodyView).setText(body);
//            }
//        }
//
//        String callToAction = nativeAd.getCallToAction();
//        if (callToActionView instanceof TextView) {
//            boolean empty = TextUtils.isEmpty(callToAction);
//            callToActionView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                ((TextView) callToActionView).setText(callToAction);
//            }
//        }
//
//        // 小图标
//        NativeAd.Image icon = nativeAd.getIcon();
//        if (iconView instanceof ImageView) {
//            boolean empty = icon == null;
//            iconView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                Drawable iconDrawable = icon.getDrawable();
//
//                Logger.e(TAG, "--> populateNativeAdView()  iconDrawable=" + iconDrawable);
//
//                RoundedCornersTransformation transformation =
//                        new RoundedCornersTransformation(ConvertUtils.dp2px(8), 0);
//
//                RequestOptions options = new RequestOptions()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .transform(new CenterCrop(), transformation);
//
//                Glide.with(activity)
//                        .asDrawable()
//                        .apply(options)
//                        .load(iconDrawable)
//                        .into((ImageView) iconView);
//            }
//        }
//
////        // 大背景
////        MediaContent mediaContent = nativeAd.getMediaContent();
////        if (mediaView != null && mediaContent != null) {
////            mediaView.setMediaContent(mediaContent);
////        }
//
//        // This method tells the Google Mobile Ads SDK that you have finished populating your
//        // native ad view with this native ad.
//        adView.setNativeAd(nativeAd);
//    }

    private boolean showBan(Activity activity, @Nullable ViewGroup container) {
        if (container == null) {
            return false;
        }

        if (isNotReady()) {
            return false;
        }

        if (listener != null) {
            listener.onAdLoadBefore(this);
        }

        AdView adView = new AdView(activity);

        adView.setAdUnitId(adCategoryBean.getId());

        adView.setAdListener(new com.google.android.gms.ads.AdListener() {

            @Override
            public void onAdClicked() { // 记录了广告获得的点击后，系统会调用 onAdClicked() 方法。
                Logger.e(TAG, "--> onAdClicked()");
            }

            @Override
            public void onAdClosed() { // 用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备。
                Logger.e(TAG, "--> onAdClosed()");
                adStatus = AdvertState.DISMISS;
                if (listener != null) {
                    listener.onAdDismiss(AdvertResource.this);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // onAdFailedToLoad() 是唯一包含参数的方法。LoadAdError 类型的错误参数描述了发生的错误。
                Logger.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);

                adStatus = AdvertState.LOAD_FAILED;

                String domain = loadAdError.getDomain();
                int code = loadAdError.getCode();
                String message = loadAdError.getMessage();
                String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);

                if (listener != null) {
                    listener.onAdLoadFailed(AdvertResource.this, code, errorMsg);
                }
            }

            @Override
            public void onAdImpression() { // 记录了广告获得的展示后，系统会调用 onAdImpression() 方法。
                Logger.e(TAG, "--> onAdImpression()");
                adStatus = AdvertState.SHOW;
                if (listener != null) {
                    listener.onAdShow(AdvertResource.this);
                }
            }

            public void onAdLoaded() { // 广告加载完成后，系统会执行 onAdLoaded() 方法。例如，如果您想将为 Activity 或 Fragment 添加 AdView 的操作推迟到您确定广告会加载时再执行，就可以通过此方法做到。
                Logger.e(TAG, "--> onAdLoaded()");

                adStatus = AdvertState.LOAD_SUCCESS;
                timeLoaded = SystemClock.elapsedRealtime();

                if (listener != null) {
                    listener.onAdLoadSuccess(AdvertResource.this);
                }
            }

            public void onAdOpened() { // 广告打开覆盖屏幕的叠加层时，系统会调用 onAdOpened() 方法。
                Logger.e(TAG, "--> onAdOpened()");
            }

        });

        container.removeAllViews();
        container.addView(adView);

        int adWidth = ConvertUtils.px2dp(container.getLayoutParams().width);
        if (adWidth <= 0) {
            adWidth = 339; // dp
        }
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        Logger.e(TAG, "--> showBan() adWidth=" + adWidth + " adSize=" + adSize);
        adView.setAdSize(adSize); // 自适应 banner

        adStatus = AdvertState.LOADING;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        ad = (T) adView;
        return true;
    }
}
