package com.pine.mvvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.databinding.MvvmShopCheckItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopCheckListPaginationAdapter extends BasePaginationListAdapter<MvvmShopItemEntity> {
    public static final int SHOP_CHECK_VIEW_HOLDER = 1;
    private boolean mIsSearchMode;
    private ArrayList<MvvmShopItemEntity> mInitCheckedList;
    private Map<String, MvvmShopItemEntity> mAllCheckedMap = new HashMap<>();
    private Map<String, MvvmShopItemEntity> mSearchModeCheckedMap = new HashMap<>();

    public MvvmShopCheckListPaginationAdapter(ArrayList<MvvmShopItemEntity> initCheckedList, int defaultItemViewType) {
        super(defaultItemViewType);
        mInitCheckedList = initCheckedList;
        if (mInitCheckedList != null) {
            for (int j = mInitCheckedList.size() - 1; j >= 0; j--) {
                mAllCheckedMap.put(mInitCheckedList.get(j).getId(), mInitCheckedList.get(j));
                mInitCheckedList.remove(j);
            }
        }
    }

    @Override
    public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case SHOP_CHECK_VIEW_HOLDER:
                viewHolder = new ShopCheckViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mvvm_item_shop_check, parent, false));
                break;
        }
        return viewHolder;
    }

    public void setData(ArrayList<MvvmShopItemEntity> data, boolean isSearchMode) {
        mIsSearchMode = isSearchMode;
        mSearchModeCheckedMap.clear();
        setData(data);
    }

    @Override
    protected List<BaseListAdapterItemEntity<MvvmShopItemEntity>> parseData(List<MvvmShopItemEntity> data, boolean reset) {
        List<BaseListAdapterItemEntity<MvvmShopItemEntity>> adapterData = new ArrayList<>();
        if (data != null) {
            BaseListAdapterItemEntity adapterEntity;
            for (int i = 0; i < data.size(); i++) {
                adapterEntity = new BaseListAdapterItemEntity();
                MvvmShopItemEntity entity = data.get(i);
                adapterEntity.setData(entity);
                adapterEntity.getPropertyEntity().setItemViewType(getDefaultItemViewType());
                adapterData.add(adapterEntity);
            }
        }
        return adapterData;
    }

    public Map<String, MvvmShopItemEntity> getAllCheckedData() {
        return mAllCheckedMap;
    }

    public void clearCheckedData() {
        if (mIsSearchMode) {
            for (String key : mSearchModeCheckedMap.keySet()) {
                mAllCheckedMap.remove(key);
            }
            mSearchModeCheckedMap.clear();
        } else {
            mSearchModeCheckedMap.clear();
            mAllCheckedMap.clear();
            if (mInitCheckedList != null) {
                mInitCheckedList.clear();
            }
        }
        notifyDataSetChanged();
    }

    public class ShopCheckViewHolder extends BaseListViewHolder<MvvmShopItemEntity> {
        private Context mContext;
        private MvvmShopCheckItemBinding mBinding;

        public ShopCheckViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final MvvmShopItemEntity content, BaseListAdapterItemProperty propertyEntity, final int position) {
            mBinding.itemCb.setChecked(mAllCheckedMap.containsKey(content.getId()));
            if (mIsSearchMode && mBinding.itemCb.isChecked()) {
                mSearchModeCheckedMap.put(content.getId(), content);
            }
            mBinding.setShop(content);
            mBinding.itemCb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBinding.itemCb.isChecked()) {
                        mAllCheckedMap.put(content.getId(), content);
                        if (mIsSearchMode) {
                            mSearchModeCheckedMap.put(content.getId(), content);
                        }
                    } else {
                        mAllCheckedMap.remove(content.getId());
                        if (mIsSearchMode) {
                            mSearchModeCheckedMap.remove(content.getId());
                        }
                    }
                }
            });
        }
    }
}
