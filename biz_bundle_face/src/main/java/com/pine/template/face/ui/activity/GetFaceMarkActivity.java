package com.pine.template.face.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.app.lib.face.detect.DetectConfig;
import com.pine.app.lib.face.detect.IOnFacePicListener;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarTextMenuActivity;
import com.pine.template.base.manager.tts.TtsManager;
import com.pine.template.face.FaceConstants;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.R;
import com.pine.template.face.databinding.ActivityGetFaceMarkBinding;
import com.pine.template.face.utils.DetectConfigUtils;
import com.pine.template.face.vm.GetFaceMarkVm;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

@PermissionsAnnotation(Permissions = {Manifest.permission.CAMERA})
public class GetFaceMarkActivity extends
        BaseMvvmActionBarTextMenuActivity<ActivityGetFaceMarkBinding, GetFaceMarkVm> {

    private Handler mDetectInitHandler = new Handler(Looper.getMainLooper());

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        return super.beforeInitOnCreate(savedInstanceState);
    }

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getTipData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer tipResId) {
                mBinding.tvTip.setText(tipResId);
                TtsManager.getInstance().play(tipResId, true);
            }
        });
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.face_activity_get_face_mark;
    }

    @Override
    protected void init(Bundle onCreateSavedInstanceState) {
        mViewModel.getTipData().setValue(R.string.face_get_face_tip_device_prepared);
        initDetect();
    }

    private void initDetect() {
        DetectConfig config = getDetectConfig();
        LogUtils.d(TAG, "initDetect " + config);
        mBinding.faceDetectView.setFaceMantleCenter(0.5f, AppUtils.isPortScreen() ? 0.4f : 0.5f);
        mBinding.faceDetectView.init(FaceConstants.DETECT_PROVIDER, config, new IOnFacePicListener() {
            @Override
            public boolean onFacePicSaved(String picPath, String compressPicPath, String faceCropFilePath) {
                LogUtils.d(TAG, "onFacePicSaved picPath:" + picPath
                        + ", compressPicPath:" + compressPicPath
                        + ", faceCropFilePath:" + faceCropFilePath);
                mBinding.faceDetectView.stopCameraPreview();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showShortToast(R.string.face_get_face_mark_msg);
                        mViewModel.getTipData().setValue(R.string.face_get_face_mark_msg);
                        onFacePicGot(picPath);
                    }
                });
                return false;
            }

            @Override
            public boolean onFail() {
                TtsManager.getInstance().play(R.string.face_get_face_tip_fail);
                restartFaceDetect();
                return false;
            }
        });
    }

    private DetectConfig getDetectConfig() {
        DetectConfig config = DetectConfigUtils.makeDetectConfig(FaceUrlConstants.PERSON_FACE_PATH());
        config.liveConfidenceEnable = false;
        config.cameraDetectProvider = DetectConfig.DETECT_PROVIDER_OPENCV;
        return config;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDetectInitHandler.post(new Runnable() {
            @Override
            public void run() {
                mBinding.faceDetectView.resetDetectConfig(getDetectConfig());
                mBinding.faceDetectView.startCameraPreview();
            }
        });
        startFaceDetect();
    }

    private void startFaceDetect() {
        mDetectInitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.faceDetectView.startFaceDetect();
            }
        }, 500);
    }

    private void restartFaceDetect() {
        mBinding.faceDetectView.stopFaceDetect();
        startFaceDetect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDetectInitHandler.removeCallbacksAndMessages(null);
        mBinding.faceDetectView.stopCameraPreview();
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.face_get_face_img_title);
        goBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuBtnTv.setText(R.string.face_get_face_save);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mBinding.faceDetectView.release();
        super.onDestroy();
    }


    private void onFacePicGot(String picPath) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("picPath", picPath);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, 1000);
    }
}
