package com.cozs.qrcode.module.net;

import androidx.annotation.NonNull;

import com.cozs.qrcode.module.library.CryptoGuard;
import com.cozs.qrcode.module.library.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class NetInterceptor implements Interceptor {

    private static final String TAG = "NetInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        boolean encrypt = Boolean.parseBoolean(request.header("encrypt"));
        Logger.e(TAG, "--> intercept()  encrypt=" + encrypt);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        BufferedSink buffer = Okio.buffer(Okio.sink(bao));

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            requestBody.writeTo(buffer);
            buffer.flush();
        }

        String originRequest = bao.toString();

        Logger.e(TAG, "HTTP Request url=" + request.url() + "  body=" + originRequest);

        byte[] requestBytes;
        if (encrypt) {
            requestBytes = CryptoGuard.nativeEncrypt(originRequest);
        } else {
            requestBytes = originRequest.getBytes(StandardCharsets.UTF_8);
        }

        RequestBody newBody = FormBody.create(MediaType.parse("application/json"), requestBytes);

        Request newRequest = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .post(newBody)
                .build();

        Response response = chain.proceed(newRequest);

        ResponseBody responseBody = response.body();
        Response.Builder responseBuilder = response.newBuilder().request(newRequest);
        if (responseBody != null) {
            byte[] responseBytes = responseBody.bytes();

//            Slog.e(TAG, "--> SecureInterceptor  responseBytes.size=" + responseBytes.length);

            byte[] dstResponseBytes;
            if (encrypt) {
                dstResponseBytes = CryptoGuard.nativeDecrypt(responseBytes);
            } else {
                dstResponseBytes = responseBytes;
            }

//            Slog.e(TAG, "--> SecureInterceptor  dstResponseBytes.len=" + dstResponseBytes.length); // java: dstResponseBytes.len=10954

            String strResponseBody = new String(dstResponseBytes, StandardCharsets.UTF_8);
            Logger.e(TAG, "HTTP Response url=" + newRequest.url() + "  body=" + strResponseBody);

            Source newSource = Okio.source(new ByteArrayInputStream(dstResponseBytes));

            String contentType = response.header("Content-Type");
            long contentLength = dstResponseBytes.length;
            responseBuilder.body(new RealResponseBody(contentType, contentLength, Okio.buffer(newSource)));
        }

        return responseBuilder.build();
    }
}
