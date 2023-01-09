# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# app
#-keep class com.cozs.qrcode.module.library.CryptoGuard { *; }
-keep class com.cozs.qrcode.module.event.bean.** { *; }
-keep class com.cozs.qrcode.module.bean.** { *; }
-keep class com.cozs.qrcode.module.zxing.ext.bean.** { *; }
-keep class com.cozs.qrcode.module.zxing.ext.history.HistoryEntity { *; }

# lottie
-keep class com.squareup.wire.** { *; }
-keep class com.opensource.svgaplayer.proto.** { *; }

##adjust
#-keep class com.adjust.sdk.**{ *; }
#-keep class com.google.android.gms.common.ConnectionResult {
#    int SUCCESS;
#}
#-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
#    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
#}
#-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
#    java.lang.String getId();
#    boolean isLimitAdTrackingEnabled();
#}
#-keep public class com.android.installreferrer.**{ *; }