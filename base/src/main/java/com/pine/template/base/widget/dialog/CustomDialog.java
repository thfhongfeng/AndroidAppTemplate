package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pine.template.base.R;

public class CustomDialog extends BaseDialog {
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

        public CustomDialog create(int layoutId, int layoutGravity,
                                   boolean fillWidth, IOnViewBindCallback callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.BaseDialogStyle);
            View layout = inflater.inflate(layoutId, null);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(layoutGravity);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            if (fillWidth) {
                p.width = d.getWidth(); //宽度设置为屏幕
            } else {
                p.width = d.getWidth() * 4 / 5; //宽度设置
            }
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            if (callback != null) {
                callback.onViewBind(layout, dialog);
            }
            return dialog;
        }

        public CustomDialog create(int layoutId, int layoutGravity, float widthPct, float heightPct,
                                   IOnViewBindCallback callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(layoutId, null);
            return create(layout, layoutGravity, widthPct, heightPct, callback);
        }

        public CustomDialog create(View layoutView, int layoutGravity, float widthPct, float heightPct,
                                   IOnViewBindCallback callback) {
            final CustomDialog dialog = new CustomDialog(context, R.style.BaseDialogStyle);
            dialog.setContentView(layoutView);
            Window window = dialog.getWindow();
            window.setGravity(layoutGravity);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = (int) (d.getWidth() * widthPct); //宽度设置
            p.height = (int) (d.getHeight() * heightPct); //宽度设置
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            if (callback != null) {
                callback.onViewBind(layoutView, dialog);
            }
            return dialog;
        }

        public CustomDialog create(int layoutId, int layoutGravity, int width, int height,
                                   IOnViewBindCallback callback) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(layoutId, null);
            return create(layout, layoutGravity, width, height, callback);
        }

        public CustomDialog create(View layoutView, int layoutGravity, int width, int height,
                                   IOnViewBindCallback callback) {
            final CustomDialog dialog = new CustomDialog(context, R.style.BaseDialogStyle);
            dialog.setContentView(layoutView);
            Window window = dialog.getWindow();
            window.setGravity(layoutGravity);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = width;
            p.height = height;
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            if (callback != null) {
                callback.onViewBind(layoutView, dialog);
            }
            return dialog;
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

    public interface IOnViewBindCallback {
        void onViewBind(View rootView, CustomDialog dialog);
    }
}
