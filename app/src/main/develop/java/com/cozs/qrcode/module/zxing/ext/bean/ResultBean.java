package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cozs.qrcode.module.zxing.ext.history.HistoryEntity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.ProductResultParser;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

public abstract class ResultBean<T extends ParsedResult> {
    protected BarcodeFormat barcodeFormat;
    protected ParsedResultType parsedResultType;
    protected long createTime;
    protected String rawText;
    protected String display;

    // 创建二维码时使用
    public ResultBean() {}

    // 生成二维码时使用
    public ResultBean(Result result, ParsedResult parsedResult) {
        this.barcodeFormat = result.getBarcodeFormat();
        this.createTime = result.getTimestamp();
        this.rawText = result.getText();
        this.parsedResultType = parsedResult.getType();
        this.display = parsedResult.getDisplayResult();
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    public ParsedResultType getParsedResultType() {
        return parsedResultType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getRawText() {
        return rawText;
    }

    public String getDisplay() {
        return display;
    }

    public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
    }

    public void setParsedResultType(ParsedResultType parsedResultType) {
        this.parsedResultType = parsedResultType;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    /*=======================================================================================*/

    public abstract void buildField(T parsedResult);

    public abstract String formatText();

    public HistoryEntity convert2HistoryEntity() {
        return new HistoryEntity(this);
    }

    public static ResultBean<?> create(@NonNull Result result, @NonNull ParsedResult parseResult) {
        if (parseResult instanceof AddressBookParsedResult) {
            return new AddressBookResultBean(result, parseResult);
        }

        if (parseResult instanceof CalendarParsedResult) {
            return new CalendarResultBean(result, parseResult);
        }

        if (parseResult instanceof EmailAddressParsedResult) {
            return new EmailAddressResultBean(result, parseResult);
        }

        if (parseResult instanceof GeoParsedResult) {
            return new GeoResultBean(result, parseResult);
        }

        if (parseResult instanceof ISBNParsedResult) {
            return new ISBNResultBean(result, parseResult);
        }

        if (parseResult instanceof ProductParsedResult) {
            return new ProductResultBean(result, parseResult);
        }

        if (parseResult instanceof ExpandedProductParsedResult) {
            return new ProductExpandResultBean(result, parseResult);
        }

        if (parseResult instanceof SMSParsedResult) {
            return new SMSResultBean(result, parseResult);
        }

        if (parseResult instanceof TelParsedResult) {
            return new TelResultBean(result, parseResult);
        }

        if (parseResult instanceof URIParsedResult) {
            return new UriResultBean(result, parseResult);
        }

        if (parseResult instanceof WifiParsedResult) {
            return new WifiResultBean(result, parseResult);
        }

        if (parseResult instanceof TextParsedResult) {
            return new TelResultBean(result, parseResult);
        }

        return new TextResultBean(result, parseResult);
    }
}
