package com.pine.app.lib.face.detect.normal.opencv;

import android.graphics.Bitmap;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.normal.ICameraFaceDetector;
import com.pine.app.lib.face.matcher.FaceMatcher;

import java.util.ArrayList;
import java.util.List;

public class OpencvCameraFaceDetector implements ICameraFaceDetector {

    private DetectConfig mDetectConfig;
    private CameraSurfaceParams mCameraSurfaceParams;

    @Override
    public void onCameraInit(int captureBitmapWidth, int captureBitmapHeight,
                             CameraSurfaceParams surfaceParams, DetectConfig config) {
        mCameraSurfaceParams = surfaceParams;
        mDetectConfig = config;

    }

    @Override
    public List<FacePosDetail> detectFaces(byte[] yuv) {
        return null;
    }

    @Override
    public FacePosDetail detectMainFace(byte[] yuv) {
        return null;
    }

    @Override
    public FacePosDetail detectMainFace(Bitmap captureBitmap) {
        List<FacePosDetail> faces = detectFaces(captureBitmap);
        if (faces == null || faces.size() < 1) {
            return null;
        }
        return faces.get(0);
    }

    @Override
    public List<FacePosDetail> detectFaces(Bitmap captureBitmap) {
        if (captureBitmap == null) {
            return null;
        }
        List<FacePosDetail> list = FaceMatcher.getInstance().toFacePosDetails(captureBitmap);
        List<FacePosDetail> faces = new ArrayList<>();
        if (list != null) {
            for (FacePosDetail item : list) {
                FacePosDetail facePosDetail = toFaceFeature(item);
                if (facePosDetail != null) {
                    faces.add(facePosDetail);
                }
            }
        }

        return faces;
    }

    private FacePosDetail toFaceFeature(FacePosDetail face) {
        if (face == null) {
            return null;
        }
        FacePosDetail realFacePosDetail = face.createBySimple(mDetectConfig.Simple);
        int tX = mCameraSurfaceParams.translationX;
        int tY = mCameraSurfaceParams.translationY;
        if (mDetectConfig.rlMirror) {
            realFacePosDetail.midPointX = mCameraSurfaceParams.frameWidth - realFacePosDetail.midPointX;
        }
        realFacePosDetail.midPointX = realFacePosDetail.midPointX + tX;
        realFacePosDetail.midPointY = realFacePosDetail.midPointY + tY;

        return realFacePosDetail;
    }
}
