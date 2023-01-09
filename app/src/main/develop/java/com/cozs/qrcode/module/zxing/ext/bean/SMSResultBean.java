package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.SMSParsedResult;

import java.util.Arrays;

public class SMSResultBean extends ResultBean<SMSParsedResult> {

    private String[] numbers;
    private String[] vias;
    private String subject;
    private String body;

    public SMSResultBean() {}

    public SMSResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
        buildField((SMSParsedResult) parsedResult);
    }

    @Override
    public void buildField(@NonNull SMSParsedResult parsedResult) {
        numbers = parsedResult.getNumbers();
        vias = parsedResult.getVias();
        subject = parsedResult.getSubject();
        body = parsedResult.getBody();
    }

    @Override
    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        if (numbers == null || numbers.length == 0) {
            return null;
        }
        return "smsto:" + numbers[0] + ":" + body;
    }

    public String[] getNumbers() {
        return numbers;
    }

    public String[] getVias() {
        return vias;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setNumbers(String... numbers) {
        this.numbers = numbers;
    }

    public void setVias(String... vias) {
        this.vias = vias;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "SMSResultBean{" +
                "numbers=" + Arrays.toString(numbers) +
                ", vias=" + Arrays.toString(vias) +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", barcodeFormat=" + barcodeFormat +
                ", parsedResultType=" + parsedResultType +
                ", createTime=" + createTime +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
