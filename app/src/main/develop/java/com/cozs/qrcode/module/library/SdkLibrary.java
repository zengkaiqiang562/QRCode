package com.cozs.qrcode.module.library;

import android.app.Application;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.cozs.qrcode.BuildConfig;
import com.cozs.qrcode.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Map;

public class SdkLibrary {

    private static final String TAG = "SdkLibrary";

    private static String admobId;

    private static FirebaseAnalytics analytics;
    private static FirebaseCrashlytics crashlytics;

    public static void initAdmob(Application application) {
        MobileAds.initialize(application, initializationStatus -> {
//            for (Map.Entry<String, AdapterStatus> entry : initializationStatus.getAdapterStatusMap().entrySet()) {
//                Logger.e(TAG, "--> initAdmob() onInitializationComplete  entry.key=" + entry.getKey()
//                        + "  entry.AdapterStatus.desc=" + entry.getValue().getDescription()
//                        + "  entry.AdapterStatus.state=" + entry.getValue().getInitializationState());
//            }
        });
    }

    public static void initFirebase(Application application) {
        // Analytics
        getAnalytics(application).setAnalyticsCollectionEnabled(true);
        // Crashlytics: release 传 true，debug 传 false
        getCrashlytics().setCrashlyticsCollectionEnabled(!BuildConfig.CLEAN_DEBUG);
    }

    public static void initFacebook(Application application) {
        String fbAppID = application.getResources().getString(R.string.fb_appid);
        String fbToken = application.getResources().getString(R.string.fb_token);
        Logger.e(TAG, "--> initFacebook()  fbAppID=" + fbAppID + "  fbToken=" + fbToken);
        FacebookSdk.setApplicationId(fbAppID); // app id（跟 AndroidManifest.xml 中配置的一样）
        FacebookSdk.setClientToken(fbToken); // token
        FacebookSdk.sdkInitialize(application);
        AppEventsLogger.activateApp(application);
        FacebookSdk.setAutoLogAppEventsEnabled(true);
    }

    public static void initAdjust(Application application) {
        String adjustToken = application.getResources().getString(R.string.adjust_token);
        Logger.e(TAG, "--> initAdjust()  adjustToken=" + adjustToken);
        /*
        Debug 时 environment 设置为 AdjustConfig.ENVIRONMENT_SANDBOX 。
        Release 时 environment 设置为 AdjustConfig.ENVIRONMENT_PRODUCTION。
         */
        String environment = BuildConfig.CLEAN_DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(application, adjustToken, environment);
        config.setLogLevel(BuildConfig.CLEAN_DEBUG ? LogLevel.WARN : LogLevel.SUPRESS);
        Adjust.onCreate(config);
    }

    public static void retrieveAdmobId(Application application) {
        try {
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(application);
            admobId = adInfo.getId();
            Logger.e(TAG, "retrieveAdmobId() -->  admobId=" + admobId);
        } catch (Exception e) {
            Logger.e(TAG, "retrieveAdmobId() -->  Exception=" + e);
        }
    }

    public static String getAdmobId() {
        return TextUtils.isEmpty(admobId) ? "" : admobId;
    }

    public static void traceEvent(String event, Map<String, Object> params, boolean unique) {
//        traceEventByFirebase(event, params, unique);
        traceEventByAdjust(event, unique);
    }


    // 埋点时需要用到 FirebaseAnalytics 实例，所以对外提供 FirebaseAnalytics 对象
    private static FirebaseAnalytics getAnalytics(Application application) {
        if (analytics == null) {
            analytics = FirebaseAnalytics.getInstance(application);
        }
        return analytics;
    }

    private static FirebaseCrashlytics getCrashlytics() {
        if (crashlytics == null) {
            crashlytics = FirebaseCrashlytics.getInstance();
        }
        return crashlytics;
    }

//    private static void traceEventByFirebase(String event, Map<String, Object> params, boolean unique) {
//        Bundle bundle = null;
//        if (params != null && !params.isEmpty()) {
//            bundle = new Bundle();
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                if (value instanceof String) {
//                    bundle.putString(key, (String) value);
//                } else if (value instanceof Integer) {
//                    bundle.putInt(key, (Integer) value);
//                }
//            }
//        }
//        FirebaseAnalytics firebaseAnalytics = getAnalytics(SCApplication.getApplication());
//        firebaseAnalytics.logEvent(event, bundle);
//    }

    private static void traceEventByAdjust(String event, boolean unique) {
        AdjustEvent adjustEvent = new AdjustEvent(event);
        if (unique) {
            adjustEvent.setOrderId(event);
        }
        Adjust.trackEvent(adjustEvent);
    }
}
