package com.pine.app.lib.face.detect.normal;

import android.graphics.Bitmap;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;

import java.util.List;

public interface ICameraFaceDetector {
    String TAG = "CameraFaceDetector";

    void onCameraInit(int captureBitmapWidth, int captureBitmapHeight,
                      CameraSurfaceParams surfaceParams, DetectConfig config);

    List<FacePosDetail> detectFaces(Bitmap bitmap);

    FacePosDetail detectMainFace(Bitmap bitmap);

    List<FacePosDetail> detectFaces(byte[] yuv);

    FacePosDetail detectMainFace(byte[] yuv);
}
