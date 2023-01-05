package com.cozs.qrcode.module.constant;

import com.cozs.qrcode.BuildConfig;

public interface Constants {

    String BASE_URL = BuildConfig.BASE_URL;
    String PATH_CONFIG = BuildConfig.PATH_CONFIG;
    String PATH_LOCAL = "http://ip-api.com/json";

    String HTTP_PRIVACY_POLICY = BuildConfig.HTTP_PRIVACY_POLICY;
    String HTTP_TERMS_OF_SERVICE = BuildConfig.HTTP_TERMS_OF_SERVICE;

    long TIME_UPDATE_APP_CONFIG = 60; // 60 min
    int DEFAULT_MAX_LAUNCH_DURATION = 10; // s
    int DEFAULT_MIN_LAUNCH_DURATION = 3; // 3s

    String SPREF_LAUNCH_AT_FIRST = "launch_at_first";
    String SPREF_TIME_LAUNCH_AT_FIRST = "time_launch_at_first";
    String SPREF_AGREE_PRIVACY_POLICY = "agree_privacy_policy";
    String SPREF_APP_CONFIG_CACHE = "app_config_cache";

    String PARAM_TYPE = "_type";
}
