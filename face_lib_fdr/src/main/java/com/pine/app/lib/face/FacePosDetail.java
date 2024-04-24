package com.pine.app.lib.face;

public class FacePosDetail {
    //face's mid-point X
    public float midPointX;
    //face's mid-point Y
    public float midPointY;
    // distance between the eyes
    public float eyesDist;
    //confidence factor between 0 and 1
    public float confidence;
    //live confidence factor between 0 and 1
    public float liveConfidence;

    public float width;
    public float height;

    public boolean yuvFrameData;

    public boolean ignoreSimple;

    public FacePosDetail createBySimple(float simple) {
        FacePosDetail facePosDetail = new FacePosDetail();
        facePosDetail.confidence = this.confidence;
        facePosDetail.liveConfidence = this.liveConfidence;
        facePosDetail.yuvFrameData = this.yuvFrameData;
        facePosDetail.ignoreSimple = this.ignoreSimple;
        float realSimple = ignoreSimple ? 1 : simple;
        facePosDetail.eyesDist = this.eyesDist / realSimple;
        facePosDetail.midPointX = this.midPointX / realSimple;
        facePosDetail.midPointY = this.midPointY / realSimple;
        facePosDetail.width = this.width / realSimple;
        facePosDetail.height = this.height / realSimple;
        return facePosDetail;
    }

    @Override
    public String toString() {
        return "FacePosDetail{" +
                "midPointX=" + midPointX +
                ", midPointY=" + midPointY +
                ", eyesDist=" + eyesDist +
                ", confidence=" + confidence +
                ", liveConfidence=" + liveConfidence +
                ", yuvFrameData=" + yuvFrameData +
                ", ignoreSimple=" + ignoreSimple +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
