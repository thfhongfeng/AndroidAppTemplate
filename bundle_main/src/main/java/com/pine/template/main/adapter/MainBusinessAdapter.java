package com.pine.template.main.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.main.R;
import com.pine.template.main.bean.MainBusinessItemEntity;
import com.pine.template.main.databinding.MainItemBinding;
import com.pine.template.main.remote.MainRouterClient;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterCommandType;

/**
 * Created by tanghongfeng on 2019/1/16
 */

public class MainBusinessAdapter extends BaseNoPaginationListAdapter {

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = new BusinessViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_item_business, parent, false));
        return viewHolder;
    }

    public class BusinessViewHolder extends BaseListViewHolder<MainBusinessItemEntity> {
        private Context mContext;
        private MainItemBinding mBinding;

        public BusinessViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MainBusinessItemEntity content,
                               BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.nameTv.setText(content.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainRouterClient.callCommand(mContext, content.getBundle(), RouterCommandType.TYPE_UI_COMMAND,
                            content.getCommand(), null, new IRouterCallback() {
                                @Override
                                public boolean onSuccess(Bundle responseBundle) {
                                    return true;
                                }

                                @Override
                                public boolean onFail(int failCode, String errorInfo) {
                                    return false;
                                }
                            });
                }
            });
            // 数据改变时立即刷新数据，解决DataBinding导致的刷新闪烁问题
            mBinding.executePendingBindings();
        }
    }
}
