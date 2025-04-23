package com.pine.template.face.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.template.base.component.image_loader.ImageCacheStrategy;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.recycle_view.adapter.BaseListAdapter;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.CustomDialog;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.face.FaceConstants;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.R;
import com.pine.template.face.adapter.PersonAdapter;
import com.pine.template.face.databinding.FaceHomeActivityBinding;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.utils.DocumentUtils;
import com.pine.template.face.vm.FaceHomeVm;
import com.pine.template.face.widgets.RefreshOverWidthRv;

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

    private final int REQUEST_IMPORT_DIR_SELECT = 99;

    private InputTextDialog mNameDialog;

    private PersonAdapter mPersonAdapter;
    private CustomDialog mPicShowDialog;
    private ImageView mPicShowDialogIv;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.personData.observe(this, new Observer<PersonEntity>() {
            @Override
            public void onChanged(PersonEntity personEntity) {
                setupPersonView(personEntity);
            }
        });
        mViewModel.personListData.observe(this, new Observer<List<PersonEntity>>() {
            @Override
            public void onChanged(List<PersonEntity> list) {
                if (mViewModel.personListData.getCustomData()) {
                    mPersonAdapter.setData(list);
                } else {
                    mPersonAdapter.addData(list);
                }
            }
        });
        mViewModel.resultPersonData.observe(this, new Observer<PersonEntity>() {
            @Override
            public void onChanged(PersonEntity entity) {
                if (entity == null) {
                    mBinding.resultTv.setText(R.string.face_no_person_match);
                    mBinding.resultIv.setImageDrawable(null);
                    return;
                } else {
                    mBinding.resultTv.setText(entity.getName());
                    ImageLoaderManager.getInstance().loadImage(FaceHomeActivity.this,
                            entity.getFacePath(), mBinding.resultIv);
                }
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPersonAdapter = new PersonAdapter();
        mPersonAdapter.setPageSize(FaceConstants.PERSON_PAGE_SIZE);
        View headView = LayoutInflater.from(this).inflate(R.layout.face_view_person_head, null);

        mPersonAdapter.setOnItemClickListener(new BaseListAdapter.IOnItemClickListener<PersonEntity>() {
            @Override
            public void onItemClick(View view, int position, String tag, PersonEntity entity) {
                if ("del".equals(tag)) {
                    DialogUtils.showConfirmDialog(FaceHomeActivity.this,
                            getString(R.string.face_person_d_delete_title),
                            getString(R.string.face_person_d_delete_msg, entity.getName()), false,
                            new DialogUtils.IActionListener() {
                                @Override
                                public boolean onLeftBtnClick(Dialog dialog) {
                                    return false;
                                }

                                @Override
                                public boolean onRightBtnClick(Dialog dialog) {
                                    mViewModel.delPerson(entity);
                                    return false;
                                }
                            });
                } else if ("mark".equals(tag)) {
                    ImageLoaderManager.getInstance().loadImage(FaceHomeActivity.this,
                            entity.getFacePath(), mPicShowDialogIv);
                    mPicShowDialog.show();
                } else if ("name".equals(tag)) {
                    mViewModel.setupPerson(entity);
                }
            }
        });
        mBinding.personRv.setup(this, headView, layoutManager, mPersonAdapter,
                new RefreshOverWidthRv.IRecycleViewListener() {
                    @Override
                    public void onRefresh() {
                        mViewModel.refreshPersonListData();
                    }

                    @Override
                    public void onLoadingMore() {
                        mViewModel.loadPersonListData(false, mPersonAdapter.getNextPageNo());
                    }
                });

        mPicShowDialog = new CustomDialog.Builder(this, R.layout.face_view_pic_show)
                .create(Gravity.CENTER, 0.8f, 0.8f, null);
        mPicShowDialogIv = mPicShowDialog.findViewById(R.id.iv_target);
        mPicShowDialogIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicShowDialog.dismiss();
            }
        });
        mPicShowDialog.setCancelable(true);
        mPicShowDialog.setCanceledOnTouchOutside(true);


        String nameLabel = getString(R.string.face_person_name_label);
        mNameDialog = DialogUtils.createTextInputDialog(this,
                nameLabel, "", 32,
                new InputTextDialog.ActionClickListener() {
                    @Override
                    public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                        return onNameChange(mBinding.nameTv, nameLabel, textList.get(0), 2, 16);
                    }
                });

        mBinding.selectIv.setOnClickListener(this);
        mBinding.inputIv.setOnClickListener(this);
        mBinding.nameTv.setOnClickListener(this);
        mBinding.importTv.setOnClickListener(this);
        mBinding.btnSaveTv.setOnClickListener(this);
        mBinding.goAddTv.setOnClickListener(this);
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
        } else if (v.getId() == mBinding.importTv.getId()) {
            requestExternalStorageDir();
        } else if (v.getId() == mBinding.goAddTv.getId()) {
            mViewModel.setupPerson(new PersonEntity());
        } else if (v.getId() == mBinding.btnSaveTv.getId()) {
            DialogUtils.showConfirmDialog(this,
                    getString(R.string.face_person_d_save_title),
                    getString(R.string.face_person_d_save_msg), false,
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
            DialogUtils.showConfirmDialog(this,
                    getString(R.string.face_person_d_clear_title),
                    getString(R.string.face_person_d_clear_msg), false,
                    new DialogUtils.IActionListener() {
                        @Override
                        public boolean onLeftBtnClick(Dialog dialog) {
                            return false;
                        }

                        @Override
                        public boolean onRightBtnClick(Dialog dialog) {
                            mViewModel.clearData();
                            return false;
                        }
                    });
        }
    }

    private boolean onNameChange(TextView textView, String labelStr, String valueStr,
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
        String picPath = TextUtils.isEmpty(personEntity.tempPicPath) ? personEntity.getFacePath() : personEntity.tempPicPath;
        if (!TextUtils.isEmpty(picPath)) {
            ImageLoaderManager.getInstance().loadImage(this, picPath,
                    mBinding.inputIv, ImageCacheStrategy.NONE);
        } else {
            mBinding.inputIv.setImageDrawable(null);
        }
        mBinding.btnSaveTv.setText(personEntity.isNew()
                ? R.string.face_add_person_data : R.string.face_update_person_data);
    }

    private void goGetFace(boolean identity) {
        DialogUtils.showConfirmDialog(this, getString(R.string.face_d_obtain_title),
                getString(R.string.face_d_obtain_msg),
                getString(R.string.face_d_obtain_method_pic),
                getString(R.string.face_d_obtain_method_camera),
                false, new DialogUtils.IActionListener() {
                    @Override
                    public boolean onLeftBtnClick(Dialog dialog) {
                        requestExternalStorageFile(identity);
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

    private void requestExternalStorageFile(boolean identity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, identity ? REQUEST_IDENTITY_BY_FILE : REQUEST_GET_FACE_MARK_BY_FILE);
    }

    private void requestExternalStorageDir() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_IMPORT_DIR_SELECT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMPORT_DIR_SELECT) {
                Uri treeUri = data.getData();
                confirmImport(treeUri);
            } else if (requestCode == REQUEST_GET_FACE_MARK_BY_CAMERA) {
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
                String path = data.getStringExtra("picPath");
                String picPath = FaceUrlConstants.IDENTITY_FACE_PATH();
                File file = new File(picPath);
                if (DocumentUtils.copyFile(new File(path), file)) {
                    ImageLoaderManager.getInstance().loadImage(this, file.getPath(),
                            mBinding.selectIv, ImageCacheStrategy.NONE);
                    mBinding.selectTv.setText(new File(path).getName());
                    mViewModel.onFacePicSelect(picPath);
                }
            } else if (requestCode == REQUEST_IDENTITY_BY_FILE) {
                Uri uri = data.getData();
                String picPath = FaceUrlConstants.IDENTITY_FACE_PATH();
                File file = new File(picPath);
                if (DocumentUtils.copyFile(this, uri, file)) {
                    ImageLoaderManager.getInstance().loadImage(this, file.getPath(),
                            mBinding.selectIv, ImageCacheStrategy.NONE);
                    mBinding.selectTv.setText(DocumentFile.fromSingleUri(this, uri).getName());
                    mViewModel.onFacePicSelect(picPath);
                }
            }
        }
    }

    private void confirmImport(Uri treeUri) {
        if (treeUri == null) {
            return;
        }
        String title = "导入人脸";
        String content = "是否导入该文件夹下的所有人脸照片";
        DialogUtils.showConfirmDialog(this, title, content, false, new DialogUtils.IActionListener() {
            @Override
            public boolean onLeftBtnClick(Dialog dialog) {
                return false;
            }

            @Override
            public boolean onRightBtnClick(Dialog dialog) {
                mViewModel.importFaces(FaceHomeActivity.this, treeUri);
                return false;
            }
        });
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        super.setLoadingUiVisibility(processing);
        if (mBinding.personRv.getSwipeRefreshLayout() == null) {
            return;
        }
        mBinding.personRv.getSwipeRefreshLayout().setRefreshing(processing);
    }
}
