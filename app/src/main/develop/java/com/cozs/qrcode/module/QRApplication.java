package com.cozs.qrcode.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cozs.qrcode.module.activity.GuideActivity;
import com.cozs.qrcode.module.activity.MainActivity;
import com.cozs.qrcode.module.advert.AdvertManager;
import com.cozs.qrcode.module.library.ActivityStackManager;
import com.cozs.qrcode.module.library.ConfigLibrary;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.library.LocalLibrary;
import com.cozs.qrcode.module.library.Logger;
import com.cozs.qrcode.module.library.SdkLibrary;

public class QRApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "QRApplication";

    private static Application application;
    private static Context context;

    private static boolean hotLaunch = false;
    private int counterOfHotLaunch = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        application = this;
        boolean mainProcess = ProcessUtils.isMainProcess();
        Logger.e(TAG, "================ QRApplication onCreate()  mainProcess=" + mainProcess + " ================");

        if (mainProcess) {
            registerActivityLifecycleCallbacks(this);
            boolean launchAtFirst = (boolean) SPUtils.getInstance().getBoolean(Constants.SPREF_LAUNCH_AT_FIRST, true);
            if (launchAtFirst) {
                SPUtils.getInstance().put(Constants.SPREF_LAUNCH_AT_FIRST, false);

                // record 1st launch time as the time of install app
                long timeLaunchAtFirst = System.currentTimeMillis();
                SPUtils.getInstance().put(Constants.SPREF_TIME_LAUNCH_AT_FIRST, timeLaunchAtFirst);
            }

            Logger.e(TAG, "--> launchAtFirst=" + launchAtFirst);

            SdkLibrary.initAdjust(this);
            SdkLibrary.initFirebase(this);
            SdkLibrary.initFacebook(this);
            SdkLibrary.initAdmob(this);

            ConfigLibrary.getInstance().updateAppConfig(true);
            LocalLibrary.getInstance().requestLocalInfo();

            new Thread(() -> {
                SdkLibrary.retrieveAdmobId(QRApplication.this);
            }).start();


        }
    }

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return context;
    }

    public static boolean isHotLaunch() {
        return hotLaunch;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Logger.e(TAG, "--> onActivityCreated()  activity=" + activity);
        AdvertManager.addAdActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Logger.e(TAG, "--> onActivityStarted()  activity=" + activity);
        if (++counterOfHotLaunch == 1 && hotLaunch) { // 热启动
            Logger.e(TAG, "--> FlashApp Foreground");
            Activity mainActivity = ActivityStackManager.getInstance().peekActivity(MainActivity.class);
            if (mainActivity != null) {
                mainActivity.startActivity(new Intent(mainActivity, GuideActivity.class));
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Logger.e(TAG, "--> onActivityResumed()  activity=" + activity);
        Adjust.onResume();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Logger.e(TAG, "--> onActivityPaused()  activity=" + activity);
        Adjust.onPause();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Logger.e(TAG, "--> onActivityStopped()  activity=" + activity);
        if (--counterOfHotLaunch <= 0) {
            Logger.e(TAG, "--> FlashApp background");
            counterOfHotLaunch = 0;
            hotLaunch = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Logger.e(TAG, "--> onActivityDestroyed()  activity=" + activity);
        AdvertManager.removeAdActivity(activity);
    }
}
