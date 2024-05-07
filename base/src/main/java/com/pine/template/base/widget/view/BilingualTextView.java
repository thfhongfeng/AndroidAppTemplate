package com.pine.template.base.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.base.R;
import com.pine.template.base.helper.ResourceHelper;

public class BilingualTextView extends LinearLayout {

    private int textStyle;
    private int textGravity;
    private int textLines;
    private int textEllipsize;
    private float textSize, secondTextSize;
    private int textColor, secondTextColor;
    private boolean enableDual;
    private int textResId;

    private TextView firstTv, secondTv;

    public static boolean ENABLE_BILINGUAL_TEXT = false;

    public static void setup(boolean defaultEnableDual) {
        ENABLE_BILINGUAL_TEXT = defaultEnableDual;
    }

    public BilingualTextView(Context context) {
        super(context);
        initView();
    }

    public BilingualTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context, attrs);
        initView();
    }

    public BilingualTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(context, attrs);
        initView();
    }

    private void parseAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseBilingualTextView);
        textStyle = typedArray.getInt(R.styleable.BaseBilingualTextView_base_textStyle, 0);
        textGravity = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "gravity", Gravity.NO_GRAVITY);
        textLines = typedArray.getInt(R.styleable.BaseBilingualTextView_base_lines, -1);
        textEllipsize = typedArray.getInt(R.styleable.BaseBilingualTextView_base_ellipsize, -1);
        textSize = typedArray.getDimension(R.styleable.BaseBilingualTextView_base_textSize, 0);
        textColor = typedArray.getColor(R.styleable.BaseBilingualTextView_base_textColor, Color.DKGRAY);
        secondTextSize = typedArray.getDimension(R.styleable.BaseBilingualTextView_base_secondTextSize, 0);
        secondTextColor = typedArray.getColor(R.styleable.BaseBilingualTextView_base_secondTextColor, textColor);
        enableDual = typedArray.getBoolean(R.styleable.BaseBilingualTextView_base_enableDual, ENABLE_BILINGUAL_TEXT);
        textResId = typedArray.getResourceId(R.styleable.BaseBilingualTextView_base_text, -1);
        typedArray.recycle();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);

        firstTv = new TextView(getContext());
        secondTv = new TextView(getContext());
        addView(firstTv, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        addView(secondTv, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        if (textResId != -1) {
            setText(textResId);
        }
        setTextColor(textColor, secondTextColor);

        textSize = textSize == 0 ? 12 : textSize;
        if (secondTextSize == 0) {
            setTextSize(textSize);
        } else {
            setTextSize(textSize, secondTextSize);
        }
        enableDual(enableDual);

        setTypeface(textStyle);
        setTextGravity(textGravity);
        setLines(textLines);
        setEllipsize(textEllipsize);
    }

    public void enableDual(boolean enable) {
        enableDual = enable;
        if (enableDual) {
            secondTv.setVisibility(VISIBLE);
        } else {
            secondTv.setVisibility(GONE);
        }
    }

    public void setLines(int lines) {
        if (lines <= 0) {
            return;
        }
        this.textLines = lines;
        firstTv.setLines(lines);
        secondTv.setLines(lines);
    }

    public void setEllipsize(int ellipsize) {
        if (ellipsize == -1) {
            return;
        }
        this.textEllipsize = ellipsize;
        firstTv.setSingleLine();
        secondTv.setSingleLine();
        switch (textEllipsize) {
            case 0: // start
                firstTv.setEllipsize(TextUtils.TruncateAt.START);
                secondTv.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 1: // end
                firstTv.setEllipsize(TextUtils.TruncateAt.END);
                secondTv.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 2: // middle
                firstTv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                secondTv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
        }
    }

    public void setTypeface(int textStyle) {
        this.textStyle = textStyle;
        switch (textStyle) {
            case 0: // normal
                firstTv.setTypeface(Typeface.DEFAULT);
                secondTv.setTypeface(Typeface.DEFAULT);
                break;
            case 1: // bold
                firstTv.setTypeface(Typeface.DEFAULT_BOLD);
                secondTv.setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case 2: // italic
                firstTv.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                secondTv.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                break;
            case 3: // bold_italic
                firstTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
                secondTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);
                break;
        }
    }

    public void setTextGravity(int gravity) {
        textGravity = gravity;
        firstTv.setGravity(gravity);
        secondTv.setGravity(gravity);
    }

    public void setTextColor(int color) {
        textColor = color;
        secondTextColor = color;
        firstTv.setTextColor(color);
        secondTv.setTextColor(color);
    }

    public void setTextColor(int color, int secondColor) {
        textColor = color;
        secondTextColor = secondColor;
        firstTv.setTextColor(color);
        secondTv.setTextColor(secondColor);
    }

    /**
     * @param size px
     */
    public void setTextSize(float size) {
        textSize = size;
        secondTextSize = size * 3 / 4;
        firstTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        secondTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondTextSize);
    }

    /**
     * @param size       px
     * @param secondSize px
     */
    public void setTextSize(float size, float secondSize) {
        textSize = size;
        secondTextSize = secondSize;
        firstTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        secondTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondTextSize);
    }

    public void setText(String text) {
        firstTv.setText(text);
        secondTv.setVisibility(GONE);
    }

    public void setText(String firstText, String secondText) {
        firstTv.setText(firstText);
        if (secondText == null) {
            secondTv.setVisibility(GONE);
        } else {
            secondTv.setText(secondText);
            secondTv.setVisibility(VISIBLE);
        }
    }

    public void setText(int resId) {
        textResId = resId;
        String firstText = ResourceHelper.getFirstString(resId);
        firstTv.setText(firstText);
        if (enableDual) {
            String secondText = ResourceHelper.getSecondString(resId);
            secondTv.setText(secondText);
            secondTv.setVisibility(VISIBLE);
        } else {
            secondTv.setVisibility(GONE);
        }
    }

    public void setText(int resId, Object... args) {
        textResId = resId;
        String firstText = ResourceHelper.getFirstString(resId, args);
        firstTv.setText(firstText);
        if (enableDual) {
            String secondText = ResourceHelper.getSecondString(resId, args);
            secondTv.setText(secondText);
            secondTv.setVisibility(VISIBLE);
        } else {
            secondTv.setVisibility(GONE);
        }
    }
}
