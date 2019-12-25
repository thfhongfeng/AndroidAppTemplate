package com.pine.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pine.base.R;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by tanghongfeng on 2018/10/24
 */

public class SelectMultiItemsDialog extends Dialog {

    protected SelectMultiItemsDialog(Context context) {
        super(context);
    }

    protected SelectMultiItemsDialog(Context context, int theme) {
        super(context, theme);
    }

    public interface IDialogSelectListener {
        void onSelect(String[] selectTextArr, int[] positionArr);
    }

    public static class Builder {
        private Context context;
        private TextView title_tv;
        private View title_divide;
        private RecyclerView recycle_view;
        private View cancel_btn_tv;
        private View confirm_btn_tv;

        public Builder(Context context) {
            this.context = context;
        }

        public SelectMultiItemsDialog create(String title, String[] itemTextList, int[] selectPosArr,
                                             IDialogSelectListener listener) {
            return create(title, itemTextList, "", selectPosArr, listener);
        }

        public SelectMultiItemsDialog create(String title, int[] itemImgList, String[] itemTextList,
                                             int[] selectPosArr, IDialogSelectListener listener) {
            return create(title, itemImgList, itemTextList, "", selectPosArr, listener);
        }

        public SelectMultiItemsDialog create(String title, String[] itemTextList, String textColor,
                                             int[] selectPosArr, final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, selectPosArr, listener);
        }

        public SelectMultiItemsDialog create(String title, int[] itemImgList, String[] itemTextList, String textColor,
                                             int[] selectPosArr, final IDialogSelectListener listener) {
            List<SelectItemBean> itemBeanList = new ArrayList<>();
            for (int i = 0; i < itemTextList.length; i++) {
                SelectItemBean itemBean = new SelectItemBean(itemImgList[i % itemImgList.length], itemTextList[i], textColor);
                itemBeanList.add(itemBean);
            }
            return create(title, itemBeanList, selectPosArr, listener);
        }

        public SelectMultiItemsDialog create(String title, final List<SelectItemBean> itemList,
                                             int[] selectPosArr, final IDialogSelectListener listener) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SelectMultiItemsDialog dialog = new SelectMultiItemsDialog(context, R.style.BaseDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_multi_item_select, null);
            dialog.setContentView(layout);
            title_tv = layout.findViewById(R.id.title_tv);
            title_divide = layout.findViewById(R.id.title_divide);
            cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            confirm_btn_tv = layout.findViewById(R.id.confirm_btn_tv);
            recycle_view = layout.findViewById(R.id.recycle_view);
            if (TextUtils.isEmpty(title)) {
                title_tv.setVisibility(View.GONE);
                title_divide.setVisibility(View.GONE);
            } else {
                title_tv.setText(title);
                title_tv.setVisibility(View.VISIBLE);
                title_divide.setVisibility(View.VISIBLE);
            }
            final DialogListAdapter dialogListAdapter = new DialogListAdapter(selectPosArr);
            dialogListAdapter.enableEmptyComplete(false, false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            recycle_view.setLayoutManager(layoutManager);
            recycle_view.setAdapter(dialogListAdapter);
            dialogListAdapter.setData(itemList);
            cancel_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirm_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        List<Integer> selectList = dialogListAdapter.getSelectedPosList();
                        if (selectList != null && selectList.size() > 0) {
                            String[] selectTextArr = new String[selectList.size()];
                            int[] positionArr = new int[selectList.size()];
                            for (int i = 0; i < selectList.size(); i++) {
                                positionArr[i] = selectList.get(i);
                                selectTextArr[i] = itemList.get(positionArr[i]).getName();
                            }
                            listener.onSelect(selectTextArr, positionArr);
                        } else {
                            listener.onSelect(null, null);
                        }
                    }
                    dialog.dismiss();
                }
            });
            return dialog;
        }
    }

    private static class DialogListAdapter extends BaseNoPaginationListAdapter<SelectItemBean> {
        private HashMap<Integer, Boolean> selectPosMap = new HashMap<>();

        public DialogListAdapter(@NonNull int[] selectPosArr) {
            if (selectPosArr != null && selectPosArr.length > 0) {
                for (int pos : selectPosArr) {
                    selectPosMap.put(pos, true);
                }
            }
        }

        public List<Integer> getSelectedPosList() {
            if (selectPosMap != null && selectPosMap.size() > 0) {
                List<Integer> list = new ArrayList<>();
                Iterator<Map.Entry<Integer, Boolean>> iterator = selectPosMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Boolean> entry = iterator.next();
                    if (entry.getValue()) {
                        list.add(entry.getKey());
                    }
                }
                return list;
            }
            return null;
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
                select_state_iv.setVisibility(selectPosMap.containsKey(position) && selectPosMap.get(position) ? View.VISIBLE : View.INVISIBLE);
                if (!TextUtils.isEmpty(content.getNameColor())) {
                    name_tv.setTextColor(Color.parseColor(content.getNameColor()));
                }
                name_tv.setText(content.getName());
                line_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean selected = selectPosMap.containsKey(position) && selectPosMap.get(position);
                        selectPosMap.put(position, !selected);
                        notifyDataSetChangedSafely();
                    }
                });
            }
        }
    }
}
