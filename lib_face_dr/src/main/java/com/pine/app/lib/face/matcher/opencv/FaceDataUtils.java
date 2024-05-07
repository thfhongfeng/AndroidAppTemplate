package com.pine.app.lib.face.matcher.opencv;

import android.graphics.Bitmap;
import android.util.Log;

import com.pine.app.lib.face.FacePosDetail;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceDataUtils {
    private static final String TAG = FaceDataUtils.class.getSimpleName();

    /**
     * 将特征向量转换为FaceFeature列表
     *
     * @param faceMat 二维的元组类型，
     *                第一纬是识别到的人脸个数，
     *                第二纬是一个二维的数组，表示的人脸坐标数据，每一个人脸坐标数据大小为15个数，其中包括：
     *                人脸左上XY坐标(2个数)(0,1)
     *                人脸宽高(2个数)(2,3)
     *                右眼瞳孔XY坐标(2个数)(4,5)
     *                左眼瞳孔XY坐标(2个数)(6,7)
     *                鼻尖XY坐标(2个数)(8,9)
     *                右嘴角XY坐标(2个数)(10,11)
     *                左嘴角XY坐标(2个数)(12,13)
     *                置信度（1个数，越接近1说明与人脸越像)(14)
     * @return
     */
    public static List<FacePosDetail> faceMatToFacePosDetails(Mat faceMat) {
        if (faceMat == null) {
            return null;
        }
        int rows = faceMat.rows();
        int cols = faceMat.cols();
        int type = faceMat.type();
        if (rows < 1 || cols != 15) {
            return null;
        }
        List<FacePosDetail> features = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            FacePosDetail facePosDetail = new FacePosDetail();
            float[] row = new float[cols];
            faceMat.get(i, 0, row);
            facePosDetail.eyesDist = Math.abs(row[4] - row[6]);
            facePosDetail.midPointX = row[0] + row[2] / 2;
            facePosDetail.midPointY = row[1] + row[3] / 2;
            facePosDetail.confidence = row[14];
            facePosDetail.liveConfidence = row[14];
            facePosDetail.width = row[2];
            facePosDetail.height = row[3];
            features.add(facePosDetail);
        }
        return features;
    }

    private static final int featureExtraHeadCount = 4;

    // 将特征向量转换为字节数组
    public static byte[] featureMatToBytes(Mat descriptors, int featureType) {
        if (descriptors == null) {
            return null;
        }
        int rows = descriptors.rows();
        int cols = descriptors.cols();
        int type = descriptors.type();

        byte[] bytesFeatureTypes = ByteBuffer.allocate(4).putInt(featureType).array();
        byte[] bytesRows = ByteBuffer.allocate(4).putInt(rows).array();
        byte[] bytesCols = ByteBuffer.allocate(4).putInt(cols).array();
        byte[] bytesTypes = ByteBuffer.allocate(4).putInt(type).array();
        Log.d(TAG, "rows:" + rows + ", cols:" + cols + ", type:" + type
                + ", featureType:" + featureType);

        byte[] faceFeaturesBytes = FeatureVectorUtil.matToByteArray(descriptors);

        byte[] byteArray = new byte[featureExtraHeadCount * 4 + faceFeaturesBytes.length];
        System.arraycopy(bytesFeatureTypes, 0, byteArray, 0, 4);
        System.arraycopy(bytesRows, 0, byteArray, 4, 4);
        System.arraycopy(bytesCols, 0, byteArray, 8, 4);
        System.arraycopy(bytesTypes, 0, byteArray, 12, 4);

        System.arraycopy(faceFeaturesBytes, 0, byteArray,
                4 * featureExtraHeadCount, faceFeaturesBytes.length);

        return byteArray;
    }

    // 将字节数组转换为特征向量
    public static Mat bytesToFeatureMat(byte[] byteArray, int featureType) {
        if (byteArray == null) {
            return null;
        }
        if (byteArray.length < 4 * featureExtraHeadCount) {
            Log.d(TAG, "byteArray extra head content not correct");
            return null;
        }
        byte[] bytesFeatureTypes = {byteArray[0], byteArray[1], byteArray[2], byteArray[3]};
        int featureTypes = ByteBuffer.wrap(bytesFeatureTypes).getInt();
        if (featureTypes != featureType) {
            Log.d(TAG, "featureTypes is not the same");
            return null;
        }
        byte[] bytesRows = {byteArray[4], byteArray[5], byteArray[6], byteArray[7]};
        int rows = ByteBuffer.wrap(bytesRows).getInt();
        byte[] bytesCols = {byteArray[8], byteArray[9], byteArray[10], byteArray[11]};
        int cols = ByteBuffer.wrap(bytesCols).getInt();
        byte[] bytesTypes = {byteArray[12], byteArray[13], byteArray[14], byteArray[15]};
        int type = ByteBuffer.wrap(bytesTypes).getInt();
        byte[] featureBytes = Arrays.copyOfRange(byteArray, 4 * featureExtraHeadCount,
                byteArray.length);
        Mat descriptors = FeatureVectorUtil.byteArrayToMat(featureBytes, rows, cols, type);
        return descriptors;
    }

    public static Mat imgBytes2Mat(byte[] bytes) {
        MatOfByte matOfByte = new MatOfByte(bytes);
        Mat mat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);
        return mat;
    }

    public static byte[] imgMat2Bytes(Mat mat, String type) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(type, mat, matOfByte);
        return matOfByte.toArray();
    }

    // 将 Mat 对象转换为 Bitmap 对象
    public static Bitmap matToBitmap(Mat src) {
        Bitmap bmp = null;
        try {
            // 将 Mat 转换为 RGBA 颜色模式
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGBA);

            // 创建 Bitmap 对象
            bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);

            // 将 Mat 数据复制到 Bitmap 对象中
            Utils.matToBitmap(src, bmp);
        } catch (Exception e) {
            Log.e(TAG, "matToBitmap: " + e.getMessage());
        }
        return bmp;
    }

    public static void releaseMat(Mat... mats) {
        if (mats == null) {
            return;
        }
        for (Mat mat : mats) {
            if (mat != null) {
                mat.release();
            }
        }
    }
}
