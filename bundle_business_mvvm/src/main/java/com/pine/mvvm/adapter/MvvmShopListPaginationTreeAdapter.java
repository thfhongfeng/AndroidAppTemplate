package com.pine.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BasePaginationTreeListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.databinding.ShopProductItemBinding;
import com.pine.mvvm.databinding.ShopTreeItemBinding;
import com.pine.mvvm.ui.activity.MvvmShopDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopListPaginationTreeAdapter extends BasePaginationTreeListAdapter<MvvmShopAndProductEntity> {
    public static final int SHOP_VIEW_HOLDER = 1;
    public static final int SHOP_PRODUCT_VIEW_HOLDER = 2;

    @Override
    public List<BaseListAdapterItemEntity<MvvmShopAndProductEntity>> parseTreeData(List<MvvmShopAndProductEntity> data,
                                                                                   boolean reset) {
        List<BaseListAdapterItemEntity<MvvmShopAndProductEntity>> adapterData = new ArrayList<>();
        if (data != null) {
            BaseListAdapterItemEntity adapterEntity;
            for (int i = 0; i < data.size(); i++) {
                MvvmShopAndProductEntity entity = data.get(i);
                adapterEntity = new BaseListAdapterItemEntity();
                adapterEntity.setData(entity);
                adapterEntity.getPropertyEntity().setItemViewType(SHOP_VIEW_HOLDER);
                List<MvvmShopAndProductEntity.ProductsBean> productList = entity.getProducts();
                adapterEntity.getPropertyEntity().setSubItemViewCount(productList == null ? 0 : productList.size());
                adapterData.add(adapterEntity);
                if (productList != null) {
                    for (int j = 0; j < productList.size(); j++) {
                        adapterEntity = new BaseListAdapterItemEntity();
                        adapterEntity.setData(productList.get(j));
                        adapterEntity.getPropertyEntity().setItemViewType(SHOP_PRODUCT_VIEW_HOLDER);
                        adapterData.add(adapterEntity);
                    }
                }
            }
        }
        return adapterData;
    }

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case SHOP_VIEW_HOLDER:
                viewHolder = new ShopViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_shop_tree, parent, false));
                break;
            case SHOP_PRODUCT_VIEW_HOLDER:
                viewHolder = new ShopProductViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_shop_product, parent, false));
                break;
        }
        return viewHolder;
    }

    public class ShopViewHolder extends BaseListViewHolder<MvvmShopAndProductEntity> {
        private Context mContext;
        private ShopTreeItemBinding mBinding;

        public ShopViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmShopAndProductEntity content, final BaseListAdapterItemProperty propertyEntity, final int position) {
            mBinding.setShop(content);
            mBinding.setShopProperty(propertyEntity);
            mBinding.setPosition(position);
            mBinding.toggleBtnTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean subWillShow = !propertyEntity.isItemViewSpread();
                    propertyEntity.setItemViewSpread(subWillShow);
                    for (int i = position + 1; i < propertyEntity.getSubItemViewCount() + position + 1; i++) {
                        mData.get(i).getPropertyEntity().setItemViewNeedShow(subWillShow);
                    }
                    notifyItemRangeChanged(position + getHeadViewCount(), propertyEntity.getSubItemViewCount() + 1);
                }
            });
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

    public class ShopProductViewHolder extends BaseListViewHolder<MvvmShopAndProductEntity.ProductsBean> {
        private Context mContext;
        private ShopProductItemBinding mBinding;

        public ShopProductViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmShopAndProductEntity.ProductsBean content,
                               BaseListAdapterItemProperty propertyEntity, int position) {
            if (!propertyEntity.isItemViewNeedShow()) {
                mBinding.container.setVisibility(View.GONE);
                return;
            }
            mBinding.container.setVisibility(View.VISIBLE);
            mBinding.setProduct(content);
            mBinding.setShopProperty(propertyEntity);
        }
    }
}
