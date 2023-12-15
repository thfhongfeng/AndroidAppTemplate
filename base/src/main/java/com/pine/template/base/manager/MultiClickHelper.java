package com.pine.template.base.manager;

import android.view.View;

public class MultiClickHelper {
    private int mClickCount;
    private long mLastClickTime;

    public void regMultiClick(final View view, final int intervalMs, final int clickCount,
                              final IMultiClickListener listener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastClickTime == 0 || System.currentTimeMillis() - mLastClickTime < intervalMs) {
                    mLastClickTime = System.currentTimeMillis();
                    mClickCount++;
                    if (mClickCount >= clickCount) {
                        if (listener != null) {
                            listener.onMultiClick(view);
                        }
                        mClickCount = 0;
                        mLastClickTime = 0;
                    }
                } else {
                    mClickCount = 0;
                    mLastClickTime = 0;
                }
            }
        });
    }

    public void unRegMultiClick(final View view) {
        view.setOnClickListener(null);
    }

    public interface IMultiClickListener {
        void onMultiClick(View view);
    }
}
