package com.pine.template.base.widget.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.bundle_base.R;

public class TextSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv, contentTv;

    private String label, content;
    private int labelWidth, divVisibility, labelVisibility;
    private Drawable contentBg;

    public TextSettingView(Context context) {
        super(context);
        initView();
    }

    public TextSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public TextSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextSettingsView);
        label = typedArray.getString(R.styleable.TextSettingsView_base_label);
        content = typedArray.getString(R.styleable.TextSettingsView_base_content);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.TextSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        divVisibility = typedArray.getInt(R.styleable.TextSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.TextSettingsView_base_label_show, View.VISIBLE);
        contentBg = typedArray.getDrawable(R.styleable.TextSettingsView_base_content_bg);
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_input_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        contentTv = rootView.findViewById(R.id.tv_content);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        contentTv.setBackground(contentBg);

        labelTv.setText(label);
        contentTv.setText(content);

        labelTv.getLayoutParams().width = labelWidth;
        div.setVisibility(divVisibility);
        labelTv.setVisibility(labelVisibility);
    }

    public void setText(String content) {
        this.content = content;
        contentTv.setText(content);
    }

    public String getText() {
        return content;
    }
}
