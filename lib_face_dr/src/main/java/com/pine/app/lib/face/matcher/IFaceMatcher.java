package com.pine.app.lib.face.matcher;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.FacePosDetail;

import java.util.List;

public interface IFaceMatcher {
    String TAG = "FaceMatcher";

    void initFaceMatcher(@NonNull Context context);

    double compareImg(String checkFaceFilePath, String standardFaceFilePath);

    double compareImg(String faceFilePath, byte[] matBytes);

    // 用户一对多对比情况
    // prepareCheckCompare->doCheckCompare->completeCheckCompare
    boolean prepareCheckCompare(String checkFilePath);

    // 用户一对多对比情况
    // prepareCheckCompare->doCheckCompare->completeCheckCompare
    double doCheckCompare(byte[] beCheckMatBytes);

    double doCheckCompare(String beCheckFilePath);

    // 用户一对多对比情况
    // prepareCheckCompare->doCheckCompare->completeCheckCompare
    boolean completeCheckCompare();

    /**
     * 通过比较以后，最大相似度的评价值是否改变
     *
     * @param maxSimilarDegree 最大相似度评价值（值与相似度关系由比较方式决定）
     * @param degree           评价值（值与相似度关系由比较方式决定）
     * @return
     */
    boolean maxSimilarChange(double maxSimilarDegree, double degree);

    /**
     * maxSimilarDegree是否符合可信度要求
     *
     * @param maxSimilarDegree 最大相似度（degree的值与相似度关系由比较方式决定）
     * @param confidence       0-100，数值越高越可信
     * @return
     */
    boolean passConfidence(double maxSimilarDegree, float confidence);

    byte[] toFaceFeatureBytes(String imgPath);

    byte[] toFaceFeatureBytes(Bitmap imgBitmap);

    List<FacePosDetail> toFacePosDetails(Bitmap imgBitmap);

    byte[] toFaceBytes(String imgPath, String extFormat);

    Bitmap toFaceBitmap(String imgPath, String extFormat);

    Bitmap toFaceBitmap(Bitmap imgBitmap);

    Bitmap toFaceBitmap(byte[] faceMatData);
}
