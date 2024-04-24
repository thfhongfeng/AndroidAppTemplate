package com.pine.app.lib.face.matcher.opencv.algorithm;

import android.content.Context;
import android.util.Log;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.opencv.FaceDataUtils;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

// 人脸检测使用FaceDetectorYN（机器学习）
// 人脸特征提取使用FaceRecognizerSF方式（机器学习）
// 人脸识别匹配使用FaceRecognizerSF对人脸特征进行识别（机器学习）（准确率高）

public class SFMatchAlgorithm extends MatchAlgorithm {
    private Object FaceDetectorYNLock = new Object();

    private volatile boolean mInit = false;

    private LinkedList<FaceDetectorYN> mFaceDetectorList = new LinkedList<>(); // OpenCV的人脸检测器
    private String detectModeFilePath = "";
    // 用于过滤出置信度低于指定值的检测框的阈值
    // 一般来说，这个值可以设置为一个较高的值，以确保只有高置信度的检测结果被保留。通常可以尝试设置为0.5或更高。
    private float score_thresh = 0.6f;
    // 用于非最大抑制的阈值。它是一个浮点数，用于抑制 IoU（交并比）大于指定值的检测框、
    // 这个值用于控制重叠检测框之间的抑制程度。较高的值会导致更少的检测框被抑制，较低的值会导致更多的检测框被抑制。通常可以尝试设置为0.3到0.5之间的值
    private float nms_thresh = 0.3f;
    // 在进行非最大抑制之前，保留的最高置信度检测结果数量
    // 这个值用于限制在非最大抑制之前保留的最高置信度检测结果的数量。这个值可以根据你的需求和应用场景来调整，
    // 通常可以尝试设置为100或更高，具体取决于你希望保留的检测结果数量。
    private int top_k = 500;

    // FaceRecognizerSF.FR_COSINE;FaceRecognizerSF.FR_NORM_L2
    private int mRecognizerType = FaceRecognizerSF.FR_COSINE;
    // 余弦匹配方式 FaceRecognizerSF.FR_COSINE
    // 余弦值越高表示相似度越高，最大值为1.0
    // cosine方法大于阈值0.363判定为同一人
    double cosine_similarity_threshold = 0.363;
    // L2匹配方式 FaceRecognizerSF.FR_NORM_L2
    // L2距离越低表示相似度越高，最小值为0.0
    // normL2方法小于阈值1.128判定为同一人
    double l2_similarity_threshold = 1.128;
    private FaceRecognizerSF mFaceRecognizer; // OpenCV的人脸识别器

    private boolean checkOpenCvPrepared() {
        return mInit && mFaceRecognizer != null;
    }

    @Override
    public boolean init(Context context) {
        Log.d(TAG, "init begin, use sf feature algorithm");
        String detectModeFileName = "face_detection_yunet_2022mar.onnx";
        String recognitionModeFileName = "face_recognition_sface_2021dec.onnx";

        File faceModeDir = context.getDir("facemode", Context.MODE_PRIVATE);
        File detectModeFile = new File(faceModeDir, detectModeFileName);
        copyRawFile(context, detectModeFileName, "raw", detectModeFile);
        File recognitionModeFile = new File(faceModeDir, recognitionModeFileName);
        copyRawFile(context, recognitionModeFileName, "raw", recognitionModeFile);

        if (detectModeFile.isFile() && detectModeFile.exists()
                && recognitionModeFile.isFile() && recognitionModeFile.exists()) {
            detectModeFilePath = detectModeFile.getAbsolutePath();
            mFaceRecognizer = FaceRecognizerSF.create(recognitionModeFile.getAbsolutePath(), "");
            mInit = true;
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
        Mat facePosMat = getFacePosMat(imgMat);
        if (facePosMat == null || facePosMat.empty()) {
            Log.d(TAG, "getFaceImgMat facePosMat is null or empty");
            return null;
        }
        if (facePosMat.rows() < 1) {
            Log.d(TAG, "getFaceImgMat no face detected");
            return null;
        }
        int index = getMainFaceImgRect(facePosMat);
        Mat alignMat = new Mat();
        mFaceRecognizer.alignCrop(imgMat, facePosMat.row(index), alignMat);
        FaceDataUtils.releaseMat(facePosMat);
        return alignMat;
    }

    private int getMainFaceImgRect(Mat facePosMat) {
        double maxArea = 0d;
        int maxIndex = 0;
        int rows = facePosMat.rows();
        int cols = facePosMat.cols();
        for (int i = 0; i < rows; i++) {
            float[] row = new float[cols];
            facePosMat.get(i, 0, row);
            double area = row[2] * row[3];
            if (area > maxArea) {
                maxArea = area;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    public List<FacePosDetail> getFacePosDetails(Mat imageMat) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
        Mat facePosMat = getFacePosMat(imageMat);
        if (facePosMat == null) {
            return null;
        }
        List<FacePosDetail> posDetails = FaceDataUtils.faceMatToFacePosDetails(facePosMat);
        FaceDataUtils.releaseMat(facePosMat);
        return posDetails;
    }

    @Override
    public Mat getFeatureMat(Mat faceImgMat) {
        if (!checkOpenCvPrepared()) {
            Log.d(TAG, "face detector not init");
            return null;
        }
        if (faceImgMat != null) {
            Mat feature = new Mat();

            mFaceRecognizer.feature(faceImgMat, feature);
            // 必须clone
            Mat cloneFeature = feature.clone();
            FaceDataUtils.releaseMat(feature);
            return cloneFeature;
        }
        return null;
    }

    @Override
    public double matchFaceFeature(Mat checkFeature, Mat standardFeature) {
        if (!checkOpenCvPrepared()) {
            return -1.0f;
        }
        double degree = -1.0f;
        if (checkFeature != null && standardFeature != null) {
            try {
                switch (mRecognizerType) {
                    case FaceRecognizerSF.FR_COSINE:
                    case FaceRecognizerSF.FR_NORM_L2:
                        degree = mFaceRecognizer.match(standardFeature, checkFeature, mRecognizerType);
                        break;
                    default:
                        degree = mFaceRecognizer.match(standardFeature, checkFeature);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return degree;
    }

    @Override
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        switch (mRecognizerType) {
            case FaceRecognizerSF.FR_NORM_L2:
                return degree < maxSimilarDegree;
            default:
                return degree > maxSimilarDegree;
        }
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
        if (maxSimilarDegree < 0) {
            return false;
        }
        int calNode = 80;
        switch (mRecognizerType) {
            case FaceRecognizerSF.FR_COSINE:
                double cosine_confidence = cosine_similarity_threshold;
                if (confidence > calNode) {
                    cosine_confidence = cosine_similarity_threshold +
                            (1 - cosine_similarity_threshold) * (confidence - calNode) / (100 - calNode);
                }
                return maxSimilarDegree >= cosine_confidence;
            case FaceRecognizerSF.FR_NORM_L2:
                double l2_confidence = l2_similarity_threshold;
                if (confidence > calNode) {
                    l2_confidence = l2_similarity_threshold -
                            (l2_similarity_threshold - 0) * (confidence - calNode) / (100 - calNode);
                }
                return maxSimilarDegree <= l2_confidence;
            default:
                return false;
        }
    }

    /**
     * 探测人脸位置Mat
     *
     * @param imageMat
     * @return 返回的是一个二维的元组类型，
     * 第一纬是识别到的人脸个数，
     * 第二纬是一个二维的数组，表示的人脸坐标数据，每一个人脸坐标数据大小为15个数，其中包括：
     * 人脸左上XY坐标(2个数)
     * 人脸宽高(2个数)
     * 右眼瞳孔XY坐标(2个数)
     * 左眼瞳孔XY坐标(2个数)
     * 鼻尖XY坐标(2个数)
     * 右嘴角XY坐标(2个数)
     * 左嘴角XY坐标(2个数)
     * 置信度（1个数，越接近1说明与人脸越像）
     */
    private Mat getFacePosMat(Mat imageMat) {
        if (imageMat == null || imageMat.empty()) {
            Log.d(TAG, "getFacePosMat image is null or empty");
            return null;
        }
        Mat img3CMat = null;
        if (imageMat.channels() != 3) {
            img3CMat = new Mat();
            // 4通道转3通道
            Imgproc.cvtColor(imageMat, img3CMat, Imgproc.COLOR_RGBA2BGR);
        } else {
            img3CMat = imageMat.clone();
        }
        Mat facePosMat = new Mat();
        FaceDetectorYN faceDetector = getFaceDetectorYN(img3CMat.width(), img3CMat.height());
        faceDetector.detect(img3CMat, facePosMat);
        synchronized (FaceDetectorYNLock) {
            recycleFaceDetectorYN(faceDetector);
        }
        FaceDataUtils.releaseMat(img3CMat);
        if (facePosMat.rows() < 1) {
            return null;
        }
        return facePosMat;
    }

    private FaceDetectorYN getFaceDetectorYN(double inputWidth, double inputHeight) {
        synchronized (FaceDetectorYNLock) {
            FaceDetectorYN faceDetector;
            if (mFaceDetectorList.size() < 1) {
                faceDetector = FaceDetectorYN.create(detectModeFilePath, "",
                        new Size(inputWidth, inputHeight),
                        score_thresh, nms_thresh, top_k);
                mFaceDetectorList.add(faceDetector);
            }
            faceDetector = mFaceDetectorList.remove(0);
            faceDetector.setInputSize(new Size(inputWidth, inputHeight));
            return faceDetector;
        }
    }

    private final int MAX_FACE_DETECTOR = 8;

    private void recycleFaceDetectorYN(FaceDetectorYN faceDetector) {
        /**
         * FaceDetectorYN finalize时容易报FinalizerDaemon错误
         * 1. 通过控制线程访问数量
         * 2. synchronized同步块包裹方法调用
         * 上面的方式似乎可以减少甚至不会出现该问题（目前没有找到原因）
         * 提高MAX_FACE_DETECTOR，尽量减少finalize频度避免问题（线程访问数量减少后，也就没有必要进行finalize了）
         */
        if (mFaceDetectorList.size() > MAX_FACE_DETECTOR) {
            Log.d(TAG, "There are too many FaceDetector, count:" + mFaceDetectorList.size());
            boolean exception = false;
            while (mFaceDetectorList.size() > MAX_FACE_DETECTOR / 4 && !exception) {
                FaceDetectorYN recycleDetector = mFaceDetectorList.remove(0);
                try {
                    recycleDetector.finalize();
                    Log.d(TAG, "There are too many FaceDetector, finalize the extra ones");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    exception = true;
                }
            }
        }
        mFaceDetectorList.add(faceDetector);
    }
}
