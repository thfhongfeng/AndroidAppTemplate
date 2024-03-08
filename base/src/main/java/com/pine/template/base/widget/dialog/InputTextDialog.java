package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pine.template.base.R;
import com.pine.template.base.widget.view.BilingualTextView;
import com.pine.template.base.widget.view.ThousandthNumberEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tanghongfeng on 2018/2/12.
 */

public class InputTextDialog extends BaseDialog {
    private Builder mBuilder;

    protected InputTextDialog(Context context) {
        super(context);
    }

    protected InputTextDialog(Context context, int theme) {
        super(context, theme);
    }

    protected InputTextDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void enablePwdMode() {
        getInputEt().setTransformationMethod(PasswordTransformationMethod.getInstance());
        getInputEndIv().setSelected(false);
        getInputEndIv().setVisibility(View.VISIBLE);
        getInputEndIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                getInputEt().setTransformationMethod(v.isSelected()
                        ? HideReturnsTransformationMethod.getInstance()
                        : PasswordTransformationMethod.getInstance());
                Editable text = getInputEt().getText();
                getInputEt().setSelection(text == null ? 0 : text.length());
            }
        });
    }

    public EditText getInputEt() {
        return mBuilder.getInputEditText();
    }

    public ImageView getInputEndIv() {
        return mBuilder.getInputEndIv();
    }

    public BilingualTextView getTitleTv() {
        return mBuilder.getTitleText();
    }

    public void setTitleText(String title) {
        mBuilder.getTitleText().setText(title);
    }

    public void setTitleText(int titleResId) {
        mBuilder.getTitleText().setText(titleResId);
    }

    public void setInputText(String text) {
        mBuilder.getInputEditText().setText(text);
        mBuilder.getInputEditText().setSelection(text.length());
    }

    public String getInputText() {
        return mBuilder.getInputEditText().getText() != null
                ? mBuilder.getInputEditText().getText().toString() : "";
    }

    public void show(boolean showKeyBoard, boolean fullScreenMode) {
        super.show(fullScreenMode);
        if (showKeyBoard) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (isShowing()) {
                                showKeyboard(mBuilder.getInputEditText());
                            }
                        }
                    });
                }
            }, 500);
        }
    }

    @Override
    public void dismiss() {
        // 必须在dismiss之前隐藏，dismiss之后edittext.getWindowToken()会为null，从而有可能导致隐藏失败
        hideKeyboard(mBuilder.getInputEditText());
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

    private TextWatcher mInputEtForAutoDismissTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            resetLastUserOperateTime(System.currentTimeMillis());
        }
    };

    @Override
    public void enableAutoDismiss(boolean ignoreUserOperate, long autoDismissIdleTime) {
        super.enableAutoDismiss(ignoreUserOperate, autoDismissIdleTime);
        getInputEt().removeTextChangedListener(mInputEtForAutoDismissTw);
        getInputEt().addTextChangedListener(mInputEtForAutoDismissTw);
    }

    public static abstract class ActionClickListener implements IActionClickListener {
        @Override
        public boolean onCancelClick(Dialog dialog) {
            return false;
        }
    }

    public interface IActionClickListener {
        boolean onSubmitClick(Dialog dialog, List<String> textList);

        boolean onCancelClick(Dialog dialog);
    }

    public static class Builder {
        private Context context;
        private IActionClickListener actionClickListener;
        private BilingualTextView title_tv;
        private EditText input_et;
        private ImageView input_end_iv;

        public Builder(Context context) {
            this.context = context;
        }

        public IActionClickListener getActionClickListener() {
            return actionClickListener;
        }

        public void setActionClickListener(IActionClickListener actionClickListener) {
            this.actionClickListener = actionClickListener;
        }

        public Dialog create(String title) {
            return this.create(title, null, -1);
        }

        public Dialog create(String title, String originalText, final int inputMaxLength) {
            return this.create(title, originalText, inputMaxLength, -1);
        }

        public InputTextDialog create(int title, String originalText, final int inputMaxLength,
                                      int inputType, boolean bilingual) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final InputTextDialog dialog = new InputTextDialog(context, R.style.BaseInputTextDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_text_input, null);
            title_tv = layout.findViewById(R.id.title_tv);
            input_et = layout.findViewById(R.id.input_et);
            input_end_iv = layout.findViewById(R.id.input_end_iv);
            input_end_iv.setVisibility(View.GONE);
            BilingualTextView cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            BilingualTextView clear_btn_tv = layout.findViewById(R.id.clear_btn_tv);
            BilingualTextView submit_btn_tv = layout.findViewById(R.id.submit_btn_tv);
            title_tv.enableDual(bilingual);
            cancel_btn_tv.enableDual(bilingual);
            clear_btn_tv.enableDual(bilingual);
            submit_btn_tv.enableDual(bilingual);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            listenViewForKeyboard(dialog, layout);
            cancel_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    if (actionClickListener == null || !actionClickListener.onCancelClick(dialog)) {
                        dialog.dismiss();
                    }
                }
            });
            clear_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_et.setText("");
                }
            });
            submit_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(input_et.getText().toString());
                    list.add(input_et.getText().toString());
                    if (actionClickListener == null || !actionClickListener.onSubmitClick(dialog, list)) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContentView(layout);
            if (inputType != -1) {
                input_et.setInputType(inputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            } else {
                input_et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
            title_tv.setText(title);
            if (inputMaxLength > 0) {
                input_et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Editable editable = input_et.getText();
                        int len = editable.length();
                        if (len > inputMaxLength) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = str.substring(0, inputMaxLength);
                            input_et.setText(newStr);
                            editable = input_et.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过字符串长度
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable, selEndIndex);
                            Toast.makeText(context, context.getString(R.string.base_text_max_length, inputMaxLength), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            if (!TextUtils.isEmpty(originalText)) {
                input_et.setText(originalText);
                input_et.setSelection(originalText.length());
            }
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = d.getWidth() * 4 / 5; //宽度设置
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.mBuilder = this;
            return dialog;
        }

        /**
         * 创建对应输入的类别的dialog
         *
         * @param title
         * @param originalText
         * @param inputMaxLength
         * @param inputType      {@link EditorInfo#inputType}
         * @return
         */
        public InputTextDialog create(String title, String originalText, final int inputMaxLength, int inputType) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final InputTextDialog dialog = new InputTextDialog(context, R.style.BaseInputTextDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_text_input, null);
            title_tv = layout.findViewById(R.id.title_tv);
            input_et = layout.findViewById(R.id.input_et);
            input_end_iv = layout.findViewById(R.id.input_end_iv);
            input_end_iv.setVisibility(View.GONE);
            BilingualTextView cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            BilingualTextView clear_btn_tv = layout.findViewById(R.id.clear_btn_tv);
            BilingualTextView submit_btn_tv = layout.findViewById(R.id.submit_btn_tv);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            listenViewForKeyboard(dialog, layout);
            cancel_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    if (actionClickListener == null || !actionClickListener.onCancelClick(dialog)) {
                        dialog.dismiss();
                    }
                }
            });
            clear_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_et.setText("");
                }
            });
            submit_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(input_et.getText().toString());
                    list.add(input_et.getText().toString());
                    if (actionClickListener == null || !actionClickListener.onSubmitClick(dialog, list)) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContentView(layout);
            if (inputType != -1) {
                input_et.setInputType(inputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            } else {
                input_et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
            title_tv.setText(title);
            if (inputMaxLength > 0) {
                input_et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Editable editable = input_et.getText();
                        int len = editable.length();
                        if (len > inputMaxLength) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            //截取新字符串
                            String newStr = str.substring(0, inputMaxLength);
                            input_et.setText(newStr);
                            editable = input_et.getText();
                            //新字符串的长度
                            int newLen = editable.length();
                            //旧光标位置超过字符串长度
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            //设置新光标所在的位置
                            Selection.setSelection(editable, selEndIndex);
                            Toast.makeText(context, context.getString(R.string.base_text_max_length, inputMaxLength), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            if (!TextUtils.isEmpty(originalText)) {
                input_et.setText(originalText);
                input_et.setSelection(originalText.length());
            }
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = d.getWidth() * 4 / 5; //宽度设置
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.mBuilder = this;
            return dialog;
        }

        /**
         * 创建数字输入dialog
         */
        public InputTextDialog thousandthNumberInputCreate(String title, String originalText,
                                                           final int inputMaxLength, boolean allowDecimal,
                                                           int decimalNum) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final InputTextDialog dialog = new InputTextDialog(context, R.style.BaseInputTextDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_thounsandth_number_text_input, null);
            BilingualTextView title_tv = layout.findViewById(R.id.title_tv);
            input_et = layout.findViewById(R.id.input_et);
            BilingualTextView cancel_btn_tv = layout.findViewById(R.id.cancel_btn_tv);
            BilingualTextView clear_btn_tv = layout.findViewById(R.id.clear_btn_tv);
            BilingualTextView submit_btn_tv = layout.findViewById(R.id.submit_btn_tv);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            listenViewForKeyboard(dialog, layout);
            cancel_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    if (actionClickListener == null || !actionClickListener.onCancelClick(dialog)) {
                        dialog.dismiss();
                    }
                }
            });
            clear_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_et.setText("");
                }
            });
            submit_btn_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hideKeyboard(input_et);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(input_et.getText().toString());
                    list.add(((ThousandthNumberEditText) input_et).getOriginalText());
                    if (actionClickListener == null || !actionClickListener.onSubmitClick(dialog, list)) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.setContentView(layout);
            ((ThousandthNumberEditText) input_et).setDecimalAllow(allowDecimal);
            ((ThousandthNumberEditText) input_et).setDecimalNum(decimalNum);
            title_tv.setText(title);
            if (inputMaxLength > 0) {
                ((ThousandthNumberEditText) input_et).setNumberMaxLength(inputMaxLength, new ThousandthNumberEditText.MaxLengthOverflowListener() {
                    @Override
                    public void onLengthOverflow() {
                        Toast.makeText(context, context.getString(R.string.base_number_max_length, inputMaxLength), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (!TextUtils.isEmpty(originalText)) {
                input_et.setText(originalText);
                input_et.setSelection(originalText.length());
            }
            dialog.mBuilder = this;
            return dialog;
        }

        private int usableHeightPrevious;

        private void listenViewForKeyboard(Dialog dialog, final View layout) {
            if (!(context instanceof Activity)) {
                return;
            }
            final View contentView = ((FrameLayout) ((Activity) context).findViewById(android.R.id.content)).getChildAt(0);
            final LinearLayout dialogContainer = layout.findViewById(R.id.container);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dialogContainer.getLayoutParams();
            final int dcMarginBottom = layoutParams.bottomMargin;
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                //一､软键盘弹起会使GlobalLayout发生变化
                public void onGlobalLayout() {
                    if (!dialogContainer.isAttachedToWindow()) {
                        return;
                    }
                    //二､当前Activity布局发生变化时，对布局进行重绘
                    //1､获取当前Activity界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
                    Rect contentRect = new Rect();
                    contentView.getWindowVisibleDisplayFrame(contentRect);
                    // Activity全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
                    int usableHeightNow = contentRect.bottom - contentRect.top;
                    //2､如果当前可用高度和原始值不一样
                    if (usableHeightNow != usableHeightPrevious) {
                        //3､获取Activity布局在当前界面显示的高度
                        int usableHeightSansKeyboard = contentView.getRootView().getHeight();
                        //4､Activity布局的高度-当前可用高度
                        int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                        //5､高度差大于屏幕1/4时，说明键盘弹出（高度差为软键盘高度）
                        if (heightDifference > (usableHeightSansKeyboard / 4)) {
                            Rect dcRect = new Rect();
                            dialogContainer.getWindowVisibleDisplayFrame(dcRect);
                            // 6､键盘弹出了，dialog底部margin高度应当加上其底部与可用高度的差值
                            layoutParams.bottomMargin = dcMarginBottom + dcRect.bottom - usableHeightNow + 10;
                        } else {
                            layoutParams.bottomMargin = dcMarginBottom;
                        }
                        dialogContainer.setLayoutParams(layoutParams);
                        //7､ 重绘布局
                        layout.requestLayout();
                        usableHeightPrevious = usableHeightNow;
                    }
                }
            });
        }

        public BilingualTextView getTitleText() {
            return title_tv;
        }

        public EditText getInputEditText() {
            return input_et;
        }

        public ImageView getInputEndIv() {
            return input_end_iv;
        }
    }
}
