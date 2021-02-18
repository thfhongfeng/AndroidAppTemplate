package com.pine.template.base.widget.dialog;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.List;

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

        public <T> CustomListDialog create(int titleLayoutId, int itemLayoutId,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            return create(titleLayoutId, itemLayoutId, -1, Gravity.BOTTOM, true, itemList, callback);
        }

        public <T> CustomListDialog create(int titleLayoutId, int itemLayoutId, boolean fillWidth,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            return create(titleLayoutId, itemLayoutId, -1, Gravity.BOTTOM, fillWidth, itemList, callback);
        }

        public <T> CustomListDialog create(int titleLayoutId, int itemLayoutId, int layoutGravity,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            return create(titleLayoutId, itemLayoutId, -1, layoutGravity, true, itemList, callback);
        }

        public <T> CustomListDialog create(String title, int itemLayoutId, int actionLayoutId,
                                           int layoutGravity, boolean fillWidth,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            TextView titleView = new TextView(context);
            titleView.setTextColor(context.getResources().getColor(R.color.dark_gray_black));
            int padding = context.getResources().getDimensionPixelOffset(R.dimen.dp_20);
            titleView.setPadding(padding, padding, padding, padding);
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextSize(18);
            titleView.setText(title);
            return create(titleView, itemLayoutId, actionLayoutId, layoutGravity, fillWidth, itemList, callback);
        }

        public <T> CustomListDialog create(int titleLayoutId, int itemLayoutId, int actionLayoutId,
                                           int layoutGravity, boolean fillWidth,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            View titleView = null;
            if (titleLayoutId > 0) {
                titleView = LayoutInflater.from(context).inflate(titleLayoutId, null);
            }
            return create(titleView, itemLayoutId, -1, layoutGravity, true, itemList, callback);
        }

        public <T> CustomListDialog create(View titleView, int itemLayoutId, int actionLayoutId,
                                           int layoutGravity, boolean fillWidth,
                                           List<T> itemList, @NonNull IOnViewBindCallback<T> callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomListDialog dialog = new CustomListDialog(context, R.style.BaseDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_custom_list, null);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            if (layoutGravity == Gravity.BOTTOM && fillWidth) {
                layout.findViewById(R.id.container_ll).setBackgroundResource(R.drawable.base_shape_round_top_white);
            } else {
                layout.findViewById(R.id.container_ll).setBackgroundResource(R.drawable.base_shape_round_white);
            }
            window.setGravity(layoutGravity);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            if (fillWidth) {
                p.width = d.getWidth(); //宽度设置为屏幕
            }
            p.height = d.getHeight() * 8 / 10;
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            RelativeLayout title_rl = layout.findViewById(R.id.title_rl);
            RecyclerView recycle_view = layout.findViewById(R.id.recycle_view);
            RelativeLayout action_rl = layout.findViewById(R.id.action_rl);
            if (titleView == null) {
                title_rl.setVisibility(View.GONE);
            } else {
                title_rl.addView(titleView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title_rl.setVisibility(View.VISIBLE);
            }
            View actionView = null;
            if (actionLayoutId < 0) {
                action_rl.setVisibility(View.GONE);
            } else {
                actionView = LayoutInflater.from(context).inflate(actionLayoutId, null);
                action_rl.addView(actionView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                action_rl.setVisibility(View.VISIBLE);
            }
            DialogListAdapter dialogListAdapter = new DialogListAdapter(titleView, itemLayoutId, dialog, callback);
            dialogListAdapter.enableEmptyComplete(false, false);
            dialog.mDialogListAdapter = dialogListAdapter;
            if (callback != null && (titleView != null || actionView != null)) {
                callback.onViewBind(titleView, actionView, dialog);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recycle_view.setLayoutManager(layoutManager);
            recycle_view.setAdapter(dialogListAdapter);
            dialogListAdapter.setData(itemList);

            return dialog;
        }
    }

    public static class DialogListAdapter<T> extends BaseNoPaginationListAdapter {
        private CustomListDialog dialog;
        private IOnViewBindCallback callback;
        private View titleView, actionView;
        private int itemLayoutId;

        public DialogListAdapter(View titleView, int itemLayoutId, CustomListDialog dialog, IOnViewBindCallback callback) {
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
            if (callback != null && (titleView != null || actionView != null)) {
                callback.onListDataChange(titleView, actionView, dialog);
            }
        }

        @Override
        protected void onDataAdd() {
            super.onDataAdd();
            if (callback != null && (titleView != null || actionView != null)) {
                callback.onListDataChange(titleView, actionView, dialog);
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
                    callback.onItemViewUpdate(itemView, position, content, dialog);
                }
            }
        }
    }

    @Override
    public void show() {
        mDialogListAdapter.notifyDataSetChangedSafely();
        super.show();
    }

    public interface IOnViewBindCallback<T> {
        void onViewBind(View titleView, View actionView, CustomListDialog dialog);

        void onItemViewUpdate(View itemView, int position, T data, CustomListDialog dialog);

        void onListDataChange(View titleView, View actionView, CustomListDialog dialog);
    }
}
