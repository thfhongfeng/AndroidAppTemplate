package com.pine.app.lib.face.matcher.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.IFaceMatcher;
import com.pine.app.lib.face.matcher.opencv.algorithm.MatchAlgorithm;
import com.pine.app.lib.face.matcher.opencv.algorithm.MatchAlgorithmFactory;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

public class OcvFaceMatcher implements IFaceMatcher {
    private MatchAlgorithm mMatchAlgorithm;
    private BaseLoaderCallback mLoaderCallback;
    private int mFeatureType = IFaceMatcher.FEATURE_TYPE_SF;

    public OcvFaceMatcher() {
        Log.d(TAG, "use opencv for face matcher");
    }

    /**
     * 初始化人脸探测器
     *
     * @param context
     */
    @Override
    public void initFaceMatcher(final Context context) {
        mLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    Log.d(TAG, "OpenCV loaded successfully");
                    if (!OpenCVLoader.initDebug()) {
                        // 在OpenCV初始化完成后加载so库
                        System.loadLibrary("detection_based_tracker");
                    }
                    mMatchAlgorithm = MatchAlgorithmFactory.createInstance(mFeatureType);
                    mMatchAlgorithm.init(context);
                } else {
                    super.onManagerConnected(status);
                }
            }
        };

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    /**
     * 特征对比
     * <p>
     * * @param checkFaceFilePath    要比对的人脸特征文件路径
     *
     * @param standardFaceFilePath 比对的基准人脸特征文件路径
     * @return 相似度
     */
    @Override
    public double compareImg(String checkFaceFilePath, String standardFaceFilePath) {
        Mat standardFaceMat = getFaceImgMat(standardFaceFilePath);
        Mat checkFaceMat = getFaceImgMat(checkFaceFilePath);
        double degree = compareImg(standardFaceMat, checkFaceMat);
        FaceDataUtils.releaseMat(standardFaceMat, checkFaceMat);
        return degree;
    }

    /**
     * 特征对比
     *
     * @param checkFaceFilePath 要比对的人脸特征文件路径
     * @param matBytes          比对的基准人脸byte[]
     * @return 相似度
     */
    @Override
    public double compareImg(String checkFaceFilePath, byte[] matBytes) {
        if (matBytes == null) {
            return -1.0f;
        }
        Mat standardFaceMat = FaceDataUtils.imgBytes2Mat(matBytes);
        Mat checkFaceMat = getFaceImgMat(checkFaceFilePath);
        double degree = compareImg(standardFaceMat, checkFaceMat);
        FaceDataUtils.releaseMat(standardFaceMat, checkFaceMat);
        return degree;
    }

    private Mat mCheckCompareFeatureMat;

    @Override
    public boolean prepareCheckCompare(String checkFilePath) {
        Mat faceImgMat = getFaceImgMat(checkFilePath);
        mCheckCompareFeatureMat = getFeatureMat(faceImgMat);
        FaceDataUtils.releaseMat(faceImgMat);
        return mCheckCompareFeatureMat != null;
    }

    @Override
    public double doCheckCompare(byte[] beCheckMatBytes) {
        if (mCheckCompareFeatureMat == null) {
            Log.d(TAG, "mCheckCompareFeatureMat is null");
            return -1.0f;
        }
        Mat featureMat = FaceDataUtils.bytesToFeatureMat(beCheckMatBytes, mFeatureType);
        double degree = compareImgFeature(mCheckCompareFeatureMat, featureMat);
        FaceDataUtils.releaseMat(featureMat);
        return degree;
    }

    @Override
    public double doCheckCompare(String beCheckFilePath) {
        if (mCheckCompareFeatureMat == null) {
            return -1.0f;
        }
        Mat featureMat = getFeatureMat(getFaceImgMat(beCheckFilePath));
        double degree = compareImgFeature(mCheckCompareFeatureMat, featureMat);
        FaceDataUtils.releaseMat(featureMat);
        return degree;
    }

    @Override
    public boolean completeCheckCompare() {
        FaceDataUtils.releaseMat(mCheckCompareFeatureMat);
        mCheckCompareFeatureMat = null;
        return true;
    }

    @Override
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        return mMatchAlgorithm.maxSimilarChange(maxSimilarDegree, degree);
    }

    /**
     * maxSimilarDegree是否符合可信度要求
     *
     * @param maxSimilarDegree 最大相似度（degree的值与相似度关系由比较方式决定）
     * @param confidence       0-100，数值越高越可信
     * @return
     */
    @Override
    public boolean passConfidence(double maxSimilarDegree, float confidence) {
        return mMatchAlgorithm.passConfidence(maxSimilarDegree, confidence);
    }

    @Override
    public byte[] toFaceFeatureBytes(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return null;
        }
        Mat detectedFaceMat = getFaceImgMat(imgPath);
        if (detectedFaceMat == null) {
            return null;
        }
        Mat featureMat = getFeatureMat(detectedFaceMat);
        byte[] faceFeatureBytes = FaceDataUtils.featureMatToBytes(featureMat, mFeatureType);
        FaceDataUtils.releaseMat(detectedFaceMat, featureMat);
        return faceFeatureBytes;
    }

    @Override
    public byte[] toFaceFeatureBytes(Bitmap imgBitmap) {
        if (imgBitmap == null) {
            return null;
        }
        Mat detectedFaceMat = getFaceImgMat(imgBitmap);
        if (detectedFaceMat == null) {
            return null;
        }
        Mat featureMat = getFeatureMat(detectedFaceMat);
        byte[] faceFeatureBytes = FaceDataUtils.featureMatToBytes(featureMat, mFeatureType);
        FaceDataUtils.releaseMat(detectedFaceMat, featureMat);
        return faceFeatureBytes;
    }

    @Override
    public List<FacePosDetail> toFacePosDetails(Bitmap imgBitmap) {
        if (imgBitmap == null) {
            return null;
        }
        Mat imageMat = new Mat();
        Utils.bitmapToMat(imgBitmap, imageMat);
        List<FacePosDetail> facePosDetails = mMatchAlgorithm.getFacePosDetails(imageMat);
        FaceDataUtils.releaseMat(imageMat);
        return facePosDetails;
    }

    @Override
    public byte[] toFaceBytes(String imgPath, String extFormat) {
        if (TextUtils.isEmpty(imgPath)) {
            return null;
        }
        Mat detectedFaceMat = getFaceImgMat(imgPath);
        if (detectedFaceMat == null) {
            return null;
        }
        byte[] faceBytes = FaceDataUtils.imgMat2Bytes(detectedFaceMat,
                "." + extFormat.replaceAll("\\.", ""));
        FaceDataUtils.releaseMat(detectedFaceMat);
        return faceBytes;
    }

    @Override
    public Bitmap toFaceBitmap(String imgPath, String extFormat) {
        if (TextUtils.isEmpty(imgPath)) {
            return null;
        }
        Mat detectedFaceMat = getFaceImgMat(imgPath);
        if (detectedFaceMat == null) {
            return null;
        }
        byte[] faceData = FaceDataUtils.imgMat2Bytes(detectedFaceMat,
                "." + extFormat.replaceAll("\\.", ""));
        Bitmap bitmap = BitmapFactory.decodeByteArray(faceData, 0, faceData.length);
        FaceDataUtils.releaseMat(detectedFaceMat);
        return bitmap;
    }

    @Override
    public Bitmap toFaceBitmap(Bitmap imgBitmap) {
        if (imgBitmap == null) {
            return null;
        }
        Mat detectedFaceMat = getFaceImgMat(imgBitmap);
        if (detectedFaceMat == null) {
            return null;
        }
        Bitmap bitmap = FaceDataUtils.matToBitmap(detectedFaceMat);
        FaceDataUtils.releaseMat(detectedFaceMat);
        return bitmap;
    }

    @Override
    public Bitmap toFaceBitmap(byte[] faceMatData) {
        if (faceMatData == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(faceMatData, 0, faceMatData.length);
    }

    /**
     * 特征对比
     *
     * @param checkFaceMat    要比对的人脸特征Mat
     * @param standardFaceMat 比对的基准人脸Mat
     * @return 相似度
     */
    private double compareImg(Mat checkFaceMat, Mat standardFaceMat) {
        double degree = -1.0f;
        Mat standardFeature = getFeatureMat(checkFaceMat);
        Mat checkFeature = getFeatureMat(standardFaceMat);
        if (standardFeature != null && checkFeature != null) {
            degree = compareImgFeature(standardFeature, checkFeature);
        }
        FaceDataUtils.releaseMat(standardFeature, checkFeature);
        return degree;
    }

    private double compareImgFeature(Mat checkFeature, Mat standardFeature) {
        double degree = mMatchAlgorithm.matchFaceFeature(checkFeature, standardFeature);
        return degree;
    }

    /**
     * 获取人脸特征向量Mat
     *
     * @param faceImgMat
     * @return
     */
    private Mat getFeatureMat(Mat faceImgMat) {
        if (faceImgMat != null) {
            return mMatchAlgorithm.getFeatureMat(faceImgMat);
        }
        return null;
    }

    /**
     * 截取人脸Mat
     *
     * @param imgPath
     * @return
     */
    private Mat getFaceImgMat(String imgPath) {
        Mat image = Imgcodecs.imread(imgPath);
        Mat faceImgMat = getFaceImgMat(image);
        FaceDataUtils.releaseMat(image);
        return faceImgMat;
    }

    private Mat getFaceImgMat(Bitmap bitmap) {
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        Mat faceImgMat = getFaceImgMat(image);
        FaceDataUtils.releaseMat(image);
        return faceImgMat;
    }

    private Mat getFaceImgMat(Mat imageMat) {
        return mMatchAlgorithm.getFaceImgMat(imageMat);
    }
}
