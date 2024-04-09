package com.pine.template.mvvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.databinding.MvvmShopCheckItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopCheckListPaginationAdapter extends BasePaginationListAdapter<MvvmShopItemEntity> {
    private boolean mIsSearchMode;
    private ArrayList<MvvmShopItemEntity> mInitCheckedList;
    private Map<String, MvvmShopItemEntity> mAllCheckedMap = new HashMap<>();
    private Map<String, MvvmShopItemEntity> mSearchModeCheckedMap = new HashMap<>();

    public MvvmShopCheckListPaginationAdapter(ArrayList<MvvmShopItemEntity> initCheckedList) {
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
        BaseListViewHolder viewHolder = new ShopCheckViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mvvm_item_shop_check, parent, false));
        return viewHolder;
    }

    public void setData(ArrayList<MvvmShopItemEntity> data, boolean isSearchMode) {
        mIsSearchMode = isSearchMode;
        mSearchModeCheckedMap.clear();
        setData(data);
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
            // 数据改变时立即刷新数据，解决DataBinding导致的刷新闪烁问题
            mBinding.executePendingBindings();
        }
    }
}
