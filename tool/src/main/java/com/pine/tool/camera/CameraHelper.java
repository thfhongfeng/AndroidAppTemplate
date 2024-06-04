package com.pine.tool.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CameraHelper {
    private static String TAG = CameraHelper.class.getSimpleName();

    private static HashMap<Integer, CameraHelper> instanceMap = new HashMap<>();

    public static synchronized CameraHelper getInstance() {
        return getInstance(0);
    }

    public static synchronized CameraHelper getInstance(int cameraIdIndex) {
        CameraHelper instance = instanceMap.get(cameraIdIndex);
        if (instance == null) {
            instance = new CameraHelper(cameraIdIndex);
            instanceMap.put(cameraIdIndex, instance);
        }
        return instance;
    }

    private CameraHelper(int cameraIdIndex) {
        mCameraIdIndex = cameraIdIndex;
        Log.d(TAG, "create camera for: " + cameraIdIndex);
    }

    private String mCameraType = CameraConfig.DEFAULT;
    private int mCameraIdIndex = 0;
    private int mCameraFacing = -1;
    private CameraConfig mCameraConfig;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    private int mDeviceRotation;
    private int mDisplayRotation;

    private CameraSurfaceParams mCameraSurfaceParams;
    private TextureView mTextureView;

    private volatile boolean mCameraInitProcessing;
    private volatile boolean mCameraInit;
    private volatile boolean mMainSurfaceInit;

    public synchronized boolean isCameraInit() {
        return mCamera != null && mCameraInfo != null && mCameraInit;
    }

    public synchronized boolean isCameraPrepared() {
        return isCameraInit() && mMainSurfaceInit;
    }

    public synchronized CameraSurfaceParams getCameraSurfaceParams() {
        return mCameraSurfaceParams;
    }

    public synchronized CameraSurfaceParams getCameraSurfaceParams(@NonNull String tag) {
        return mCameraSurfaceParams;
    }

    public synchronized void initCamera(@NonNull Context context,
                                        @NonNull final CameraConfig config,
                                        ICameraCallback.ICameraInitListener listener) {
        Log.d(TAG, "initCamera config: " + config);
        if (mCameraInitProcessing) {
            if (listener != null) {
                listener.onCameraInitProcessing();
            }
            Log.d(TAG, "initCamera is processing");
            return;
        }
        release();
        mCameraInitProcessing = true;
        mCameraConfig = config;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                mDeviceRotation = 0;
                break;
            case Surface.ROTATION_90:
                mDeviceRotation = 90;
                break;
            case Surface.ROTATION_180:
                mDeviceRotation = 180;
                break;
            case Surface.ROTATION_270:
                mDeviceRotation = 270;
                break;
        }
        openCamera(config, listener);
    }

    private void openCamera(@NonNull final CameraConfig config,
                            ICameraCallback.ICameraInitListener listener) {
        try {
            if (!isSupportCamera()) {
                Log.d(TAG, "openCamera camera is not support");
                if (listener != null) {
                    listener.onCameraInit(false);
                }
                mCameraInitProcessing = false;
                return;
            }
            if (config.cameraIndex >= 0) {
                mCamera = openCamera(config.cameraIndex);
            } else {
                if (TextUtils.equals(mCameraType, CameraConfig.DEFAULT)) {
                    mCameraType = config.cameraType;
                }
                mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                switch (mCameraType) {
                    case CameraConfig.FRONT:
                        mCamera = openFrontCamera();
                        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        break;
                    default:
                        mCamera = openBackCamera();
                        mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                        break;
                }
            }
            //如果都初始化失败了，不区别摄像头类型重新初始化一遍(默认情况下会打开后置摄像头)
            if (mCamera == null) {
                Log.w(TAG, "openCamera ignore camera facing, some problem may happen for camera using");
                mCamera = Camera.open();
                if (mCamera != null) {
                    mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                    mCameraType = CameraConfig.BACK;
                }
            }
            mCameraInfo = getCameraInfo(mCameraFacing);
            boolean openSuccess = mCamera != null && mCameraInfo != null;
            Log.d(TAG, "openCamera success:" + openSuccess + ", mCameraType:" + mCameraType);
            mCameraInit = openSuccess && judgeOrientation(config);
            if (listener != null) {
                listener.onCameraInit(mCameraInit);
            }
            mCameraInitProcessing = false;
            return;
        } catch (Exception e) {
            Log.e(TAG, "openCamera exception:" + e);
            if (listener != null) {
                listener.onCameraInit(false);
            }
            mCameraInitProcessing = false;
        }
    }

    private boolean judgeOrientation(@NonNull CameraConfig config) {
        if (config == null || mCameraInfo == null) {
            return false;
        }
        Log.i(TAG, "judgeOrientation camera sensor orientation:" + mCameraInfo.orientation
                + ", device rotation:" + mDeviceRotation
                + ", config.deviceFixOrientation:" + config.deviceFixOrientation);
        int cameraOrientation = mCameraInfo.orientation;
        int deviceRotation = config.deviceFixOrientation;
        if (deviceRotation == 0 || deviceRotation == 90 || deviceRotation == 180 || deviceRotation == 270) {
            mDeviceRotation = deviceRotation;
        }
        int displayRotation = 0;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayRotation = (cameraOrientation + mDeviceRotation) % 360;
            displayRotation = (360 - displayRotation) % 360;  // compensate the mirror
        } else {  // back-facing
            displayRotation = (cameraOrientation - mDeviceRotation + 360) % 360;
        }
        mDisplayRotation = displayRotation;
        Log.i(TAG, "Camera set displayRotation: " + displayRotation);
        return true;
    }

    public boolean setupMainSurfaceView(@NonNull TextureView textureView,
                                        @NonNull ViewGroup innerFrame,
                                        CameraConfig config,
                                        int containerW, int containerH,
                                        ICameraCallback.ICameraSetListener listener) {
        Log.d(TAG, "setupMainSurfaceView mCameraInit:" + mCameraInit);
        if (config == null || !isCameraInit()) {
            return false;
        }
        // 要以不变化的外部容器的宽高作为基准，而不能用会被放大的innerFrame宽高做为基准
        Log.d(TAG, "setupMainSurfaceView containerW:" + containerW + ",containerH:" + containerH);
        if (containerW <= 0 || containerH <= 0) {
            return false;
        }
        Camera.Parameters cameraParam = mCamera.getParameters();
        Camera.Size preSize = getBestSize(mDisplayRotation, containerW, containerH,
                cameraParam.getSupportedPreviewSizes());
        if (preSize == null) {
            return false;
        }

        if (mCameraSurfaceParams == null) {
            mCameraSurfaceParams = new CameraSurfaceParams(mCameraType, CameraSurfaceParams.MAIN_TAG);
        }

        //设置Pre尺寸
        int preSizeW = config.preWidth;
        int preSizeH = config.preHeight;
        if (preSizeW <= 0 || preSizeH <= 0) {
            preSizeW = preSize.width;
            preSizeH = preSize.height;
        }
        mCameraSurfaceParams.setPreWidth(preSizeW);
        mCameraSurfaceParams.setPreHeight(preSizeH);

        boolean success = judgeSurfaceView(mCameraSurfaceParams, containerW, containerH);
        if (!success) {
            return false;
        }

        mCameraSurfaceParams.setDisplayRotation(mDisplayRotation);

        int yuvDataRotate = mDisplayRotation;
        boolean yuvDataRlMirror = config.rlMirror;
        // 系统打开前置摄像头会自动镜像摄像头数据。
        // TextureView的getBitmap获取的数据是被镜像过的；
        // mDisplayRotation值的确定是以TextureView为基础的，
        // 而Camera的onPreviewFrame获得的图片数据是相机原始数据，是没有被镜像的。
        // 为保持一致，Camera的onPreviewFrame的数据需要再镜像一下（旋转同时也要镜像）。
        if (mCameraType == CameraConfig.FRONT) {
            yuvDataRlMirror = !yuvDataRlMirror;
            yuvDataRotate = (360 - yuvDataRotate) % 360;
        }
        mCameraSurfaceParams.yuvDataRotate = yuvDataRotate;
        mCameraSurfaceParams.yuvDataRlMirror = yuvDataRlMirror;

        Camera.Size picSize = getBestSize(mDisplayRotation,
                mCameraSurfaceParams.frameWidth > 0 ? mCameraSurfaceParams.frameWidth : containerW,
                mCameraSurfaceParams.frameHeight > 0 ? mCameraSurfaceParams.frameHeight : containerH,
                cameraParam.getSupportedPictureSizes());
        //设置Pic, 视频时可以忽略Pic尺寸
        int picSizeW = config.picWidth;
        int picSizeH = config.picHeight;
        if (picSizeW <= 0 || picSizeH <= 0) {
            picSizeW = picSize.width;
            picSizeH = picSize.height;
        }
        mCameraSurfaceParams.setPicWidth(picSizeW);
        mCameraSurfaceParams.setPicHeight(picSizeH);
        if (picSize == null) {
            return false;
        }
        Log.e(TAG, "setupMainSurfaceView cameraSurfaceParams:" + mCameraSurfaceParams);

        if (preSizeW > 0 && preSizeH > 0) {
            Log.i(TAG, "Camera Parameters preSize " + preSizeW + " - " + preSizeH);
            cameraParam.setPreviewSize(preSizeW, preSizeH);
        }
        if (picSizeW > 0 && picSizeH > 0) {//拍摄图片时不可null
            Log.i(TAG, "Camera Parameters picSize " + picSizeW + " - " + picSizeH);
            cameraParam.setPictureSize(picSizeW, picSizeH);
        }
        cameraParam.setPictureFormat(PixelFormat.JPEG);
        cameraParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        if (cameraParam.isZoomSupported()) {
            int maxZoom = cameraParam.getMaxZoom();
            int defaultZoom = cameraParam.getZoom();
            int zoom = cameraParam.getZoom() + mCameraConfig.zoomOffset;
            if (zoom > maxZoom) {
                zoom = maxZoom;
            } else if (zoom < 0) {
                zoom = 1;
            }
            cameraParam.setZoom(zoom);
            Log.i(TAG, "Camera Parameters support zoom maxZoom: " + maxZoom
                    + ", default zoom:" + defaultZoom + ", set zoom: " + zoom);
        }
//        cameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        mCamera.setDisplayOrientation(mDisplayRotation);
        mCamera.setParameters(cameraParam);

        // 先比例缩放再transformMatrix（先镜像而后移动）。顺序不不同，宽高参数不同，所以要明确顺序
        ViewGroup.LayoutParams layoutParams = innerFrame.getLayoutParams();
        layoutParams.width = mCameraSurfaceParams.frameWidth;
        layoutParams.height = mCameraSurfaceParams.frameHeight;
        innerFrame.setLayoutParams(layoutParams);
        transformMatrix(textureView, mCameraConfig);

        mTextureView = textureView;
        mMainSurfaceInit = true;

        if (listener != null) {
            listener.onParamSet(mCameraSurfaceParams);
        }
        return true;
    }

    // 用于获取照片数据的Matrix，以便对照片数据进行纠正。
    // 因为照片数据与TextureView数据在方向，缩放，镜像，位移等属性上并非一致，所以在使用preview的数据时，要进行矩阵转换。
    // 方向旋转已由摄像头参数实施，所以这里不用考虑。
    // 无论是通过TextureView的getBitmap，还是Camera的onPreviewFrame获得的图片数据，其镜像属性不会因为TextureView的Matrix改变而改变。
    // 缩放与位移是一体的，对于照片来说这里也不用考虑缩放与位移。这个在后面的UI处理中根据mCameraSurfaceParams去处理。输出尽量保持原数据

    // 两种照片数据：
    // 1.TextureView的getBitmap获取的数据:
    //      方向会根据Camera.setDisplayOrientation变化；镜像不会因为TextureView的Matrix改变而改变；
    // 2.Camera的onPreviewFrame获得的图片数据:
    //      方向不会因为Camera.setDisplayOrientation而变化；镜像也不会因为TextureView的Matrix改变而改变；
    // 因此需要该方法来变换照片数据
    // 缩放与位移是一体的，对于照片来说这里也不用考虑缩放与位移。
    // 缩放与位移在后面的UI处理中根据mCameraSurfaceParams去处理。
    public Matrix getCameraPreviewPicMatrix(boolean isYuvFrame) {
        if (mCameraConfig == null) {
            return new Matrix();
        }
        Matrix matrix = new Matrix();
        int xScale = 1;
        // 先旋转再镜像原则
        if (isYuvFrame) {
            int rotation = mCameraSurfaceParams.yuvDataRotate;
            if (mCameraSurfaceParams.yuvDataRlMirror) {
                xScale = -xScale;
            }
            matrix.postRotate(rotation);
        } else {
            if (mCameraConfig.rlMirror) {
                xScale = -xScale;
            }
        }
        matrix.postScale(xScale, 1);
        return matrix;
    }

    public Matrix getCameraTakePicMatrix() {
        if (mCameraConfig == null) {
            return new Matrix();
        }
        Matrix matrix = new Matrix();
        int xScale = 1;
        // 先旋转再镜像原则
        int rotation = mCameraConfig.takePicRotation;
        if (rotation < 0) {
            rotation = mCameraSurfaceParams.yuvDataRotate;
            if (mCameraSurfaceParams.yuvDataRlMirror) {
                xScale = -xScale;
            }
        } else {
            if (mCameraConfig.takePicRlMirror) {
                xScale = -xScale;
            }
        }
        matrix.postRotate(rotation);
        matrix.postScale(xScale, 1);
        return matrix;
    }

    public void transformMatrix(@NonNull TextureView textureView, CameraConfig config) {
        if (textureView == null) {
            return;
        }
        try {
            Matrix matrix = new Matrix();
            float xScale = 1;
            float yScale = 1;
            int width = 0;
            int height = 0;
            if (config.rlMirror) {
                // 因为是在放大的基础上进行镜像而后移动，所以用frameWidth
                width = mCameraSurfaceParams.frameWidth;
                xScale = -1;
            }
            width = width + mCameraSurfaceParams.translationX;
            height = height + mCameraSurfaceParams.translationY;
            matrix.postScale(xScale, yScale);
            matrix.postTranslate(width, height);
            textureView.setTransform(matrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean judgeSurfaceView(@NonNull CameraSurfaceParams cameraSurfaceParams,
                                     int containerW, int containerH) {
        float frameZoomRatio = 1.0f;
        int frameWidth = 0;
        int frameHeight = 0;
        int translationX = 0;
        int translationY = 0;
        int preSizeW = cameraSurfaceParams.getPreWidth();
        int preSizeH = cameraSurfaceParams.getPreHeight();
        if (preSizeW > 0 && preSizeH > 0) {
            // 如果摄像头有做旋转，要根据旋转情况判定计算innerFrame宽高时用到的基准宽高数据
            if (mDisplayRotation == 90 || mDisplayRotation == 270) {
                int tmp = preSizeW;
                preSizeW = preSizeH;
                preSizeH = tmp;
            }
            frameZoomRatio = Math.max(containerH / (float) preSizeH, containerW / (float) preSizeW);
            int fixTranslationX = (int) (mCameraConfig.displayFixTranslationX * frameZoomRatio);
            int fixTranslationY = (int) (mCameraConfig.displayFixTranslationY * frameZoomRatio);
            float fixTranslationXRatio = 2 * Math.abs(fixTranslationX) / (float) preSizeW;
            frameZoomRatio = frameZoomRatio + fixTranslationXRatio;
            frameWidth = (int) (preSizeW * frameZoomRatio);
            frameHeight = (int) (preSizeH * frameZoomRatio);
            // 以为后面是以中心点做镜像，且满铺View，所以位移必为负数
            translationX = -Math.abs((int) (frameWidth / 2.0 - containerW / 2.0));
            translationY = -Math.abs((int) (frameHeight / 2.0 - containerH / 2.0));
            fixTranslationX = mCameraConfig.rlMirror ? -fixTranslationX : fixTranslationX;
            translationX = translationX + fixTranslationX;
            translationY = translationY + fixTranslationY;
        }
        cameraSurfaceParams.setFrameWidth(frameWidth);
        cameraSurfaceParams.setFrameHeight(frameHeight);
        cameraSurfaceParams.setFrameZoomRatio(frameZoomRatio);
        cameraSurfaceParams.setTranslationX(translationX);
        cameraSurfaceParams.setTranslationY(translationY);

        return true;
    }

    public synchronized void startCameraPreview() {
        if (isRecording) {
            return;
        }
        try {
            if (isCameraPrepared() && mTextureView != null) {
                mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopCameraPreview() {
        if (isRecording) {
            return;
        }
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ICameraCallback.PreviewCallback mPreviewCallback;

    public synchronized void listenFrameData(final ICameraCallback.PreviewCallback callback) {
        if (mCamera != null) {
            mPreviewCallback = callback;
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (callback != null) {
                        callback.onPreviewFrame(data);
                    }
                }
            });
        }
    }

    public synchronized void unListenFrameData() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mPreviewCallback = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void release() {
        Log.d(TAG, "release mCamera:" + mCamera);
        unListenFrameData();
        stopRecording(false);
        stopCameraPreview();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mTextureView = null;
        }
    }

    public synchronized void takePicture(final ICameraCallback.TakePicListener listener) {
        takePicture(false, listener);
    }

    public synchronized void takePicture(final boolean restartPreview,
                                         final ICameraCallback.TakePicListener listener) {
        if (isRecording) {
            return;
        }
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (listener != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(
                                data, 0, data.length);
                        if (bitmap != null) {
                            Matrix m = getCameraTakePicMatrix();
                            Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), m, true);
                            listener.onPictureTaken(finalBitmap);
                        }
                    }
                    if (restartPreview) {
                        startCameraPreview();
                    }
                }
            });
        }
    }

    /**
     * 是否支持相机
     *
     * @return
     */
    public boolean isSupportCamera() {
        return Camera.getNumberOfCameras() > 0;
    }

    public synchronized Camera openCamera(int cameraIndex) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int index = 0; index < numberOfCameras; index++) {
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraIndex == index) {
                mCameraFacing = cameraInfo.facing;
                switch (mCameraFacing) {
                    case Camera.CameraInfo.CAMERA_FACING_FRONT:
                        mCameraType = CameraConfig.FRONT;
                        break;
                    case Camera.CameraInfo.CAMERA_FACING_BACK:
                        mCameraType = CameraConfig.BACK;
                        break;
                }
                return Camera.open(index);
            }
        }
        return null;
    }

    public synchronized Camera openFrontCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return Camera.open(cameraId);
            }
        }
        return null;
    }

    public synchronized Camera openBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return Camera.open(cameraId);
            }
        }
        return null;
    }

    public Camera.CameraInfo getCameraInfo(int cameraType) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == cameraType) {
                return cameraInfo;
            }
        }
        return null;
    }

    //获取与指定宽高相等或最接近的尺寸
    public static Camera.Size getBestSize(int rotation, int targetWidth, int targetHeight, List<Camera.Size> sizeList) {
        Camera.Size bestSize = null;
        int minDiff = Integer.MAX_VALUE;
        StringBuilder sizeLogSb = new StringBuilder();
        for (Camera.Size size : sizeList) {
            sizeLogSb.append("size:" + size.width + "*" + size.height).append(",");
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size;
                break;
            }
            int supportW = size.width;
            int supportH = size.height;
            if (rotation == 90 || rotation == 270) {
                supportW = size.height;
                supportH = size.width;
            }
            int offsetW = Math.abs(targetWidth - supportW);
            int offsetH = Math.abs(targetHeight - supportH);
            int offset = offsetW * offsetW + offsetH * offsetH;
            if (Math.abs(offset) < minDiff) {
                minDiff = offset;
                bestSize = size;
            }
        }
        Log.d(TAG, "support sizes:" + (sizeLogSb.length() > 0 ? sizeLogSb.substring(0, sizeLogSb.length() - 1) : ""));
        Log.d(TAG, "目标尺寸:" + targetWidth + "*" + targetHeight);
        Log.d(TAG, "最优尺寸:" + bestSize.width + "*" + bestSize.height + " for rotation:" + rotation);
        return bestSize;
    }

    private MediaRecorder mMediaRecorder;
    private String mOutputRecordFilePath;
    private volatile boolean isRecording = false;
    private Handler mStopHandler = new Handler(Looper.getMainLooper());

    private ICameraCallback.IRecordCallback mRecordCallback;

    public synchronized boolean startRecording(RecordConfig config,
                                               ICameraCallback.IRecordCallback callback) {
        if (mTextureView == null) {
            Log.d(TAG, "startRecording mTextureView is null");
            if (callback != null) {
                callback.onRecordFail();
            }
            return false;
        }
        return startRecording(config, new Surface(mTextureView.getSurfaceTexture()), callback);
    }

    public synchronized boolean startRecording(RecordConfig config, Surface surface,
                                               ICameraCallback.IRecordCallback callback) {
        if (callback != null) {
            callback.onRecordPrepare();
        }

        if (!isCameraPrepared() || mCameraSurfaceParams == null) {
            if (callback != null) {
                callback.onRecordFail();
            }
            Log.d(TAG, "startRecording fail for camera not prepared");
            return false;
        }
        File outputFile = getOutputMediaFile(config.recordFilePath);
        if (outputFile == null) {
            if (callback != null) {
                callback.onRecordFail();
            }
            Log.d(TAG, "startRecording fail for outputFile create fail");
            return false;
        }
        mStopHandler.removeCallbacksAndMessages(null);
        stopRecording(false, false, true);

        mRecordCallback = callback;
        mMediaRecorder = new MediaRecorder();

        List<Integer> supportedFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
        int frameRate = 0;
        if (supportedFrameRates != null) {
            int offsetRate = Integer.MAX_VALUE;
            for (int rate : supportedFrameRates) {
                Log.d(TAG, "supportedFrameRates:" + rate);
                if (Math.abs(rate - config.videoFrameRate) < offsetRate) {
                    offsetRate = Math.abs(rate - config.videoFrameRate);
                    frameRate = rate;
                }
            }
            Log.d(TAG, "use frameRate:" + (frameRate > 0 ? frameRate : "default"));
        }

        // 设置相机
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // 设置音频源和视频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 设置输出格式和编码格式
        mMediaRecorder.setOutputFormat(config.outputFormat);
        mMediaRecorder.setAudioEncoder(config.audioEncoder);
        mMediaRecorder.setVideoEncoder(config.videoEncoder);

        mMediaRecorder.setVideoEncodingBitRate(config.videoEncodingBitRate);
        // 选择一个合适的帧率
        if (frameRate > 0) {
            mMediaRecorder.setVideoFrameRate(frameRate);
        }

        // 设置输出文件
        mOutputRecordFilePath = outputFile.getPath();
        mMediaRecorder.setOutputFile(mOutputRecordFilePath);

        // 设置预览显示
        mMediaRecorder.setPreviewDisplay(surface);
        int rotation = mCameraSurfaceParams.displayRotation;
        if (mCameraType == CameraConfig.FRONT) {
            rotation = (360 - rotation) % 360;
        }
        mMediaRecorder.setOrientationHint(rotation);
        mMediaRecorder.setVideoSize(mCameraSurfaceParams.getPreWidth(), mCameraSurfaceParams.getPreHeight());

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
            if (callback != null) {
                callback.onRecordStart();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onRecordFail();
            }
            stopRecording(true, false, true);
            Log.d(TAG, "startRecording IOException:" + e);
            return false;
        }
        return true;
    }

    private File getOutputMediaFile(String filePath) {
        File outputFile = null;
        // 在这里定义输出文件的位置和格式
        // 这里使用外部存储目录示例
        if (TextUtils.isEmpty(filePath)) {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                    "CameraRecord");
            outputFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + System.currentTimeMillis() + ".mp4");
        } else {
            outputFile = new File(filePath);
        }
        if (outputFile.exists()) {
            outputFile.delete();
        } else {
            File mediaStorageDir = outputFile.getParentFile();
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
        }
        return outputFile;
    }

    public void completeRecording(final boolean resumePreview) {
        mStopHandler.removeCallbacksAndMessages(null);
        // 进行延时stop，防止刚start马上stop，减少可能导致的Stop() called but track is not started or stopped异常
        mStopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording(resumePreview, true, false);
            }
        }, 100);
    }

    public void stopRecording(final boolean resumePreview) {
        // 进行延时stop，防止刚start马上stop，减少可能导致的Stop() called but track is not started or stopped异常
        mStopHandler.removeCallbacksAndMessages(null);
        mStopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording(resumePreview, false, false);
            }
        }, 100);
    }

    private synchronized void stopRecording(boolean resumePreview, boolean complete, boolean justStopOnly) {
        if (isRecording) {
            if (mMediaRecorder != null) {
                try {
                    mMediaRecorder.stop();
                } catch (Exception e) {
                    Log.d(TAG, "stopRecording Exception:" + e);
                }
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            isRecording = false;
            if (!justStopOnly) {
                if (mRecordCallback != null) {
                    if (complete) {
                        mRecordCallback.onRecordComplete(mOutputRecordFilePath);
                    } else {
                        mRecordCallback.onRecordRelease();
                    }
                }
            }
        }
        mOutputRecordFilePath = null;
        mRecordCallback = null;
        if (resumePreview) {
            // 重新配置相机并启动预览
            resumeCameraPreview();
        }
    }

    private void resumeCameraPreview() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.reconnect(); // 重新连接相机
            startCameraPreview();
            if (mPreviewCallback != null) {
                listenFrameData(mPreviewCallback);
            }
        } catch (IOException e) {
            Log.d(TAG, "resumeCameraPreview fail:" + e);
        }
    }
}