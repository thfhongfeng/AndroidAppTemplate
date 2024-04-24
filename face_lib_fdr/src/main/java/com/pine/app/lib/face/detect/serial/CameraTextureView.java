package com.pine.app.lib.face.detect.serial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.detect.CameraHelper;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.ICameraCallback;
import com.pine.app.lib.face.detect.PicSaver;
import com.pine.app.lib.face.detect.RecordConfig;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTextureView extends TextureView implements View.OnLayoutChangeListener {
    private String TAG = "CameraTextureView";

    /**
     * frame为innerFrame容器，innerFrame为TextureView容器
     * FaceTextureView与innerFrame宽高保持一致
     * innerFrame在initCameraParam时会被放大从而以溢出方式用图像铺满整个frame。从而避免预览界面出现黑边或变形的情况
     */
    private LinearLayout frame = null;
    private RelativeLayout innerFrame = null;
    private DetectConfig config = new DetectConfig("");

    private int mInnerFrameWidth, mInnerFrameHeight;

    private CameraHelper mCameraHelper = CameraHelper.getInstance();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public CameraTextureView(Context context) {
        super(context);
        initView();
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        innerFrame = new RelativeLayout(getContext());
        innerFrame.addView(this);
        frame = new LinearLayout(getContext());
        frame.addView(innerFrame);
    }

    public void init(DetectConfig config, ICameraPreparedCallback listener) {
        setConfig(config);
        setCameraPreparedListener(listener);
    }

    public LinearLayout getFrame() {
        return frame;
    }

    public DetectConfig getConfig() {
        return config;
    }

    public void setConfig(DetectConfig config) {
        this.config = config;
    }

    private ICameraPreparedCallback mCameraPreparedListener;

    public void setCameraPreparedListener(ICameraPreparedCallback listener) {
        mCameraPreparedListener = listener;
    }

    //初始化摄像头
    private void initCamera(boolean force, final ICameraPreparedCallback listener) {
        boolean needForce = force && mInnerFrameWidth != getWidth() && mInnerFrameHeight != getHeight();
        Log.d(TAG, "initCamera force:" + force + ", needForce:" + needForce);
        if (!needForce && mCameraHelper.isCameraPrepared()) {
            if (listener != null) {
                listener.onCameraPrepared(true, mCameraHelper.getCameraSurfaceParams());
            }
            return;
        }
        if (!needForce && mCameraHelper.isCameraInit()) {
            prepareCameraView(listener);
            return;
        }
        mCameraHelper.initCamera(getContext(), getConfig(), new ICameraCallback.ICameraInitListener() {
            @Override
            public void onCameraInit(boolean success) {
                Log.d(TAG, "onCameraInit:" + success);
                if (success) {
                    prepareCameraView(listener);
                } else {
                    if (listener != null) {
                        listener.onCameraPrepared(false, null);
                    }
                }
            }

            @Override
            public void onCameraInitProcessing() {
                if (listener != null) {
                    listener.onCameraPrepared(false, null);
                }
            }
        });
    }

    private void prepareCameraView(final ICameraPreparedCallback listener) {
        if (!mCameraHelper.isCameraInit()) {
            if (listener != null) {
                listener.onCameraPrepared(false, null);
            }
            return;
        }
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        boolean setup = mCameraHelper.setupMainSurfaceView(
                CameraTextureView.this, innerFrame, getConfig(),
                frameWidth, frameHeight, new ICameraCallback.ICameraSetListener() {
                    @Override
                    public boolean onParamSet(@NonNull CameraSurfaceParams params) {
                        Log.d(TAG, "onParamSet:" + params);
                        mInnerFrameWidth = params.frameWidth;
                        mInnerFrameHeight = params.frameHeight;

                        setSurfaceTextureListener(surfaceTextureListener);
                        if (listener != null) {
                            listener.onCameraPrepared(true, params);
                        }
                        return false;
                    }
                });
        if (!setup) {
            if (listener != null) {
                listener.onCameraPrepared(false, null);
            }
        }
    }

    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable, width:" + width + ",height:" + height);
            startCameraPreview(true);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged, width:" + width + ",height:" + height);
            startCameraPreview(true);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d(TAG, "onSurfaceTextureDestroyed");
            stopCameraPreview();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public void startCameraPreview() {
        startCameraPreview(false);
    }

    public void startCameraPreview(final boolean force) {
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                initCamera(force, new ICameraPreparedCallback() {
                    @Override
                    public void onCameraPrepared(boolean prepared, CameraSurfaceParams params) {
                        if (prepared) {
                            mCameraHelper.startCameraPreview();
                        }
                        if (mCameraPreparedListener != null) {
                            mCameraPreparedListener.onCameraPrepared(prepared, params);
                        }
                    }
                });
            }
        });
    }

    public void stopCameraPreview() {
        mMainHandler.removeCallbacksAndMessages(null);
        mCameraHelper.stopCameraPreview();
        mCameraHelper.release();
    }

    public synchronized void listenFrameData(final ICameraCallback.PreviewCallback callback) {
        mCameraHelper.listenFrameData(callback);
    }

    public synchronized void unListenFrameData() {
        mCameraHelper.unListenFrameData();
    }

    public boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback) {
        return mCameraHelper.startRecording(config, callback);
    }

    public void completeRecording(boolean resumePreview) {
        mCameraHelper.completeRecording(resumePreview);
    }

    public void stopRecording(boolean resumePreview) {
        mCameraHelper.stopRecording(resumePreview);
    }

    public void release() {
        stopRecording(false);
        stopCameraPreview();
    }

    public interface PicCallback {
        void onPicTaken(String picPath);

        void onFail();
    }

    /**
     * 拍摄照片
     *
     * @param callback
     */
    public void takePicture(final PicCallback callback) {
        mCameraHelper.takePicture(new ICameraCallback.TakePicListener() {
            @Override
            public void onPictureTaken(Bitmap bitmap) {
                Log.d(TAG, "takePicture bitmap:" + bitmap);
                mExecutorService.submit(new SavePicRunnable(bitmap, callback));
            }

            @Override
            public void onFail() {
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }

    private class SavePicRunnable implements Runnable {
        Bitmap bitmap;
        PicCallback listener;

        SavePicRunnable(Bitmap bitmap, PicCallback listener) {
            this.bitmap = bitmap;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                if (bitmap != null) {
                    String picPath = getConfig().savePicFilePath;
                    if (TextUtils.isEmpty(picPath)) {
                        picPath = getContext().getExternalCacheDir().getAbsolutePath()
                                + File.separator + "pic_camera_taken.jpg";
                    }
                    if (listener != null) {
                        if (PicSaver.saveFacePicToLocal(picPath, bitmap)) {
                            final String finalPicPath = picPath;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onPicTaken(finalPicPath);
                                    }
                                }
                            });
                            return;
                        }
                    }
                }
            } catch (Exception exc) {
                Log.d(TAG, "savePicture: exception = " + exc.toString());
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            });
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public interface ICameraPreparedCallback {
        void onCameraPrepared(boolean prepared, CameraSurfaceParams params);
    }
}
