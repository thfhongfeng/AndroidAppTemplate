package com.pine.app.lib.face.detect.normal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.detect.CameraHelper;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.FaceRange;
import com.pine.app.lib.face.detect.ICameraCallback;
import com.pine.app.lib.face.detect.RecordConfig;
import com.pine.app.lib.face.detect.normal.google.GoogleCameraFaceDetector;
import com.pine.app.lib.face.detect.normal.minivision.MiniVisionCameraFaceDetector;
import com.pine.app.lib.face.detect.normal.opencv.OpencvCameraFaceDetector;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FaceTextureView extends TextureView implements View.OnLayoutChangeListener {
    private String TAG = this.getClass().getSimpleName();

    /**
     * frame为innerFrame容器，innerFrame为TextureView容器
     * FaceTextureView与innerFrame宽高保持一致
     * innerFrame在initCameraParam时会被放大从而以溢出方式用图像铺满整个frame。从而避免预览界面出现黑边或变形的情况
     */
    private LinearLayout frame = null;
    private RelativeLayout innerFrame = null;
    private int mInnerFrameWidth, mInnerFrameHeight;

    private CameraSurfaceParams mCameraSurfaceParams;

    private volatile int captureBitmapWidth, captureBitmapHeight;
    private Object syncLock = new Object();

    private DetectConfig config = new DetectConfig("");
    private IFaceRectView faceRectView;//人脸检测绘制框，不指定的话，初始化摄像头是默认使用一个
    private volatile FaceRange mFaceValidRange = new FaceRange();
    private ExecutorService executorService = null;

    private ICameraFaceDetector mFaceDetector;

    private CameraHelper mCameraHelper = CameraHelper.getInstance();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public FaceTextureView(Context context) {
        super(context);
        initView();
    }

    public FaceTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        innerFrame = new RelativeLayout(getContext());
        innerFrame.addView(this, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frame = new LinearLayout(getContext());
        frame.addView(innerFrame);
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
        shutDownExecutor();
        // 根据设备性能确定线程数量
        executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(2),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        boolean setup = mCameraHelper.setupMainSurfaceView(
                FaceTextureView.this, innerFrame, getConfig(),
                frameWidth, frameHeight, new ICameraCallback.ICameraSetListener() {
                    @Override
                    public boolean onParamSet(@NonNull CameraSurfaceParams params) {
                        Log.d(TAG, "onParamSet:" + params);
                        mCameraSurfaceParams = params;
                        mInnerFrameWidth = params.frameWidth;
                        mInnerFrameHeight = params.frameHeight;

                        setSurfaceTextureListener(surfaceTextureListener);
                        initOthers();
                        if (framePreViewListener != null) {
                            framePreViewListener.onParamSet(params);
                        }
                        if (getConfig().cameraDetectProvider == DetectConfig.DETECT_PROVIDER_MINI_VISION) {
                            mCameraHelper.listenFrameData(new ICameraCallback.PreviewCallback() {
                                @Override
                                public void onPreviewFrame(byte[] data) {
                                    if (data != null && checkDetectAccess()) {
                                        executorService.execute(new FaceCapturedRunnable(data));
                                    }
                                }
                            });
                        }

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

    private void initOthers() {
        switch (getConfig().cameraDetectProvider) {
            case DetectConfig.DETECT_PROVIDER_OPENCV:
                mFaceDetector = new OpencvCameraFaceDetector();
                Log.d(TAG, "use opencv for face detect");
                break;
            case DetectConfig.DETECT_PROVIDER_MINI_VISION:
                mFaceDetector = new MiniVisionCameraFaceDetector(getContext());
                Log.d(TAG, "use mini vision for face detect");
                break;
            default:
                mFaceDetector = new GoogleCameraFaceDetector();
                Log.d(TAG, "use google for face detect");
                break;
        }
        Log.d(TAG, "initOthers mInnerFrameWidth:" + mInnerFrameWidth
                + ", mInnerFrameHeight:" + mInnerFrameHeight);
        if (mInnerFrameWidth > 0 && mInnerFrameHeight > 0) {
            int scaleW = mInnerFrameWidth;
            int scaleH = mInnerFrameHeight;
            // 适配了采样率，后续相关计算需要考虑采样率
            int with = (int) (scaleW * getConfig().Simple);
            int height = (int) (scaleH * getConfig().Simple);
            // captureBitmap宽度必须为偶数，否则FaceDetect会报错
            if (with % 2 != 0) {
                with = with - 1;
            }
            captureBitmapWidth = with;
            captureBitmapHeight = height;
            mFaceDetector.onCameraInit(captureBitmapWidth, captureBitmapHeight,
                    mCameraSurfaceParams, getConfig());
        }
    }

    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable ,width:" + width + " ,height:" + height);
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
            if (getConfig().cameraDetectProvider == DetectConfig.DETECT_PROVIDER_MINI_VISION) {
                return;
            }
            if (captureBitmapWidth > 0 && captureBitmapHeight > 0 && checkDetectAccess()) {
                Bitmap captureBitmap = Bitmap.createBitmap(captureBitmapWidth, captureBitmapHeight, Bitmap.Config.RGB_565);
                /* TextureView的getBitmap方法不要在子线程中调用，否则多线程问题可能会造成crash:
                 * signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x78
                 * Cause: null pointer dereference
                 * pc 000000000023438c  /system/lib64/libhwui.so (android::uirenderer::DeferredLayerUpdater::apply()+40)
                 */
                getBitmap(captureBitmap);
                executorService.execute(new FaceCapturedRunnable(captureBitmap));
            }
        }
    };

    private boolean checkDetectAccess() {
        if (getConfig().getEnableFaceDetect()) {
            //here to preview each frame
            long currentTime = System.currentTimeMillis();
            long detectTime = getConfig().MinDetectTime;
            if (getConfig().EnableIdleSleepOption) {
                if (currentTime - getConfig().PreFaceTime > getConfig().IdleSleepOptionJudgeTime) {
                    detectTime = getConfig().MaxDetectTime;
                    // Log.i(tag, "进入空闲休眠检测状态");
                }
            }
            if (currentTime - getConfig().PreDetectTime >= detectTime) {
                getConfig().PreDetectTime = currentTime;
                return true;
            }
        }
        return false;
    }

    private class FaceCapturedRunnable implements Runnable {

        private Bitmap captureBitmap;
        private byte[] yuv;
        private boolean yuvMode;

        public FaceCapturedRunnable(Bitmap captureBitmap) {
            this.captureBitmap = captureBitmap;
            yuvMode = false;
        }

        public FaceCapturedRunnable(byte[] yuv) {
            this.yuv = yuv;
            yuvMode = true;
        }

        private boolean dataValid() {
            boolean valid = false;
            if (yuvMode) {
                valid = yuv != null;
            } else {
                valid = captureBitmap != null;
            }
            return valid;
        }

        private Bitmap yuv2Bitmap() {
            if (yuv != null) {
                try {
                    YuvImage img = new YuvImage(yuv, ImageFormat.NV21, mCameraSurfaceParams.preWidth,
                            mCameraSurfaceParams.preHeight, null);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img.compressToJpeg(new Rect(0, 0, mCameraSurfaceParams.preWidth,
                            mCameraSurfaceParams.preHeight), 100, stream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    stream.close();
                    Bitmap copiedBitmap = bitmap.copy(bitmap.getConfig(), true);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    return copiedBitmap;
                } catch (Exception ex) {
                    Log.e(TAG, "yuv2Bitmap error:" + ex.getMessage());
                }
            }
            return null;
        }

        @Override
        public void run() {
            if (mFaceDetector == null || !dataValid() || !getConfig().getEnableFaceDetect()
                    || mCameraSurfaceParams == null) {
                if (captureBitmap != null && !captureBitmap.isRecycled()) {
                    captureBitmap.recycle();
                }
                return;
            }
            List<FacePosDetail> facePosDetails = null;
            if (yuvMode) {
                facePosDetails = mFaceDetector.detectFaces(yuv);
                captureBitmap = yuv2Bitmap();
                if (captureBitmap == null) {
                    return;
                }
            } else {
                facePosDetails = mFaceDetector.detectFaces(captureBitmap);
            }
            boolean hasFace = facePosDetails != null && facePosDetails.size() > 0;
            if (hasFace && getConfig().getEnableFaceDetect()) {
                getConfig().PreFaceTime = System.currentTimeMillis();
                List<FaceRange> faceRangeList = null;
                synchronized (syncLock) {
                    if (faceRectView != null) {
                        if (getConfig().getEnableFaceDetect()) {
                            faceRectView.drawFacesBorder(facePosDetails, getConfig(), innerFrame.getWidth(),
                                    innerFrame.getHeight());
                            faceRangeList = faceRectView.getFaceRangList();
                        }
                    }
                }
                boolean faceMatchValid = false;
                synchronized (syncLock) {
                    if (mFaceValidRange != null) {
                        if (faceRangeList != null
                                && mFaceValidRange.matchDetect(faceRangeList, getConfig())) {
                            faceMatchValid = true;
                        }
                    } else {
                        faceMatchValid = true;
                    }
                }
                synchronized (syncLock) {
                    if (faceMatchValid && framePreViewListener != null && captureBitmap != null) {
                        Matrix m = mCameraHelper.getCameraPreviewPicMatrix(yuvMode);
                        Bitmap mutableBitmap = captureBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Bitmap faceBitmap = Bitmap.createBitmap(mutableBitmap, 0, 0,
                                mutableBitmap.getWidth(),
                                captureBitmap.getHeight(), m, true);
                        boolean deal = framePreViewListener.onFaceFrame(faceBitmap, facePosDetails);
                        if (!deal && !faceBitmap.isRecycled()) {
                            faceBitmap.recycle();
                        }
                    }
                }
            } else {
                synchronized (syncLock) {
                    if (faceRectView != null) {
                        faceRectView.clearBorder();
                    }
                }
            }
            if (captureBitmap != null && !captureBitmap.isRecycled()) {
                captureBitmap.recycle();
            }
        }
    }

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
        getConfig().setEnableFaceDetect(false);
        mMainHandler.removeCallbacksAndMessages(null);
        mCameraHelper.release();
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

    public synchronized void release() {
        shutDownExecutor();
        setFaceRectView(null);
        setFramePreViewListener(null);
        stopRecording(false);
        stopCameraPreview();
    }

    private synchronized void shutDownExecutor() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                executorService = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private volatile IFramePreViewListener framePreViewListener;

    public void setFramePreViewListener(IFramePreViewListener framePreViewListener) {
        synchronized (syncLock) {
            this.framePreViewListener = framePreViewListener;
        }
    }

    public void setFaceRectView(IFaceRectView faceRectView) {
        synchronized (syncLock) {
            this.faceRectView = faceRectView;
        }
    }

    public void setFaceValidRange(FaceRange faceValidRange) {
        synchronized (syncLock) {
            mFaceValidRange = faceValidRange;
        }
    }

    //PreView each frame of the camera
    public interface IFramePreViewListener {
        //这个preFrame没次都复制一份出来，记得主动回收
        boolean onFaceFrame(Bitmap faceFrame, List<FacePosDetail> facePosDetails);

        boolean onParamSet(@NonNull CameraSurfaceParams params);
    }

    public interface ICameraPreparedCallback {
        void onCameraPrepared(boolean prepared, CameraSurfaceParams params);
    }
}
