package com.pine.template.face.vm;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.app.lib.face.matcher.FaceMatcher;
import com.pine.template.face.FaceConstants;
import com.pine.template.face.FaceUrlConstants;
import com.pine.template.face.R;
import com.pine.template.face.db.LocalWorker;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.template.face.model.PersonDataWorker;
import com.pine.template.face.utils.DocumentUtils;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;
import com.pine.tool.util.LogUtils;

import java.io.File;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class FaceHomeVm extends ViewModel {
    private PersonEntity personEntity = new PersonEntity();
    public MutableLiveData<PersonEntity> personData = new MutableLiveData<>();

    public MutableLiveData<PersonEntity> resultPersonData = new MutableLiveData<>();

    public ParametricLiveData<List<PersonEntity>, Boolean> personListData = new ParametricLiveData<>();

    @Override
    public void afterViewInit(Context activity) {
        super.afterViewInit(activity);
        personData.setValue(personEntity);
    }

    public void setupPerson(PersonEntity entity) {
        personEntity = entity;
        personData.setValue(entity);
    }

    public void onFacePicGet(String picPath) {
        LogUtils.d(TAG, "onFacePicGet picPath:" + picPath);
        byte[] faceFeature = FaceMatcher.getInstance().toFaceFeatureBytes(picPath);
        if (faceFeature != null && faceFeature.length > 0) {
            personEntity.tempPicPath = picPath;
            personEntity.setFaceFeatureBytes(faceFeature);
            personData.setValue(personEntity);
        } else {
            setToastResId(R.string.face_get_face_tip_fail);
        }
    }

    public void onFacePicSelect(String picPath) {
        LogUtils.d(TAG, "onFacePicSelect picPath:" + picPath);
        setUiLoading(true);
        PersonDataWorker.getInstance().identityCheck(picPath, new IAsyncResponse<PersonEntity>() {
            @Override
            public void onResponse(PersonEntity entity) {
                setUiLoading(false);
                resultPersonData.setValue(entity);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                resultPersonData.setValue(null);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
                resultPersonData.setValue(null);
            }
        });
    }

    public void refreshPersonListData() {
        loadPersonListData(true, 1);
    }

    public void loadPersonListData(final boolean refresh, int pageNo) {
        if (isUiLoading()) {
            return;
        }
        setUiLoading(true);
        LocalWorker.getInstance().requestPersonListData(pageNo, FaceConstants.PERSON_PAGE_SIZE,
                new IAsyncResponse<List<PersonEntity>>() {
                    @Override
                    public void onResponse(List<PersonEntity> list) {
                        setUiLoading(false);
                        personListData.setValue(list, refresh);
                    }

                    @Override
                    public boolean onFail(Exception e) {
                        setUiLoading(false);
                        if (!toastFailMsg(e)) {
                            setToastResId(R.string.base_item_load_fail);
                        }
                        return true;
                    }

                    @Override
                    public void onCancel() {
                        setUiLoading(false);
                    }
                });
    }

    public void delPerson(PersonEntity entity) {
        setUiLoading(true);
        LocalWorker.getInstance().requestDeletePerson(entity, new IAsyncResponse<Boolean>() {
            @Override
            public void onResponse(Boolean success) {
                setUiLoading(false);
                setToastResId(R.string.base_delete_success);
                refreshPersonListData();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (!toastFailMsg(e)) {
                    setToastResId(R.string.base_delete_fail);
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    public boolean checkName(String name) {
        return LocalWorker.getInstance().checkNameExist(name) == null;
    }

    public void savePerson() {
        boolean newAdd = personEntity.isNew();
        if (newAdd && !checkName(personEntity.getName())) {
            setToastResId(R.string.face_name_exist);
            return;
        }
        setUiLoading(true);
        if (TextUtils.isEmpty(personEntity.getName())) {
            setToastResFormat(R.string.input_empty_or_format_err_msg, R.string.face_person_name_label);
            setUiLoading(false);
            return;
        }
        if (TextUtils.isEmpty(personEntity.getFaceFeatureData())) {
            setToastResFormat(R.string.input_empty_or_format_err_msg, R.string.face_person_pic_label);
            setUiLoading(false);
            return;
        }
        if (TextUtils.isEmpty(personEntity.getFacePath())) {
            if (!TextUtils.isEmpty(personEntity.tempPicPath)) {
                String dbFilePath = FaceUrlConstants.PERSON_DB_FACE_PATH(personEntity.getName() + ".jpg");
                if (DocumentUtils.copyFile(new File(personEntity.tempPicPath), new File(dbFilePath))) {
                    personEntity.setFacePath(dbFilePath);
                }
            } else {
                setToastResFormat(R.string.input_empty_or_format_err_msg, R.string.face_person_pic_label);
                setUiLoading(false);
                return;
            }
        }
        LocalWorker.getInstance().requestSavePerson(newAdd, personEntity, new IAsyncResponse<PersonEntity>() {
            @Override
            public void onResponse(PersonEntity entity) {
                setUiLoading(false);
                setToastResId(R.string.base_save_success);
                refreshPersonListData();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (!toastFailMsg(e)) {
                    setToastResId(R.string.base_save_fail);
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    public void clearData() {
        setUiLoading(true);
        LocalWorker.getInstance().requestClearPersonList(new IAsyncResponse<Boolean>() {
            @Override
            public void onResponse(Boolean success) {
                setUiLoading(false);
                setToastResId(success ? R.string.base_delete_success : R.string.base_delete_fail);
                refreshPersonListData();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (!toastFailMsg(e)) {
                    setToastResId(R.string.base_delete_fail);
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    public void importFaces(Context context, Uri uri) {
        setUiLoading(true);
        LocalWorker.getInstance().requestImportPersonList(context, uri, new IAsyncResponse<Boolean>() {
            @Override
            public void onResponse(Boolean success) {
                setUiLoading(false);
                setToastResId(success ? R.string.face_import_person_success : R.string.face_import_person_fail);
                refreshPersonListData();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                setToastResId(R.string.face_import_person_fail);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    private boolean toastFailMsg(Exception e) {
        if (e instanceof MessageException) {
            if (!TextUtils.isEmpty(e.getMessage())) {
                setToastMsg(e.getMessage());
                return true;
            }
        }
        return false;
    }
}
