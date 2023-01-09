package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public class AddressBookResultBean extends ResultBean {

    private String name;
    private String company;
//    private final String title;
    private String tel;
//    private final String url;
    private String email;
    private String address;
//    private final String address2;
    private String notes; // memo

    public AddressBookResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull AddressBookParsedResult parsedResult) {
        String[] names = parsedResult.getNames();
        String org = parsedResult.getOrg(); // company
        String[] phoneNumbers = parsedResult.getPhoneNumbers();
        String[] emails = parsedResult.getEmails();
        String[] addresses = parsedResult.getAddresses();
        String note = parsedResult.getNote();

        name = names == null ? "" : names[0];
        company = org == null ? "" : org;
        tel = phoneNumbers == null ? "" : phoneNumbers[0];
        email = emails == null ? "" : emails[0];
        address = addresses == null ? "" : addresses[0];
        notes = note == null ? "" : note;
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        return getMeCard(name, company, null, tel, null, email, address, null, notes);
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getNotes() {
        return notes;
    }

    private static String getMeCard(String name,
                                    String company,
                                    String title,
                                    String tel,
                                    String url,
                                    String email,
                                    String address,
                                    String address2,
                                    String memo) {
        StringBuilder output = new StringBuilder(100);
        output.append("MECARD:");
        maybeAppendMECARD(output, "N", name.replace(",", ""));
        maybeAppendMECARD(output, "ORG", company);
        maybeAppendMECARD(output, "TEL", tel == null ? null : tel.replaceAll("[^0-9+]+", ""));
        maybeAppendMECARD(output, "URL", url);
        maybeAppendMECARD(output, "EMAIL", email);
        maybeAppendMECARD(output, "ADR", buildAddress(address, address2));
        StringBuilder memoContents = new StringBuilder();
        if (memo != null) {
            memoContents.append(memo);
        }
        if (title != null) {
            if (memoContents.length() > 0) {
                memoContents.append('\n');
            }
            memoContents.append(title);
        }
        maybeAppendMECARD(output, "NOTE", memoContents.toString());
        output.append(';');
        return output.toString();
    }

    private static String buildAddress(String address, String address2) {
        if (!address.isEmpty()) {
            if (!address2.isEmpty()) {
                return address + ' ' + address2;
            }
            return address;
        }
        if (!address2.isEmpty()) {
            return address2;
        }
        return "";
    }

    private static void maybeAppendMECARD(StringBuilder output, String prefix, String value) {
        if (value != null && !value.isEmpty()) {
            value = value.replaceAll("([\\\\:;])", "\\\\$1");
            value = value.replaceAll("\\n", "");
            output.append(prefix).append(':').append(value).append(';');
        }
    }
}
