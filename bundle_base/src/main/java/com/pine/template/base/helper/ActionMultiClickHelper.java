package com.pine.template.base.helper;

import android.app.Dialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.bundle_base.R;
import com.pine.tool.ui.Activity;

import java.util.HashMap;
import java.util.List;

public class ActionMultiClickHelper {
    private final String TAG = this.getClass().getSimpleName();

    private Activity mActivity;

    private InputTextDialog mPwdDialog;
    private HashMap<Integer, ActionMultiClickBean> mActionClickBeanMap = new HashMap<>();
    private ActionMultiClickBean mPwdDialogAttachBean;

    public ActionMultiClickHelper(Activity activity) {
        mActivity = activity;
        int inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        inputType = inputType | InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        mPwdDialog = DialogUtils.createTextInputDialog(mActivity,
                "", "", 100, inputType,
                new InputTextDialog.IActionClickListener() {
                    @Override
                    public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                        if (!AdminUtils.checkAdminPwd(textList.get(0))) {
                            Toast.makeText(mActivity, R.string.pwd_not_correct_msg, Toast.LENGTH_SHORT).show();
                        } else {
                            performAction(mPwdDialogAttachBean);
                        }
                        return false;
                    }

                    @Override
                    public boolean onCancelClick(Dialog dialog) {
                        return false;
                    }
                });
//        mPwdDialog.getInputEt().setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mPwdDialog.enablePwdMode();
        mPwdDialog.setCanceledOnTouchOutside(false);
        mPwdDialog.enableAutoDismiss(false, 30 * 1000);
    }

    public boolean isPwdDialogShow() {
        return mPwdDialog.isShowing();
    }

    public void regCustomMultiClick(int actionDescResId, int accessType,
                                    @NonNull View view, int intervalMs, final int clickCount,
                                    @NonNull ActionMultiClickBean.IOnMultiClickListener listener) {
        regMultiClick(ActionMultiClickBean.ACTION_CUSTOM, actionDescResId, accessType,
                view, intervalMs, clickCount, listener);
    }

    public void regMultiClick(int actionType, int actionDescId,
                              @NonNull View view, int intervalMs, final int clickCount) {
        regMultiClick(actionType, actionDescId, ActionMultiClickBean.ACCESS_BY_PWD,
                view, intervalMs, clickCount, null);
    }

    public void regMultiClick(int actionType, int actionDescId, int accessType,
                              @NonNull View view, int intervalMs, final int clickCount,
                              ActionMultiClickBean.IOnMultiClickListener listener) {
        ActionMultiClickBean bean = new ActionMultiClickBean(view.getId(), actionType, accessType);
        bean.setActionDescId(actionDescId);
        bean.setIntervalMs(intervalMs);
        bean.setClickCount(clickCount);
        bean.setAccessType(accessType);
        bean.setListener(listener);
        view.setOnClickListener(new View.OnClickListener() {
            private int curClickCount;
            private long lastClickTime;

            @Override
            public void onClick(View v) {
                if (lastClickTime == 0 || System.currentTimeMillis() - lastClickTime < intervalMs) {
                    lastClickTime = System.currentTimeMillis();
                    curClickCount++;
                    if (curClickCount >= clickCount) {
                        preformMultiClick(bean);
                        curClickCount = 0;
                        lastClickTime = 0;
                    }
                } else {
                    curClickCount = 0;
                    lastClickTime = 0;
                }
            }
        });
    }

    public void unRegMultiClick(@NonNull View view) {
        mActionClickBeanMap.remove(view.getId());
        view.setOnClickListener(null);
    }

    private void preformMultiClick(ActionMultiClickBean bean) {
        if (bean == null) {
            return;
        }
        ActionMultiClickBean.IOnMultiClickListener listener = bean.getListener();
        boolean consume = false;
        if (listener != null) {
            consume = listener.onAccessCheck(bean);
        }
        if (!consume) {
            switch (bean.getAccessType()) {
                case ActionMultiClickBean.ACCESS_BY_PWD:
                    preformMultiClickByPwd(bean);
                    break;
                case ActionMultiClickBean.ACCESS_BY_CUSTOM:
                    break;
                default:
                    performAction(bean);
                    break;
            }
        }
    }

    public void preformMultiClickByPwd(ActionMultiClickBean bean) {
        if (bean == null) {
            return;
        }
        int titleResId = bean.getActionDescId();
        if (titleResId > 0) {
            mPwdDialog.setTitleText(titleResId);
        }
        mPwdDialog.setInputText("");
        mPwdDialog.show(false, true);
        mPwdDialogAttachBean = bean;
    }

    public void performAction(ActionMultiClickBean bean) {
        if (mPwdDialog.isShowing()) {
            mPwdDialog.dismiss();
        }
        if (mActivity == null || bean == null) {
            return;
        }
        ActionMultiClickBean.IOnMultiClickListener listener = bean.getListener();
        boolean consume = false;
        if (listener != null) {
            consume = listener.onMultiAction(bean);
        }
        if (!consume) {
            switch (bean.getActionType()) {
                case ActionMultiClickBean.ACTION_CUSTOM:
                    break;
                case ActionMultiClickBean.ACTION_FINISH:
                    try {
                        DeviceSdkManager.getInstance().setForegroundAppKeepLive(
                                mActivity.getPackageName(), 10 * 60);
                    } catch (DeviceSdkException e) {
                    }
                    mActivity.finish();
                    mActivity = null;
                    break;
                case ActionMultiClickBean.ACTION_GO_BACK:
                    // 创建一个返回键按下事件
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
                    // 分发事件
                    mActivity.dispatchKeyEvent(event);
                    break;
            }
        }
    }

    public void release() {
        mActionClickBeanMap.clear();
        mPwdDialogAttachBean = null;
        mActivity = null;
        if (mPwdDialog.isShowing()) {
            mPwdDialog.dismiss();
        }
    }
}
