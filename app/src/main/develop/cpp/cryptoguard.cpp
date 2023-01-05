#include <jni.h>
#include <android/log.h>
#include <string>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__) // 定义LOGE类型

static const char* clazzPath_CryptoGuard = "com/cozs/qrcode/module/library/CryptoGuard";

jbyteArray nativeEncrypt(JNIEnv *env, jobject instance, jstring origin) {
    LOGE("--> nativeEncrypt()");
//    LOGE("--> nativeEncrypt() SIGN=%s", SIGN);
//    LOGE("--> nativeEncrypt() CKEY=%s", CKEY);
    jclass clazz_CrytoGuard = env->FindClass(clazzPath_CryptoGuard);

    jmethodID mid_encrypt = env->GetStaticMethodID(clazz_CrytoGuard, "encrypt", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[B");

    jstring sign = env ->NewStringUTF(SIGN);
    jstring key = env ->NewStringUTF(CKEY);

    jbyteArray result = (jbyteArray) env ->CallStaticObjectMethod(clazz_CrytoGuard, mid_encrypt, sign, origin, key);

    env->DeleteLocalRef(sign);
    env->DeleteLocalRef(key);
    env->DeleteLocalRef(clazz_CrytoGuard);

    return result;
}

jbyteArray nativeDecrypt(JNIEnv *env, jobject instance, jbyteArray jdata) {
    LOGE("--> nativeDecrypt()");
//    LOGE("--> nativeEncrypt() SKEY=%s", SKEY);
    jclass clazz_CryptoGuard = env->FindClass(clazzPath_CryptoGuard);

    jmethodID mid_decrypt = env->GetStaticMethodID(clazz_CryptoGuard, "decrypt", "([BLjava/lang/String;)[B");

    jstring key = env ->NewStringUTF(SKEY);

    jbyteArray result = (jbyteArray) env ->CallStaticObjectMethod(clazz_CryptoGuard, mid_decrypt, jdata, key);

    env->DeleteLocalRef(key);
    env->DeleteLocalRef(clazz_CryptoGuard);

    return result;
}

jstring nativeRetrieveConfig(JNIEnv *env, jobject instance) {
    LOGE("--> nativeRetrieveConfig()");
    jclass cls_Securer = env->FindClass(clazzPath_CryptoGuard);

    jmethodID mid_decrypt = env->GetStaticMethodID(cls_Securer, "decrypt", "(Ljava/lang/String;)Ljava/lang/String;");

    jstring enProfile = env ->NewStringUTF(PROFILE_CACHE);
    jstring deProfile = (jstring) env ->CallStaticObjectMethod(cls_Securer, mid_decrypt, enProfile);
    env->DeleteLocalRef(enProfile);
    env->DeleteLocalRef(cls_Securer);
    return deProfile;
}

static const JNINativeMethod methods[] = {
        {"nativeEncrypt",   "(Ljava/lang/String;)[B", (jbyteArray *) nativeEncrypt},
        {"nativeDecrypt", "([B)[B", (jbyteArray *) nativeDecrypt},
        {"nativeRetrieveConfig", "()Ljava/lang/String;", (jbyteArray *) nativeRetrieveConfig},
};

int register_CryptoGuard(JNIEnv *env) {
    jclass jcls = env->FindClass(clazzPath_CryptoGuard);
    return env->RegisterNatives(jcls, methods, sizeof(methods) / sizeof(JNINativeMethod));
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGE("--> JNI_OnLoad()");
    JNIEnv *env = nullptr;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("--> JNI_OnLoad() GetEnv failed");
        goto error;
    }

    if (register_CryptoGuard(env) < 0) {
        LOGE("--> JNI_OnLoad() register_CryptoGuard failed !!!");
        goto error;
    }

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    error:
    return result;
}