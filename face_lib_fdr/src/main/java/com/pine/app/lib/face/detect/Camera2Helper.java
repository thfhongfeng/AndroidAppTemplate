package com.pine.app.lib.face.detect;

import static android.content.Context.CAMERA_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 尚未测试验证，待验证优化
 */
public class Camera2Helper {
    private static String TAG = Camera2Helper.class.getSimpleName();

    private static HashMap<String, Camera2Helper> instanceMap = new HashMap<>();

    public static synchronized Camera2Helper getInstance() {
        Camera2Helper instance = instanceMap.get(CameraConfig.DEFAULT);
        if (instance == null) {
            instance = new Camera2Helper(CameraConfig.DEFAULT);
            instanceMap.put(CameraConfig.DEFAULT, instance);
        }
        return instance;
    }

    public static synchronized Camera2Helper getInstance(String cameraType) {
        Camera2Helper instance = instanceMap.get(cameraType);
        if (instance == null) {
            instance = new Camera2Helper(cameraType);
            instanceMap.put(cameraType, instance);
        }
        return instance;
    }

    private Camera2Helper(String cameraType) {
        mCameraType = cameraType;
        Log.d(TAG, "create camera for: " + cameraType);
    }

    private String mCameraType = CameraConfig.DEFAULT;
    private int mCameraFacing = -1;
    private CameraConfig mCameraConfig;

    private TextureView mMainTextureView;

    private int mDeviceRotation, mCameraSensorRotation;
    private int mDisplayRotation, mYuvDataRotate;
    private boolean mYuvDataRlMirror;

    private String mCameraId;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCharacteristics mCharacteristics;

    private HashMap<String, CameraSurfaceParams> mSurfaceParamsMap = new HashMap<>();

    private HashMap<String, Surface> mPreviewSurfaceMap = new HashMap<>();
    private CaptureRequest.Builder mPreviewBuilder;

    private ImageReader mFrameImageReader;

    private ImageReader mPhotoImageReader;
    private CaptureRequest.Builder mPhotoBuilder;

    private volatile boolean mCameraInitProcessing;
    private volatile boolean mCameraInit;
    private volatile boolean mMainSurfaceInit;

    public synchronized boolean isCameraInit() {
        return mCameraDevice != null && !TextUtils.isEmpty(mCameraId)
                && mCharacteristics != null && mCameraInit;
    }

    public synchronized boolean isCameraPrepared() {
        return isCameraInit() && mMainSurfaceInit;
    }

    public synchronized CameraSurfaceParams getCameraSurfaceParams() {
        return mSurfaceParamsMap.get(CameraSurfaceParams.MAIN_TAG);
    }

    public synchronized CameraSurfaceParams getCameraSurfaceParams(@NonNull String tag) {
        return mSurfaceParamsMap.get(tag);
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
        mCameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        openCamera(config, listener);
    }

    @SuppressLint("MissingPermission")
    private void openCamera(@NonNull final CameraConfig config,
                            final ICameraCallback.ICameraInitListener listener) {
        if (TextUtils.equals(mCameraType, CameraConfig.DEFAULT)) {
            mCameraType = config.cameraType;
        }
        String cameraId;
        switch (mCameraType) {
            case CameraConfig.FRONT:
                cameraId = chooseCamera(CameraCharacteristics.LENS_FACING_FRONT);
                break;
            case CameraConfig.BACK:
                cameraId = chooseCamera(CameraCharacteristics.LENS_FACING_BACK);
                break;
            case CameraConfig.EXTERNAL:
                cameraId = chooseCamera(CameraCharacteristics.LENS_FACING_EXTERNAL);
                break;
            default:
                cameraId = chooseCamera(-1);
                break;
        }
        if (TextUtils.isEmpty(cameraId)) {
            Log.d(TAG, "openCamera fail for mCameraType:" + mCameraType + ", try any camera that can open");
            cameraId = chooseCamera(-1);
        }
        if (TextUtils.isEmpty(cameraId)) {
            Log.d(TAG, "openCamera there has no support camera");
            if (listener != null) {
                listener.onCameraInit(false);
            }
            mCameraInitProcessing = false;
            return;
        }
        mCameraId = cameraId;
        try {
            mCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            mCameraFacing = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.d(TAG, "camera onOpened cameraId:" + mCameraId + ", facing:" + mCameraFacing);
                    mCameraDevice = camera;
                    mCameraInit = judgeOrientation(config);
                    if (listener != null) {
                        listener.onCameraInit(mCameraInit);
                    }
                    mCameraInitProcessing = false;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.d(TAG, "camera onDisconnected");
                    release();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.d(TAG, "camera onError:" + error);
                    if (mCameraDevice == null) {
                        if (listener != null) {
                            listener.onCameraInit(false);
                        }
                    }
                    release();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera CameraAccessException:" + e);
            if (listener != null) {
                listener.onCameraInit(false);
            }
            mCameraInitProcessing = false;
        }
    }

    private boolean judgeOrientation(@NonNull CameraConfig config) {
        if (config == null || mCharacteristics == null) {
            return false;
        }
        Integer orientationObj = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Integer facingObj = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
        Log.i(TAG, "judgeOrientation camera sensor orientation:" + orientationObj
                + ", mDeviceRotation:" + mDeviceRotation
                + ", config.deviceFixOrientation:" + config.deviceFixOrientation);
        mCameraSensorRotation = orientationObj != null ? orientationObj.intValue() : 0;
        int cameraOrientation = mCameraSensorRotation;
        int deviceRotation = config.deviceFixOrientation;
        if (deviceRotation == 0 || deviceRotation == 90 || deviceRotation == 180 || deviceRotation == 270) {
            mDeviceRotation = deviceRotation;
        }
        Log.i(TAG, "after adjust camera orientation: " + cameraOrientation
                + ", mDeviceRotation:" + mDeviceRotation);
        int displayRotation = 0;
        if (facingObj.intValue() == CameraCharacteristics.LENS_FACING_FRONT) {
            displayRotation = (cameraOrientation + mDeviceRotation) % 360;
        } else {  // back-facing
            displayRotation = (cameraOrientation - mDeviceRotation + 360) % 360;
        }
        mDisplayRotation = displayRotation;
        Log.i(TAG, "Camera set displayRotation: " + displayRotation);
        return true;
    }

    public boolean setupMainSurfaceView(@NonNull TextureView textureView,
                                        @NonNull ViewGroup innerFrame,
                                        final CameraConfig config,
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
        Size preSize = choosePreSize(mCameraId, containerW, containerH);
        Log.d(TAG, "setupMainSurfaceView preSize:" + preSize);
        if (preSize == null) {
            return false;
        }
        try {
            if (mPreviewBuilder == null) {
                mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            }
            if (mPhotoBuilder == null) {
                mPhotoBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "setupMainSurfaceView CameraAccessException:" + e);
        }
        if (mPreviewBuilder == null || mPhotoBuilder == null) {
            return false;
        }
        Surface surface = new Surface(textureView.getSurfaceTexture());
        mMainTextureView = textureView;
        mPreviewBuilder.addTarget(surface);

        String mainTag = CameraSurfaceParams.MAIN_TAG;
        CameraSurfaceParams cameraSurfaceParams = null;
        if (mSurfaceParamsMap.containsKey(mainTag)) {
            cameraSurfaceParams = mSurfaceParamsMap.get(mainTag);
        } else {
            cameraSurfaceParams = new CameraSurfaceParams(mCameraType, mainTag);
        }

        //设置Pre尺寸
        int preSizeW = config.preWidth;
        int preSizeH = config.preHeight;
        if (preSizeW <= 0 || preSizeH <= 0) {
            preSizeW = preSize.getWidth();
            preSizeH = preSize.getHeight();
        }
        cameraSurfaceParams.setPreWidth(preSizeW);
        cameraSurfaceParams.setPreHeight(preSizeH);

        mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(0, 0, preSizeW, preSizeH));

        boolean success = judgeSurfaceView(cameraSurfaceParams, containerW, containerH);
        if (!success) {
            return false;
        }

        cameraSurfaceParams.setDisplayRotation(mDisplayRotation);

        int yuvDataRotate = mDisplayRotation;
        boolean yuvDataRlMirror = config.rlMirror;
        // 系统打开前置摄像头会自动镜像摄像头数据
        if (mCameraType == CameraConfig.FRONT) {
            yuvDataRlMirror = !yuvDataRlMirror;
        }
        cameraSurfaceParams.yuvDataRotate = yuvDataRotate;
        cameraSurfaceParams.yuvDataRlMirror = yuvDataRlMirror;

        Size picSize = choosePicSize(mCameraId,
                cameraSurfaceParams.frameWidth > 0 ? cameraSurfaceParams.frameWidth : containerW,
                cameraSurfaceParams.frameHeight > 0 ? cameraSurfaceParams.frameHeight : containerH);
        Log.d(TAG, "setupMainSurfaceView picSize:" + picSize);
        //设置Pic, 视频时可以忽略Pic尺寸
        int picSizeW = config.picWidth;
        int picSizeH = config.picHeight;
        if (picSizeW <= 0 || picSizeH <= 0) {
            picSizeW = picSize.getWidth();
            picSizeH = picSize.getHeight();
        }
        cameraSurfaceParams.setPicWidth(picSizeW);
        cameraSurfaceParams.setPicHeight(picSizeH);
        if (picSize == null) {
            return false;
        }
        Log.e(TAG, "setupMainSurfaceView cameraSurfaceParams:" + cameraSurfaceParams);

        // 先比例缩放再transformMatrix（先镜像而后移动）。顺序不不同，宽高参数不同，所以要明确顺序
        ViewGroup.LayoutParams layoutParams = innerFrame.getLayoutParams();
        layoutParams.width = cameraSurfaceParams.frameWidth;
        layoutParams.height = cameraSurfaceParams.frameHeight;
        innerFrame.setLayoutParams(layoutParams);
        transformMatrix(textureView, config, cameraSurfaceParams);

        if (mFrameImageReader == null) {
            mFrameImageReader = ImageReader.newInstance(preSizeW, preSizeH, ImageFormat.YUV_420_888, 2);
            mPreviewBuilder.addTarget(mFrameImageReader.getSurface());
            mFrameImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    synchronized (Camera2Helper.this) {
                        // 一旦把ImageReader的Surface添加到mPreviewBuilder。
                        // 必须拿出image并close，否则会界面会被卡住。
                        // setOnImageAvailableListener也不能传null。
                        // 也就是说必须做处理动作？什么原因？
                        Image image = reader.acquireLatestImage();
                        if (image != null) {
                            if (mPreviewCallback != null) {
                                mPreviewCallback.onPreviewFrame(processYUVImage(image));
                            }
                            image.close();
                        }
                    }
                }
            }, null);
        }

        if (mPhotoImageReader == null) {
            mPhotoImageReader = ImageReader.newInstance(picSizeW, picSizeH, ImageFormat.JPEG, 1);
            mPhotoBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mPhotoBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mPhotoBuilder.addTarget(mPhotoImageReader.getSurface());
            mPhotoImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    if (image != null) {
                        // JEPG只需要拿第一个plane
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        synchronized (Camera2Helper.this) {
                            if (mTakePicListener != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(
                                        bytes, 0, bytes.length);
                                if (bitmap != null) {
                                    Matrix m = getCameraPreviewPicMatrix(true);
                                    Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                            bitmap.getHeight(), m, true);
                                    mTakePicListener.onPictureTaken(finalBitmap);
                                }
                            }
                        }
                        image.close();
                    } else {
                        synchronized (Camera2Helper.this) {
                            if (mTakePicListener != null) {
                                mTakePicListener.onFail();
                            }
                        }
                    }
                    synchronized (Camera2Helper.this) {
                        if (mTakePicListener.restartPreview) {
                            startCameraPreview();
                        }
                    }
                }
            }, null);
        }

        mSurfaceParamsMap.put(mainTag, cameraSurfaceParams);
        mPreviewSurfaceMap.put(mainTag, surface);
        mMainSurfaceInit = true;

        if (listener != null) {
            listener.onParamSet(cameraSurfaceParams);
        }

        return true;
    }

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
            int rotation = mYuvDataRotate;
            if (mYuvDataRlMirror) {
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

    public void transformMatrix(@NonNull TextureView textureView, CameraConfig config,
                                CameraSurfaceParams cameraSurfaceParams) {
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
                width = cameraSurfaceParams.frameWidth;
                xScale = -1;
            }
            width = width + cameraSurfaceParams.translationX;
            height = height + cameraSurfaceParams.translationY;
            matrix.postScale(xScale, yScale);
            matrix.postTranslate(width, height);
            textureView.setTransform(matrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void attachSurfaceView(@NonNull String tag,
                                               @NonNull TextureView textureView,
                                               @NonNull ViewGroup innerFrame,
                                               int containerW, int containerH,
                                               ICameraCallback.ICameraSetListener listener) {
        Log.d(TAG, "attachSurfaceView containerW:" + containerW + ",containerH:" + containerW);
        String mainTag = CameraSurfaceParams.MAIN_TAG;
        if (containerW <= 0 || containerH <= 0 || !mSurfaceParamsMap.containsKey(mainTag)
                || mPreviewSurfaceMap.containsKey(tag)) {
            return;
        }
        CameraSurfaceParams params = mSurfaceParamsMap.get(mainTag).copyMain(tag);

        judgeSurfaceView(params, containerW, containerH);

        // 先比例缩放再transformMatrix（先镜像而后移动）。顺序不不同，宽高参数不同，所以要明确顺序
        ViewGroup.LayoutParams layoutParams = innerFrame.getLayoutParams();
        layoutParams.width = params.frameWidth;
        layoutParams.height = params.frameHeight;
        innerFrame.setLayoutParams(layoutParams);
        transformMatrix(textureView, mCameraConfig, params);

        if (listener != null) {
            listener.onParamSet(params);
        }

        mSurfaceParamsMap.put(tag, params);
        Surface surface = new Surface(textureView.getSurfaceTexture());
        mPreviewSurfaceMap.put(tag, surface);
        if (mPreviewBuilder != null) {
            mPreviewBuilder.addTarget(surface);
        }
    }

    public synchronized void detachSurfaceView(@NonNull String tag) {
        if (mPreviewSurfaceMap.containsKey(tag)) {
            if (mPreviewBuilder != null) {
                mPreviewBuilder.removeTarget(mPreviewSurfaceMap.get(tag));
            }
            mSurfaceParamsMap.remove(tag);
            mPreviewSurfaceMap.remove(tag);
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
            // 如果摄像头有角度旋转，要根据旋转情况判定计算innerFrame宽高时用到的基准宽高数据
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
            fixTranslationX = mCameraConfig.rlMirror ? fixTranslationX : -fixTranslationX;
            translationX = -(int) (frameWidth / 2.0 - containerW / 2.0) + fixTranslationX;
            translationY = -(int) (frameHeight / 2.0 - containerH / 2.0) + fixTranslationY;
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
            stopRecording(true);
            return;
        }
        if (isCameraPrepared()) {
            ArrayList<Surface> list = map2List(mPreviewSurfaceMap);
            Log.d(TAG, "startCameraPreview preview surface size:" + list.size());
            list.add(mFrameImageReader.getSurface());
            closeCameraSession();
            try {
                mCameraDevice.createCaptureSession(list, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        Log.d(TAG, "startCameraPreview onConfigured");
                        mCameraCaptureSession = session;
                        try {
                            mCameraCaptureSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "startCameraPreview CameraAccessException:" + e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.d(TAG, "startCameraPreview onConfigureFailed");
                    }
                }, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "startCameraPreview createCaptureSession CameraAccessException:" + e);
            }
        }
    }

    public synchronized void stopCameraPreview() {
        if (isRecording) {
            return;
        }
        closeCameraSession();
    }

    private ICameraCallback.PreviewCallback mPreviewCallback;

    public synchronized void listenFrameData(final ICameraCallback.PreviewCallback callback) {
        mPreviewCallback = callback;
    }

    public synchronized void unListenFrameData() {
        mPreviewCallback = null;
    }

    private ICameraCallback.TakePicListener mTakePicListener;

    public synchronized void takePicture(final ICameraCallback.TakePicListener listener) {
        takePicture(true, listener);
    }

    public synchronized void takePicture(final boolean restartPreview,
                                         final ICameraCallback.TakePicListener listener) {
        mTakePicListener = listener;
        mTakePicListener.restartPreview = restartPreview;
        if (isCameraPrepared()) {
            closeCameraSession();
            try {
                mCameraDevice.createCaptureSession(Arrays.asList(mPhotoImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                Log.d(TAG, "takePicture onConfigured");
                                mCameraCaptureSession = session;
                                try {
                                    mCameraCaptureSession.capture(mPhotoBuilder.build(), null, null);
                                } catch (CameraAccessException e) {
                                    Log.e(TAG, "takePicture CameraAccessException:" + e);
                                    if (listener != null) {
                                        listener.onFail();
                                    }
                                    if (restartPreview) {
                                        startCameraPreview();
                                    }
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                Log.d(TAG, "takePicture onConfigureFailed");
                                if (listener != null) {
                                    listener.onFail();
                                }
                                if (restartPreview) {
                                    startCameraPreview();
                                }
                            }
                        }, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "takePicture createCaptureSession CameraAccessException:" + e);
            }
        } else {
            if (listener != null) {
                listener.onFail();
            }
        }
    }

    private void closeCameraSession() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }

    public synchronized void release() {
        Log.d(TAG, "release mCameraDevice:" + mCameraDevice);
        stopCameraPreview();
        closeCameraSession();
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mFrameImageReader != null) {
            mFrameImageReader.close();
            mFrameImageReader = null;
        }
        if (mPhotoImageReader != null) {
            mPhotoImageReader.close();
            mPhotoImageReader = null;
        }
        mPreviewSurfaceMap.clear();
        mSurfaceParamsMap.clear();
        mPreviewBuilder = null;
        mPhotoBuilder = null;
        mCameraInit = false;
        mCameraInitProcessing = false;
        mMainSurfaceInit = false;
        mPreviewCallback = null;
        mTakePicListener = null;
    }

    private byte[] processYUVImage(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        byte[] nv21Data = new byte[imageWidth * imageHeight * 3 / 2];
        yBuffer.get(nv21Data, 0, imageWidth * imageHeight);
        uBuffer.get(nv21Data, imageWidth * imageHeight, imageWidth * imageHeight / 4);
        vBuffer.get(nv21Data, imageWidth * imageHeight * 5 / 4, imageWidth * imageHeight / 4);

        return nv21Data;
    }

    private String chooseCamera(int cameraType) {
        try {
            String[] cameraIds = mCameraManager.getCameraIdList();
            if (cameraIds == null || cameraIds.length <= 0) {
                return null;
            }
            if (cameraType < 0) {
                return cameraIds[0];
            }
            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer itemFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (itemFacing != null && cameraType == itemFacing) {
                    return cameraId;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Size choosePreSize(String cameraId, int targetWidth, int targetHeight) {
        List<Size> sizes = null;
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] supportedSizes = streamConfigMap.getOutputSizes(SurfaceTexture.class);
            sizes = new ArrayList<>();
            for (int i = 0; i < supportedSizes.length; i++) {
                sizes.add(new Size(supportedSizes[i].getWidth(), supportedSizes[i].getHeight()));
            }
        } catch (Exception e) {
            return null;
        }
        return getBestSize(targetWidth, targetHeight, sizes);
    }

    private Size choosePicSize(String cameraId, int targetWidth, int targetHeight) {
        List<Size> sizes = null;
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] supportedSizes = streamConfigMap.getOutputSizes(ImageFormat.JPEG);
            sizes = new ArrayList<>();
            for (int i = 0; i < supportedSizes.length; i++) {
                sizes.add(new Size(supportedSizes[i].getWidth(), supportedSizes[i].getHeight()));
            }
        } catch (Exception e) {
            return null;
        }
        return getBestSize(targetWidth, targetHeight, sizes);
    }

    //获取与指定宽高相等或最接近的尺寸
    public static Size getBestSize(int targetWidth, int targetHeight, List<Size> sizeList) {
        Size bestSize = null;
        int minDiff = Integer.MAX_VALUE;
        StringBuilder sizeLogSb = new StringBuilder();
        for (Size size : sizeList) {
            sizeLogSb.append("size:" + size.getWidth() + "*" + size.getHeight()).append(",");
            if (size.getWidth() == targetHeight && size.getHeight() == targetWidth) {
                bestSize = size;
                break;
            }
            int offsetW = Math.abs(targetWidth - size.getWidth());
            int offsetH = Math.abs(targetHeight - size.getHeight());
            int offset = offsetW * offsetW + offsetH * offsetH;
            if (Math.abs(offset) < minDiff) {
                minDiff = offset;
                bestSize = size;
            }
        }
        Log.d(TAG, "support sizes:" + (sizeLogSb.length() > 0 ? sizeLogSb.substring(0, sizeLogSb.length() - 1) : ""));
        Log.d(TAG, "目标尺寸:" + targetWidth + "*" + targetHeight);
        Log.d(TAG, "最优尺寸:" + bestSize.getWidth() + "*" + bestSize.getHeight());
        return bestSize;
    }


    private <T> ArrayList<T> map2List(@NonNull HashMap<String, T> map) {
        ArrayList<T> list = new ArrayList<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            list.add(map.get(key));
        }
        return list;
    }

    private boolean isRecording = false;
    private String mOutputRecordFilePath;

    private ICameraCallback.IRecordCallback mRecordCallback;

    public synchronized boolean startRecording(String recordFilePath, ICameraCallback.IRecordCallback callback) {
        if (mMainTextureView != null) {
            return startRecording(recordFilePath, new Surface(mMainTextureView.getSurfaceTexture()), callback);
        }
        Log.d(TAG, "startRecording mTextureView is null");
        if (callback != null) {
            callback.onRecordFail();
        }
        return false;
    }

    public synchronized boolean startRecording(String recordFilePath, Surface surface,
                                               ICameraCallback.IRecordCallback callback) {
        if (!isCameraPrepared()) {
            if (callback != null) {
                callback.onRecordFail();
            }
            Log.d(TAG, "startRecording fail for camera not prepared");
            return false;
        }
        File outputFile = getOutputMediaFile(recordFilePath);
        if (outputFile == null) {
            if (callback != null) {
                callback.onRecordFail();
            }
            Log.d(TAG, "startRecording fail for outputFile create fail");
            return false;
        }

        if (isRecording) {
            stopRecording(false);
        }
        mRecordCallback = callback;

        mOutputRecordFilePath = outputFile.getPath();

        isRecording = true;
        if (callback != null) {
            callback.onRecordStart();
        }
        if (callback != null) {
            callback.onRecordFail();
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

    public void completeRecording(boolean resumePreview) {
        stopRecording(resumePreview, true, false);
    }

    public synchronized void stopRecording(boolean resumePreview) {
        stopRecording(resumePreview, false, false);
    }

    private synchronized void stopRecording(boolean resumePreview, boolean complete, boolean ignoreCb) {
        isRecording = false;
        if (!ignoreCb && mRecordCallback != null) {
            if (complete) {
                mRecordCallback.onRecordComplete(mOutputRecordFilePath);
            } else {
                mRecordCallback.onRecordRelease();
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
    }
}
