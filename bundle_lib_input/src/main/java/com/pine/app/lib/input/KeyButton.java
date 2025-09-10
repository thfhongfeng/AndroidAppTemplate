package com.pine.app.lib.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;

public class KeyButton extends androidx.appcompat.widget.AppCompatTextView {
    public KeyButton(Context context) {
        super(context);
    }

    public KeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public KeyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private int code;

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputKeyButton);
        code = typedArray.getInt(R.styleable.InputKeyButton_input_code, Integer.MAX_VALUE);

        setBackgroundResource(R.drawable.input_selector_key_btn_bg);
        setTextSize(14);
        setTextColor(getResources().getColor(R.color.black));
        setGravity(Gravity.CENTER);
    }

    public int getInputCode() {
        return code;
    }
}
