package com.pine.app.lib.face.matcher.opencv.algorithm;

import static org.opencv.core.Core.NORM_HAMMING;

import android.content.Context;
import android.util.Log;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.opencv.FaceDataUtils;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.ORB;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 人脸检测使用CascadeClassifier
// 人脸特征提取使用ORB方式
// 人脸识别匹配使用BFMatcher对人脸特征进行暴力破解方式（准确率低，不合适）

public class ORBMatchAlgorithm extends MatchAlgorithm {
    // ORB 特征点最大数量
    private static final int MAX_FEATURE_NUM = 1000;
    // 匹配最小距离
    private static final double MIN_DISTANCE = 50;

    private volatile boolean mInit = false;

    private ORB mFeatureDetector;
    private CascadeClassifier mFaceDetector; // OpenCV的人脸检测器
    private CascadeClassifier mEyeDetector;

    private boolean checkOpenCvPrepared() {
        return mInit && mFeatureDetector != null && mFaceDetector != null && mEyeDetector != null;
    }

    @Override
    public boolean init(Context context) {
        Log.d(TAG, "init begin, use orb feature algorithm");
        File faceModeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        String faceModeFileName = "haarcascade_frontalface_alt.xml";
        String eyesModeFileName = "haarcascade_eye.xml";

        File faceModeFile = new File(faceModeDir, faceModeFileName);
        copyRawFile(context, faceModeFileName, "raw", faceModeFile);
        File eyesModeFile = new File(faceModeDir, eyesModeFileName);
        copyRawFile(context, eyesModeFileName, "raw", eyesModeFile);

        if (faceModeFile.isFile() && faceModeFile.exists()
                && eyesModeFile.isFile() && eyesModeFile.exists()) {
            // 根据级联文件创建OpenCV的人脸检测器
            mFaceDetector = new CascadeClassifier(faceModeFile.getAbsolutePath());
            mEyeDetector = new CascadeClassifier(eyesModeFile.getAbsolutePath());
            if (mFaceDetector.empty() || mEyeDetector.empty()) {
                Log.d(TAG, "Failed to load cascade classifier mode files "
                        + ", faceDetector is empty:" + mFaceDetector.empty()
                        + ", eyeDetector is empty:" + mEyeDetector.empty());
                mFaceDetector = null;
                mEyeDetector = null;
            } else {
                mFeatureDetector = ORB.create(MAX_FEATURE_NUM);
                mInit = true;
                Log.d(TAG, "Loaded face mode file from " + faceModeFile.getAbsolutePath());
                Log.d(TAG, "Loaded eyes mode file from " + eyesModeFile.getAbsolutePath());
            }
        } else {
            Log.d(TAG, "mode file not exist, init fail");
        }
        faceModeDir.delete();
        return mInit;
    }

    @Override
    public Mat getFaceImgMat(Mat imgMat) {
        if (imgMat == null || imgMat.empty()) {
            Log.d(TAG, "getFaceImgMat image is null or empty");
            return null;
        }
        if (!checkOpenCvPrepared()) {
            return null;
        }
        //探测人脸
        MatOfRect faceDetections = new MatOfRect();
        mFaceDetector.detectMultiScale(imgMat, faceDetections);
        //rect中人脸图片的范围
        Rect rect = getMainFaceImgRect(faceDetections);
        Mat face = new Mat(imgMat, rect);
        FaceDataUtils.releaseMat(faceDetections);
        return face;
    }

    private Rect getMainFaceImgRect(MatOfRect faceDetections) {
        double maxArea = 0d;
        Rect[] rectArr = faceDetections.toArray();
        Rect maxRect = null;
        for (int i = 0; i < rectArr.length; i++) {
            Rect rect = rectArr[i];
            double area = rect.area();
            if (area > maxArea) {
                maxArea = area;
                maxRect = rect;
            }
        }
        return maxRect;
    }

    @Override
    public List<FacePosDetail> getFacePosDetails(Mat imageMat) {
        if (!checkOpenCvPrepared()) {
            return null;
        }
        MatOfRect faceDetections = new MatOfRect();
        mFaceDetector.detectMultiScale(imageMat, faceDetections);
        List<FacePosDetail> posDetails = new ArrayList<>();
        // 遍历所有检测到的人脸
        for (Rect faceRect : faceDetections.toArray()) {
            FacePosDetail posDetail = new FacePosDetail();
            // 获取人脸中心点位置和宽高
            posDetail.midPointX = faceRect.x + faceRect.width / 2;
            posDetail.midPointY = faceRect.y + faceRect.height / 2;

            // 检测眼睛位置
            Rect eyeRect = new Rect(faceRect.x, faceRect.y + faceRect.height / 4,
                    faceRect.width, faceRect.height / 2);
            Mat eyeRegion = new Mat(imageMat, eyeRect);
            MatOfRect eyeDetections = new MatOfRect();
            mEyeDetector.detectMultiScale(eyeRegion, eyeDetections);

            // 处理眼睛位置
            if (eyeDetections.toArray().length == 2) {
                Rect[] eyeRects = eyeDetections.toArray();

                Point eyeCenter1 = new Point(faceRect.x + eyeRects[0].x + eyeRects[0].width * 0.5,
                        faceRect.y + eyeRects[0].y + eyeRects[0].height * 0.5);
                Point eyeCenter2 = new Point(faceRect.x + eyeRects[1].x + eyeRects[1].width * 0.5,
                        faceRect.y + eyeRects[1].y + eyeRects[1].height * 0.5);
                posDetail.eyesDist = (float) Math.sqrt(Math.pow(eyeCenter1.x - eyeCenter2.x, 2)
                        + Math.pow(eyeCenter1.y - eyeCenter2.y, 2));
            }
            posDetail.confidence = posDetail.eyesDist > 0 ? 1 : 0;
            posDetail.width = faceRect.width;
            posDetail.height = faceRect.height;
            posDetails.add(posDetail);
            FaceDataUtils.releaseMat(eyeRegion, eyeDetections);
        }
        FaceDataUtils.releaseMat(faceDetections);
        return posDetails;
    }

    @Override
    public Mat getFeatureMat(Mat faceImgMat) {
        if (!checkOpenCvPrepared()) {
            return null;
        }
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Mat mask = new Mat();
        Mat descriptors = new Mat();
        mFeatureDetector.detectAndCompute(faceImgMat, mask, keyPoints, descriptors);
        FaceDataUtils.releaseMat(mask);
        return descriptors;
    }

    @Override
    public double matchFaceFeature(Mat checkFeature, Mat standardFeature) {
        if (!checkOpenCvPrepared()) {
            return -1.0f;
        }
        double degree = -1.0f;
        if (checkFeature != null && standardFeature != null) {
            BFMatcher matcher = BFMatcher.create(NORM_HAMMING);
            // 特征匹配
            List<MatOfDMatch> matches = new ArrayList<>();
            matcher.knnMatch(checkFeature, standardFeature, matches, 2);

            // 筛选匹配结果
            List<DMatch> goodMatches = new ArrayList<>();
            float threshold = 0.99f;
            for (int i = 0; i < matches.size(); i++) {
                DMatch[] matchArray = matches.get(i).toArray();
                if (matchArray.length >= 2) {
                    float offset = Math.abs(matchArray[0].distance - matchArray[1].distance);
                    float offsetRatio = offset * 2 / (matchArray[0].distance + matchArray[1].distance);
                    if (offsetRatio < (1 - threshold)) {
                        goodMatches.add(matchArray[0]);
                    }
                }
            }
            // 计算匹配度
            degree = (float) goodMatches.size() / matches.size();
        }
        return degree;
    }

    @Override
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        if (maxSimilarDegree < 0) {
            return true;
        }
        if (degree < 0) {
            return false;
        }
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
        Log.d(TAG, "passConfidence maxSimilarDegree:" + maxSimilarDegree + ", confidence:" + confidence);
        return maxSimilarDegree >= confidence / 100;
    }
}
