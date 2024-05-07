package com.pine.app.lib.face.detect.normal;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.FaceRange;

import java.util.List;

public interface IFaceRectView {

    void setupDetectConfig(@NonNull DetectConfig detectConfig);

    /**
     * @param facePosDetails 人脸特征参数
     * @param config         DetectConfig
     */
    void drawFacesBorder(List<FacePosDetail> facePosDetails, DetectConfig config,
                         int innerFrameW, int innerFrameH);

    //清除边框线
    void clearBorder();

    List<FaceRange> getFaceRangList();
}
