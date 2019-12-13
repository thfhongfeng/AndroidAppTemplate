package com.pine.mvp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pine.base.component.image_loader.ImageLoaderManager;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BasePaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.mvp.R;
import com.pine.mvp.bean.MvpShopItemEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopCheckListPaginationAdapter extends BasePaginationListAdapter<MvpShopItemEntity> {
    private boolean mIsSearchMode;
    private ArrayList<MvpShopItemEntity> mInitCheckedList;
    private Map<String, MvpShopItemEntity> mAllCheckedMap = new HashMap<>();
    private Map<String, MvpShopItemEntity> mSearchModeCheckedMap = new HashMap<>();

    public MvpShopCheckListPaginationAdapter(ArrayList<MvpShopItemEntity> initCheckedList) {
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
                .inflate(R.layout.mvp_item_shop_check, parent, false));
        return viewHolder;
    }

    public void setData(ArrayList<MvpShopItemEntity> data, boolean isSearchMode) {
        mIsSearchMode = isSearchMode;
        mSearchModeCheckedMap.clear();
        setData(data);
    }

    public Map<String, MvpShopItemEntity> getAllCheckedData() {
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

    public class ShopCheckViewHolder extends BaseListViewHolder<MvpShopItemEntity> {
        private Context mContext;
        private LinearLayout location_ll;
        private CheckBox item_cb;
        private ImageView photo_iv;
        private TextView title_tv, location_tv;

        public ShopCheckViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            item_cb = itemView.findViewById(R.id.item_cb);
            photo_iv = itemView.findViewById(R.id.photo_iv);
            title_tv = itemView.findViewById(R.id.title_tv);
            location_ll = itemView.findViewById(R.id.location_ll);
            location_tv = itemView.findViewById(R.id.location_tv);
        }

        @Override
        public void updateData(final MvpShopItemEntity content, BaseListAdapterItemProperty propertyEntity, final int position) {
            item_cb.setChecked(mAllCheckedMap.containsKey(content.getId()));
            if (mIsSearchMode && item_cb.isChecked()) {
                mSearchModeCheckedMap.put(content.getId(), content);
            }
            ImageLoaderManager.getInstance().loadImage(mContext, content.getMainImgUrl(), photo_iv);
            title_tv.setText(content.getName());
            String distanceStr = content.getFormatDistance();
            if (TextUtils.isEmpty(distanceStr)) {
                location_tv.setText("");
                location_ll.setVisibility(View.GONE);
            } else {
                location_tv.setText(distanceStr);
                location_ll.setVisibility(View.VISIBLE);
            }
            item_cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item_cb.isChecked()) {
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
