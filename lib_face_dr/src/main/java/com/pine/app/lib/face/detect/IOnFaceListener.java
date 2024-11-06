package com.pine.app.lib.face.detect;

import android.widget.TextView;

import java.util.List;

public interface IOnFaceListener {

    /**
     * @param filePath         原照片路径（用于人脸处理）
     * @param compressFilePath 压缩照片路径（用于留存痕迹等次要业务操作），两个路径可以根据需求进行使用
     * @param faceCropFilePath 裁剪后的人脸的照片路径
     * @return 是否继续进行Pic保存
     */
    boolean onFacePicSaved(String filePath, String compressFilePath, String faceCropFilePath);

    /**
     * @return 是否继续进行Pic保存
     */
    boolean onFacePicSavedFail();

    /**
     * 获取人脸，对规定范围进行判断的回调（获取过程持续回调）
     *
     * @param mainFaceBorder 最大人脸边框信息
     * @param faceBorders    人脸边框信息集合
     * @param middleTipTv    人脸边框的中心的提示框
     * @return
     */
    boolean onFaceRangeJudged(FaceBorder mainFaceBorder, List<FaceBorder> faceBorders, TextView middleTipTv);
}
