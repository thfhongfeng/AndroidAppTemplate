package com.pine.tool.camera;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class PicSaver {
    private static String TAG = "PicSaver";

    public static boolean saveFacePicToLocal(String savePath, Bitmap bitmap) {
        return saveFacePicToLocal(savePath, bitmap, Bitmap.CompressFormat.JPEG, 0, false);
    }

    public static boolean saveFacePicToLocal(String savePath, Bitmap bitmap, long maxByteSize) {
        return saveFacePicToLocal(savePath, bitmap, Bitmap.CompressFormat.JPEG, maxByteSize, false);
    }

    public static boolean saveFacePicToLocal(String savePath, Bitmap bitmap,
                                             Bitmap.CompressFormat format,
                                             long maxByteSize, boolean recycle) {
        if (TextUtils.isEmpty(savePath) || bitmap == null
                || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            Log.d(TAG, "save fail for args incorrect");
            return false;
        }
        File saveFile = new File(savePath);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        if (saveFile.exists()) {
            saveFile.delete();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] bytes;
        int compressQuality = 0;
        if (baos.size() > maxByteSize && maxByteSize > 0) {
            Log.d(TAG, "需要压缩到" + maxByteSize + "byte，" +
                    "图片压缩前大小：" + baos.toByteArray().length + "byte");
            baos.reset();
            bitmap.compress(format, 0, baos);
            if (baos.size() < maxByteSize) { // 最差质量不小于最大字节，则返回最差质量
                // 二分法寻找最佳质量
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    bitmap.compress(format, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    bitmap.compress(format, st, baos);
                    compressQuality = st;
                }
            }
            Log.d(TAG, "图片降低质量压缩后大小：" + baos.toByteArray().length + "byte" + ", quality:" + compressQuality);
        }
        bytes = baos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "save fail:" + e);
            return false;
        } finally {
            if (recycle && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
