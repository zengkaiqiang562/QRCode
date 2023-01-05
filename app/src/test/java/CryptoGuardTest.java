
import com.cozs.qrcode.module.bean.ConfigInfoBean;
import com.cozs.qrcode.module.library.GZipArchive;
import com.google.gson.Gson;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class CryptoGuardTest {

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
        if (hexString == null || hexString.length() == 0) {
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

    @Test
    public void testProfileCache() {
        System.out.println("sProfileCache=" + sProfileCache);
        System.out.println("######################################");

        byte[] gzCompress = GZipArchive.archive(sProfileCache.getBytes(StandardCharsets.UTF_8));

        String encodePrifleCache = bytesToHexString(gzCompress);
        System.out.println("encodePrifleCache=" + encodePrifleCache);

        System.out.println("=====================================");

//        byte[] decodeHex = hexStringToBytes(encodePrifleCache);
        byte[] decodeHex = hexStringToBytes(sEnProfileCache);
        byte[] gzDecompress = GZipArchive.unarchive(decodeHex);
        String decodePrifleCache = new String(gzDecompress, StandardCharsets.UTF_8);
        System.out.println("decodePrifleCache=" + decodePrifleCache);

        ConfigInfoBean configInfoBean = new Gson().fromJson(decodePrifleCache, ConfigInfoBean.class);
        System.out.println("configInfoBean=" + configInfoBean);
        System.out.println("getAdTypeInfo=" + configInfoBean.getAdPlaceInfos().get(0).getAdTypeInfo());
    }

    private static final String sEnProfileCache = "1f8b0800000000000000cd91c16bc32014c6eff92b8ae784c568dab95b6130063b14721ca358e356c11a5193d296fcef7bcdd2926c0decd877d2f7fddec7f3f314cda0102fdf940fe869f6deddcf75ba9e7aa229f62a882d40c1d5321ecbca18e996e5c8e1b6d3c0716d3517126690e009b736b1f526218ca6593e4f196334cb297d2014b34792678ca278d2291c6c67e40377618a13baf2b250c733994e7a356b2fb8ee983f483bea7cc4bf335a5d1e54ec600fe97ed6b9526d7c7ff1e29410bc200b9cfd235e65ee285c51412a62186f341844207faaaf570364c335cccdfbb5908760fcf6b9763ca8ca80822f8a347ca3e54bad4ad97f44d47e030efd476823030000";

    private static final String sProfileCache = "{\n" +
            "    \"adList\": [\n" +
            "        {\n" +
            "            \"advSwitch\": true,\n" +
            "            \"innerAd\": [\n" +
            "                {\n" +
            "                    \"ad_place\": \"ca-app-pub-3940256099942544/3419835294\",\n" +
            "                    \"ad_type\": \"start\",\n" +
            "                    \"closeSize\": 0,\n" +
            "                    \"adv_scale\": 0\n" +
            "                }\n" +
            "            ],\n" +
            "            \"adPlace\": \"Smarterstart\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"advSwitch\": true,\n" +
            "            \"innerAd\": [\n" +
            "                {\n" +
            "                    \"ad_place\": \"ca-app-pub-3940256099942544/1033173712\",\n" +
            "                    \"ad_type\": \"int\",\n" +
            "                    \"closeSize\": 0,\n" +
            "                    \"adv_scale\": 0\n" +
            "                }\n" +
            "            ],\n" +
            "            \"adPlace\": \"Smarterconnect\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"configInterval\": 60,\n" +
            "    \"splashDuration\": 10,\n" +
            "    \"enableGuide\": true\n" +
            "}";
}
