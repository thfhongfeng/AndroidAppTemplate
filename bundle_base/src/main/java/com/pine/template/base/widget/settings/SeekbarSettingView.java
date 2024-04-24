package com.pine.template.base.widget.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.bundle_base.R;

public class SeekbarSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv;
    private SeekBar seekBar;

    private String label;
    private int labelWidth, divVisibility, labelVisibility;
    private Drawable contentBg;

    public SeekbarSettingView(Context context) {
        super(context);
        initView();
    }

    public SeekbarSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public SeekbarSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarSettingsView);
        label = typedArray.getString(R.styleable.SeekBarSettingsView_base_label);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.SeekBarSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        divVisibility = typedArray.getInt(R.styleable.SeekBarSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.SeekBarSettingsView_base_label_show, View.VISIBLE);
        contentBg = typedArray.getDrawable(R.styleable.SeekBarSettingsView_base_content_bg);
        typedArray.recycle();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_seekbar_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        seekBar = rootView.findViewById(R.id.seek_bar);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        seekBar.setBackground(contentBg);

        labelTv.setText(label);

        labelTv.getLayoutParams().width = labelWidth;
        div.setVisibility(divVisibility);
        labelTv.setVisibility(labelVisibility);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mListener != null) {
                    mListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    public int getProgress() {
        return seekBar.getProgress();
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    private SeekBar.OnSeekBarChangeListener mListener;

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mListener = listener;
    }
}
