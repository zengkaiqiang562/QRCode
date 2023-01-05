package com.cozs.qrcode.module.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.cozs.qrcode.module.activity.BaseActivity;


public class ActivityUtils {
    /**
     * @return context 无效返回 false，有效返回 true
     */
    public static boolean checkContext(Context context) {
        if (context == null) {
            return false;
        }
        return !(context instanceof Activity) || checkActivity((Activity) context);
    }

    // 根据网址跳转第三方浏览器
    public static void startUrlViewer(@NonNull BaseActivity activity, @NonNull String url) {
        Intent intent = new Intent();
        // 设置意图动作为打开浏览器
        intent.setAction(Intent.ACTION_VIEW);
        // 声明一个Uri
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        if (intent.resolveActivity(activity.getPackageManager()) != null && activity.enableGoto()) {
            activity.startActivity(intent);
        }
    }

    /**
     * 跳转到电子邮箱，给 email 发邮箱
     */
    public static void startThirdEmail(@NonNull BaseActivity activity, @NonNull String email) {
        Uri uri = Uri.parse("mailto:" + email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        // 设置对方邮件地址
        intent.putExtra(Intent.EXTRA_EMAIL, email);
//        // 设置标题内容
//        intent.putExtra(Intent.EXTRA_SUBJECT, title);
//        // 设置邮件文本内容
//        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (intent.resolveActivity(activity.getPackageManager()) != null && activity.enableGoto()) {
            activity.startActivity(Intent.createChooser(intent, "Select Email"));
        }
    }


    /**
     * 跳转到应用商店中的 pkgName 对应的 app 详情页
     */
    public static void startPlayStore(@NonNull BaseActivity activity, @NonNull String pkgName) {
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(activity.getPackageManager()) != null && activity.enableGoto()) {
            activity.startActivity(intent);
        }
    }


    /**
     * 分享网页
     */
    public static void startAppShare(@NonNull BaseActivity activity, @NonNull String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        if (stringCheck(className) && stringCheck(packageName)) {
//            ComponentName componentName = new ComponentName(packageName, className);
//            intent.setComponent(componentName);
//        } else if (stringCheck(packageName)) {
//            intent.setPackage(packageName);
//        }


//            intent.putExtra(Intent.EXTRA_TEXT, content);
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        intent.putExtra(Intent.EXTRA_TEXT, uri.toString());



//        if (null != title && !TextUtils.isEmpty(title)) {
//            intent.putExtra(Intent.EXTRA_TITLE, title);
//        }
//        if (null != subject && !TextUtils.isEmpty(subject)) {
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        }
//        intent.putExtra(Intent.EXTRA_TITLE, title);
        if (intent.resolveActivity(activity.getPackageManager()) != null && activity.enableGoto()) {
            Intent chooserIntent = Intent.createChooser(intent, "Share to: ");
            activity.startActivity(chooserIntent);
        }
    }

    /**
     * @return activity 无效返回 false，有效返回 true
     */
    private static boolean checkActivity(Activity activity) {
        if (activity == null) {
            return false;
        }

        return !activity.isDestroyed() && !activity.isFinishing();
    }
}
