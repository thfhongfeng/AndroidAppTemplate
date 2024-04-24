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
import com.pine.template.base.widget.dialog.DateSelectDialog;
import com.pine.template.bundle_base.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateSettingView extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView, div;
    private TextView labelTv, startTv, middleTv, endTv;

    private String label, startDate, endDate, dialogTitle;
    private int labelWidth, divVisibility, labelVisibility;
    private Drawable contentBg;
    private boolean dialogFullScreen, rangDateTime;

    private DateSelectDialog mStartDialog, mEndDialog;

    public DateSettingView(Context context) {
        super(context);
        initView();
    }

    public DateSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public DateSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mStartDialog != null && mStartDialog.isShowing()) {
            mStartDialog.dismiss();
        }
        if (mEndDialog != null && mEndDialog.isShowing()) {
            mEndDialog.dismiss();
        }
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DateSettingsView);
        label = typedArray.getString(R.styleable.DateSettingsView_base_label);
        labelWidth = typedArray.getDimensionPixelOffset(R.styleable.DateSettingsView_base_label_width,
                context.getResources().getDimensionPixelOffset(R.dimen.dp_120));
        divVisibility = typedArray.getInt(R.styleable.DateSettingsView_base_div_show, View.VISIBLE);
        labelVisibility = typedArray.getInt(R.styleable.DateSettingsView_base_label_show, View.VISIBLE);
        dialogTitle = typedArray.getString(R.styleable.DateSettingsView_base_dialog_title);
        dialogFullScreen = typedArray.getBoolean(R.styleable.DateSettingsView_base_dialog_fullscreen, false);
        rangDateTime = typedArray.getBoolean(R.styleable.DateSettingsView_base_rang_datetime, false);
        contentBg = typedArray.getDrawable(R.styleable.DateSettingsView_base_content_bg);
        typedArray.recycle();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_datetime_settings, this, true);
        labelTv = rootView.findViewById(R.id.tv_label);
        startTv = rootView.findViewById(R.id.tv_start);
        middleTv = rootView.findViewById(R.id.tv_middle);
        endTv = rootView.findViewById(R.id.tv_end);
        div = rootView.findViewById(R.id.div);

        setOrientation(VERTICAL);

        startTv.setBackground(contentBg);
        endTv.setBackground(contentBg);

        labelTv.setText(label);

        labelTv.getLayoutParams().width = labelWidth;

        div.setVisibility(divVisibility);
        labelTv.setVisibility(labelVisibility);

        if (TextUtils.isEmpty(dialogTitle) && !TextUtils.isEmpty(label)) {
            dialogTitle = label;
            if (label.endsWith(":") || label.endsWith("：")) {
                dialogTitle = label.substring(0, label.length() - 1);
            }
        }

        startTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDialog();
            }
        });
        setEndTimeView();
    }

    private void setEndTimeView() {
        if (rangDateTime) {
            middleTv.setVisibility(VISIBLE);
            endTv.setVisibility(VISIBLE);
            endTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEndDialog();
                }
            });
        } else {
            middleTv.setVisibility(GONE);
            endTv.setVisibility(GONE);
        }
    }

    private String getDateTimeStr(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        String formatStr = "yyyy-MM-dd";
        String text = new SimpleDateFormat(formatStr).format(calendar.getTime());
        return text;
    }

    private void showStartDialog() {
        if (mStartDialog == null) {
            mStartDialog = DialogUtils.createDateSelectDialog(getContext(),
                    startYear, endYear, new DateSelectDialog.IDialogDateSelected() {
                        @Override
                        public void onSelected(Calendar calendar) {
                            startTv.setText(getDateTimeStr(calendar));
                            if (mStartListener != null) {
                                mStartListener.onSelect(calendar);
                            }
                        }
                    });
        }
        mStartDialog.show(dialogFullScreen);
    }

    private void showEndDialog() {
        if (mEndDialog == null) {
            mEndDialog = DialogUtils.createDateSelectDialog(getContext(),
                    startYear, endYear, new DateSelectDialog.IDialogDateSelected() {
                        @Override
                        public void onSelected(Calendar calendar) {
                            endTv.setText(getDateTimeStr(calendar));
                            if (mEndListener != null) {
                                mEndListener.onSelect(calendar);
                            }
                        }
                    });
        }
        mEndDialog.show(dialogFullScreen);
    }

    private int startYear = 2000;
    private int endYear = 2050;

    public void setup(boolean rangDateTime) {
        setup(startYear, endYear, rangDateTime);
    }

    public void setup(int startYear, int endYear) {
        setup(startYear, endYear, false);
    }

    public void setup(int startYear, int endYear, boolean rangDateTime) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.rangDateTime = rangDateTime;
        setEndTimeView();
    }

    public void setText(String date) {
        setStartText(date);
    }

    public void setStartText(String date) {
        startDate = date;
        startTv.setText(startDate);
    }

    public void setEndText(String date) {
        endDate = date;
        endTv.setText(endDate);
    }

    private IOnDateSelectListener mStartListener, mEndListener;

    public void setDateListener(IOnDateSelectListener listener) {
        mStartListener = listener;
    }

    public void setRangDateListener(IOnDateSelectListener startListener, IOnDateSelectListener endListener) {
        mStartListener = startListener;
        mEndListener = endListener;
    }

    public interface IOnDateSelectListener {
        void onSelect(Calendar calendar);
    }
}
