package com.pine.template.base.widget.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ToastTipView extends BilingualTextView {
    private final String TAG = this.getClass().getSimpleName();

    private Handler mToastTipHandler;

    public ToastTipView(Context context) {
        super(context);
        init();
    }

    public ToastTipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToastTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mToastTipHandler = new Handler(Looper.getMainLooper());
    }

    public void show(ToastTip toastTip) {
        if (toastTip == null || toastTip.invalid()) {
            return;
        }
        if (toastTip.isImmediately()) {
            clear();
        }
        mToastQueue.offer(toastTip);
        notifyHandler();
    }

    public void clear() {
        mToastTipHandler.removeCallbacksAndMessages(null);
        mToastQueue.clear();
        setText("");
    }

    public void release() {
        clear();
    }

    private ConcurrentLinkedQueue<ToastTip> mToastQueue = new ConcurrentLinkedQueue<>();

    private void notifyHandler() {
        ToastTip toastTip = mToastQueue.poll();
        if (toastTip == null) {
            setText("");
            return;
        }
        Object[] objs = toastTip.getFormatObjs();
        if (objs != null && objs.length > 0) {
            setText(toastTip.getMsgResId(), objs);
        } else {
            setText(toastTip.getMsgResId());
        }
        mToastTipHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyHandler();
            }
        }, toastTip.getHoldTime());
    }

    public static class ToastTip {
        private int msgResId;
        private Object[] formatObjs;
        private boolean immediately;

        // 持续时间。单位：毫秒
        private long holdTime = 5000;

        public ToastTip(int msgResId) {
            this.msgResId = msgResId;
        }

        public ToastTip(int msgResId, long holdTime) {
            this.msgResId = msgResId;
            this.holdTime = holdTime;
        }

        public ToastTip(int msgResId, Object[] formatObjs) {
            this.msgResId = msgResId;
            this.formatObjs = formatObjs;
        }

        public ToastTip(int msgResId, long holdTime, Object[] formatObjs) {
            this.msgResId = msgResId;
            this.holdTime = holdTime;
            this.formatObjs = formatObjs;
        }

        public int getMsgResId() {
            return msgResId;
        }

        public void setMsgResId(int msgResId) {
            this.msgResId = msgResId;
        }

        public Object[] getFormatObjs() {
            return formatObjs;
        }

        public void setFormatObjs(Object[] formatObjs) {
            this.formatObjs = formatObjs;
        }

        public boolean isImmediately() {
            return immediately;
        }

        public void setImmediately(boolean immediately) {
            this.immediately = immediately;
        }

        public long getHoldTime() {
            return holdTime;
        }

        public void setHoldTime(long holdTime) {
            this.holdTime = holdTime;
        }

        public boolean invalid() {
            return msgResId <= 0;
        }
    }
}
