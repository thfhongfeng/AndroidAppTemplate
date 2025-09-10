package com.pine.app.lib.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.pine.app.lib.input.candidate.CandidateAssemble;
import com.pine.app.lib.input.candidate.CandidateBean;
import com.pine.app.lib.input.candidate.CandidateView;
import com.pine.app.lib.input.databinding.InputChineseInterpunctionBinding;
import com.pine.app.lib.input.databinding.InputChineseKeyboardBinding;
import com.pine.app.lib.input.databinding.InputQwertyInterpunctionBinding;
import com.pine.app.lib.input.databinding.InputQwertyKeyboardBinding;
import com.pine.app.lib.input.pinyin.PinyinEngine;
import com.pine.app.lib.input.pinyin.PinyinEntity;
import com.pine.tool.util.LogUtils;

public class FlexibleKeyboard extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private static final int MODE_ENGLISH = 0;
    private static final int MODE_CHINESE = 1;

    private static final int MODE_ENGLISH_INTERPUNCTION = 2;
    private static final int MODE_CHINESE_INTERPUNCTION = 3;
    private int currentMode = MODE_CHINESE;

    private boolean shiftStyle;
    private FrameLayout keyboardContainer;
    private CandidateView candidateView;
    private EditText activeEditText;
    private boolean editTextSingleLine;
    private PinyinEngine pinyinEngine;
    private PinyinEntity currentPinyin = new PinyinEntity();

    public FlexibleKeyboard(Context context) {
        super(context);
        init(context);
    }

    public FlexibleKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        View layout = LayoutInflater.from(context).inflate(R.layout.input_keyboard_view, this, true);
        keyboardContainer = layout.findViewById(R.id.keyboard_container);
        candidateView = layout.findViewById(R.id.candidate_view);

        candidateView.setKeyboard(this);

        pinyinEngine = PinyinEngine.getInstance();
        pinyinEngine.loadDictionary(getContext());
        loadKeyboardLayout();
    }

    private View mKeyboardView1, mKeyboardView2, mKeyboardView3, mKeyboardView4;
    private InputQwertyKeyboardBinding mBinding1;
    private InputChineseKeyboardBinding mBinding2;

    private InputQwertyInterpunctionBinding mBinding3;

    private InputChineseInterpunctionBinding mBinding4;

    @SuppressLint("ResourceType")
    private void loadKeyboardLayout() {
        LogUtils.d(TAG, "loadKeyboardLayout");
        keyboardContainer.removeAllViews();
        shiftStyle = false;

        mKeyboardView1 = LayoutInflater.from(getContext()).inflate(R.layout.input_qwerty_keyboard, null, false);
        mBinding1 = DataBindingUtil.bind(mKeyboardView1);
        mBinding1.setKeyboard(FlexibleKeyboard.this);
        keyboardContainer.addView(mKeyboardView1);

        mKeyboardView2 = LayoutInflater.from(getContext()).inflate(R.layout.input_chinese_keyboard, null, false);
        mBinding2 = DataBindingUtil.bind(mKeyboardView2);
        mBinding2.setKeyboard(FlexibleKeyboard.this);
        keyboardContainer.addView(mKeyboardView2);

        mKeyboardView3 = LayoutInflater.from(getContext()).inflate(R.layout.input_qwerty_interpunction, null, false);
        mBinding3 = DataBindingUtil.bind(mKeyboardView3);
        mBinding3.setKeyboard(FlexibleKeyboard.this);
        keyboardContainer.addView(mKeyboardView3);

        mKeyboardView4 = LayoutInflater.from(getContext()).inflate(R.layout.input_chinese_interpunction, null, false);
        mBinding4 = DataBindingUtil.bind(mKeyboardView4);
        mBinding4.setKeyboard(FlexibleKeyboard.this);
        keyboardContainer.addView(mKeyboardView4);

        resetKeyboardLayout();
    }

    private void resetKeyboardLayout() {
        LogUtils.d(TAG, "resetKeyboardLayout currentMode:" + currentMode);
        switch (currentMode) {
            case MODE_ENGLISH:
                mBinding1.setShiftStyle(shiftStyle);
                mKeyboardView1.setVisibility(VISIBLE);
                mKeyboardView2.setVisibility(GONE);
                mKeyboardView3.setVisibility(GONE);
                mKeyboardView4.setVisibility(GONE);
                break;
            case MODE_CHINESE:
                mBinding2.setShiftStyle(shiftStyle);
                mKeyboardView1.setVisibility(GONE);
                mKeyboardView2.setVisibility(VISIBLE);
                mKeyboardView3.setVisibility(GONE);
                mKeyboardView4.setVisibility(GONE);
                break;
            case MODE_ENGLISH_INTERPUNCTION:
                mKeyboardView1.setVisibility(GONE);
                mKeyboardView2.setVisibility(GONE);
                mKeyboardView3.setVisibility(VISIBLE);
                mKeyboardView4.setVisibility(GONE);
                break;
            case MODE_CHINESE_INTERPUNCTION:
                mKeyboardView1.setVisibility(GONE);
                mKeyboardView2.setVisibility(GONE);
                mKeyboardView3.setVisibility(GONE);
                mKeyboardView4.setVisibility(VISIBLE);
                break;
        }
        updateFuncKeyBtn();
    }

    private TextView getEnterView() {
        switch (currentMode) {
            case MODE_ENGLISH:
                return mBinding1.enterBtn;
            case MODE_CHINESE:
                return mBinding2.enterBtn;
            case MODE_ENGLISH_INTERPUNCTION:
                return mBinding3.enterBtn;
            case MODE_CHINESE_INTERPUNCTION:
                return mBinding4.enterBtn;
        }
        return null;
    }

    public void handleKeyPress(View keyButton) {
        String text = ((KeyButton) keyButton).getText().toString();
        int code = ((KeyButton) keyButton).getInputCode();
        if (code == Integer.MAX_VALUE && !TextUtils.isEmpty(text) && text.length() == 1) {
            if (currentMode == MODE_CHINESE) {
                int textCode = text.charAt(0);
                if (textCode >= 65 && textCode <= 90 || textCode >= 65 && textCode <= 122) {
                    // 字母
                    handleChineseInput(text);
                } else {
                    commitText(currentPinyin.getText() + text);
                }
                updateFuncKeyBtn();
            } else {
                commitText(text);
            }
        } else {
            if (code == KeyCodeConstants.KEYCODE_SWITCH) {
                toggleLanguageMode();
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_INTERPUNCTION) {
                switch (currentMode) {
                    case MODE_ENGLISH:
                        toMode(MODE_ENGLISH_INTERPUNCTION);
                        break;
                    case MODE_CHINESE:
                        toMode(MODE_CHINESE_INTERPUNCTION);
                        break;
                }
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_GO_BACK) {
                switch (currentMode) {
                    case MODE_ENGLISH_INTERPUNCTION:
                        toMode(MODE_ENGLISH);
                        break;
                    case MODE_CHINESE_INTERPUNCTION:
                        toMode(MODE_CHINESE);
                        break;
                }
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_DELETE) {
                handleBackspace();
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_SHIFT) {
                keyButton.setSelected(!keyButton.isSelected());
                switch (currentMode) {
                    case MODE_ENGLISH:
                        mBinding1.setShiftStyle(keyButton.isSelected());
                        break;
                    case MODE_CHINESE:
                        mBinding2.setShiftStyle(keyButton.isSelected());
                        break;
                }
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_SPACE) {
                if (currentMode == MODE_CHINESE) {
                    if (currentPinyin.isComplete()) {
                        commitText(" ");
                    } else {
                        commitText(currentPinyin.getText());
                    }
                } else {
                    commitText(" ");
                }
                return;
            }
            if (code == KeyCodeConstants.KEYCODE_ENTER) {
                if (currentMode == MODE_CHINESE) {
                    if (keyButton.isSelected()) {
                        commitText(currentPinyin.getText());
                        updateFuncKeyBtn();
                    } else {
                        commitEnter();
                    }
                } else {
                    commitEnter();
                }
            }
        }
    }

    private void commitEnter() {
        if (editTextSingleLine) {
            commitText("");
        } else {
            if (activeEditText.getLineCount() <= 50) {
                commitText("\n");
            }
        }
    }

    private void toggleLanguageMode() {
        int mode = (currentMode == MODE_ENGLISH) ? MODE_CHINESE : MODE_ENGLISH;
        toMode(mode);
    }

    private void toMode(int mode) {
        currentMode = mode;
        currentPinyin.clear();
        candidateView.clearCandidatesView();
        resetKeyboardLayout();
    }

    private void handleChineseInput(@NonNull String text) {
        if (currentPinyin.length() > 60) {
            return;
        }
        String charStr = text.toLowerCase();
        currentPinyin.append(charStr);
        updatePinyinCandidates();
    }

    public void onCandidateSelect(String pinyin, @NonNull CandidateBean selectBean) {
        if (TextUtils.isEmpty(pinyin) || selectBean == null) {
            return;
        }
        currentPinyin.onCandidateSelect(pinyin, selectBean);
        if (currentPinyin.isComplete()) {
            commitText(currentPinyin.getText());
        } else {
            updatePinyinCandidates();
        }
        updateFuncKeyBtn();
    }

    private void updateFuncKeyBtn() {
        switch (currentMode) {
            case MODE_ENGLISH:
                mBinding1.shiftBtn.setSelected(shiftStyle);
                mBinding1.langBtn.setSelected(true);
                break;
            case MODE_CHINESE:
                mBinding2.shiftBtn.setSelected(shiftStyle);
                mBinding2.langBtn.setSelected(false);
                break;
            case MODE_ENGLISH_INTERPUNCTION:
                break;
            case MODE_CHINESE_INTERPUNCTION:
                break;
        }
        TextView enterView = getEnterView();
        if (enterView != null) {
            if (currentPinyin.isComplete()) {
                enterView.setText(R.string.base_newline);
                enterView.setSelected(false);
            } else {
                enterView.setText(R.string.base_confirm);
                enterView.setSelected(true);
            }
        }
    }

    private void updatePinyinCandidates() {
        CandidateAssemble candidates = pinyinEngine.getCandidates(currentPinyin);
        candidateView.setCandidates(currentPinyin, candidates);
    }

    private void handleBackspace() {
        LogUtils.d(TAG, "handleBackspace");
        if (currentMode == MODE_CHINESE && currentPinyin.length() > 0) {
            currentPinyin.backspace();
            updatePinyinCandidates();
        } else {
            performBackspace();
        }
        updateFuncKeyBtn();
    }

    private void performBackspace() {
        LogUtils.d(TAG, "performBackspace");
        if (activeEditText != null) {
            int start = activeEditText.getSelectionStart();
            if (start > 0) {
                activeEditText.getText().delete(start - 1, start);
                activeEditText.setSelection(start - 1);
                activeEditText.requestFocus();
            }
        }
    }

    public void commitText(String text) {
        LogUtils.d(TAG, "commitText text:" + text);
        currentPinyin.clear();
        candidateView.clearCandidatesView();
        if (activeEditText != null && !TextUtils.isEmpty(text)) {
            int start = activeEditText.getSelectionStart();
            Editable editable = activeEditText.getEditableText();
            editable.insert(start, text);
            activeEditText.setSelection(start + text.length());
        }
        activeEditText.requestFocus();
    }

    public void attachToEditText(EditText editText) {
        activeEditText = editText;
        // 拦截系统输入法请求（关键！）
        editText.setShowSoftInputOnFocus(false);
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 确保视图已测量完成
                if (editText.getWidth() <= 0) return false;
                // 1. 计算点击位置对应的字符偏移量
                int offset = editText.getOffsetForPosition(event.getX(), event.getY());
                // 2. 强制设置光标位置（关键步骤）
                editText.setSelection(offset);
                // 3. 显式请求焦点（绕过系统状态判断）
                editText.requestFocus();
                // 4. 立即刷新视图（解决延迟问题）
                editText.post(() -> editText.invalidate());
            }
            return false; // 返回false允许EditText继续处理事件
        });
        LogUtils.d(TAG, "attachToEditText editText:" + editText);
    }

    public void setSingleLine(boolean singleLine) {
        LogUtils.d(TAG, "setSingleLine singleLine:" + singleLine);
        editTextSingleLine = singleLine;
    }

    public void reset(boolean resetMode) {
        if (resetMode) {
            toMode(MODE_ENGLISH);
        } else {
            currentPinyin.clear();
            candidateView.clearCandidatesView();
            resetKeyboardLayout();
        }
    }
}