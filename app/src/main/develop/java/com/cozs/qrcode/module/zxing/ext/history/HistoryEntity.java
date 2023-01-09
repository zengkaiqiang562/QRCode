package com.cozs.qrcode.module.zxing.ext.history;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.cozs.qrcode.module.zxing.ext.bean.ResultBean;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

@Entity(tableName = "result_history")
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    public int id;

    public String barcodeFormat;
    public String parsedResultType;
    public long createTime;
    public String rawText;
    public String display;
    public boolean favorite;

    @Ignore
    private Result result;

    @Ignore
    private ParsedResult parsedResult;

    // Room 使用
    public HistoryEntity(int id,
                         String barcodeFormat,
                         String parsedResultType,
                         long createTime,
                         String rawText,
                         String display,
                         boolean favorite) {
        this.id = id;
        this.barcodeFormat = barcodeFormat;
        this.parsedResultType = parsedResultType;
        this.createTime = createTime;
        this.rawText = rawText;
        this.display = display;
        this.favorite = favorite;
    }

    // 扫描二维码使用
    @Ignore
    public HistoryEntity(String barcodeFormat,
                         String parsedResultType,
                         long createTime,
                         String rawText,
                         String display) {
        this.barcodeFormat = barcodeFormat;
        this.createTime = createTime;
        this.rawText = rawText;
        this.parsedResultType = parsedResultType;
        this.display = display;
    }

    // 扫描二维码使用
    @Ignore
    public HistoryEntity(@NonNull Result result, @NonNull ParsedResult pResult) {
        this.barcodeFormat = result.getBarcodeFormat().name();
        this.createTime = result.getTimestamp();
        this.rawText = result.getText();
        this.parsedResultType = pResult.getType().name();
        this.display = pResult.getDisplayResult();
    }

    // 创建二维码使用
    @Ignore
    public HistoryEntity(ResultBean<?> resultBean) {
        this.barcodeFormat = resultBean.getBarcodeFormat().name();
        this.parsedResultType = resultBean.getParsedResultType().name();
        this.createTime = resultBean.getCreateTime();
        this.rawText = resultBean.getRawText();
        this.display = resultBean.getDisplay();
    }

    public Result getResult() {
        if (result == null) {
            result = new Result(rawText, null, null, BarcodeFormat.valueOf(barcodeFormat), createTime);
        }
        return result;
    }

    public ParsedResult getParsedResult() {
        if (parsedResult == null) {
            ResultParser.parseResult(getResult());
        }
        return parsedResult;
    }

    public ResultBean<?> convert2ResultBean() {
        return ResultBean.create(getResult(), getParsedResult());
    }

    @Override
    public String toString() {
        return "HistoryEntity{" +
                "id=" + id +
                ", barcodeFormat='" + barcodeFormat + '\'' +
                ", parsedResultType='" + parsedResultType + '\'' +
                ", createTime=" + createTime +
                ", favorite=" + favorite +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
