package com.pine.app.lib.face.detect.normal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.FacePosDetail;
import com.pine.app.lib.face.R;
import com.pine.app.lib.face.detect.CameraSurfaceParams;
import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.FaceRange;
import com.pine.app.lib.face.detect.FrameLayoutWithHole;
import com.pine.app.lib.face.detect.ICameraCallback;
import com.pine.app.lib.face.detect.IFaceDetectView;
import com.pine.app.lib.face.detect.IOnFaceListener;
import com.pine.app.lib.face.detect.PicSaver;
import com.pine.app.lib.face.detect.RecordConfig;
import com.pine.app.lib.face.detect.serial.ISerialActionProxy;
import com.pine.app.lib.face.matcher.FaceMatcher;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceView extends RelativeLayout implements IFaceDetectView {
    private long startDetectTimeStamp = 0;

    private FrameLayoutWithHole faceMantleView;
    private int faceMantleBgColor;
    // 单位px
    private int faceMantleRadius;
    private int finalFaceMantleRadius;
    // 单位px
    private int faceMantleRx;//默认在中心x位置
    // 单位px
    private int faceMantleRy;//默认在中心y位置

    private float faceMantleRxWeight = 0.5f;//默认在中心x位置权重（0~1），没有指定faceMantleRx，使用此值进行计算
    private float faceMantleRyWeight = 0.5f;//默认在中心y位置权重（0~1），没有指定faceMantleRy，使用此值进行计算

    public FaceView(Context context) {
        super(context);
        initView();
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*
        hole_radius 为镂空圆的半径，单位px
        background_color 为透明色背景
        radius_x 为圆心的x轴坐标，单位px
        radius_y 为圆心的y轴坐标，单位px
        */
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FaceDetectView);
        faceMantleBgColor = ta.getColor(R.styleable.FaceDetectView_hole_container_bg, -1);
        faceMantleRadius = ta.getDimensionPixelOffset(R.styleable.FaceDetectView_hole_radius, 0);
        faceMantleRx = ta.getDimensionPixelOffset(R.styleable.FaceDetectView_hole_radius_x, Integer.MAX_VALUE);
        faceMantleRy = ta.getDimensionPixelOffset(R.styleable.FaceDetectView_hole_radius_y, Integer.MAX_VALUE);
        initView();
    }

    private FaceTextureView faceTextureView;
    private IFaceRectView faceRectView;
    private FaceTextureView.IFramePreViewListener framePreViewListener;
    private DetectConfig mConfig = new DetectConfig("");
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean allowPicSave = true;
    private Handler mOnFacePicSaveH = new Handler(Looper.getMainLooper());
    private Handler mOnFaceGetProcessH = new Handler(Looper.getMainLooper());

    private void initView() {
        faceTextureView = new FaceTextureView(getContext());
        faceTextureView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        // 初始化一个绘制框
        if (faceRectView == null) {
//            faceRectView = new FaceBorderView(getContext());
            faceRectView = new FaceRectView(getContext());
        }
        ((View) faceRectView).setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        faceTextureView.setFaceRectView(faceRectView);

        addView(faceTextureView.getFrame());
        addView(((View) faceRectView));

        if (faceMantleBgColor <= 0) {
            faceMantleBgColor = Color.parseColor("#66000000");
        }
    }

    @Override
    public void init(@NonNull DetectConfig config, final IOnFaceListener listener) {
        mConfig.merge(config);
        faceTextureView.setConfig(mConfig);
        faceRectView.setupDetectConfig(mConfig);
        framePreViewListener = new FaceTextureView.IFramePreViewListener() {
            @Override
            public boolean onFaceFrame(Bitmap faceFrame, List<FacePosDetail> facePosDetails) {
                if (startDetectTimeStamp + mConfig.delayForSaveFlow > System.currentTimeMillis()) {
                    return false;
                }
                Log.d(TAG, "当前图片人脸个数：" + facePosDetails.size() + ", allowPicSave:" + allowPicSave);
                //faces是检测出来的人脸参数
                //检测到人脸的回调,保存人脸图片到本地
                if (allowPicSave) {
                    mOnFaceGetProcessH.removeCallbacksAndMessages(null);
                    allowPicSave = false;
                    executorService.submit(new SavePicRunnable(faceFrame, listener));
                }
                //这帧preFrame处理了就是进行了回收，返回true
                //否则返回false，内部进行回收处理
                return true;
            }

            private void setDiffTip(int resId) {
                if (faceMantleDiffTipTv == null) {
                    return;
                }
                if (resId > -1) {
                    faceMantleDiffTipTv.setText(resId);
                } else {
                    faceMantleDiffTipTv.setText("");
                }
            }

            @Override
            public boolean onFaceRangeJudge(final boolean centerMatch, final int rectState) {
                if (startDetectTimeStamp + mConfig.delayForSaveFlow / 2 > System.currentTimeMillis()) {
                    return false;
                }
                if (listener != null) {
                    mOnFaceGetProcessH.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!centerMatch) {
                                setDiffTip(mConfig.centerDiffTipResId);
                            } else {
                                switch (rectState) {
                                    case IOnFaceListener.RECT_SMALL:
                                        setDiffTip(mConfig.edgeSmallTipResId);
                                        break;
                                    case IOnFaceListener.RECT_BIG:
                                        setDiffTip(mConfig.edgeBigTipResId);
                                        break;
                                    default:
                                        setDiffTip(-1);
                                        break;
                                }
                            }
                            if (listener != null) {
                                listener.onFaceRangeJudge(centerMatch, rectState, faceMantleDiffTipTv);
                            }
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onParamSet(@NonNull CameraSurfaceParams params) {
                finalFaceMantleRadius = faceMantleRadius;
                int preW = params.getPreWidth();
                int preH = params.getPreHeight();
                int width = getWidth();
                int height = getHeight();
                if (params != null && params.frameWidth > 0) {
                    int minHalf = Math.min(preW / 2, preH / 2);
                    if (width > 0) {
                        minHalf = Math.min(minHalf, width / 2);
                    }
                    if (height > 0) {
                        minHalf = Math.min(minHalf, height / 2);
                    }
                    if (mConfig.autoCalFaceRang) {
                        finalFaceMantleRadius = minHalf * 6 / 10;
                    } else {
                        if (faceMantleRadius <= 0 && mConfig.faceRangeRatio > 0) {
                            finalFaceMantleRadius = (int) (minHalf * mConfig.faceRangeRatio);
                        }
                    }
                }
                Log.d(TAG, "final face mantle radius：" + finalFaceMantleRadius
                        + ", faceRangeRatio:" + mConfig.faceRangeRatio
                        + ", view w:" + width
                        + ", view h:" + height
                        + ", pre size w:" + preW
                        + ", pre size h:" + preH
                        + ", frame w:" + params.getFrameWidth()
                        + ", frame h:" + params.getFrameHeight()
                        + ", frameZoomRatio:" + params.getFrameZoomRatio()
                        + ", " + mConfig);
                if (mConfig.getEnableFaceDetect()) {
                    addFaceMantleView();
                }
                return false;
            }
        };
        faceTextureView.setFramePreViewListener(framePreViewListener);
    }

    @Override
    public void resetDetectConfig(@NonNull DetectConfig config) {
        mConfig.mergeChange(config);
    }

    @Override
    public void setFaceMantleCenter(float faceMantleRxWeight, float faceMantleRyWeight) {
        this.faceMantleRxWeight = faceMantleRxWeight;
        this.faceMantleRyWeight = faceMantleRyWeight;
        if (mConfig != null && mConfig.getEnableFaceDetect()) {
            addFaceMantleView();
        }
    }

    public void setFaceMantleCenter(int faceMantleRx, int faceMantleRy) {
        setFaceMantleAttr(faceMantleBgColor, faceMantleRadius, faceMantleRx, faceMantleRy);
    }

    @Override
    public void setFaceMantleAttr(int faceMantleBgColor, int faceMantleRadius,
                                  int faceMantleRx, int faceMantleRy) {
        this.faceMantleBgColor = faceMantleBgColor;
        this.faceMantleRadius = faceMantleRadius;
        this.faceMantleRx = faceMantleRx;
        this.faceMantleRy = faceMantleRy;
        if (mConfig != null && mConfig.getEnableFaceDetect()) {
            addFaceMantleView();
        }
    }

    @Override
    public void setSerialActionProxy(@Nullable ISerialActionProxy proxy) {

    }

    @Override
    public void onSerialActionDone(int actionStep, boolean success) {

    }

    @Override
    public boolean startFaceDetect() {
        startDetectTimeStamp = System.currentTimeMillis();
        mConfig.setEnableFaceDetect(true);
        mConfig.PreFaceTime = System.currentTimeMillis();
        allowPicSave = true;
        addFaceMantleView();
        return true;
    }

    @Override
    public void stopFaceDetect() {
        mOnFaceGetProcessH.removeCallbacksAndMessages(null);
        mConfig.setEnableFaceDetect(false);
        if (getFaceRectView() != null) {
            getFaceRectView().clearBorder();
        }
        removeFaceMantleView();
    }

    @Override
    public void startCameraPreview() {
        if (faceTextureView != null) {
            faceTextureView.startCameraPreview();
        }
    }

    @Override
    public void stopCameraPreview() {
        if (faceTextureView != null) {
            faceTextureView.stopCameraPreview();
        }
        if (getFaceRectView() != null) {
            getFaceRectView().clearBorder();
        }
    }

    @Override
    public boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback) {
        if (faceTextureView != null) {
            return faceTextureView.startRecording(config, callback);
        }
        if (callback != null) {
            callback.onRecordFail();
        }
        return false;
    }

    @Override
    public void completeRecording(boolean resumePreview) {
        if (faceTextureView != null) {
            faceTextureView.completeRecording(resumePreview);
        }
    }

    @Override
    public void stopRecording(boolean resumePreview) {
        if (faceTextureView != null) {
            faceTextureView.stopRecording(resumePreview);
        }
    }

    @Override
    public void stop() {
        stopRecording(false);
        stopFaceDetect();
        stopCameraPreview();
    }

    @Override
    public void release() {
        mOnFaceGetProcessH.removeCallbacksAndMessages(null);
        mOnFacePicSaveH.removeCallbacksAndMessages(null);
        if (faceTextureView != null) {
            faceTextureView.release();
        }
        removeAllViews();
    }

    private class SavePicRunnable implements Runnable {
        Bitmap bitmap;
        IOnFaceListener listener;

        SavePicRunnable(Bitmap bitmap, IOnFaceListener listener) {
            this.bitmap = bitmap;
            this.listener = listener;
        }

        @Override
        public void run() {
            boolean saved = PicSaver.saveFacePicToLocal(mConfig.savePicFilePath, bitmap);
            File file = new File(mConfig.savePicFilePath);
            int index = file.getName().lastIndexOf(".");

            String compressFileName = file.getName().substring(0, index) + "_compress" + file.getName().substring(index);
            final String compressFilePath = new File(file.getParent(), compressFileName).getPath();
            saved = saved && PicSaver.saveFacePicToLocal(compressFilePath, bitmap, mConfig.saveCompressPicMaxSize);

            Bitmap faceCropBitmap = FaceMatcher.getInstance().toFaceBitmap(file.getPath(), "jpg");
            String faceCropFileName = file.getName().substring(0, index) + "_crop" + file.getName().substring(index);
            final String faceCropFilePath = new File(file.getParent(), faceCropFileName).getPath();
            saved = saved && PicSaver.saveFacePicToLocal(faceCropFilePath, faceCropBitmap);
            if (faceCropBitmap != null && !faceCropBitmap.isRecycled()) {
                faceCropBitmap.recycle();
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (listener != null) {
                final boolean finalSave = saved;
                mOnFacePicSaveH.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            if (finalSave) {
                                allowPicSave = listener.onFacePicSaved(mConfig.savePicFilePath,
                                        compressFilePath, faceCropFilePath);
                            } else {
                                allowPicSave = listener.onFacePicSavedFail();
                            }
                        }
                    }
                });
            }
        }
    }

    private TextView faceMantleDiffTipTv;

    private void addFaceMantleView() {
        if (faceTextureView == null) {
            return;
        }
        if (faceMantleRx == Integer.MAX_VALUE) {
            faceMantleRx = (int) ((getRight() + getLeft()) * faceMantleRxWeight);
        }
        if (faceMantleRy == Integer.MAX_VALUE) {
            faceMantleRy = (int) ((getBottom() + getTop()) * faceMantleRyWeight);
        }
        if (faceMantleRx == Integer.MAX_VALUE || faceMantleRy == Integer.MAX_VALUE) {
            return;
        }
        int faceRadius = finalFaceMantleRadius > 0 ? finalFaceMantleRadius : faceMantleRadius;
        int height = Math.abs(getBottom() - getTop());
        int width = Math.abs(getRight() - getLeft());
        if (height > 0 && width > 0) {
            int minBord = Math.min(height, width);
            faceRadius = Math.min(faceRadius, minBord * 8 / 10);
        }
        Log.d(TAG, "face bord view use range radius:" + faceRadius
                + ", finalFaceMantleRadius:" + finalFaceMantleRadius
                + ", faceMantleRx:" + faceMantleRx + ", faceMantleRy:" + faceMantleRy
                + ", faceMantleRxWeight:" + faceMantleRxWeight + ", faceMantleRyWeight:" + faceMantleRyWeight);
        removeFaceMantleView();
        faceMantleView = new FrameLayoutWithHole(getContext(), faceMantleBgColor, faceRadius,
                faceMantleRx, faceMantleRy, false);

        faceMantleDiffTipTv = new TextView(getContext());
        faceMantleDiffTipTv.setTextColor(Color.parseColor("#FF8C00"));
        faceMantleDiffTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams tipLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2 * faceRadius);
        tipLp.setMargins(20, faceMantleRy - faceRadius, 20, 0);
        faceMantleDiffTipTv.setGravity(Gravity.CENTER);
        faceMantleDiffTipTv.setLayoutParams(tipLp);
        faceMantleView.addView(faceMantleDiffTipTv);

        FaceRange faceRange = new FaceRange();
        int innerRecHalf = faceRadius * 5 / 6;
        faceRange.left = faceMantleRx - innerRecHalf;
        faceRange.right = faceMantleRx + innerRecHalf;
        faceRange.top = faceMantleRy - innerRecHalf;
        faceRange.bottom = faceMantleRy + innerRecHalf;
        faceTextureView.setFaceValidRange(faceRange);
        Log.d(TAG, "face valid rang:" + faceRange);
        addView(faceMantleView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void removeFaceMantleView() {
        if (faceMantleView != null) {
            faceMantleView.removeAllViews();
            removeView(faceMantleView);
        }
    }

    public Bitmap getCurrentBitmap() {
        return faceTextureView == null ? null : faceTextureView.getBitmap();
    }

    public FaceTextureView getFaceDetectTextureView() {
        return faceTextureView;
    }

    private IFaceRectView getFaceRectView() {
        return faceRectView;
    }

    public void setFaceRectView(IFaceRectView faceRectView) {
        this.faceRectView = faceRectView;
    }

    public FaceTextureView.IFramePreViewListener getFramePreViewListener() {
        return framePreViewListener;
    }
}
