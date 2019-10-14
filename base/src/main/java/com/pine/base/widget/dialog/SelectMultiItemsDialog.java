package com.pine.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pine.base.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        private ListView list_view;
        private View cancel_btn_tv;
        private View confirm_btn_tv;

        public Builder(Context context) {
            this.context = context;
        }

        public SelectMultiItemsDialog create(String title, String[] itemTextList, int[] selectPosArr,
                                             IDialogSelectListener listener) {
            return this.create(title, itemTextList, null, selectPosArr, listener);
        }

        public SelectMultiItemsDialog create(String title, final String[] itemTextList, int[] textColors,
                                             int[] selectPosArr, final IDialogSelectListener listener) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SelectMultiItemsDialog dialog = new SelectMultiItemsDialog(context, R.style.BaseSelectItemDialog);
            View layout = inflater.inflate(R.layout.base_dialog_multi_item_select, null);
            dialog.setContentView(layout);
            title_tv = layout.findViewById(R.id.title_tv);
            title_divide = layout.findViewById(R.id.title_divide);
            cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            confirm_btn_tv = layout.findViewById(R.id.confirm_btn_tv);
            list_view = layout.findViewById(R.id.list_view);
            if (TextUtils.isEmpty(title)) {
                title_tv.setVisibility(View.GONE);
                title_divide.setVisibility(View.GONE);
            } else {
                title_tv.setText(title);
                title_tv.setVisibility(View.VISIBLE);
                title_divide.setVisibility(View.VISIBLE);
            }
            final DialogListAdapter dialogListAdapter = new DialogListAdapter(context, itemTextList,
                    textColors, selectPosArr);
            list_view.setAdapter(dialogListAdapter);

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
                                selectTextArr[i] = itemTextList[positionArr[i]];
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

        public void scrollTo(int position) {
            list_view.smoothScrollToPosition(position);
        }
    }

    private static class DialogListAdapter extends BaseAdapter {
        private String[] data;
        private int[] colors;
        private Context context;
        private LayoutInflater mInflater = null;
        private HashMap<Integer, Boolean> map = new HashMap<>();

        public DialogListAdapter(Context context, String[] data, int[] selectPosArr) {
            this(context, data, null, selectPosArr);
        }

        public DialogListAdapter(Context context, String[] data, int[] textColors,
                                 int[] selectPosArr) {
            this.context = context;
            this.data = data;
            this.colors = textColors;
            mInflater = LayoutInflater.from(context);
            if (selectPosArr != null && selectPosArr.length > 0) {
                for (int pos : selectPosArr) {
                    map.put(pos, true);
                }
            }
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.base_item_multi_select, null);
                holder = new ViewHolder();
                holder.item_tv = convertView.findViewById(R.id.item_tv);
                holder.item_check_iv = convertView.findViewById(R.id.item_check_iv);
                holder.line_rl = convertView.findViewById(R.id.line_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.item_tv.setText(data[position]);
            if (colors != null) {
                holder.item_tv.setTextColor(colors[position % colors.length]);
            }
            if (map.containsKey(position) && map.get(position)) {
                holder.item_check_iv.setVisibility(View.VISIBLE);
            } else {
                holder.item_check_iv.setVisibility(View.INVISIBLE);
            }
            holder.line_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean selected = map.containsKey(position) && map.get(position);
                    view.findViewById(R.id.item_check_iv).setVisibility(selected ? View.INVISIBLE : View.VISIBLE);
                    map.put(position, !selected);
                }
            });
            return convertView;
        }

        public List<Integer> getSelectedPosList() {
            if (map != null && map.size() > 0) {
                List<Integer> list = new ArrayList<>();
                Iterator<Map.Entry<Integer, Boolean>> iterator = map.entrySet().iterator();
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

        public class ViewHolder {
            public TextView item_tv;
            public ImageView item_check_iv;
            public LinearLayout line_rl;
        }
    }
}
