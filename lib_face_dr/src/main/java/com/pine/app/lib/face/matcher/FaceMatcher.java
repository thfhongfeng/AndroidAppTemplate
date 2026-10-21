package com.pine.app.lib.face.matcher;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.opencv.OcvFaceMatcher;

import java.util.List;

public class FaceMatcher {

    private static FaceMatcher instance;
    private static IFaceMatcher mProxy;

    private FaceMatcher() {
    }

    public synchronized static FaceMatcher getInstance() {
        if (instance == null) {
            instance = new FaceMatcher();
        }
        return instance;
    }

    public void initFaceMatcher(@NonNull Context context) {
        mProxy = new OcvFaceMatcher(OcvFaceMatcher.FEATURE_TYPE_SF);
        mProxy.initFaceMatcher(context);
    }

    public void initFaceMatcher(@NonNull Context context, IFaceMatcher matcher) {
        mProxy = matcher;
        mProxy.initFaceMatcher(context);
    }

    public double compareImg(String checkFaceFilePath, String standardFaceFilePath) {
        return mProxy.compareImg(checkFaceFilePath, standardFaceFilePath);
    }

    public double compareImg(String checkFaceFilePath, byte[] matBytes) {
        return mProxy.compareImg(checkFaceFilePath, matBytes);
    }

    public boolean prepareCheckCompare(String checkFilePath) {
        return mProxy.prepareCheckCompare(checkFilePath);
    }

    public double doCheckCompare(byte[] beCheckMatBytes) {
        return mProxy.doCheckCompare(beCheckMatBytes);
    }

    public double doCheckCompare(String beCheckFilePath) {
        return mProxy.doCheckCompare(beCheckFilePath);
    }

    public boolean completeCheckCompare() {
        return mProxy.completeCheckCompare();
    }

    /**
     * 通过比较以后，最大相似度的评价值是否改变
     *
     * @param maxSimilarDegree 最大相似度评价值（值与相似度关系由比较方式决定）
     * @param degree           评价值（值与相似度关系由比较方式决定）
     * @return
     */
    public boolean maxSimilarChange(double maxSimilarDegree, double degree) {
        return mProxy.maxSimilarChange(maxSimilarDegree, degree);
    }

    /**
     * maxSimilarDegree是否符合可信度要求
     *
     * @param maxSimilarDegree 最大相似度（degree的值与相似度关系由比较方式决定）
     * @param confidence       0-100，数值越高越可信
     * @return
     */
    public boolean passConfidence(double maxSimilarDegree, float confidence) {
        return mProxy.passConfidence(maxSimilarDegree, confidence);
    }

    public List<FacePosDetail> toFacePosDetails(Bitmap imgBitmap) {
        return mProxy.toFacePosDetails(imgBitmap);
    }

    public byte[] toFaceFeatureBytes(String imgPath) {
        return mProxy.toFaceFeatureBytes(imgPath);
    }

    public byte[] toFaceFeatureBytes(Bitmap imgBitmap) {
        return mProxy.toFaceFeatureBytes(imgBitmap);
    }

    public byte[] toFaceBytes(String imgPath, String extFormat) {
        return mProxy.toFaceBytes(imgPath, extFormat);
    }

    public Bitmap toFaceBitmap(String imgPath, String extFormat) {
        return mProxy.toFaceBitmap(imgPath, extFormat);
    }

    public Bitmap toFaceBitmap(Bitmap imgBitmap) {
        return mProxy.toFaceBitmap(imgBitmap);
    }

    public Bitmap toFaceBitmap(byte[] faceMatData) {
        return mProxy.toFaceBitmap(faceMatData);
    }
}
