package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pine.template.base.R;

public class CustomDialog extends Dialog {
    protected CustomDialog(Context context) {
        super(context);
    }

    protected CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDialog create(int layoutId, int layoutGravity, boolean fillWidth, IOnViewBindCallback callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.BaseDialogStyle);
            View layout = inflater.inflate(layoutId, null);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(layoutGravity);
            if (fillWidth) {
                WindowManager m = ((Activity) context).getWindowManager();
                Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
                WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
                p.width = d.getWidth(); //宽度设置为屏幕
                dialog.getWindow().setAttributes(p); //设置生效
            }
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            if (callback != null) {
                callback.onViewBind(layout, dialog);
            }
            return dialog;
        }
    }

    public interface IOnViewBindCallback {
        void onViewBind(View rootView, CustomDialog dialog);
    }
}
