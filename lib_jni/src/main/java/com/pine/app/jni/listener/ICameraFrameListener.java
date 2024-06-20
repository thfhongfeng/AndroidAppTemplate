package com.pine.app.jni.listener;

public interface ICameraFrameListener {
    void onFrame(int cameraIndex, byte[] buff, int size, int mtype, int frameIndex, long ts);
}
