package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

public class WifiResultBean extends ResultBean {

    private static final String TAG = "WifiResultBean";

    private String ssid; // wifi 名称
    private String networkEncryption; // 类型：WEP、WPA、nopass
    private String password; // wifi 密码
    private boolean hidden;
    private String identity;
    private String anonymousIdentity;
    private String eapMethod;
    private String phase2Method;

    public WifiResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull WifiParsedResult parsedResult) {
        ssid = parsedResult.getSsid();
        networkEncryption = parsedResult.getNetworkEncryption();
        password = parsedResult.getPassword();
        hidden = parsedResult.isHidden();
        identity = parsedResult.getIdentity();
        anonymousIdentity = parsedResult.getIdentity();
        eapMethod = parsedResult.getEapMethod();
        phase2Method = parsedResult.getPhase2Method();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }

        String ssid = getSsidField();
        String password = getPasswordField();
        // Build the output with obtained data.
        return getWifiString(ssid, password, null, false);
    }

    public String getSsid() {
        return ssid;
    }

    public String getNetworkEncryption() {
        return networkEncryption;
    }

    public String getPassword() {
        return password;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getIdentity() {
        return identity;
    }

    public String getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public String getEapMethod() {
        return eapMethod;
    }

    public String getPhase2Method() {
        return phase2Method;
    }


    private static String getWifiString(String ssid, String password, String type, boolean hidden) {
        StringBuilder output = new StringBuilder(100);
        output.append("WIFI:");
        output.append("S:").append(ssid).append(';');
        if (type != null && !type.isEmpty() && !"nopass".equals(type)) {
            maybeAppend(output, "T:", type);
        }
        maybeAppend(output, "P:", password);
        if (hidden) {
            maybeAppend(output, "H:", "true");
        }
        output.append(';');
        return output.toString();
    }

    private static void maybeAppend(StringBuilder output, String prefix, String value) {
        if (value != null && !value.isEmpty()) {
            output.append(prefix).append(value).append(';');
        }
    }

    private String getSsidField() {
        if (ssid.isEmpty()) {
            Log.e(TAG, "SSID must be at least 1 character.");
        }
        String parsed = parseTextField("SSID", ssid);
        return quoteHex(parsed); // Android needs hex-like SSIDs quoted or will be read as hex
    }

    private String getPasswordField() {
        return parseTextField("Password", password);
    }

    private static String parseTextField(String name, String input) {
        if (input.isEmpty()) {
            return "";
        }
        if (input.contains("\n")) {
            Log.e(TAG, name + " field must not contain \\n characters.");
        }
        return input.replaceAll("([\\\\:;])", "\\\\$1");
    }

    private static String quoteHex(String value) {
        if (value != null && value.matches("[0-9A-Fa-f]+")) {
            if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                return value;
            }
            return '\"' + value + '\"';
        }
        return value;
    }
}
