package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pine.template.base.R;

public class CustomDialog extends BaseDialog {
    protected CustomDialog(Context context) {
        super(context);
    }

    protected CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private int themeResId = R.style.BaseDialogStyle;
        private View layout;

        private void setupLayoutView(int layoutId) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = inflater.inflate(layoutId, null);
        }

        public Builder(Context context, int layoutId) {
            this.context = context;
            setupLayoutView(layoutId);
        }

        public Builder(Context context, View layout) {
            this.context = context;
            this.layout = layout;
        }

        public Builder(Context context, int themeResId, int layoutId) {
            this.context = context;
            this.themeResId = themeResId;
            setupLayoutView(layoutId);
        }

        public Builder(Context context, int themeResId, View layout) {
            this.context = context;
            this.themeResId = themeResId;
            this.layout = layout;
        }

        public CustomDialog create(IOnViewBindCallback callback) {
            return create(1, 0.8f, callback);
        }

        public CustomDialog create(int layoutGravity, IOnViewBindCallback callback) {
            return create(layoutGravity, 1, 0.8f, callback);
        }

        public CustomDialog create(float widthPct, float heightPct,
                                   IOnViewBindCallback callback) {
            return create(Gravity.CENTER, widthPct, heightPct, callback);
        }

        public CustomDialog create(int layoutGravity, float widthPct, float heightPct,
                                   IOnViewBindCallback callback) {
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            int width = (int) (d.getWidth() * widthPct); //宽度
            int height = (int) (d.getHeight() * heightPct); //高度
            return create(layoutGravity, width, height, callback);
        }

        public CustomDialog create(int width, int height, IOnViewBindCallback callback) {
            return create(Gravity.CENTER, width, height, callback);
        }

        public CustomDialog create(int layoutGravity, int width, int height,
                                   IOnViewBindCallback callback) {
            final CustomDialog dialog = new CustomDialog(context, themeResId);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(layoutGravity);
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = width;
            p.height = height;
            dialog.getWindow().setAttributes(p); //设置生效
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
