package com.pine.template.base.widget.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.SelectItemDialog;
import com.pine.template.bundle_base.R;
import com.pine.tool.util.ArrayUtils;

public class SelectSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv, contentTv, descTv;

    private String label, desc, content, dialogTitle;
    private int labelWidth, contentWidth, divVisibility, labelVisibility;
    private Drawable contentBg;
    private boolean dialogFullScreen;

    private SelectItemDialog mDialog;

    public SelectSettingView(Context context) {
        super(context);
        initView();
    }

    public SelectSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public SelectSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectSettingsView);
        label = typedArray.getString(R.styleable.SelectSettingsView_base_label);
        content = typedArray.getString(R.styleable.SelectSettingsView_base_content);
        desc = typedArray.getString(R.styleable.SelectSettingsView_base_desc);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.SelectSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        contentWidth = typedArray.getDimensionPixelOffset(R.styleable.SelectSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_100));
        divVisibility = typedArray.getInt(R.styleable.SelectSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.SelectSettingsView_base_label_show, View.VISIBLE);
        dialogTitle = typedArray.getString(R.styleable.DateSettingsView_base_dialog_title);
        dialogFullScreen = typedArray.getBoolean(R.styleable.SelectSettingsView_base_dialog_fullscreen, false);
        contentBg = typedArray.getDrawable(R.styleable.SelectSettingsView_base_content_bg);
        typedArray.recycle();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_select_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        contentTv = rootView.findViewById(R.id.tv_content);
        descTv = rootView.findViewById(R.id.tv_desc);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        contentTv.setBackground(contentBg);

        labelTv.setText(label);
        contentTv.setText(content);
        if (TextUtils.isEmpty(desc)) {
            descTv.setVisibility(GONE);
        } else {
            descTv.setText(desc);
            descTv.setVisibility(VISIBLE);
        }

        labelTv.getLayoutParams().width = labelWidth;
        contentTv.getLayoutParams().width = contentWidth;

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
                showSelectDialog();
            }
        });
    }

    private void showSelectDialog() {
        if (mDialog == null) {
            mDialog = DialogUtils.createItemSelectDialog(getContext(),
                    dialogTitle, mDescArr, mSelectPos,
                    new SelectItemDialog.DialogSelectListener() {
                        @Override
                        public void onSelect(String selectText, int position) {
                            if (mListener != null) {
                                mListener.onSelect(selectText, position);
                            }
                            content = selectText;
                            mSelectPos = position;
                            contentTv.setText(selectText);
                        }
                    });
        }
        if (mDialog != null) {
            mDialog.setSelectPos(mSelectPos);
        }
        mDialog.show(dialogFullScreen);
    }

    private String[] mDescArr;
    private int mSelectPos;

    public void setup(String[] descArr) {
        mDescArr = descArr;
        if (!TextUtils.isEmpty(content)) {
            int index = ArrayUtils.searchFirst(mDescArr, content);
            if (index > -1) {
                mSelectPos = index;
            }
        }
        setSelect(mSelectPos);
    }

    public void setup(String[] descArr, int selectPos) {
        mDescArr = descArr;
        mSelectPos = selectPos;
        setSelect(mSelectPos);
    }

    public void setText(String text) {
        content = text;
        contentTv.setText(content);
        if (!TextUtils.isEmpty(content)) {
            int index = ArrayUtils.searchFirst(mDescArr, content);
            if (index > -1) {
                mSelectPos = index;
            }
        }
        setSelect(mSelectPos);
    }

    public String getText() {
        return content;
    }

    private void setSelect(int pos) {
        if (mDescArr == null) {
            mSelectPos = pos;
            return;
        }
        if (mDescArr != null && pos < 0 && pos >= mDescArr.length) {
            mSelectPos = pos;
            content = mDescArr[mSelectPos];
        }
        contentTv.setText(content);
        if (mDialog != null) {
            mDialog.setSelectPos(mSelectPos);
        }
    }

    private IOnSelectListener mListener;

    public void setOnSelectListener(IOnSelectListener listener) {
        mListener = listener;
    }

    public interface IOnSelectListener {
        void onSelect(String selectText, int position);
    }
}
