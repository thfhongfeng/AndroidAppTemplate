package com.pine.template.base.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.template.base.R;

public class CountDownTimerView extends FrameLayout {
    private int layout_id = R.layout.base_count_down;
    private int descResId;
    private int textColor;
    private int textStyle;
    private float textSize, descTextSize;

    private View rootView;
    private TextView tvCount, tvDesc;

    private Handler mMainHandler;
    private CountDownTimer mTimer;
    private int mCount;

    public CountDownTimerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CountDownTimerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        initView();
    }

    public CountDownTimerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        initView();
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseCountDownTimerView);
        layout_id = typedArray.getResourceId(R.styleable.BaseCountDownTimerView_base_layout, R.layout.base_count_down);
        descResId = typedArray.getResourceId(R.styleable.BaseCountDownTimerView_base_desc, -1);
        textColor = typedArray.getColor(R.styleable.BaseCountDownTimerView_base_textColor, Color.parseColor("#AA1E90FF"));
        textStyle = typedArray.getInt(R.styleable.BaseCountDownTimerView_base_textStyle, -1);
        textSize = typedArray.getDimension(R.styleable.BaseCountDownTimerView_base_textSize, -1);
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(layout_id, this, true);
        tvDesc = rootView.findViewById(R.id.tv_desc);
        tvCount = rootView.findViewById(R.id.tv_count);
        mMainHandler = new Handler(Looper.getMainLooper());
        if (descResId != -1) {
            setCountDesc(descResId);
        }
        setTextColor(textColor);

        if (textSize != -1) {
            setTextSize(textSize);
        }
        if (textStyle != -1) {
            setTypeface(textStyle);
        }
    }

    public void setTypeface(int textStyle) {
        this.textStyle = textStyle;
        if (tvCount == null) {
            return;
        }
        switch (textStyle) {
            case 0: // normal
                tvCount.setTypeface(Typeface.DEFAULT);
                break;
            case 1: // bold
                tvCount.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case 2: // italic
                tvCount.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                break;
            case 3: // bold_italic
                tvCount.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
                break;
        }
    }

    public void setTextSize(float size) {
        textSize = size;
        descTextSize = size * 3 / 4;
        if (tvCount != null) {
            tvCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        if (tvDesc != null) {
            tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, descTextSize);
        }
    }

    public void setTextColor(int textColor) {
        if (tvDesc != null) {
            tvDesc.setTextColor(textColor);
        }
        if (tvCount != null) {
            tvCount.setTextColor(textColor);
        }
    }

    public void setCountDesc(int resId) {
        if (resId == -1) {
            return;
        }
        setCountDesc(getContext().getString(resId));
    }

    public void setCountDesc(String desc) {
        if (tvDesc != null) {
            if (!TextUtils.isEmpty(desc)) {
                tvDesc.setVisibility(VISIBLE);
                tvDesc.setText(desc);
            } else {
                tvDesc.setVisibility(GONE);
            }
        }
    }

    public void startCountDown(int count, final ICountDownCallback callback) {
        startCountDown(count, R.id.tv_count, R.id.tv_desc, callback);
    }

    public void startCountDown(int count, int countTvId, int descTvId, final ICountDownCallback callback) {
        reset();
        tvCount = rootView.findViewById(countTvId);
        tvDesc = rootView.findViewById(descTvId);
        mCount = count;
        if (tvCount != null) {
            tvCount.setText(String.valueOf(mCount));
        }
        if (count <= 0) {
            if (callback != null) {
                callback.onFinish();
            }
            return;
        }
        mTimer = new CountDownTimer(count * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tvCount != null) {
                    tvCount.setText(String.valueOf(mCount));
                }
                if (mCount > 0) {
                    mCount--;
                }
            }

            @Override
            public void onFinish() {
                if (tvCount != null) {
                    tvCount.setText("");
                }
                if (callback != null) {
                    callback.onFinish();
                }
            }
        }.start();
    }

    public void release() {
        reset();
    }

    private void reset() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mCount = 0;
    }

    public interface ICountDownCallback {
        void onFinish();
    }
}
