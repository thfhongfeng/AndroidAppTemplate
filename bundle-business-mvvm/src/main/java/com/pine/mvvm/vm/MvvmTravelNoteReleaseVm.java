package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.editor.bean.TextImageEditorItemData;
import com.pine.base.component.editor.bean.TextImageItemEntity;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.model.IMvvmTravelNoteModel;
import com.pine.mvvm.model.MvvmModelFactory;
import com.pine.mvvm.ui.activity.MvvmShopSearchCheckActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmTravelNoteReleaseVm extends BaseViewModel {
    IMvvmTravelNoteModel mTravelNoteModel = MvvmModelFactory.getMvvmTravelNoteModel();

    @Override
    public void afterViewInit() {
        setNoteDetail(new MvvmTravelNoteDetailEntity());
    }

    public void onBelongShopSelected(Intent data) {
        setBelongShopList(data.<MvvmShopItemEntity>getParcelableArrayListExtra(MvvmShopSearchCheckActivity.RESULT_CHECKED_LIST_KEY));
    }

    public void addNote(List<List<TextImageEditorItemData>> noteDayList) {
        if (isUiLoading()) {
            return;
        }
        MvvmTravelNoteDetailEntity entity = mNoteDetailData.getValue();

        if (TextUtils.isEmpty(entity.getTitle())) {
            setToastResId(R.string.mvvm_note_release_title_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getSetOutDate())) {
            setToastResId(R.string.mvvm_note_release_set_out_date_need);
            return;
        }
        if (entity.getDayCount() < 1) {
            setToastResId(R.string.mvvm_note_release_day_count_incorrect);
            return;
        }
        if (entity.getBelongShops() == null || entity.getBelongShops().size() < 1) {
            setToastResId(R.string.mvvm_note_release_belong_shops_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getPreface())) {
            setToastResId(R.string.mvvm_note_release_preface_need);
            return;
        }
        if (noteDayList == null && noteDayList.size() < 1) {
            setToastResId(R.string.mvvm_note_release_note_content_need);
            return;
        }
        List<MvvmTravelNoteDetailEntity.DayBean> days = new ArrayList<>();
        for (int i = 0; i < noteDayList.size(); i++) {
            List<TextImageEditorItemData> dayContentList = noteDayList.get(i);
            if (dayContentList == null || dayContentList.size() < 1) {
                setToastResId(R.string.mvvm_note_release_day_note_need);
                return;
            }
            List<MvvmTravelNoteDetailEntity.DayBean.Content> contentList = new ArrayList<>();
            for (int j = 0; j < dayContentList.size(); j++) {
                TextImageEditorItemData itemData = dayContentList.get(j);
                switch (itemData.getType()) {
                    case TextImageItemEntity.TYPE_TEXT:
                        if (TextUtils.isEmpty(itemData.getText())) {
                            setToastResId(R.string.mvvm_note_release_day_note_text_need);
                            return;
                        }
                        break;
                    case TextImageItemEntity.TYPE_IMAGE:
                        if (TextUtils.isEmpty(itemData.getRemoteFilePath())) {
                            setToastResId(R.string.mvvm_note_release_day_note_image_need);
                            return;
                        }
                        break;
                    default:
                        setToastResId(R.string.mvvm_note_release_day_note_content_incorrect);
                        return;
                }
                MvvmTravelNoteDetailEntity.DayBean.Content dayContent = new MvvmTravelNoteDetailEntity.DayBean.Content();
                dayContent.setType(itemData.getType());
                dayContent.setText(itemData.getText());
                dayContent.setRemoteFilePath(itemData.getRemoteFilePath());
                dayContent.setOrderNum(j + 1);
                contentList.add(dayContent);
            }
            MvvmTravelNoteDetailEntity.DayBean dayBean = new MvvmTravelNoteDetailEntity.DayBean();
            dayBean.setContentList(contentList);
            days.add(dayBean);
        }
        entity.setDays(days);
        setUiLoading(true);
        mTravelNoteModel.requestAddTravelNote(entity.toMapJsonIgnoreEmpty(), new IModelAsyncResponse<MvvmTravelNoteDetailEntity>() {
            @Override
            public void onResponse(MvvmTravelNoteDetailEntity entity) {
                setUiLoading(false);
                setToastResId(R.string.mvvm_note_release_success);
                finishUi();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    private MutableLiveData<MvvmTravelNoteDetailEntity> mNoteDetailData = new MutableLiveData<>();

    public void setNoteDetail(MvvmTravelNoteDetailEntity noteDetail) {
        mNoteDetailData.setValue(noteDetail);
    }

    public MutableLiveData<MvvmTravelNoteDetailEntity> getNoteDetailData() {
        return mNoteDetailData;
    }

    private MutableLiveData<ArrayList<MvvmShopItemEntity>> mBelongShopListData = new MutableLiveData<>();

    public void setBelongShopList(ArrayList<MvvmShopItemEntity> list) {
        mBelongShopListData.setValue(list);
    }

    public MutableLiveData<ArrayList<MvvmShopItemEntity>> getBelongShopListData() {
        return mBelongShopListData;
    }
}