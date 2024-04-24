package com.pine.app.lib.face.matcher.opencv.algorithm;

import android.content.Context;

import androidx.annotation.RawRes;

import com.pine.app.lib.face.FacePosDetail;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public abstract class MatchAlgorithm {
    String TAG = "FaceMatcher";

    public abstract boolean init(Context context);

    public abstract Mat getFaceImgMat(Mat imgMat);

    public abstract List<FacePosDetail> getFacePosDetails(Mat imageMat);

    public abstract Mat getFeatureMat(Mat faceImgMat);

    public abstract double matchFaceFeature(Mat checkFeature, Mat standardFeature);

    /**
     * 通过比较以后，最大相似度的评价值是否改变
     *
     * @param maxSimilarDegree 最大相似度评价值（值与相似度关系由比较方式决定）
     * @param degree           评价值（值与相似度关系由比较方式决定）
     * @return
     */
    public abstract boolean maxSimilarChange(double maxSimilarDegree, double degree);

    /**
     * maxSimilarDegree是否符合可信度要求
     *
     * @param maxSimilarDegree 最大相似度（degree的值与相似度关系由比较方式决定）
     * @param confidence       0-100，数值越高越可信
     * @return
     */
    public abstract boolean passConfidence(double maxSimilarDegree, float confidence);

    public boolean copyRawFile(Context context, String fileName, String resType, File targetFile) {
        return copyRawFile(context, context.getResources().getIdentifier(
                fileName.substring(0, fileName.lastIndexOf(".")),
                resType, context.getPackageName()), targetFile);
    }

    public boolean copyRawFile(Context context, @RawRes int rawId, File targetFile) {
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
}
