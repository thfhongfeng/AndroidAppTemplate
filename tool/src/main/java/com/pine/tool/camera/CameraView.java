package com.pine.tool.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class CameraView extends RelativeLayout {

    private CameraTexture cameraTexture;

    public CameraView(Context context) {
        super(context);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        removeAllViews();

        cameraTexture = new CameraTexture(getContext());
        cameraTexture.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cameraTexture.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(cameraTexture.getFrame());
    }

    public void init(@NonNull CameraConfig config, final CameraTexture.ICameraPreparedCallback listener) {
        initView();
        cameraTexture.init(config, listener);
    }

    public void startCameraPreview() {
        if (cameraTexture != null) {
            cameraTexture.startCameraPreview();
        }
    }

    public void stopCameraPreview() {
        if (cameraTexture != null) {
            cameraTexture.stopCameraPreview();
        }
    }

    public void takePicture(final CameraTexture.PicCallback callback) {
        if (cameraTexture != null) {
            cameraTexture.takePicture(callback);
        }
    }

    public synchronized void listenFrameData(final ICameraCallback.PreviewCallback callback) {
        if (cameraTexture != null) {
            cameraTexture.listenFrameData(callback);
        }
    }

    public synchronized void unListenFrameData() {
        if (cameraTexture != null) {
            cameraTexture.unListenFrameData();
        }
    }

    public boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback) {
        if (cameraTexture != null) {
            return cameraTexture.startRecording(config, callback);
        }
        return false;
    }

    public void completeRecording(boolean resumePreview) {
        if (cameraTexture != null) {
            cameraTexture.completeRecording(resumePreview);
        }
    }

    public void stopRecording(boolean resumePreview) {
        if (cameraTexture != null) {
            cameraTexture.stopRecording(resumePreview);
        }
    }

    public void stop() {
        stopCameraPreview();
    }

    public void release() {
        if (cameraTexture != null) {
            cameraTexture.release();
        }
        removeAllViews();
    }
}
