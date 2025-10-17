package com.pine.template.base.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.template.bundle_base.R;

public class WebLoadingView extends FrameLayout {
    private Context mContext;

    private View rootView;

    public WebLoadingView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WebLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mContext = context;
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.web_loading_view, this, true);
    }

    public void show(boolean hasBg) {
        if (hasBg) {
            setBackgroundColor(mContext.getColor(R.color.white));
        } else {
            setBackgroundColor(mContext.getColor(R.color.transparent));
        }
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}
