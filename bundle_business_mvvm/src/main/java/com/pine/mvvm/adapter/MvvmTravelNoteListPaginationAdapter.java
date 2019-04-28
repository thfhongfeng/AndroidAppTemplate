package com.pine.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.mvvm.databinding.TravelNoteItemBinding;
import com.pine.mvvm.ui.activity.MvvmTravelNoteDetailActivity;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteListPaginationAdapter extends BasePaginationListAdapter {
    public static final int TRAVEL_NOTE_VIEW_HOLDER = 1;

    public MvvmTravelNoteListPaginationAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case TRAVEL_NOTE_VIEW_HOLDER:
                viewHolder = new TravelNoteViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_travel_note, parent, false));
                break;
        }
        return viewHolder;
    }

    public class TravelNoteViewHolder extends BaseListViewHolder<MvvmTravelNoteItemEntity> {
        private Context mContext;
        private TravelNoteItemBinding mBinding;

        public TravelNoteViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmTravelNoteItemEntity content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setTravelNote(content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MvvmTravelNoteDetailActivity.class);
                    intent.putExtra("id", content.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
