package com.cozs.qrcode.module.bean;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.library.LocalLibrary;
import com.cozs.qrcode.module.library.SdkLibrary;
import com.cozs.qrcode.BuildConfig;
import com.google.gson.annotations.SerializedName;

public class RequestBean {
    /*
    参数名（new）	类型	必选	说明	old

    productName	String	是	包名	pkg
    versionId	Integer	是	版本	ver
    aid	String	是	设备标识码	aid
    googleId	String	否	Google ID/苹果的是idfa	adid
    kitNumber	Integer	是	sdk版本	sdk
    softLanguage	String	是	语言，默认传en	lang
    national	String	否	国家	country
    current_time	Long	是	请求时间戳,毫秒	currentTime
    mobileNetWork	String	否	公共陆地移动网(取不到传空)	plmn
    areaSim	String	否	sim卡国家(取不到传空)	simCountryIos
    innerAppTime	Long	是	首次安装时间	firstOpen
     */

    @SerializedName("productName")
    protected String packageName;

    @SerializedName("versionId")
    protected Integer versionCode;

    @SerializedName("aid")
    protected String androidId;

    @SerializedName("googleId")
    protected String admobId;

    @SerializedName("kitNumber")
    protected Integer androidLevel;

    @SerializedName("softLanguage")
    protected String mobileLang;

    @SerializedName("national")
    protected String mobileCountryCode;

    @SerializedName("current_time")
    protected Long timeRequest;

    @SerializedName("mobileNetWork")
    protected String mobileNet;

    @SerializedName("areaSim")
    protected String simCountryCode;

    @SerializedName("innerAppTime")
    protected Long timeInit;

    protected String mobilePlatform;
    protected String manufacturer;
    protected String mobileModel;
    protected String mobileId;
    protected boolean isVirtualMobile;

    public RequestBean() {
        packageName = AppUtils.getAppPackageName();
        if (BuildConfig.CLEAN_DEBUG) {
            versionCode = 300; // 测试传 [101, 1000]
        } else {
            versionCode = AppUtils.getAppVersionCode();
        }
        androidId = DeviceUtils.getAndroidID();
        androidLevel = DeviceUtils.getSDKVersionCode();
        mobileLang = "en";
        timeRequest = System.currentTimeMillis();
        timeInit = SPUtils.getInstance().getLong(Constants.SPREF_TIME_LAUNCH_AT_FIRST, 0L);

        admobId = SdkLibrary.getAdmobId();
        mobileCountryCode = LocalLibrary.getInstance().getLocalCountryCode();
        simCountryCode = LocalLibrary.getInstance().getLocalCountryCodeFromSim();
        mobileNet = ""; // 拿不到传空即可
        mobileId = DeviceUtils.getUniqueDeviceId();
        mobilePlatform = "android";
        manufacturer = DeviceUtils.getManufacturer();
        mobileModel = DeviceUtils.getModel();
        isVirtualMobile = DeviceUtils.isEmulator();
    }
}
