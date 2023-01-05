package com.cozs.qrcode.module.library;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class CryptoGuard {

    static {
        System.loadLibrary("cryptoguard");
    }

    private static final String TAG = "CryptoGuard";
    private static final String DES = "DES";

    public static String decrypt(String enData) {
//        Log.e(TAG, "--> decrypt()  enData=" + enData);
        byte[] decodeHex = hexStringToBytes(enData);
        byte[] gzDecompress = GZipArchive.unarchive(decodeHex);
        return new String(gzDecompress, StandardCharsets.UTF_8);
    }

    public static String encrypt(String srcData) {
        byte[] gzCompress = GZipArchive.archive(srcData.getBytes(StandardCharsets.UTF_8));
        String hexString = bytesToHexString(gzCompress);
        if (hexString != null) {
            return hexString;
        }
        return "";
    }

    public static native byte[] nativeEncrypt(@NonNull String origin);

    public static native byte[] nativeDecrypt(@NonNull byte[] data);

    public static native String nativeRetrieveConfig();

    /**
     * 先压缩再加密
     *
     * @param origin 原始数据
     */
    private static byte[] encrypt(@NonNull String sign, @NonNull String origin, @NonNull String strKey) {
        // 0. 数据封装
        byte[] originBytes = origin.getBytes();
        byte[] signBytes = sign.getBytes();
        byte[] dstData = new byte[signBytes.length + originBytes.length + 1];
        dstData[0] = (byte) signBytes.length;
        System.arraycopy(signBytes, 0, dstData, 1, signBytes.length);
        System.arraycopy(originBytes, 0, dstData, signBytes.length + 1, originBytes.length);
//        Log.e(TAG, "--> encrypt() packageData=" + new String(data, StandardCharsets.UTF_8));
//        Log.e(TAG, "--> encrypt() packageData Hex=" + bytesToHexString(data));
//        Log.e(TAG, "----------------------------------------------------");

        // 1. 压缩
        byte[] compressData = GZipArchive.archive(dstData);

        if (compressData == null) {
            return null;
        }
//            Log.e(TAG, "--> encrypt() compressData.len=" + compressData.length);
//            Log.e(TAG, "--> encrypt() compressData Hex=" + bytesToHexString(compressData));
//            Log.e(TAG, "----------------------------------------------------");

        // 2. 加密
        byte[] encryptData = null;
        try {
            Cipher cipher = createEncryptCipher(strKey);
            encryptData = cipher.doFinal(compressData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> encrypt() failed !!! e=" + e);
        }

        if (encryptData == null) {
            return null;
        }

        // 3. 转 hex
        byte[] result = null;
        String hexString = bytesToHexString(encryptData);
        if (hexString != null) {
            result = hexString.getBytes(StandardCharsets.UTF_8);
            return result;
        }
//                Log.e(TAG, "--> encrypt() hexString=" + hexString);
//                Log.e(TAG, "----------------------------------------------------");
        return result;
    }

    /**
     * 先解密再解压
     */
    private static byte[] decrypt(@NonNull byte[] data, @NonNull String strKey) {
        // 1. hexstring 还原
        String hexstring = new String(data, StandardCharsets.UTF_8);
//        Log.e(TAG, "--> decrypt()  hexstring=" + hexstring);
        byte[] reconvertData = hexStringToBytes(hexstring);

        if (reconvertData == null) {
            Log.e(TAG, "--> decrypt()  failed !!!  reconvertData == null");
//            Log.e(TAG, "----------------------------------------------------");
            return null;
        }

        // 2. 解密
        byte[] decryptData = null;
        try {
            Cipher cipher = createDecryptCipher(strKey);
            decryptData = cipher.doFinal(reconvertData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> decrypt() failed !!! e=" + e);
        }

        if (decryptData == null) {
            return null;
        }

        // 3. 解压
        byte[] result = GZipArchive.unarchive(decryptData);
//            Log.e(TAG, "--> decrypt() decompressData.len=" + decompressData.length);
//            Log.e(TAG, "--> decrypt() decompressData Hex=" + bytesToHexString(decompressData));
//            Log.e(TAG, "--> decrypt() origin=" + new String(decompressData, StandardCharsets.UTF_8));
//            Log.e(TAG, "----------------------------------------------------");
        return result;
    }

    /*===================================================================*/

    @NonNull
    private static Cipher createDecryptCipher(@NonNull String strKey) throws Exception {
        return createCipher(strKey, Cipher.DECRYPT_MODE);
    }

    @NonNull
    private static Cipher createEncryptCipher(@NonNull String strKey) throws Exception {
        return createCipher(strKey, Cipher.ENCRYPT_MODE);
    }

    /**
     * @param cryptModel 加密传 Cipher.ENCRYPT_MODE；解密传 Cipher.DECRYPT_MODE
     */
    @NonNull
    private static Cipher createCipher(@NonNull String strKey, int cryptModel)  throws Exception {

        /*
         * 实例化
         * 使用 PKCS7Padding 填充方式，按如下方式实现
         * Security.addProvider(new BouncyCastleProvider());
         * Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM，"BC");
         */
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        //还原密钥
        Key k = createKey(strKey);

        cipher.init(cryptModel, k);

        return cipher;
    }

    @Nullable
    private static Key createKey(@NonNull String strKey) throws Exception {
        //初始化密钥
        byte[] keyBytes = strKey.getBytes(StandardCharsets.UTF_8);

        //实例化DES密钥材料
        DESKeySpec dks = new DESKeySpec(keyBytes);
        //实例化秘密密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        //生成秘密密钥
        return keyFactory.generateSecret(dks);
    }

    /*--------------------------------------------------------------------*/

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制(基数 16)无符号整数形式返回一个整数参数的字符串表示形式
            hv = Integer.toHexString(src[i] & 0xFF).toLowerCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }
}
