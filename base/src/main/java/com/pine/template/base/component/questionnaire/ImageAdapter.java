package com.pine.template.base.component.questionnaire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.databinding.BaseUiQImgBinding;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.BaseViewHolder> {
    private List<String> mData;

    public void setData(List<String> data) {
        mData = data;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        viewHolder = new ImageViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                .inflate(R.layout.base_ui_q_img, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.updateData(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ImageViewHolder extends BaseViewHolder<String> {
        private Context mContext;
        private BaseUiQImgBinding mBinding;

        public ImageViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(String content, int position) {
            ImageLoaderManager.getInstance().loadImage(mContext, content, mBinding.imageIv);
        }
    }

    public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void updateData(T content, int position);
    }
}
