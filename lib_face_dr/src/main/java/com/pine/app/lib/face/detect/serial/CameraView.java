package com.pine.app.lib.face.detect.serial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.ICameraCallback;
import com.pine.app.lib.face.detect.IFaceDetectView;
import com.pine.app.lib.face.detect.IOnFacePicListener;
import com.pine.app.lib.face.detect.RecordConfig;


public class CameraView extends RelativeLayout implements IFaceDetectView {
    private IOnFacePicListener mOnFacePicListener;

    private ISerialActionProxy mSerialActionProxy;

    public CameraView(Context context) {
        super(context);
        initView();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private CameraTextureView textureView;
    private DetectConfig mConfig = new DetectConfig("");

    private void initView() {
        textureView = new CameraTextureView(getContext());
        textureView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(textureView.getFrame());
    }

    @Override
    public void init(@NonNull DetectConfig config, IOnFacePicListener listener) {
        mConfig.merge(config);
        textureView.setConfig(mConfig);
        mOnFacePicListener = listener;
    }

    @Override
    public void resetDetectConfig(@NonNull DetectConfig config) {
        mConfig.mergeChange(config);
    }

    @Override
    public void setFaceMantleCenter(float faceMantleRxWeight, float faceMantleRyWeight) {

    }

    @Override
    public void setFaceMantleAttr(int faceMantleBgColor, int faceMantleRadius, int faceMantleRx, int faceMantleRy) {

    }

    @Override
    public void setSerialActionProxy(@Nullable ISerialActionProxy proxy) {
        mSerialActionProxy = proxy;
    }

    @Override
    public void onSerialActionDone(int actionStep, boolean success) {
        if (success && actionStep == ISerialActionProxy.ACTIONS_START) {
            takePicture(new CameraTextureView.PicCallback() {

                @Override
                public void onPicTaken(String picPath) {
                    if (mOnFacePicListener != null) {
                        mOnFacePicListener.onFacePicSaved(picPath, picPath, picPath);
                    }
                    startCameraPreview();
                }

                @Override
                public void onFail() {
                    if (mOnFacePicListener != null) {
                        mOnFacePicListener.onFacePicSavedFail();
                    }
                    startCameraPreview();
                }
            });
        }
    }

    @Override
    public boolean startFaceDetect() {
        if (mSerialActionProxy != null) {
            return mSerialActionProxy.onSerialAction(ISerialActionProxy.ACTIONS_START);
        }
        return false;
    }

    @Override
    public void stopFaceDetect() {

    }

    public void startCameraPreview() {
        if (textureView != null) {
            textureView.startCameraPreview();
        }
    }

    public void stopCameraPreview() {
        if (textureView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    textureView.stopCameraPreview();
                }
            });
        }
    }

    @Override
    public boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback) {
        if (textureView != null) {
            return textureView.startRecording(config, callback);
        }
        if (callback != null) {
            callback.onRecordFail();
        }
        return false;
    }

    @Override
    public void completeRecording(boolean resumePreview) {
        if (textureView != null) {
            textureView.completeRecording(resumePreview);
        }
    }

    @Override
    public void stopRecording(boolean resumePreview) {
        if (textureView != null) {
            textureView.stopRecording(resumePreview);
        }
    }

    @Override
    public void stop() {
        stopRecording(false);
        stopFaceDetect();
        stopCameraPreview();
    }

    public void release() {
        if (textureView != null) {
            textureView.release();
        }
        removeAllViews();
    }

    public void takePicture(final CameraTextureView.PicCallback callback) {
        if (textureView != null) {
            textureView.takePicture(callback);
        }
    }

    public DetectConfig getConfig() {
        return textureView == null ? null : textureView.getConfig();
    }
}
