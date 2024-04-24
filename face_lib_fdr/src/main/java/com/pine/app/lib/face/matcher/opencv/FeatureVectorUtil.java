package com.pine.app.lib.face.matcher.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class FeatureVectorUtil {
    // 将任意类型的人脸特征向量Mat转为字节数组
    public static byte[] matToByteArray(Mat featureVectorMat) {
        byte[] byteArray = null;
        switch (featureVectorMat.type()) {
            case CvType.CV_8U:
            case CvType.CV_8S:
                // Byte类型
                byteArray = new byte[featureVectorMat.cols()];
                featureVectorMat.get(0, 0, byteArray);
                break;
            case CvType.CV_16U:
            case CvType.CV_16S:
                // Short类型
                short[] shortArray = new short[featureVectorMat.cols()];
                featureVectorMat.get(0, 0, shortArray);
                ByteBuffer bufferShort = ByteBuffer.allocate(shortArray.length * 2);
                for (short s : shortArray) {
                    bufferShort.putShort(s);
                }
                byteArray = bufferShort.array();
                break;
            case CvType.CV_32F:
                // Float类型
                float[] floatArray = new float[featureVectorMat.cols()];
                featureVectorMat.get(0, 0, floatArray);
                ByteBuffer bufferFloat = ByteBuffer.allocate(floatArray.length * 4);
                for (float f : floatArray) {
                    bufferFloat.putFloat(f);
                }
                byteArray = bufferFloat.array();
                break;
            case CvType.CV_32S:
                // Integer类型
                int[] intArray = new int[featureVectorMat.cols()];
                featureVectorMat.get(0, 0, intArray);
                ByteBuffer bufferInt = ByteBuffer.allocate(intArray.length * 4);
                for (int i : intArray) {
                    bufferInt.putInt(i);
                }
                byteArray = bufferInt.array();
                break;
            case CvType.CV_64F:
                // Double类型
                double[] doubleArray = new double[featureVectorMat.cols()];
                featureVectorMat.get(0, 0, doubleArray);
                ByteBuffer bufferDouble = ByteBuffer.allocate(doubleArray.length * 8);
                for (double d : doubleArray) {
                    bufferDouble.putDouble(d);
                }
                byteArray = bufferDouble.array();
                break;
        }
        return byteArray;
    }

    public static Mat byteArrayToMat(byte[] data, int rows, int cols, int type) {
        Mat mat = null;
        if (data != null && data.length > 0) {
            mat = new Mat(rows, cols, type);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            switch (type) {
                case CvType.CV_8U:
                case CvType.CV_8S:
                    if (data.length == rows * cols) {
                        mat.put(0, 0, data);
                    }
                    break;
                case CvType.CV_16U:
                case CvType.CV_16S:
                    if (data.length == rows * cols * 2) {
                        ShortBuffer shortBuffer = buffer.asShortBuffer();
                        short[] shortArray = new short[shortBuffer.limit()];
                        shortBuffer.get(shortArray);
                        mat.put(0, 0, shortArray);
                    }
                    break;
                case CvType.CV_32F:
                    if (data.length == rows * cols * 4) {
                        FloatBuffer floatBuffer = buffer.asFloatBuffer();
                        float[] floatArray = new float[floatBuffer.limit()];
                        floatBuffer.get(floatArray);
                        mat.put(0, 0, floatArray);
                    }
                    break;
                case CvType.CV_32S:
                    if (data.length == rows * cols * 4) {
                        IntBuffer intBuffer = buffer.asIntBuffer();
                        int[] intArray = new int[intBuffer.limit()];
                        intBuffer.get(intArray);
                        mat.put(0, 0, intArray);
                    }
                    break;
                case CvType.CV_64F:
                    if (data.length == rows * cols * 8) {
                        DoubleBuffer doubleBuffer = buffer.asDoubleBuffer();
                        double[] doubleArray = new double[doubleBuffer.limit()];
                        doubleBuffer.get(doubleArray);
                        mat.put(0, 0, doubleArray);
                    }
                    break;
                default:
                    return null;
            }
        }
        return mat;
    }
}
