import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Google Services plugin
    id("com.google.firebase.crashlytics") // Apply the Crashlytics Gradle plugin

    id("com.deploy.plugin") // Custom Gradle Plugin in buildSrc
}

android {
    namespace = deployExt.pkgName
    compileSdk = 32

    defaultConfig {
        applicationId = deployExt.pkgName
        minSdk = 21
        targetSdk = 32
        versionCode = deployExt.versionCode
        versionName = deployExt.versionName

        externalNativeBuild {
            ndk {
                abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            }
        }

        buildConfigField("boolean", "CLEAN_DEBUG", "${deployExt.debug}")
        buildConfigField("boolean", "LOG_ENABLE", "${deployExt.enableLog}")

        buildConfigField("String", "HTTP_PRIVACY_POLICY", "\"${deployExt.urlPrivacyPolicy}\"")
        buildConfigField("String", "HTTP_TERMS_OF_SERVICE", "\"${deployExt.urlTermsOfService}\"")

        buildConfigField("String", "BASE_URL", "\"${deployExt.baseUrl}\"")
        buildConfigField("String", "PATH_CONFIG", "\"${deployExt.pathConfig}\"")

        resValue("string", "fb_appid", deployExt.fbAppId)
        resValue("string", "fb_token", deployExt.fbToken)
        resValue("string", "adjust_token", deployExt.adjustToken)
        resValue("string", "admob_appid", deployExt.admobAppId)
    }

    signingConfigs {
        register("release") {
            enableV1Signing = true
            enableV2Signing = true
            keyAlias = deployExt.signPwd
            keyPassword = deployExt.signPwd
            storePassword = deployExt.signPwd
            storeFile = file(deployExt.signPath)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), deployExt.proguardPath)

            // 设置是否要自动上传（默认为true，要自动上传），测试环境为 false，正式环境为 true
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = deployExt.enableUploadMappingFile
            }

            // 放开注释，aab 包体积会减小。
            // 因为会把 aab 包中 BUNDLE_MEATADATA 目录下的 debugsymbols 文件夹去掉，即不添加调试符号
            // 不添加调试符号虽然可以减小 aab 的体积，但 native crash 时无法跟踪到问题代码
            ndk {
                debugSymbolLevel = "none"
            }
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs(deployExt.assetsPath)
            java.srcDirs(deployExt.javaPath)
            aidl.srcDirs(deployExt.aidlPath)
            res.srcDirs(deployExt.resPath)
            manifest.srcFile(deployExt.manifestPath)
        }
    }

    externalNativeBuild {
        cmake {
            path(deployExt.cmakePath)
            version = "3.18.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dataBinding {
        enable = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation(project(":zxing"))

    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")

    implementation("com.airbnb.android:lottie:5.2.0")

    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")

    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.greenrobot:eventbus:3.3.1")

    // https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
    implementation("com.blankj:utilcodex:1.31.0") // AndroidUtilCode 是一个强大易用的安卓工具类库

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation("com.github.getActivity:XXPermissions:16.0")

    /* Firebase */
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:30.5.0"))
    // Declare the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    /* Facebook */
    implementation("com.facebook.android:facebook-core:12.1.0")
    implementation("com.facebook.android:facebook-applinks:12.1.0")

    /* Admob */
    implementation("com.google.android.gms:play-services-ads:20.6.0")

    /* Adjust */
    implementation("com.adjust.sdk:adjust-android:4.33.0")
    implementation("com.android.installreferrer:installreferrer:2.2")
    // Add the following if you are using the Adjust SDK inside web views on your app
    implementation("com.adjust.sdk:adjust-android-webbridge:4.33.0")
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.1")
}