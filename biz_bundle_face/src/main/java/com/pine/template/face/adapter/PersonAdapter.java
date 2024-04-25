package com.pine.template.face.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.face.R;
import com.pine.template.face.databinding.FaceItemPersonBinding;
import com.pine.template.face.db.entity.PersonEntity;

public class PersonAdapter extends BasePaginationListAdapter<PersonEntity> {

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = new ViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.face_item_person, parent, false));
        return viewHolder;
    }

    public class ViewHolder extends BaseListViewHolder<PersonEntity> {
        private Context mContext;
        private FaceItemPersonBinding mBinding;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final PersonEntity content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setPersonData(content);
            // 数据改变时立即刷新数据，解决DataBinding导致的刷新闪烁问题
            mBinding.executePendingBindings();
            if (mItemClickListener != null) {
                mBinding.tvDelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(mBinding.tvDelBtn, position, "del", content);
                    }
                });
                mBinding.tvMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(mBinding.tvMark, position, "mark", content);
                    }
                });
                mBinding.tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(mBinding.tvMark, position, "name", content);
                    }
                });
            }
        }
    }
}
