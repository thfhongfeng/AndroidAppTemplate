package com.pine.template.base.widget.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.bundle_base.R;

public class SwitchSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv, descTv;

    private Switch switchView;

    private String label, desc;
    private int labelWidth, divVisibility, labelVisibility;
    private Drawable contentBg;

    public SwitchSettingView(Context context) {
        super(context);
        initView();
    }

    public SwitchSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public SwitchSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchSettingsView);
        label = typedArray.getString(R.styleable.SwitchSettingsView_base_label);
        desc = typedArray.getString(R.styleable.SwitchSettingsView_base_desc);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.SwitchSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        divVisibility = typedArray.getInt(R.styleable.SwitchSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.SwitchSettingsView_base_label_show, View.VISIBLE);
        contentBg = typedArray.getDrawable(R.styleable.SwitchSettingsView_base_content_bg);
        typedArray.recycle();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_switch_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        descTv = rootView.findViewById(R.id.tv_desc);
        switchView = rootView.findViewById(R.id.switch_view);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        switchView.setBackground(contentBg);

        labelTv.setText(label);
        if (TextUtils.isEmpty(desc)) {
            descTv.setVisibility(GONE);
        } else {
            descTv.setText(desc);
            descTv.setVisibility(VISIBLE);
        }

        labelTv.getLayoutParams().width = labelWidth;
        div.setVisibility(divVisibility);
        labelTv.setVisibility(labelVisibility);

        switchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSwitch(switchView.isChecked());
                }
            }
        });
    }

    public void setChecked(boolean check) {
        switchView.setChecked(check);
    }

    public boolean isChecked() {
        return switchView.isChecked();
    }

    private IOnSwitchListener mListener;

    public void setOnSwitchListener(IOnSwitchListener listener) {
        mListener = listener;
    }

    public interface IOnSwitchListener {
        void onSwitch(boolean checked);
    }
}
