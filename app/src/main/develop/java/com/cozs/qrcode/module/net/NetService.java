package com.cozs.qrcode.module.net;

import com.cozs.qrcode.module.bean.ConfigInfoBean;
import com.cozs.qrcode.module.bean.LocalInfoBean;
import com.cozs.qrcode.module.bean.RequestBean;
import com.cozs.qrcode.module.bean.ResponseBean;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.library.JsonUtils;
import com.cozs.qrcode.module.library.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetService {
    private static final String TAG = "NetService";

    private volatile static NetService instance;

    public static NetService getInstance() {
        if (instance == null) {
            synchronized (NetService.class) {
                if (instance == null) {
                    instance = new NetService();
                }
            }
        }
        return instance;
    }

    private final Retrofit retrofit;

    private NetService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //禁止重定向操作，避免出现 too many follow-up requests: 21 的异常信息
                .followRedirects(false)
                .followSslRedirects(false);

        builder.addInterceptor(new NetInterceptor());

        OkHttpClient okHttpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void requestConfigInfo(Callback<ResponseBean<ConfigInfoBean>> callback) {
        RequestBean params = new RequestBean();
        Logger.e("发起<获取全局配置>请求 ## " + JsonUtils.toJson(params));

        NetApi netApi = retrofit.create(NetApi.class);
        Call<ResponseBean<ConfigInfoBean>> call = netApi.requestConfigInfo(params);
        call.enqueue(callback);
    }

    public void requestLocalInfo(Callback<LocalInfoBean> callback) {
        NetApi netApi = retrofit.create(NetApi.class);
        Logger.e(TAG, "--> requestLocalInfo()");
        Call<LocalInfoBean> call = netApi.requestLocalInfo();
        call.enqueue(callback);
    }
}
