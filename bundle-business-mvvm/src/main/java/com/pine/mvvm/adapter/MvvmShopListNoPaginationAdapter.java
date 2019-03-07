package com.pine.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pine.base.component.image_loader.ImageLoaderManager;
import com.pine.base.list.BaseListViewHolder;
import com.pine.base.list.adapter.BaseNoPaginationListAdapter;
import com.pine.base.list.bean.BaseListAdapterItemPropertyEntity;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.databinding.ShopItemBinding;
import com.pine.mvvm.ui.activity.MvvmShopDetailActivity;
import com.pine.tool.util.DecimalUtils;

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
        private ShopItemBinding mBinding;

        public ShopViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmShopItemEntity content, BaseListAdapterItemPropertyEntity propertyEntity, int position) {
            mBinding.setShop(content);
            ImageLoaderManager.getInstance().loadImage(mContext, content.getImgUrl(), mBinding.photoIv);
            String distanceStr = content.getDistance();
            if (TextUtils.isEmpty(distanceStr)) {
                mBinding.locationTv.setText("");
            } else {
                float distance = Float.parseFloat(distanceStr);
                if (distance >= 1000.0f) {
                    mBinding.locationTv.setText(DecimalUtils.divide(distance, 1000.0f, 2) + mContext.getString(R.string.unit_kilometre));
                } else {
                    mBinding.locationTv.setText(distance + mContext.getString(R.string.unit_metre));
                }
            }
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
