package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.base.util.DialogUtils;

import java.util.List;

public class CustomListDialog extends BaseDialog {
    protected DialogListAdapter mDialogListAdapter;

    protected CustomListDialog(Context context) {
        super(context);
    }

    protected CustomListDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogListAdapter getListAdapter() {
        return mDialogListAdapter;
    }

    public static class Builder {
        private Context context;
        private int themeResId = R.style.BaseDialogStyle;
        private View topLayout, bottomLayout;
        private int itemLayoutId;

        private void setupTopLayoutView(int layoutId) {
            if (layoutId < 0) {
                return;
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.topLayout = inflater.inflate(layoutId, null);
        }

        private void setupBottomLayoutView(int layoutId) {
            if (layoutId < 0) {
                return;
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.bottomLayout = inflater.inflate(layoutId, null);
        }

        public Builder(Context context, int itemLayoutId, int topLayoutId, int bottomLayoutId) {
            this.context = context;
            this.itemLayoutId = itemLayoutId;
            setupTopLayoutView(topLayoutId);
            setupBottomLayoutView(bottomLayoutId);
        }

        public Builder(Context context, int itemLayoutId, View topLayout, View bottomLayout) {
            this.context = context;
            this.itemLayoutId = itemLayoutId;
            this.topLayout = topLayout;
            this.bottomLayout = bottomLayout;
        }

        public Builder(Context context, int themeResId, int itemLayoutId, int topLayoutId, int bottomLayoutId) {
            this.context = context;
            this.themeResId = themeResId;
            this.itemLayoutId = itemLayoutId;
            setupTopLayoutView(topLayoutId);
            setupBottomLayoutView(bottomLayoutId);
        }

        public Builder(Context context, int themeResId, int itemLayoutId, View topLayout, View bottomLayout) {
            this.context = context;
            this.themeResId = themeResId;
            this.itemLayoutId = itemLayoutId;
            this.topLayout = topLayout;
            this.bottomLayout = bottomLayout;
        }

        public <T> CustomListDialog create(List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            return create(Gravity.BOTTOM, itemList, callback);
        }

        public <T> CustomListDialog create(int layoutGravity, List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            return create(layoutGravity, 1, 0.8f, itemList, callback);
        }

        public <T> CustomListDialog create(float widthPct, float heightPct, List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            return create(Gravity.BOTTOM, widthPct, heightPct, itemList, callback);
        }

        public <T> CustomListDialog create(int layoutGravity, float widthPct, float heightPct, List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            int width = (int) (d.getWidth() * widthPct); //宽度
            int height = (int) (d.getHeight() * heightPct); //高度
            return create(layoutGravity, width, height, itemList, callback);
        }

        public <T> CustomListDialog create(int width, int height, List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            return create(Gravity.BOTTOM, width, height, itemList, callback);
        }

        public <T> CustomListDialog create(int layoutGravity, int width, int height, List<T> itemList,
                                           @NonNull IOnViewBindCallback<T> callback) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomListDialog dialog = new CustomListDialog(context, themeResId);
            View layout = null;
            if (DialogUtils.heightTooSmall(context)) {
                layout = inflater.inflate(R.layout.base_dialog_custom_list_scroll, null);
            } else {
                layout = inflater.inflate(R.layout.base_dialog_custom_list, null);
            }
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            if (layoutGravity == Gravity.BOTTOM) {
                layout.findViewById(R.id.container_ll).setBackgroundResource(R.drawable.base_shape_round_top_white);
            } else {
                layout.findViewById(R.id.container_ll).setBackgroundResource(R.drawable.base_shape_round_white);
            }
            window.setGravity(layoutGravity);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = width; //宽度设置为屏幕
            p.height = height;
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            RelativeLayout top_rl = layout.findViewById(R.id.top_rl);
            RecyclerView recycle_view = layout.findViewById(R.id.recycle_view);
            RelativeLayout bottom_rl = layout.findViewById(R.id.bottom_rl);
            if (topLayout == null) {
                top_rl.setVisibility(View.GONE);
            } else {
                top_rl.addView(topLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                top_rl.setVisibility(View.VISIBLE);
            }
            if (bottomLayout == null) {
                bottom_rl.setVisibility(View.GONE);
            } else {
                bottom_rl.addView(bottomLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                bottom_rl.setVisibility(View.VISIBLE);
            }
            DialogListAdapter dialogListAdapter = new DialogListAdapter(itemLayoutId, dialog, callback);
            dialogListAdapter.enableEmptyComplete(false, false);
            dialog.mDialogListAdapter = dialogListAdapter;
            if (callback != null) {
                callback.onViewBind(topLayout, bottomLayout, dialog);
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
        private int itemLayoutId;

        public DialogListAdapter(int itemLayoutId, CustomListDialog dialog, IOnViewBindCallback callback) {
            this.dialog = dialog;
            this.itemLayoutId = itemLayoutId;
            this.callback = callback;
        }

        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(this, LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false));
        }

        @Override
        protected void onDataSet() {
            super.onDataSet();
            if (callback != null) {
                callback.onListDataChange(dialog);
            }
        }

        @Override
        protected void onDataAdd() {
            super.onDataAdd();
            if (callback != null) {
                callback.onListDataChange(dialog);
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
        void onViewBind(View topLayout, View bottomLayout, CustomListDialog dialog);

        void onItemViewUpdate(View itemView, int position, T data, CustomListDialog dialog);

        void onListDataChange(CustomListDialog dialog);
    }
}
