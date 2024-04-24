package com.pine.template.face.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.template.base.component.image_loader.ImageCacheStrategy;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.R;
import com.pine.template.face.databinding.FaceHomeActivityBinding;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.utils.DocumentUtils;
import com.pine.template.face.vm.FaceHomeVm;

import java.io.File;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/2/25
 */

public class FaceHomeActivity extends BaseMvvmActionBarActivity<FaceHomeActivityBinding, FaceHomeVm>
        implements View.OnClickListener {

    private final int REQUEST_GET_FACE_MARK_BY_FILE = 1;
    private final int REQUEST_IDENTITY_BY_FILE = 2;

    private final int REQUEST_GET_FACE_MARK_BY_CAMERA = 11;
    private final int REQUEST_IDENTITY_BY_CAMERA = 12;

    private InputTextDialog mNameDialog;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.personData.observe(this, new Observer<PersonEntity>() {
            @Override
            public void onChanged(PersonEntity personEntity) {
                setupPersonView(personEntity);
            }
        });
    }

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        return false;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.face_activity_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String nameLabel = getString(R.string.face_person_name_label);
        mNameDialog = DialogUtils.createTextInputDialog(this,
                nameLabel, "", 32,
                new InputTextDialog.ActionClickListener() {
                    @Override
                    public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                        return onTextViewChange(mBinding.nameTv, nameLabel, textList.get(0), 2, 16);
                    }
                });

        mBinding.selectIv.setOnClickListener(this);
        mBinding.inputIv.setOnClickListener(this);
        mBinding.nameTv.setOnClickListener(this);
        mBinding.btnSaveTv.setOnClickListener(this);
        mBinding.btnClearTv.setOnClickListener(this);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        goBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv.setText(R.string.face_home_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.selectIv.getId()) {
            goGetFace(true);
        } else if (v.getId() == mBinding.inputIv.getId()) {
            goGetFace(false);
        } else if (v.getId() == mBinding.nameTv.getId()) {
            mNameDialog.show();
        } else if (v.getId() == mBinding.btnSaveTv.getId()) {
            PersonEntity entity = mBinding.getPersonData();
            DialogUtils.showConfirmDialog(this,
                    getString(R.string.face_person_d_save_title),
                    getString(R.string.face_person_d_save_msg), true,
                    new DialogUtils.IActionListener() {
                        @Override
                        public boolean onLeftBtnClick(Dialog dialog) {
                            return false;
                        }

                        @Override
                        public boolean onRightBtnClick(Dialog dialog) {
                            mViewModel.savePerson();
                            return false;
                        }
                    });
        } else if (v.getId() == mBinding.btnClearTv.getId()) {
            mViewModel.clearData();
        }
    }

    private boolean onTextViewChange(TextView textView, String labelStr, String valueStr,
                                     int minLength, int maxLength) {
        if (!TextUtils.isEmpty(valueStr)) {
            if (valueStr.length() >= minLength && valueStr.length() <= maxLength) {
                textView.setText(valueStr);
                return false;
            } else {
                showShortToast(getString(R.string.input_str_range_err_msg,
                        labelStr, minLength, maxLength));
                return true;
            }
        } else {
            showShortToast(getString(R.string.input_empty_or_format_err_msg,
                    labelStr));
            return true;
        }
    }

    private void setupPersonView(PersonEntity personEntity) {
        mBinding.setPersonData(personEntity);
        if (!TextUtils.isEmpty(personEntity.getFacePath())) {
            ImageLoaderManager.getInstance().loadImage(this, personEntity.getFacePath(),
                    mBinding.inputIv, ImageCacheStrategy.NONE);
        } else {
            mBinding.inputIv.setImageDrawable(null);
        }
    }

    private void goGetFace(boolean identity) {
        DialogUtils.showConfirmDialog(this, "人脸获取", "选择获取人脸方式",
                "照片", "拍照", true, new DialogUtils.IActionListener() {
                    @Override
                    public boolean onLeftBtnClick(Dialog dialog) {
                        requestExternalStoragePermission(identity);
                        return false;
                    }

                    @Override
                    public boolean onRightBtnClick(Dialog dialog) {
                        startActivityForResult(new Intent(FaceHomeActivity.this, GetFaceMarkActivity.class),
                                identity ? REQUEST_IDENTITY_BY_CAMERA : REQUEST_GET_FACE_MARK_BY_CAMERA);
                        return false;
                    }
                });
    }

    private void requestExternalStoragePermission(boolean identity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, identity ? REQUEST_IDENTITY_BY_FILE : REQUEST_GET_FACE_MARK_BY_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GET_FACE_MARK_BY_CAMERA) {
                String picPath = data.getStringExtra("picPath");
                mViewModel.onFacePicGet(picPath);
            } else if (requestCode == REQUEST_GET_FACE_MARK_BY_FILE) {
                Uri uri = data.getData();
                String picPath = FaceUrlConstants.PERSON_FACE_PATH();
                File file = new File(picPath);
                if (DocumentUtils.copyFile(this, uri, file)) {
                    mViewModel.onFacePicGet(picPath);
                }
            } else if (requestCode == REQUEST_IDENTITY_BY_CAMERA) {
            } else if (requestCode == REQUEST_IDENTITY_BY_FILE) {

            }
        }
    }
}
