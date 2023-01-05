package com.cozs.qrcode.module.net;

import com.cozs.qrcode.module.bean.ConfigInfoBean;
import com.cozs.qrcode.module.bean.LocalInfoBean;
import com.cozs.qrcode.module.bean.RequestBean;
import com.cozs.qrcode.module.bean.ResponseBean;
import com.cozs.qrcode.module.constant.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NetApi {
    @POST(Constants.PATH_CONFIG)
    @Headers("encrypt: true")
    Call<ResponseBean<ConfigInfoBean>> requestConfigInfo(@Body RequestBean param);

    @POST(Constants.PATH_LOCAL)
    @Headers("encrypt: false")
    Call<LocalInfoBean> requestLocalInfo();
}
