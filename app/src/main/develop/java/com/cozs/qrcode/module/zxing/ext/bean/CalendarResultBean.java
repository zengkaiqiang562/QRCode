package com.cozs.qrcode.module.zxing.ext.bean;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.TimeUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

import java.util.Date;

public class CalendarResultBean extends ResultBean {

    private static final String TAG = "CalendarResultBean";

    private String summary; // title
    private long start;
    private boolean startAllDay; // start 日期为全天，即不需要提供时分秒
    private long end;
    private boolean endAllDay;
    private String location;
    private String description;

    public CalendarResultBean(Result result, ParsedResult parsedResult) {
        super(result, parsedResult);
    }

    public void buildField(@NonNull CalendarParsedResult parsedResult) {
        summary = parsedResult.getSummary();
        start = parsedResult.getStartTimestamp();
        startAllDay = parsedResult.isStartAllDay();
        end = parsedResult.getEndTimestamp();
        endAllDay = parsedResult.isEndAllDay();
        location = parsedResult.getLocation();
        description = parsedResult.getDescription();
    }

    public String formatText() {
        if (!TextUtils.isEmpty(rawText)) {
            return rawText;
        }
        String eventName = getEventNameField();
        String dates = getDateTimeFields();
        String location = getLocationField();
        String description = getDescriptionField();
        return "BEGIN:VEVENT\r\n" + eventName + dates + location + description + "END:VEVENT\r\n";
    }

    public String getSummary() {
        return summary;
    }

    public long getStart() {
        return start;
    }

    public boolean isStartAllDay() {
        return startAllDay;
    }

    public long getEnd() {
        return end;
    }

    public boolean isEndAllDay() {
        return endAllDay;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    private String getEventNameField() {
        String inputName = summary;
        if (inputName.isEmpty()) {
            Log.e(TAG, "Event name must be at least 1 character.");
        }
        if (inputName.contains("\n")) {
            Log.e(TAG, "Event name should not contain \\n characters.");
        }
        return "SUMMARY:" + inputName + "\r\n";
    }

    private String getLocationField() {
        String locationString = location;
        if (locationString.isEmpty()) {
            return "";
        }
        if (locationString.contains("\n")) {
            Log.e(TAG, "Location should not contain \\n characters.");
        }
        return "LOCATION:" + locationString + "\r\n";
    }

    private String getDescriptionField() {
        String descriptionString = description;
        if (descriptionString.isEmpty()) {
            return "";
        }
        if (descriptionString.contains("\n")) {
            Log.e(TAG, "Description should not contain \\n characters.");
        }
        return "DESCRIPTION:" + descriptionString + "\r\n";
    }

    private String getDateTimeFields() {
        if (start > end) {
            Log.e(TAG, "Ending date/time cannot be before starting date/time.");
        }

        String dstStart;
        if (startAllDay) {
            dstStart = getDateFormatWithFullDay(start, false);
        } else {
            dstStart = getDateFormatWithoutFullDay(start, false);
        }

        String dstEnd;
        if (endAllDay) {
            dstEnd = getDateFormatWithFullDay(end, true);
        } else {
            dstEnd = getDateFormatWithoutFullDay(end, true);
        }

        return dstStart + dstEnd;
    }

    private String getDateFormatWithFullDay(long time, boolean end) {
        if (end) { // 结束日期
            // Specify end date as +1 day since it's exclusive
            time = time + 24 * 60 * 60 * 1000;
            return "DTEND;VALUE=DATE:" + TimeUtils.millis2String(time, "yyyyMMdd") + "\r\n";
        } else {
            return "DTSTART;VALUE=DATE:" + TimeUtils.millis2String(time, "yyyyMMdd") + "\r\n";
        }
    }

    private String getDateFormatWithoutFullDay(long time, boolean end) {
        if (end) {
            return "DTEND:" + TimeUtils.millis2String(time, "yyyyMMdd'T'HHmmss'Z'") + "\r\n";
        } else {
            return "DTSTART:" + TimeUtils.millis2String(time, "yyyyMMdd'T'HHmmss'Z'") + "\r\n";
        }
    }

    private static Date mergeDateAndTime(Date date, Date time) {
        String d = TimeUtils.date2String(date, "yyyyMMdd");
        String t = TimeUtils.date2String(time, "HHmm") + "00";
        return TimeUtils.string2Date(d + t, "yyyyMMddHHmmss");
    }

    private static Date addMilliseconds(Date time1, long diffTimeZone) { // GMT，diffTimeZone is 0
        return new Date(time1.getTime() + diffTimeZone);
    }
}
