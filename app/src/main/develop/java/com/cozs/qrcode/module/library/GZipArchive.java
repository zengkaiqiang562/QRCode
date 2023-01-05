package com.cozs.qrcode.module.library;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipArchive {
    private static final String TAG = "GZipArchive";

    public static final int BUFFER = 1024;

    /**
     * GZip 压缩
     *
     * @param data 未压缩的数据
     */
    public static byte[] archive(byte[] data) {
        byte[] output = null;
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();

            // 压缩
            innerArchive(bais, baos);

            output = baos.toByteArray();

            baos.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> archive() failed !!! e=" + e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "--> archive() io close failed !!! e=" + e);
            }
        }
        return output;
    }

    /**
     * GZip 解压
     *
     * @param data 压缩后的数据
     * @return 返回解压后的未压缩数据
     */
    public static byte[] unarchive(byte[] data) {

        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;

        byte[] result = null;

        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();

            // 解压缩
            innerUnarchive(bais, baos);

            result = baos.toByteArray();

            baos.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> unarchive() failed !!! e=" + e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "--> unarchive() io close failed !!! e=" + e);
            }
        }
        return result;
    }

    /**
     *
     * @param is 压缩源 io
     * @param os 压缩结果 io
     */
    private static void innerArchive(InputStream is, OutputStream os) {
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(os);

            int count;
            byte[] data = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }

            gos.finish();
            gos.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> innerArchive() failed !!! e=" + e);
        } finally {
            try {
                if (gos != null) {
                    gos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "--> innerArchive() io close failed !!! e=" + e);
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param is 解压源 io
     * @param os 解压后的结果 io
     */
    private static void innerUnarchive(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> innerUnarchive() failed !!! e=" + e);
        } finally {
            try {
                if (gis != null) {
                    gis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "--> innerUnarchive() io close failed !!! e=" + e);
            }
        }
    }
}
