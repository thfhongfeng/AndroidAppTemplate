package com.pine.app.lib.input.candidate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.app.lib.input.R;
import com.pine.app.lib.input.databinding.InputCandidateItemBinding;
import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;

public class CandidateAdapter extends BaseNoPaginationListAdapter<CandidateBean> {
    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = new ViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.input_candidate_item, parent, false));
        return viewHolder;
    }

    class ViewHolder extends BaseListViewHolder<CandidateBean> {
        private Context mContext;
        private InputCandidateItemBinding mBinding;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final CandidateBean content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.tv.setText(content.getCandidate());
            // 数据改变时立即刷新数据，解决DataBinding导致的刷新闪烁问题
            mBinding.executePendingBindings();
            if (mItemClickListener != null) {
                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(mBinding.getRoot(), position, "root", content);
                    }
                });
            }
        }
    }
}
