package com.pine.template.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.databinding.MvvmShopItemBinding;
import com.pine.template.mvvm.ui.activity.MvvmShopDetailActivity;

//import com.pine.template.mvvm.ui.activity.MvvmShopDetailActivity;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopListPaginationAdapter extends BasePaginationListAdapter {

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = new ShopViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mvvm_item_shop, parent, false));
        return viewHolder;
    }

    public class ShopViewHolder extends BaseListViewHolder<MvvmShopItemEntity> {
        private Context mContext;
        private MvvmShopItemBinding mBinding;

        public ShopViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmShopItemEntity content, BaseListAdapterItemProperty propertyEntity, int position) {
            mBinding.setShop(content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MvvmShopDetailActivity.class);
                    intent.putExtra("id", content.getId());
                    mContext.startActivity(intent);
                }
            });
            // 数据改变时立即刷新数据，解决DataBinding导致的刷新闪烁问题
            mBinding.executePendingBindings();
        }
    }
}
