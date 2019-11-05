package com.pine.mvvm.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BaseComplexListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.databinding.MvvmTravelNoteCommentItemBinding;
import com.pine.mvvm.databinding.MvvmTravelNoteDayItemBinding;
import com.pine.mvvm.databinding.MvvmTravelNoteHeadItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteDetailComplexAdapter extends BaseComplexListAdapter<MvvmTravelNoteDetailEntity, MvvmTravelNoteCommentEntity> {
    public static final int TRAVEL_NOTE_HEAD_VIEW_HOLDER = 1;
    public static final int TRAVEL_NOTE_DAY_VIEW_HOLDER = 2;
    public static final int TRAVEL_NOTE_COMMENT_HEAD_VIEW_HOLDER = 3;
    public static final int TRAVEL_NOTE_COMMENT_VIEW_HOLDER = 4;

    public BaseListViewHolder<String> getEmptyBackgroundViewHolder(ViewGroup parent) {
        return new EmptyBackgroundViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(com.pine.base.R.layout.base_item_empty_background, parent, false));
    }

    @Override
    public List<BaseListAdapterItemEntity<MvvmTravelNoteDetailEntity>> parseFirstPartData(List<MvvmTravelNoteDetailEntity> data) {
        List<BaseListAdapterItemEntity<MvvmTravelNoteDetailEntity>> adapterData = new ArrayList<>();
        if (data != null) {
            BaseListAdapterItemEntity adapterEntity;
            for (int i = 0; i < data.size(); i++) {
                MvvmTravelNoteDetailEntity entity = data.get(i);
                adapterEntity = new BaseListAdapterItemEntity();
                adapterEntity.setData(entity);
                adapterEntity.getPropertyEntity().setItemViewType(TRAVEL_NOTE_HEAD_VIEW_HOLDER);
                List<MvvmTravelNoteDetailEntity.DayBean> dayList = entity.getDays();
                adapterData.add(adapterEntity);
                if (dayList != null) {
                    for (int j = 0; j < dayList.size(); j++) {
                        adapterEntity = new BaseListAdapterItemEntity();
                        adapterEntity.setData(dayList.get(j));
                        adapterEntity.getPropertyEntity().setItemViewType(TRAVEL_NOTE_DAY_VIEW_HOLDER);
                        adapterData.add(adapterEntity);
                    }
                }
            }
            adapterEntity = new BaseListAdapterItemEntity();
            adapterEntity.setData("");
            adapterEntity.getPropertyEntity().setItemViewType(TRAVEL_NOTE_COMMENT_HEAD_VIEW_HOLDER);
            adapterData.add(adapterEntity);
        }
        return adapterData;
    }

    @Override
    public List<BaseListAdapterItemEntity<MvvmTravelNoteCommentEntity>> parseSecondPartData(List<MvvmTravelNoteCommentEntity> data) {
        List<BaseListAdapterItemEntity<MvvmTravelNoteCommentEntity>> adapterData = new ArrayList<>();
        if (data != null) {
            BaseListAdapterItemEntity adapterEntity;
            for (int i = 0; i < data.size(); i++) {
                adapterEntity = new BaseListAdapterItemEntity();
                adapterEntity.setData(data.get(i));
                adapterEntity.getPropertyEntity().setItemViewType(TRAVEL_NOTE_COMMENT_VIEW_HOLDER);
                adapterData.add(adapterEntity);
            }
        }
        return adapterData;
    }

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case TRAVEL_NOTE_HEAD_VIEW_HOLDER:
                viewHolder = new TravelNoteHeadViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_travel_note_head, parent, false));
                break;
            case TRAVEL_NOTE_DAY_VIEW_HOLDER:
                viewHolder = new TravelNoteDayViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_travel_note_day, parent, false));
                break;
            case TRAVEL_NOTE_COMMENT_HEAD_VIEW_HOLDER:
                viewHolder = new TravelNoteCommentHeadViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_travel_note_comment_head, parent, false));
                break;
            case TRAVEL_NOTE_COMMENT_VIEW_HOLDER:
                viewHolder = new TravelNoteCommentViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_travel_note_comment, parent, false));
                break;
        }
        return viewHolder;
    }

    public class TravelNoteHeadViewHolder extends BaseListViewHolder<MvvmTravelNoteDetailEntity> {
        private Context mContext;
        private MvvmTravelNoteHeadItemBinding mBinding;

        public TravelNoteHeadViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(MvvmTravelNoteDetailEntity content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setTravelNoteDetail(content);
        }
    }

    public class TravelNoteDayViewHolder extends BaseListViewHolder<MvvmTravelNoteDetailEntity.DayBean> {
        private Context mContext;
        private MvvmTravelNoteDayItemBinding mBinding;

        public TravelNoteDayViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(MvvmTravelNoteDetailEntity.DayBean content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setTravelNoteDayDetail(content);
        }
    }

    public class TravelNoteCommentHeadViewHolder extends BaseListViewHolder<String> {
        private Context mContext;

        public TravelNoteCommentHeadViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {
        }
    }

    public class TravelNoteCommentViewHolder extends BaseListViewHolder<MvvmTravelNoteCommentEntity> {
        private Context mContext;
        private MvvmTravelNoteCommentItemBinding mBinding;

        public TravelNoteCommentViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(MvvmTravelNoteCommentEntity content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setTravelNoteComment(content);
        }
    }
}
