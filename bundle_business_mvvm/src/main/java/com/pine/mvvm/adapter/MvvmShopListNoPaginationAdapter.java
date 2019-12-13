package com.pine.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.databinding.MvvmShopItemBinding;
import com.pine.mvvm.ui.activity.MvvmShopDetailActivity;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopListNoPaginationAdapter extends BaseNoPaginationListAdapter {
    public static final int SHOP_VIEW_HOLDER = 1;

    public MvvmShopListNoPaginationAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case SHOP_VIEW_HOLDER:
                viewHolder = new ShopViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_shop, parent, false));
                break;
        }
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
        }
    }
}
