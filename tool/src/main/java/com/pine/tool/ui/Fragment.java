package com.pine.tool.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.access.UiAccessManager;
import com.pine.tool.access.UiAccessTimeInterval;
import com.pine.tool.permission.IPermissionCallback;
import com.pine.tool.permission.PermissionBean;
import com.pine.tool.permission.PermissionManager;
import com.pine.tool.permission.easy.AppSettingsDialog;
import com.pine.tool.permission.easy.AppSettingsDialogHolderActivity;
import com.pine.tool.permission.easy.EasyPermissions;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class Fragment extends androidx.fragment.app.Fragment
        implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());
    // UiAccess（比如需要登陆）是否检查通过，没有则结束当前界面；
    private boolean mUiAccessReady;
    public String[] mUiAccessTypes;
    public String[] mUiAccessArgs;
    public HashMap<String, String> mUiAccessActionsMap = new HashMap<>();
    private HashMap<Integer, PermissionBean> mPermissionRequestMap = new HashMap<>();
    // onAllAccessRestrictionReleased方法是否被调用过（该方法在fragment的生命周期中只会调用一次，onCreateView才会重置）；
    // 该参数保证一次生命周期中init方法只会执行一次，并且可以用来判断init是否已经执行。
    private boolean mOnAllAccessRestrictionReleasedMethodCalled;

    private Bundle mOnCreateSavedInstanceState;

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mOnCreateSavedInstanceState = savedInstanceState;
        mOnAllAccessRestrictionReleasedMethodCalled = false;
        beforeInitOnCreateView(savedInstanceState);
        View layout = setContentView(inflater, container, savedInstanceState);

        // 进入界面准入流程
        mUiAccessReady = true;
        UiAccessAnnotation uiAccessAnnotation = getClass().getAnnotation(UiAccessAnnotation.class);
        if (uiAccessAnnotation != null) {
            mUiAccessTypes = uiAccessAnnotation.AccessTypes();
            mUiAccessArgs = uiAccessAnnotation.AccessArgs();
            String[] actions = uiAccessAnnotation.AccessActions();
            if (actions != null && actions.length > 0) {
                for (String action : actions) {
                    mUiAccessActionsMap.put(action, action);
                }
            }
        }
        if (!UiAccessManager.getInstance().checkCanAccess(this,
                UiAccessTimeInterval.UI_ACCESS_ON_CREATE, mUiAccessTypes, mUiAccessArgs,
                mUiAccessActionsMap)) {
            mUiAccessReady = false;
            onUiAccessForbidden(UiAccessTimeInterval.UI_ACCESS_ON_CREATE);
        }

        findViewOnCreateView(layout, savedInstanceState);

        tryInitOnAllRestrictionReleased();

        return layout;
    }

    /**
     * onCreateView中前置初始化
     */
    protected void beforeInitOnCreateView(@Nullable Bundle savedInstanceState) {

    }

    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutResId(), container, false);
    }

    protected abstract int getFragmentLayoutResId();

    /**
     * onCreateView中初始化View
     */
    protected abstract void findViewOnCreateView(View layout, Bundle savedInstanceState);


    protected boolean isInit() {
        return mOnAllAccessRestrictionReleasedMethodCalled;
    }

    /**
     * 尝试进入界面初始化（先判断界面进入限制是否都已经解除）
     */
    private void tryInitOnAllRestrictionReleased() {
        if (!mOnAllAccessRestrictionReleasedMethodCalled &&
                mUiAccessReady) {
            mOnAllAccessRestrictionReleasedMethodCalled = true;
            onAllAccessRestrictionReleased();
        }
    }

    /**
     * 在界面进入限制都被解除后，进行界面初始化
     */
    private void onAllAccessRestrictionReleased() {
        if (!parseArguments()) {
            init(mOnCreateSavedInstanceState);
            afterInit();
        }
    }

    /**
     * 用于分析传入参数是否非法
     *
     * @return true表示非法， false表示合法
     */
    protected abstract boolean parseArguments();

    /**
     * 所有准入条件(如：登陆限制，权限限制等)全部解除后回调（界面的数据业务初始化动作推荐在此进行）
     */
    protected abstract void init(Bundle onCreateSavedInstanceState);

    /**
     * onCreate中结束初始化
     */
    protected abstract void afterInit();

    /**
     * 当UiAccess准入条件不具备时的回调
     *
     * @param accessTimeInterval UiAccess检查阶段
     * @return
     */
    protected void onUiAccessForbidden(UiAccessTimeInterval accessTimeInterval) {
    }

    @CallSuper
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "onActivityResult requestCode:" + requestCode +
                ", resultCode:" + resultCode);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE && data != null) {
            int permRequestCode = data.getIntExtra(AppSettingsDialogHolderActivity.REQUEST_CODE_KEY, -1);
            if (permRequestCode != -1) {
                String[] permissions = data.getStringArrayExtra(AppSettingsDialogHolderActivity.REQUEST_PERMISSIONS_KEY);
                if (!EasyPermissions.hasPermissions(getContext(), permissions)) {
                    PermissionBean bean = mPermissionRequestMap.get(permRequestCode);
                    if (bean != null && bean.getCallback() != null) {
                        List<String> denied = new ArrayList<>();
                        for (int i = 0; i < permissions.length; i++) {
                            String perm = permissions[i];
                            if (ContextCompat.checkSelfPermission(getContext(), perm)
                                    != PackageManager.PERMISSION_GRANTED) {
                                denied.add(perm);
                            }
                        }
                        bean.getCallback().onPermissionsDenied(permRequestCode, denied);
                    }
                } else {
                    onAllPermissionGranted(permRequestCode);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(TAG, "onPermissionsGranted: requestCode(" + requestCode + "),size:" + perms.size());
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (bean != null && bean.getCallback() != null) {
            bean.getCallback().onPermissionsGranted(requestCode, perms);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(TAG, "onPermissionsDenied: requestCode(" + requestCode + "),size:" + perms.size());
        String[] permArr = new String[perms.size()];
        for (int i = 0; i < perms.size(); i++) {
            permArr[i] = perms.get(i);
        }
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (!PermissionManager.showGoAppSettingsDialog(this, requestCode, bean, permArr)) {
            if (bean != null && bean.getCallback() != null) {
                bean.getCallback().onPermissionsDenied(requestCode, perms);
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        LogUtils.d(TAG, "onRationaleAccepted: requestCode(" + requestCode + ")");
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (bean != null && bean.getCallback() != null) {
            bean.getCallback().onRationaleAccepted(requestCode);
        }
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        LogUtils.d(TAG, "onRationaleDenied: requestCode(" + requestCode + ")");
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (bean != null && bean.getCallback() != null) {
            bean.getCallback().onRationaleDenied(requestCode);
        }
    }

    @Override
    public final void onAllPermissionGranted(int requestCode) {
        LogUtils.d(TAG, "onAllPermissionGranted: requestCode(" + requestCode + ")");
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (bean != null && bean.getCallback() != null) {
            bean.getCallback().onAllPermissionGranted(requestCode);
        }
    }

    public @NonNull
    HashMap<Integer, PermissionBean> getPermissionRequestMap() {
        return mPermissionRequestMap;
    }

    /**
     * 判断是否有相应的权限
     *
     * @param perms
     * @return
     */
    public boolean hasPermissions(@Size(min = 1) @NonNull String... perms) {
        return PermissionManager.hasPermissions(getContext(), perms);
    }

    /**
     * 申请对应的权限
     *
     * @param requestCode
     * @param callback
     * @param perms       {@link Manifest}
     */
    public void requestPermission(int requestCode, IPermissionCallback callback,
                                  @Size(min = 1) @NonNull String... perms) {
        PermissionManager.requestPermission(this, requestCode, callback, perms);
    }

    /**
     * 申请对应的权限
     *
     * @param bean
     */
    public void requestPermission(PermissionBean bean) {
        PermissionManager.requestPermission(this, bean);
    }

    public void hideSoftInputFromWindow() {
        //如果软键盘已弹出，收回软键盘
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private LinkedList<ToastEntity> mToastList = new LinkedList<>();
    private int mMaxWaitToast = 1;
    private int mMaxToastDuration = 10 * 1000;

    public void setupWaitToastConfig(int maxWaitToast, int maxToastDuration) {
        mMaxWaitToast = maxWaitToast;
        if (maxToastDuration > 5 * 1000 && maxToastDuration < 60 * 1000) {
            mMaxToastDuration = maxToastDuration;
        }
    }

    private void showToast(@NonNull Toast toast) {
        toast.show();
        long now = System.currentTimeMillis();
        // 清理掉残留
        while (mToastList.size() > 0 &&
                (now - mToastList.getFirst().getTimeStamp()) > mToastList.size() * mMaxToastDuration) {
            mToastList.removeFirst();
        }

        if (mToastList.size() > 0 && mToastList.size() > mMaxWaitToast) {
            ToastEntity toastOld = mToastList.removeFirst();
            if (toastOld.getToast() != null) {
                toastOld.getToast().cancel();
            }
        }
        ToastEntity toastEntity = new ToastEntity(toast, now);
        mToastList.add(toastEntity);
    }

    private synchronized void showToast(boolean immediately, boolean notAllowInterrupt, String message, int duration) {
        ToastEntity toastEntity = new ToastEntity();
        toastEntity.setContent(message);
        toastEntity.setImmediately(immediately);
        toastEntity.setNotAllowInterrupt(notAllowInterrupt);
        toastEntity.setDuration(duration == Toast.LENGTH_LONG ? 5 * 1000 : 3 * 1000);
        showToast(toastEntity);
    }

    public synchronized void showToast(@NonNull ToastEntity toastEntity) {
        if (!TextUtils.isEmpty(toastEntity.getContent())) {
            Toast toast = Toast.makeText(getContext(), toastEntity.getContent(),
                    toastEntity.getDuration() > 3 * 1000 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            showToast(toast);
        } else if (toastEntity.getResId() > 0) {
            if (toastEntity.getResFormId() != null && toastEntity.getResFormId().length > 0) {
                Object[] args = new Object[toastEntity.getResFormId().length];
                for (int i = 0; i < toastEntity.getResFormId().length; i++) {
                    Object idObj = toastEntity.getResFormId()[i];
                    args[i] = getString((int) idObj);
                }
                Toast toast = Toast.makeText(getContext(), getString(toastEntity.getResId(), args),
                        toastEntity.getDuration() > 3 * 1000 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                showToast(toast);
            } else {
                Toast toast = Toast.makeText(getContext(), toastEntity.getContent(),
                        toastEntity.getDuration() > 3 * 1000 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                showToast(toast);
            }
        }
    }

    public boolean getToastDefaultImmediately() {
        return false;
    }

    public synchronized void showShortToast(String message) {
        showShortToast(getToastDefaultImmediately(), message);
    }

    public synchronized void showShortToast(boolean immediately, String message) {
        showShortToast(immediately, false, message);
    }

    public synchronized void showShortToast(boolean immediately, boolean notAllowInterrupt, String message) {
        showToast(immediately, notAllowInterrupt, message, Toast.LENGTH_SHORT);
    }

    public synchronized void showShortToast(@StringRes int resId) {
        showShortToast(getToastDefaultImmediately(), resId);
    }

    public synchronized void showShortToast(boolean immediately, @StringRes int resId) {
        showShortToast(immediately, false, resId);
    }

    public synchronized void showShortToast(boolean immediately, boolean notAllowInterrupt, @StringRes int resId) {
        showShortToast(immediately, notAllowInterrupt, getString(resId));
    }

    public synchronized void showShortToast(@StringRes int resId, Integer... formatArgs) {
        showShortToast(getToastDefaultImmediately(), resId, formatArgs);
    }

    public synchronized void showShortToast(boolean immediately, @StringRes int resId, Integer... formatArgs) {
        showShortToast(immediately, false, resId, formatArgs);
    }

    public synchronized void showShortToast(boolean immediately, boolean notAllowInterrupt, @StringRes int resId, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        showShortToast(immediately, notAllowInterrupt, getString(resId, args));
    }

    public synchronized void showLongToast(String message) {
        showLongToast(getToastDefaultImmediately(), message);
    }

    public synchronized void showLongToast(boolean immediately, String message) {
        showLongToast(immediately, false, message);
    }

    public synchronized void showLongToast(boolean immediately, boolean notAllowInterrupt, String message) {
        showToast(immediately, notAllowInterrupt, message, Toast.LENGTH_LONG);
    }

    public synchronized void showLongToast(@StringRes int resId) {
        showLongToast(getToastDefaultImmediately(), resId);
    }

    public synchronized void showLongToast(boolean immediately, @StringRes int resId) {
        showLongToast(immediately, false, resId);
    }

    public synchronized void showLongToast(boolean immediately, boolean notAllowInterrupt, @StringRes int resId) {
        showLongToast(immediately, notAllowInterrupt, getString(resId));
    }

    public synchronized void showLongToast(@StringRes int resId, Integer... formatArgs) {
        showLongToast(getToastDefaultImmediately(), resId, formatArgs);
    }

    public synchronized void showLongToast(boolean immediately, @StringRes int resId, Integer... formatArgs) {
        showLongToast(immediately, false, resId, formatArgs);
    }

    public synchronized void showLongToast(boolean immediately, boolean notAllowInterrupt, @StringRes int resId, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        showLongToast(immediately, notAllowInterrupt, getString(resId, args));
    }
}
