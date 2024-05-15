package com.pine.template.base.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.base.util.DialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/10/24
 */

public class SelectItemDialog extends BaseDialog {
    private Builder mBuilder;

    protected SelectItemDialog(Context context) {
        super(context);
    }

    protected SelectItemDialog(Context context, int theme) {
        super(context, theme);
    }

    public void disableCancel() {
        mBuilder.disableCancel();
    }

    public void setSelectPos(int pos) {
        mBuilder.setSelectPos(pos);
    }

    public static abstract class DialogSelectListener implements IDialogSelectListener {
        public abstract void onSelect(String selectText, int position);

        public void onCancel() {

        }
    }

    public interface IDialogSelectListener {
        void onSelect(String selectText, int position);

        void onCancel();
    }

    public static class Builder {
        private Context context;
        private TextView title_tv;
        private RecyclerView recycle_view;
        private DialogListAdapter dialogListAdapter;
        private View cancel_btn_tv;

        public Builder(Context context) {
            this.context = context;
        }

        public SelectItemDialog create(String title, String[] itemTextList, IDialogSelectListener listener) {
            return create(title, itemTextList, "", listener);
        }

        public SelectItemDialog create(String title, String[] itemTextList, int curPosition,
                                       IDialogSelectListener listener) {
            return create(title, itemTextList, "", curPosition, listener);
        }

        public SelectItemDialog create(String title, int[] itemImgList, String[] itemTextList,
                                       IDialogSelectListener listener) {
            return create(title, itemImgList, itemTextList, "", listener);
        }

        public SelectItemDialog create(String title, int[] itemImgList, String[] itemTextList, int curPosition,
                                       IDialogSelectListener listener) {
            return create(title, itemImgList, itemTextList, "", curPosition, listener);
        }

        public SelectItemDialog create(String title, String[] itemTextList, String textColor,
                                       final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, -1, false, listener);
        }

        public SelectItemDialog create(String title, String[] itemTextList, String textColor,
                                       int curPosition, final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, curPosition, true, listener);
        }

        public SelectItemDialog create(String title, int[] itemImgList, String[] itemTextList, String textColor,
                                       final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemImgList[i % itemImgList.length], itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, -1, false, listener);
        }

        public SelectItemDialog create(String title, int[] itemImgList, String[] itemTextList, String textColor,
                                       int curPosition, final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemImgList[i % itemImgList.length], itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, curPosition, true, listener);
        }

        public SelectItemDialog create(String title, List<SelectItemBean> itemList, int curPosition,
                                       boolean showSelectState, final IDialogSelectListener listener) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SelectItemDialog dialog = new SelectItemDialog(context, R.style.BaseDialogStyle);
            View layout = null;
            if (DialogUtils.outOfScreen(context)) {
                layout = inflater.inflate(R.layout.base_dialog_item_select_scroll, null);
            } else {
                layout = inflater.inflate(R.layout.base_dialog_item_select, null);
            }
            dialog.setContentView(layout);
            title_tv = layout.findViewById(R.id.title_tv);
            cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            recycle_view = layout.findViewById(R.id.recycle_view);
            cancel_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (listener != null) {
                        listener.onCancel();
                    }
                }
            });
            if (TextUtils.isEmpty(title)) {
                title_tv.setVisibility(View.GONE);
            } else {
                title_tv.setText(title);
                title_tv.setVisibility(View.VISIBLE);
            }
            dialogListAdapter = new DialogListAdapter(curPosition, showSelectState,
                    new IDialogSelectListener() {
                        @Override
                        public void onSelect(String selectText, int position) {
                            dialog.dismiss();
                            if (listener != null) {
                                listener.onSelect(selectText, position);
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
            dialogListAdapter.enableEmptyComplete(false, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recycle_view.setLayoutManager(layoutManager);
            recycle_view.setAdapter(dialogListAdapter);
            dialogListAdapter.setData(itemList);
            dialog.mBuilder = this;
            return dialog;
        }

        public void setSelectPos(int pos) {
            dialogListAdapter.setCurSelectPos(pos);
        }

        public void disableCancel() {
            cancel_btn_tv.setVisibility(View.GONE);
        }
    }

    private static class DialogListAdapter extends BaseNoPaginationListAdapter<SelectItemBean> {
        private IDialogSelectListener listener;
        private int curPosition = -1;
        private boolean showSelectState;

        public void setCurSelectPos(int pos) {
            curPosition = pos;
        }

        public DialogListAdapter(int curPosition, boolean showSelectState, IDialogSelectListener listener) {
            this.curPosition = curPosition;
            this.showSelectState = showSelectState;
            this.listener = listener;
        }

        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_item_select, parent, false));
        }

        public class ViewHolder extends BaseListViewHolder<SelectItemBean> {
            private Context context;
            private LinearLayout line_ll;
            private View divider_view;
            private TextView name_tv;
            private ImageView select_state_iv;

            public ViewHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;
                line_ll = itemView.findViewById(R.id.line_ll);
                divider_view = itemView.findViewById(R.id.divider_view);
                name_tv = itemView.findViewById(R.id.name_tv);
                select_state_iv = itemView.findViewById(R.id.select_state_iv);
            }

            @Override
            public void updateData(final SelectItemBean content, BaseListAdapterItemProperty propertyEntity, final int position) {
                if (content.getImgResId() > 0) {
                    name_tv.setGravity(Gravity.LEFT);
                    Drawable drawable = context.getDrawable(content.getImgResId());
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    name_tv.setCompoundDrawables(drawable, null, null, null);
                } else {
                    name_tv.setCompoundDrawables(null, null, null, null);
                    name_tv.setGravity(Gravity.CENTER);
                }
                divider_view.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                select_state_iv.setVisibility(showSelectState && curPosition == position ? View.VISIBLE : View.INVISIBLE);
                if (!TextUtils.isEmpty(content.getNameColor())) {
                    name_tv.setTextColor(Color.parseColor(content.getNameColor()));
                }
                name_tv.setText(content.getName());
                line_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        curPosition = position;
                        listener.onSelect(content.getName(), curPosition);
                        if (showSelectState) {
                            notifyDataSetChangedSafely();
                        }
                    }
                });
            }
        }
    }

    public void show(boolean fullScreenMode) {
        if (fullScreenMode) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            show();
        }
    }
}
