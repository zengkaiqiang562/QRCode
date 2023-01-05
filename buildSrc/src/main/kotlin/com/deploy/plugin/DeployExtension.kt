package com.deploy.plugin

open class DeployExtension {
    
    val versionName = "1.0.0"
    val versionCode = 1

    val signPwd = "qrcode_debug"  // TODO 在打包服务器上替换为正式签名密钥
    val signPath = "sign/debug/qrcode_debug.jks"  // TODO 在打包服务器上替换为正式签名路径

    val debug = true // TODO 正式环境改为 false
    val enableDevPkgName = true // TODO 正式环境改为 false
    val enableLog = true // TODO 正式环境改为 false
    val enableUploadMappingFile = false // TODO 正式环境改为 true

    val devPkgName = "com.cozs.qrcode"
    val dstPkgName = "com.conezeroseven.qrcode"  // TODO 在打包服务器上替换为正式包名
    val pkgName = if (enableDevPkgName) devPkgName else dstPkgName

    val devPkgPath = "com/cozs/qrcode"
    val dstPkgPath = "com/conezeroseven/qrcode"  // TODO 在打包服务器上替换为正式包名路径
//    val pkgPath = if (debug) devPkgPath else dstPkgPath

    val mainDevelopDir = "src/main/develop"
    val mainProductDir = "src/main/product"
    private val mainDir = if (enableDevPkgName) mainDevelopDir else mainProductDir

    val assetsPath = "${mainDir}/assets"
    val javaPath = "${mainDir}/java"
    val aidlPath = "${mainDir}/aidl"
    val resPath = "${mainDir}/res"
    val manifestPath = "${mainDir}/AndroidManifest.xml"
    val cmakePath = "${mainDir}/cpp/CMakeLists.txt"
    val proguardPath = "${mainDir}/proguard-rules.pro"

    val fbAppId = "222222222222222" // TODO 需要替换为正式环境的
    val fbToken = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" // TODO 需要替换为正式环境的

    val adjustToken = "{YourAppToken}" // TODO 需要替换为正式环境的

    //<!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
    //<!-- 注意：实际开发时要替换成自己的 App Id -->
    val admobAppId = "ca-app-pub-3940256099942544~3347511713" // TODO 需要替换为正式环境的


    val urlPrivacyPolicy = "" // TODO
    val urlTermsOfService = "" // TODO

    val baseUrl = "http://m.2ksj7.top/" // TODO 需要替换为正式环境的
    val pathConfig = "/uhxY/KjPRdInW/0" // TODO 需要替换为正式环境的
}