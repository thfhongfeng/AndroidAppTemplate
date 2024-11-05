package com.pine.app.lib.face.detect;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.app.lib.face.R;
import com.pine.app.lib.face.detect.normal.FaceView;
import com.pine.app.lib.face.detect.serial.ISerialActionProxy;

public class FaceDetectView extends FrameLayout implements IFaceDetectView {
    // 使用人脸检测SDK对通用摄像头进行检测的方式
    public final static int DETECT_TYPE_NORMAL = 0;

    private int mDetectType = DETECT_TYPE_NORMAL;
    private IFaceDetectView mDetectView;

    private Context mContext;

    private int faceMantleBgColor;
    // 单位px
    private int faceMantleRadius;
    // 单位px
    private int faceMantleRx;//默认在中心x位置
    // 单位px
    private int faceMantleRy;//默认在中心y位置

    private float faceMantleRxWeight = 0.5f;//默认在中心x位置权重（0~1），没有指定faceMantleRx，使用此值进行计算
    private float faceMantleRyWeight = 0.5f;//默认在中心y位置权重（0~1），没有指定faceMantleRy，使用此值进行计算

    public FaceDetectView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public FaceDetectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
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
    }

    public void init(int detectType, @NonNull DetectConfig config, final IOnFaceListener listener) {
        mDetectType = detectType;
        init(config, listener);
    }

    @Override
    public void init(@NonNull DetectConfig config, IOnFaceListener listener) {
        switch (mDetectType) {
            default:
                mDetectView = new FaceView(mContext);
                break;
        }
        addView((View) mDetectView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mDetectView.init(config, listener);
        if (faceMantleBgColor <= 0) {
            faceMantleBgColor = Color.parseColor("#66000000");
        }
        mDetectView.setFaceMantleAttr(faceMantleBgColor, faceMantleRadius, faceMantleRx, faceMantleRy);
        mDetectView.setFaceMantleCenter(faceMantleRxWeight, faceMantleRyWeight);
    }

    @Override
    public void resetDetectConfig(@NonNull DetectConfig config) {
        if (mDetectView != null) {
            mDetectView.resetDetectConfig(config);
        }
    }

    @Override
    public void setFaceMantleCenter(float faceMantleRxWeight, float faceMantleRyWeight) {
        this.faceMantleRxWeight = faceMantleRxWeight;
        this.faceMantleRyWeight = faceMantleRyWeight;
        if (mDetectView != null) {
            mDetectView.setFaceMantleCenter(faceMantleRxWeight, faceMantleRyWeight);
        }
    }

    public void setFaceMantleCenter(int faceMantleRx, int faceMantleRy) {
        setFaceMantleAttr(faceMantleBgColor, faceMantleRadius, faceMantleRx, faceMantleRy);
    }

    @Override
    public void setFaceMantleAttr(int faceMantleBgColor, int faceMantleRadius, int faceMantleRx, int faceMantleRy) {
        this.faceMantleBgColor = faceMantleBgColor;
        this.faceMantleRadius = faceMantleRadius;
        this.faceMantleRx = faceMantleRx;
        this.faceMantleRy = faceMantleRy;
        if (mDetectView != null) {
            mDetectView.setFaceMantleAttr(faceMantleBgColor, faceMantleRadius, faceMantleRx, faceMantleRy);
        }
    }

    @Override
    public void setSerialActionProxy(@Nullable ISerialActionProxy proxy) {
        if (mDetectView != null) {
            mDetectView.setSerialActionProxy(proxy);
        }
    }

    @Override
    public void onSerialActionDone(int actionStep, boolean success) {
        if (mDetectView != null) {
            mDetectView.onSerialActionDone(actionStep, success);
        }
    }

    @Override
    public boolean startFaceDetect() {
        if (mDetectView != null) {
            return mDetectView.startFaceDetect();
        }
        return false;
    }

    @Override
    public void stopFaceDetect() {
        if (mDetectView != null) {
            mDetectView.stopFaceDetect();
        }
    }

    @Override
    public void startCameraPreview() {
        if (mDetectView != null) {
            mDetectView.startCameraPreview();
        }
    }

    @Override
    public void stopCameraPreview() {
        if (mDetectView != null) {
            mDetectView.stopCameraPreview();
        }
    }

    @Override
    public boolean startRecording(RecordConfig config, ICameraCallback.IRecordCallback callback) {
        if (mDetectView != null) {
            return mDetectView.startRecording(config, callback);
        }
        if (callback != null) {
            callback.onRecordFail();
        }
        return false;
    }

    @Override
    public void completeRecording(boolean resumePreview) {
        if (mDetectView != null) {
            mDetectView.completeRecording(resumePreview);
        }
    }

    @Override
    public void stopRecording(boolean resumePreview) {
        if (mDetectView != null) {
            mDetectView.stopRecording(resumePreview);
        }
    }

    public void stop() {
        if (mDetectView != null) {
            mDetectView.stop();
        }
    }

    @Override
    public void release() {
        if (mDetectView != null) {
            mDetectView.release();
            mDetectView = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }
}
