package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CollectionUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class EmailAddressResultBean extends ResultBean {

    private static final String TAG = "EmailAddressResultBean";

    private static final Pattern AMPERSAND = Pattern.compile("&");
    private static final Pattern EQUALS = Pattern.compile("=");

    private String[] tos; // 发送
    private String[] ccs; // 抄送
    private String subject; // 标题
    private String body; // 内容

    public EmailAddressResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull EmailAddressParsedResult parsedResult) {
        tos = parsedResult.getTos();
        ccs = parsedResult.getCCs();
        subject = parsedResult.getSubject();
        body = parsedResult.getBody();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        String tosFormat;
        StringBuilder sbTos = new StringBuilder();
        if (tos != null && tos.length > 0) {
            for(int i = 0; i < tos.length; i++) {
                if (i != 0) {
                    sbTos.append(",");
                }
                sbTos.append(tos[i]);
            }
        }
        tosFormat = urlEncode(sbTos.toString());

        String ccsFormat;
        StringBuilder sbCCs = new StringBuilder();
        if (ccs != null && ccs.length > 0) {
            for(int i = 0; i < ccs.length; i++) {
                if (i != 0) {
                    sbCCs.append(",");
                }
                sbCCs.append(ccs[i]);
            }
        }
        ccsFormat = "cc=" + urlEncode(sbCCs.toString());

        String subJectFormat = "subject=" + urlEncode(subject);
        String bodyFormat = "body=" + urlEncode(body);

        return "mailto:" + tosFormat + "?"
                + ccsFormat + "&"
                + subJectFormat + "&"
                + bodyFormat;
    }

    public String[] getTos() {
        return tos;
    }

    public String[] getCcs() {
        return ccs;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    private static String urlEncode(String raw) {
        try {
            return URLEncoder.encode(raw, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            Log.e(TAG, "--> urlEncode() UnsupportedEncodingException=" + uee);
            return raw;
        }
    }
}
