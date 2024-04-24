package com.pine.template.base.widget.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.bundle_base.R;

import java.util.List;

public class InputSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv, contentTv;
    private Switch switchView;

    private String label, hit, content, dialogTitle;
    private int labelWidth, divVisibility, labelVisibility, inputType, inputMaxLength;
    private Drawable contentBg;
    private boolean inputCanEmpty, dialogFullScreen, enablePwdMode, hasSwitch;

    private InputTextDialog mDialog;

    public InputSettingView(Context context) {
        super(context);
        initView();
    }

    public InputSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public InputSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputSettingsView);
        label = typedArray.getString(R.styleable.InputSettingsView_base_label);
        hit = typedArray.getString(R.styleable.InputSettingsView_base_hint);
        content = typedArray.getString(R.styleable.InputSettingsView_base_content);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.InputSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        divVisibility = typedArray.getInt(R.styleable.InputSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.InputSettingsView_base_label_show, View.VISIBLE);
        dialogTitle = typedArray.getString(R.styleable.InputSettingsView_base_content);
        inputCanEmpty = typedArray.getBoolean(R.styleable.InputSettingsView_base_input_can_empty, false);
        inputType = typedArray.getInt(R.styleable.InputSettingsView_android_inputType, EditorInfo.TYPE_NULL);
        inputMaxLength = typedArray.getInt(R.styleable.InputSettingsView_base_max_length, 1000);
        dialogFullScreen = typedArray.getBoolean(R.styleable.InputSettingsView_base_dialog_fullscreen, false);
        enablePwdMode = typedArray.getBoolean(R.styleable.InputSettingsView_base_pwd_mode, false);
        hasSwitch = typedArray.getBoolean(R.styleable.InputSettingsView_base_has_switch, false);
        contentBg = typedArray.getDrawable(R.styleable.InputSettingsView_base_content_bg);
        typedArray.recycle();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_input_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        contentTv = rootView.findViewById(R.id.tv_content);
        switchView = rootView.findViewById(R.id.switch_view);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        contentTv.setBackground(contentBg);

        labelTv.setText(label);
        contentTv.setText(hit);
        contentTv.setText(content);
        if (inputType != EditorInfo.TYPE_NULL) {
            contentTv.setInputType(inputType);
        }

        labelTv.getLayoutParams().width = labelWidth;

        div.setVisibility(divVisibility);
        labelTv.setVisibility(labelVisibility);

        if (TextUtils.isEmpty(dialogTitle) && !TextUtils.isEmpty(label)) {
            dialogTitle = label;
            if (label.endsWith(":") || label.endsWith("：")) {
                dialogTitle = label.substring(0, label.length() - 1);
            }
        }

        contentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        if (hasSwitch) {
            switchView.setVisibility(VISIBLE);
            switchView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwitchListener != null) {
                        mSwitchListener.onSwitch(switchView.isChecked());
                    }
                }
            });
        } else {
            switchView.setVisibility(GONE);
        }
    }

    private void showInputDialog() {
        if (mDialog == null) {
            mDialog = DialogUtils.createTextInputDialog(getContext(),
                    dialogTitle, content, inputMaxLength, inputType,
                    new InputTextDialog.ActionClickListener() {

                        @Override
                        public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                            String value = textList.get(0);
                            if (inputCanEmpty) {
                                if (mInputListener != null) {
                                    mInputListener.onInput(value);
                                }
                            } else {
                                if (!TextUtils.isEmpty(value)) {
                                    if (mInputListener != null) {
                                        mInputListener.onInput(value);
                                    }
                                } else {
                                    Toast.makeText(getContext(), getContext().getString(
                                            R.string.base_empty_or_format_err_msg, dialogTitle),
                                            Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            }
                            setText(value);
                            return false;
                        }
                    });
        }
        if (enablePwdMode) {
            mDialog.enablePwdMode();
        }
        mDialog.show(dialogFullScreen);
    }

    public void enablePwdMode() {
        enablePwdMode = true;
    }

    public void setText(String content) {
        this.content = content;
        contentTv.setText(content);
    }

    public String getText() {
        return content;
    }

    private IOnInputListener mInputListener;

    public void setOnInputListener(IOnInputListener listener) {
        mInputListener = listener;
    }

    public interface IOnInputListener {
        void onInput(String input);
    }

    public void setChecked(boolean check) {
        switchView.setChecked(check);
    }

    public boolean isChecked() {
        return switchView.isChecked();
    }

    private SwitchSettingView.IOnSwitchListener mSwitchListener;

    public void setOnSwitchListener(SwitchSettingView.IOnSwitchListener listener) {
        mSwitchListener = listener;
    }

    public interface IOnSwitchListener {
        void onSwitch(boolean checked);
    }
}
