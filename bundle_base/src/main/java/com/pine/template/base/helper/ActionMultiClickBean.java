package com.pine.template.base.helper;

public class ActionMultiClickBean {
    public static final int ACTION_CUSTOM = 0;
    public static final int ACTION_FINISH = 1;
    public static final int ACTION_GO_BACK = 2;

    public static final int ACCESS_DIRECTLY = 0;
    public static final int ACCESS_BY_PWD = 1;
    public static final int ACCESS_BY_CUSTOM = 2;

    private int tag;
    private int actionType;
    private int accessType;
    private int intervalMs;
    private int clickCount;
    private int actionDescId;
    private IOnMultiClickListener listener;

    public ActionMultiClickBean(int tag, int actionType, int accessType) {
        this.tag = tag;
        this.actionType = actionType;
        this.accessType = accessType;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public int getActionDescId() {
        return actionDescId;
    }

    public void setActionDescId(int actionDescId) {
        this.actionDescId = actionDescId;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    public IOnMultiClickListener getListener() {
        return listener;
    }

    public void setListener(IOnMultiClickListener listener) {
        this.listener = listener;
    }

    public interface IOnMultiClickListener {
        void onAccessCheck(ActionMultiClickBean bean);

        void onMultiAction(ActionMultiClickBean bean);
    }

    public static abstract class OnMultiClickListener implements IOnMultiClickListener {
        @Override
        public void onAccessCheck(ActionMultiClickBean bean) {

        }
    }
}
