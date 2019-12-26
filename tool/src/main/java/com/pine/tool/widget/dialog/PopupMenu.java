package com.pine.tool.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class PopupMenu extends PopupWindow {

    public PopupMenu(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public PopupMenu create(@NonNull View view, @NonNull View anchorView) {
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = view.getMeasuredWidth();
            PopupMenu popupMenu = new PopupMenu(context);
            popupMenu.setContentView(view);
            popupMenu.setOutsideTouchable(true);
            popupMenu.setWidth(width > 0 ? width : ViewGroup.LayoutParams.WRAP_CONTENT);
            popupMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupMenu.setBackgroundDrawable(view.getBackground());
            return popupMenu;
        }

        public PopupMenu create(@LayoutRes int layoutId, @NonNull View anchorView) {
            View view = LayoutInflater.from(context).inflate(layoutId, null);
            return create(view, anchorView);
        }
    }
}
