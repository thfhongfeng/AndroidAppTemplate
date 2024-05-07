package com.pine.app.lib.face.matcher;

import android.content.Context;
import android.graphics.Bitmap;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.matcher.opencv.OcvFaceMatcher;

import java.util.List;

public class FaceMatcher implements IFaceMatcher {

    private static FaceMatcher instance;
    private static IFaceMatcher mProxy;

    private FaceMatcher() {
        mProxy = new OcvFaceMatcher();
    }

    public synchronized static FaceMatcher getInstance() {
        if (instance == null) {
            instance = new FaceMatcher();
        }
        return instance;
    }

    @Override
    public void initFaceMatcher(Context context) {
        mProxy.initFaceMatcher(context);
    }

    @Override
    public double compareImg(String checkFaceFilePath, String standardFaceFilePath) {
        return mProxy.compareImg(checkFaceFilePath, standardFaceFilePath);
    }

    @Override
    public double compareImg(String checkFaceFilePath, byte[] matBytes) {
        return mProxy.compareImg(checkFaceFilePath, matBytes);
    }

    @Override
    public boolean prepareCheckCompare(String checkFilePath) {
        return mProxy.prepareCheckCompare(checkFilePath);
    }

    @Override
    public double doCheckCompare(byte[] beCheckMatBytes) {
        return mProxy.doCheckCompare(beCheckMatBytes);
    }

    @Override
    public double doCheckCompare(String beCheckFilePath) {
        return mProxy.doCheckCompare(beCheckFilePath);
    }

    @Override
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
    @Override
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
    @Override
    public boolean passConfidence(double maxSimilarDegree, float confidence) {
        return mProxy.passConfidence(maxSimilarDegree, confidence);
    }

    @Override
    public List<FacePosDetail> toFacePosDetails(Bitmap imgBitmap) {
        return mProxy.toFacePosDetails(imgBitmap);
    }

    @Override
    public byte[] toFaceFeatureBytes(String imgPath) {
        return mProxy.toFaceFeatureBytes(imgPath);
    }

    @Override
    public byte[] toFaceFeatureBytes(Bitmap imgBitmap) {
        return mProxy.toFaceFeatureBytes(imgBitmap);
    }

    @Override
    public byte[] toFaceBytes(String imgPath, String extFormat) {
        return mProxy.toFaceBytes(imgPath, extFormat);
    }

    @Override
    public Bitmap toFaceBitmap(String imgPath, String extFormat) {
        return mProxy.toFaceBitmap(imgPath, extFormat);
    }

    @Override
    public Bitmap toFaceBitmap(Bitmap imgBitmap) {
        return mProxy.toFaceBitmap(imgBitmap);
    }

    @Override
    public Bitmap toFaceBitmap(byte[] faceMatData) {
        return mProxy.toFaceBitmap(faceMatData);
    }
}
