package com.pine.app.lib.face.matcher.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RawRes;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.IFaceMatcher;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

// 人脸检测使用CascadeClassifier（机器学习）
// 人脸特征提取使用ORB方式
// 人脸识别匹配使用BFMatcher对人脸特征进行暴力破解方式（准确率低，不合适）

public class OcvFaceMatcherHist implements IFaceMatcher {
    private volatile boolean mInit = false;

    private CascadeClassifier mFaceDetector; // OpenCV的人脸检测器
    private BaseLoaderCallback mLoaderCallback;

    public OcvFaceMatcherHist() {
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
                    File faceModeDir = context.getDir("cascade", Context.MODE_PRIVATE);
                    String faceModeFileName = "haarcascade_frontalface_alt.xml";

                    File faceModeFile = new File(faceModeDir, faceModeFileName);
                    copyRawFile(context, faceModeFileName, "xml", faceModeFile);

                    if (faceModeFile.isFile() && faceModeFile.exists()) {
                        // 根据级联文件创建OpenCV的人脸检测器
                        mFaceDetector = new CascadeClassifier(faceModeFile.getAbsolutePath());
                        if (mFaceDetector.empty()) {
                            Log.d(TAG, "Failed to load cascade classifier mode files");
                            mFaceDetector = null;
                        } else {
                            mInit = true;
                            Log.d(TAG, "Loaded face mode file from " + faceModeFile.getAbsolutePath());
                        }
                    }

                    faceModeDir.delete();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean copyRawFile(Context context, String fileName, String fileType, File targetFile) {
        return copyRawFile(context, context.getResources().getIdentifier(
                fileName.substring(0, fileName.lastIndexOf(".")),
                fileType, context.getPackageName()), targetFile);
    }

    private boolean copyRawFile(Context context, @RawRes int rawId, File targetFile) {
        // 从应用程序资源加载mode文件
        try (InputStream is = context.getResources().openRawResource(rawId);
             FileOutputStream os = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkOpenCvPrepared() {
        return mInit && mFaceDetector != null;
    }

    /**
     * 特征对比
     *
     * @param checkFaceFilePath    要比对的人脸特征文件路径
     * @param standardFaceFilePath 比对的基准人脸特征文件路径
     * @return 相似度
     */
    @Override
    public double compareImg(String checkFaceFilePath, String standardFaceFilePath) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
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
     * @param matBytes          比对的基准人脸bitmap
     * @return 相似度
     */
    @Override
    public double compareImg(String checkFaceFilePath, byte[] matBytes) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
        if (matBytes == null) {
            Log.d(TAG, "face standardFaceBitmap is null");
            return -1.0f;
        }
        Mat standardFaceMat = FaceDataUtils.imgBytes2Mat(matBytes);
        Mat checkFaceMat = getFaceImgMat(checkFaceFilePath);
        double degree = compareImg(standardFaceMat, checkFaceMat);
        FaceDataUtils.releaseMat(standardFaceMat, checkFaceMat);
        return degree;
    }

    private Mat mCheckCompareHistMat;

    @Override
    public boolean prepareCheckCompare(String checkFilePath) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return false;
        }
        Mat faceImgMat = getFaceImgMat(checkFilePath);
        mCheckCompareHistMat = getHistMat(faceImgMat);
        FaceDataUtils.releaseMat(faceImgMat);
        return true;
    }

    @Override
    public double doCheckCompare(byte[] beCheckMatBytes) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
        if (mCheckCompareHistMat == null) {
            return -1.0f;
        }
        Mat faceImgMat = FaceDataUtils.imgBytes2Mat(beCheckMatBytes);
        double degree = compareCheckHist(mCheckCompareHistMat, faceImgMat);
        FaceDataUtils.releaseMat(faceImgMat);
        return degree;
    }

    @Override
    public double doCheckCompare(String beCheckFilePath) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
        if (mCheckCompareHistMat == null) {
            return -1.0f;
        }
        Mat faceImgMat = getFaceImgMat(beCheckFilePath);
        double degree = compareCheckHist(mCheckCompareHistMat, faceImgMat);
        FaceDataUtils.releaseMat(faceImgMat);
        return degree;
    }

    @Override
    public boolean completeCheckCompare() {
        FaceDataUtils.releaseMat(mCheckCompareHistMat);
        mCheckCompareHistMat = null;
        return true;
    }

    @Override
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        return degree > maxSimilarDegree;
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
        return maxSimilarDegree >= confidence / 100;
    }

    @Override
    public byte[] toFaceFeatureBytes(String imgPath) {
        return null;
    }

    @Override
    public byte[] toFaceFeatureBytes(Bitmap imgBitmap) {
        return null;
    }

    @Override
    public List<FacePosDetail> toFacePosDetails(Bitmap imgBitmap) {
        return null;
    }

    @Override
    public byte[] toFaceBytes(String imgPath, String extFormat) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
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
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
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
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
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
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
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
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
        double degree = -1.0f;
        if (standardFaceMat != null && checkFaceMat != null) {
            Mat checkMat = checkFaceMat;
            Imgproc.resize(checkFaceMat, checkMat, new Size(standardFaceMat.width(), standardFaceMat.height()));

            Mat histStandard = getHistMat(standardFaceMat);
            Mat histCheck = getHistMat(checkFaceMat);
            //相关系数
            degree = Imgproc.compareHist(histStandard, histCheck, Imgproc.CV_COMP_CORREL);
            FaceDataUtils.releaseMat(checkMat, histStandard, histCheck);
        }
        Log.d(TAG, "use hist compare, face compared degree:" + degree);
        return degree;
    }

    private double compareCheckHist(Mat histCheck, Mat standardFaceMat) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return -1.0f;
        }
        double degree = -1.0f;
        if (standardFaceMat != null && histCheck != null) {
            Mat standardMat = new Mat();
            Imgproc.resize(standardFaceMat, standardMat, new Size(histCheck.width(), histCheck.height()));
            Mat histStandard = getHistMat(standardFaceMat);
            //相关系数
            degree = Imgproc.compareHist(histStandard, histCheck, Imgproc.CV_COMP_CORREL);
            FaceDataUtils.releaseMat(standardMat, histStandard);
        }
        Log.d(TAG, "use hist compare, face compared degree:" + degree);
        return degree;
    }

    private Mat getHistMat(Mat faceMat) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
        if (faceMat != null) {
            Mat histMat = new Mat();
            MatOfInt channels = new MatOfInt(0);
            //直方图大小， 越大匹配越精确 (越慢)
            //这里参数我从1000改大，10000以后再改大也没有效果了
            MatOfInt histSizes = new MatOfInt(10000);
            //颜色范围
            MatOfFloat ranges = new MatOfFloat(0f, 256f);

//            MatOfInt channels = new MatOfInt(0, 1, 2);
//            //直方图大小， 越大匹配越精确 (越慢)
//            MatOfInt histSizes = new MatOfInt(100, 100, 100);
//            //颜色范围
//            MatOfFloat ranges = new MatOfFloat(0f, 256f, 0f, 256f, 0f, 256f);

            Imgproc.calcHist(Arrays.asList(faceMat), channels, new Mat(), histMat, histSizes, ranges);
            Core.normalize(histMat, histMat, 0, 1, Core.NORM_MINMAX, -1, new Mat());
            FaceDataUtils.releaseMat(channels, histSizes, ranges);
            return histMat;
        }
        return null;
    }


    /**
     * 灰度化人脸
     *
     * @param imageMat
     * @return 灰度化人脸
     */
    private Mat grayMat(Mat imageMat) {
        if (imageMat == null || imageMat.empty()) {
            Log.d(TAG, "grayMat image is null or empty");
            return null;
        }
        Mat grayImg = new Mat();
        //灰度化
        //色彩设置 COLOR_BGR2GRAY-置灰;COLOR_BGRA2RGBA-原色
        Imgproc.cvtColor(imageMat, grayImg, Imgproc.COLOR_BGR2GRAY);
        return grayImg;
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
        if (imageMat == null || imageMat.empty()) {
            Log.d(TAG, "getFaceImgMat image is null or empty");
            return null;
        }
        Mat grayImg = new Mat();
        //灰度化
        //色彩设置 COLOR_BGR2GRAY-置灰;COLOR_BGRA2RGBA-原色
        Imgproc.cvtColor(imageMat, grayImg, Imgproc.COLOR_BGR2GRAY);
        //探测人脸 特征匹配
        MatOfRect faceDetections = new MatOfRect();
        mFaceDetector.detectMultiScale(grayImg, faceDetections);
        //rect中人脸图片的范围
        Mat face = null;
        for (Rect rect : faceDetections.toArray()) {
            face = new Mat(grayImg, rect);
        }
        FaceDataUtils.releaseMat(grayImg, faceDetections);
        return face;
    }
}
