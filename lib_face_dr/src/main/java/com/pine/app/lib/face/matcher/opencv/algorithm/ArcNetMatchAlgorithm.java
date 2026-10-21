package com.pine.app.lib.face.matcher.opencv.algorithm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.opencv.FaceDataUtils;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.FaceDetectorYN;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ArcNetMatchAlgorithm extends MatchAlgorithm {
    private Object FaceDetectorYNLock = new Object();
    private volatile boolean mInit = false;
    private LinkedList<FaceDetectorYN> mFaceDetectorList = new LinkedList<>(); // OpenCV的人脸检测器
    private String detectModeFilePath = "";

    // 检测参数保持原样
    private float score_thresh = 0.75f;
    private float nms_thresh = 0.3f;
    private int top_k = 100;

    // ===================== ArcFace‑R50 配置 =====================
    private Net mArcFaceNet;
    // ArcFace 余弦阈值，根据业务微调 0.40 ~ 0.48
    double cosine_similarity_threshold = 0.45;

    // ArcFace标准5点模板(112×112)，顺序：右眼，左眼，鼻尖，右嘴角，左嘴角
    private static final Point[] STD_5PTS = {
            new Point(38.2946, 51.6963),
            new Point(73.5318, 51.5014),
            new Point(56.0252, 71.7366),
            new Point(41.5493, 92.3655),
            new Point(70.7299, 92.2041)
    };
    private static final Size FACE_OUT_SIZE = new Size(112, 112);
    private static final Scalar MEAN = new Scalar(127.5, 127.5, 127.5);
    private static final double SCALE = 1.0 / 127.5;

    private boolean checkOpenCvPrepared() {
        return mInit && mArcFaceNet != null;
    }

    @Override
    public boolean init(Context context) {
        Log.d(TAG, "init begin, use ArcFace‑R50 w600k_r50.onnx");
        String detectModeFileName = "face_detection_yunet_2022mar.onnx";
        // 替换为arcface模型
        String recognitionModeFileName = "w600k_r50.onnx";

        File faceModeDir = context.getDir("facemode", Context.MODE_PRIVATE);
        File detectModeFile = new File(faceModeDir, detectModeFileName);
        copyRawFile(context, detectModeFileName, "raw", detectModeFile);
        File recognitionModeFile = new File(faceModeDir, recognitionModeFileName);
        if(!copyRawFile(context, recognitionModeFileName, "raw", recognitionModeFile)) {
            String msg  = "请先将ExtraFiles下的w600k_r50压缩文件解压到lib_face_dr模块的raw资源目录下";
            Log.w(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            return false;
        }

        if (detectModeFile.isFile() && detectModeFile.exists()
                && recognitionModeFile.isFile() && recognitionModeFile.exists()) {
            detectModeFilePath = detectModeFile.getAbsolutePath();
            try {
                InputStream is = new FileInputStream(recognitionModeFile);
                byte[] modelBytes = readStreamToBytes(is);
                is.close();

                MatOfByte mob = new MatOfByte(modelBytes);
                mArcFaceNet = Dnn.readNetFromONNX(mob);
                mob.release();

                // Android端使用CPU，关闭OpenCL避免兼容性问题
                mArcFaceNet.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
                mArcFaceNet.setPreferableTarget(Dnn.DNN_TARGET_CPU);
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "load w600k_r50.onnx error", e);
                mInit = false;
                return false;
            }
            mInit = true;
        } else {
            Log.d(TAG, "mode file not exist, init fail");
            mInit = false;
        }
        faceModeDir.delete();
        return mInit;
    }

    /**
     * 自定义仿射对齐，替代原 mFaceRecognizer.alignCrop
     * @param srcImg 原图BGR Mat
     * @param faceRowMat 单个人脸行Mat(1行15列float)，来自facePosMat.row(index)
     * @return 对齐后112×112 BGR Mat
     */
    private Mat alignCropCustom(Mat srcImg, Mat faceRowMat) {
        float[] row = new float[15];
        faceRowMat.get(0, 0, row);
        // YunNet输出关键点顺序：右眼、左眼、鼻尖、右嘴角、左嘴角
        Point[] srcPts = new Point[5];
        srcPts[0] = new Point(row[4], row[5]);
        srcPts[1] = new Point(row[6], row[7]);
        srcPts[2] = new Point(row[8], row[9]);
        srcPts[3] = new Point(row[10], row[11]);
        srcPts[4] = new Point(row[12], row[13]);

        // 计算相似变换矩阵
        Mat transform = calcSimilarityTransform(srcPts, STD_5PTS);
        Mat aligned = new Mat(FACE_OUT_SIZE, CvType.CV_8UC3);
        Imgproc.warpAffine(srcImg, aligned, transform, FACE_OUT_SIZE);

        transform.release();
        return aligned;
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
        Mat oneFaceRow = facePosMat.row(index);
        Mat alignMat = alignCropCustom(imgMat, oneFaceRow);

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
            Log.d(TAG, "getFeatureMat not init");
            return null;
        }
        if (faceImgMat == null || faceImgMat.empty()) {
            return null;
        }
        // blobFromImage：BGR输入，swapRB=false
        Mat blob = Dnn.blobFromImage(faceImgMat, SCALE, FACE_OUT_SIZE, MEAN, false, false);
        mArcFaceNet.setInput(blob);
        Mat featureRaw = mArcFaceNet.forward();

        Mat normFeature = l2NormalizeMat(featureRaw);
        Mat cloneFeature = normFeature.clone();

        FaceDataUtils.releaseMat(blob);
        FaceDataUtils.releaseMat(featureRaw);
        FaceDataUtils.releaseMat(normFeature);
        return cloneFeature;
    }

    /** L2归一化，输出1×512 CV_32F Mat */
    private Mat l2NormalizeMat(Mat inMat) {
        float[] arr = new float[512];
        inMat.get(0, 0, arr);
        double sumSquare = 0;
        for (float v : arr) {
            sumSquare += v * v;
        }
        double norm = Math.sqrt(sumSquare);
        if (norm > 1e-6) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] /= norm;
            }
        }
        Mat out = new Mat(1, 512, CvType.CV_32F);
        out.put(0, 0, arr);
        return out;
    }

    @Override
    public double matchFaceFeature(Mat checkFeature, Mat standardFeature) {
        if (!checkOpenCvPrepared()) {
            return -1.0f;
        }
        if (checkFeature == null || standardFeature == null
                || checkFeature.rows() != 1 || standardFeature.rows() != 1) {
            return -1.0;
        }
        float[] f1 = new float[512];
        float[] f2 = new float[512];
        checkFeature.get(0, 0, f1);
        standardFeature.get(0, 0, f2);
        double dot = 0.0;
        for (int i = 0; i < 512; i++) {
            dot += f1[i] * f2[i];
        }
        return dot;
    }

    @Override
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        if (maxSimilarDegree < 0) {
            return true;
        }
        if (degree < 0) {
            return false;
        }
        // ArcFace只用余弦：值越大越相似
        return degree > maxSimilarDegree;
    }

    @Override
    public boolean passConfidence(double maxSimilarDegree, float confidence) {
        if (maxSimilarDegree < 0) {
            return false;
        }
        int calNode = 80;
        double dynamicThreshold = cosine_similarity_threshold;
        if (confidence > calNode) {
            dynamicThreshold = cosine_similarity_threshold
                    + (0.8 - cosine_similarity_threshold) * (confidence - calNode) / (100 - calNode);
        }
        boolean pass = maxSimilarDegree >= dynamicThreshold;
        Log.d(TAG, "ArcFace passConfidence:" + pass
                + "; confidence:" + confidence
                + ", maxSimilarDegree:" + maxSimilarDegree
                + ", dynamicThreshold:" + dynamicThreshold);
        return pass;
    }

    // ========== 下面检测相关代码完全原样保留，无修改 ==========
    private Mat getFacePosMat(Mat imageMat) {
        if (imageMat == null || imageMat.empty()) {
            Log.d(TAG, "getFacePosMat image is null or empty");
            return null;
        }
        Mat img3CMat = null;
        if (imageMat.channels() != 3) {
            img3CMat = new Mat();
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

    /**
     * 【无OpenCV calib3d依赖】手动求解相似变换（等价estimateAffinePartial2D，无RANSAC，5点人脸对齐）
     * @param srcPts 源5个关键点
     * @param dstPts 目标标准5点
     * @return 2×3 Mat 变换矩阵
     */
    private Mat calcSimilarityTransform(Point[] srcPts, Point[] dstPts) {
        int n = srcPts.length;
        double sumX1 = 0, sumY1 = 0;
        double sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            sumX1 += srcPts[i].x;
            sumY1 += srcPts[i].y;
            sumX2 += dstPts[i].x;
            sumY2 += dstPts[i].y;
        }
        double meanX1 = sumX1 / n;
        double meanY1 = sumY1 / n;
        double meanX2 = sumX2 / n;
        double meanY2 = sumY2 / n;

        double s1 = 0, s2 = 0;
        double a = 0, b = 0;
        for (int i = 0; i < n; i++) {
            double x1 = srcPts[i].x - meanX1;
            double y1 = srcPts[i].y - meanY1;
            double x2 = dstPts[i].x - meanX2;
            double y2 = dstPts[i].y - meanY2;
            a += x1 * x2 + y1 * y2;
            b += -x1 * y2 + y1 * x2;
            s1 += x1 * x1 + y1 * y1;
        }
        double scaleR = Math.sqrt(a * a + b * b);
        double s = scaleR / s1;
        double theta = Math.atan2(b, a);
        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);

        // 2x3 相似变换矩阵 [ s*cosθ  -s*sinθ  tx;  s*sinθ   s*cosθ  ty ]
        Mat mat = new Mat(2, 3, CvType.CV_64FC1);
        double tx = meanX2 - s * (cosT * meanX1 - sinT * meanY1);
        double ty = meanY2 - s * (sinT * meanX1 + cosT * meanY1);
        mat.put(0, 0, s * cosT);
        mat.put(0, 1, -s * sinT);
        mat.put(0, 2, tx);
        mat.put(1, 0, s * sinT);
        mat.put(1, 1, s * cosT);
        mat.put(1, 2, ty);
        return mat;
    }

    /**
     * 读取输入流到byte数组
     */
    private byte[] readStreamToBytes(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }
}