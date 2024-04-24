package com.pine.template.face.vm;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.app.lib.face.matcher.FaceMatcher;
import com.pine.template.face.R;
import com.pine.template.face.db.LocalWorker;
import com.pine.template.face.db.entity.PersonEntity;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class FaceHomeVm extends ViewModel {

    private PersonEntity personEntity = new PersonEntity();
    public MutableLiveData<PersonEntity> personData = new MutableLiveData<>();

    public MutableLiveData<Integer> saveResultData = new MutableLiveData<>();

    @Override
    public void afterViewInit(Context activity) {
        super.afterViewInit(activity);
        personData.setValue(personEntity);
    }

    public void onFacePicGet(String picPath) {
        LogUtils.d(TAG, "onFacePicGet picPath:" + picPath);
        byte[] faceFeature = FaceMatcher.getInstance().toFaceFeatureBytes(picPath);
        if (faceFeature != null && faceFeature.length > 0) {
            personEntity.setFacePath(picPath);
            personEntity.setFaceFeatureBytes(faceFeature);
            personData.setValue(personEntity);
        } else {
            setToastResId(R.string.face_get_face_tip_fail);
        }
    }

    public void savePerson() {
        setUiLoading(true);
        if (TextUtils.isEmpty(personEntity.getName())) {
            setToastResFormat(R.string.input_empty_or_format_err_msg, R.string.face_person_name_label);
            setUiLoading(false);
            return;
        }
        if (TextUtils.isEmpty(personEntity.getFaceFeatureData()) || TextUtils.isEmpty(personEntity.getFacePath())) {
            setToastResFormat(R.string.input_empty_or_format_err_msg, R.string.face_person_pic_label);
            setUiLoading(false);
            return;
        }
        LocalWorker.getInstance().requestAddPerson(personEntity, new IModelAsyncResponse<PersonEntity>() {
            @Override
            public void onResponse(PersonEntity entity) {
                setUiLoading(false);
                setToastResId(R.string.base_save_success);
                saveResultData.setValue(hashCode());
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
