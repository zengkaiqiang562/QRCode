package com.cozs.qrcode.module.zxing.ext.history;

import static android.content.Context.WINDOW_SERVICE;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.cozs.qrcode.module.QRApplication;
import com.cozs.qrcode.module.zxing.ext.bean.ResultBean;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

@Entity(tableName = "result_history")
public class HistoryEntity {
    @Ignore
    private static final String TAG = "HistoryEntity";
    @Ignore
    private static final int WHITE = 0xFFFFFFFF;
    @Ignore
    private static final int BLACK = 0xFF000000;

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

    // 生成二维码图片
    public Bitmap encodeAsBitmap() throws WriterException {
        String contentsToEncode = rawText;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        BitMatrix result;
        try {
            BarcodeFormat format = BarcodeFormat.valueOf(this.barcodeFormat);
            int[] sizes = createSize(format == BarcodeFormat.QR_CODE);
            result = new MultiFormatWriter().encode(contentsToEncode, format, sizes[0], sizes[1], hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            Log.e(TAG, "--> encodeAsBitmap()  IllegalArgumentException=" + iae);
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    /**
     * @param qrcode 是否是二维码
     * @return arr[0] width; arr[1] height
     */
    private int[] createSize(boolean qrcode) {
        WindowManager manager = (WindowManager) QRApplication.getContext().getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        int smallerDimension = Math.min(width, height);
        int reuslt = smallerDimension * 7 / 8;

        if (qrcode) {
            return new int[]{reuslt, reuslt};
        } else {
            return new int[]{reuslt, (int) (reuslt * 0.26f)};
        }
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
