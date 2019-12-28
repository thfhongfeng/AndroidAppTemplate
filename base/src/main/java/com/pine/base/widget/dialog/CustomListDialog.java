package com.pine.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.pine.base.R;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomListDialog extends Dialog {
    protected DialogListAdapter mDialogListAdapter;

    protected CustomListDialog(Context context) {
        super(context);
    }

    protected CustomListDialog(Context context, int theme) {
        super(context, theme);
    }

    public DialogListAdapter getListAdapter() {
        return mDialogListAdapter;
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public <T> CustomListDialog create(int titleLayoutId, @LayoutRes int itemLayoutId,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomListDialog dialog = new CustomListDialog(context, R.style.BaseDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_custom_list, null);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = d.getWidth(); //宽度设置为屏幕
            p.height = d.getHeight() * 8 / 10;
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            RelativeLayout title_rl = layout.findViewById(R.id.title_rl);
            RecyclerView recycle_view = layout.findViewById(R.id.recycle_view);
            View titleView = null;
            if (titleLayoutId < 0) {
                title_rl.setVisibility(View.GONE);
            } else {
                titleView = LayoutInflater.from(context).inflate(titleLayoutId, null);
                title_rl.addView(titleView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title_rl.setVisibility(View.VISIBLE);
            }
            DialogListAdapter dialogListAdapter = new DialogListAdapter(titleView, itemLayoutId, dialog, callback);
            dialogListAdapter.enableEmptyComplete(false, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recycle_view.setLayoutManager(layoutManager);
            recycle_view.setAdapter(dialogListAdapter);
            dialogListAdapter.setData(itemList);
            dialog.mDialogListAdapter = dialogListAdapter;
            return dialog;
        }
    }

    public static class DialogListAdapter<T> extends BaseNoPaginationListAdapter {
        private CustomListDialog dialog;
        private IOnViewBindCallback callback;
        private View titleView;
        @LayoutRes
        private int itemLayoutId;

        public DialogListAdapter(View titleView, @LayoutRes int itemLayoutId, CustomListDialog dialog, IOnViewBindCallback callback) {
            this.dialog = dialog;
            this.titleView = titleView;
            this.itemLayoutId = itemLayoutId;
            this.callback = callback;
        }

        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new DialogListAdapter.ViewHolder(this, LayoutInflater.from(parent.getContext())
                    .inflate(itemLayoutId, parent, false));
        }

        @Override
        protected void onDataSet() {
            super.onDataSet();
            if (callback != null && titleView != null) {
                callback.onTitleBind(titleView, dialog);
            }
        }

        @Override
        protected void onDataAdd() {
            super.onDataAdd();
            if (callback != null && titleView != null) {
                callback.onTitleBind(titleView, dialog);
            }
        }

        public class ViewHolder extends BaseListViewHolder<T> {
            private DialogListAdapter adapter;

            public ViewHolder(DialogListAdapter adapter, View itemView) {
                super(itemView);
                this.adapter = adapter;
            }

            @Override
            public void updateData(final T content, BaseListAdapterItemProperty propertyEntity, final int position) {
                if (callback != null) {
                    callback.onItemBind(itemView, position, content, dialog);
                }
            }
        }
    }

    public interface IOnViewBindCallback<T> {
        void onTitleBind(View titleView, CustomListDialog dialog);

        void onItemBind(View itemView, int position, T data, CustomListDialog dialog);
    }
}
