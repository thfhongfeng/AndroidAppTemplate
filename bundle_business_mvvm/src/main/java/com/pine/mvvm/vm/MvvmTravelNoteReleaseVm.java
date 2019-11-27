package com.pine.mvvm.vm;

import android.content.Intent;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.base.component.editor.bean.TextImageEntity;
import com.pine.base.component.editor.bean.TextImageItemEntity;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.model.MvvmTravelNoteModel;
import com.pine.mvvm.ui.activity.MvvmShopSearchCheckActivity;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmTravelNoteReleaseVm extends ViewModel {
    MvvmTravelNoteModel mTravelNoteModel = new MvvmTravelNoteModel();

    @Override
    public void afterViewInit() {
        setNoteDetail(new MvvmTravelNoteDetailEntity());
    }

    public void onBelongShopSelected(Intent data) {
        setBelongShopList(data.<MvvmShopItemEntity>getParcelableArrayListExtra(MvvmShopSearchCheckActivity.RESULT_CHECKED_LIST_KEY));
    }

    public void addNote(List<TextImageEntity> noteDayList) {
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
            TextImageEntity day = noteDayList.get(i);
            List<TextImageItemEntity> dayContentList = day.getItemList();
            if (dayContentList == null || dayContentList.size() < 1) {
                setToastResId(R.string.mvvm_note_release_day_note_need);
                return;
            }
            List<MvvmTravelNoteDetailEntity.DayBean.Content> contentList = new ArrayList<>();
            for (int j = 0; j < dayContentList.size(); j++) {
                TextImageItemEntity itemData = dayContentList.get(j);
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
            dayBean.setId((i + 1) + "");
            dayBean.setDay(entity.getTitle());
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
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        setToastMsg(e.getMessage());
                    }
                    return true;
                }
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
