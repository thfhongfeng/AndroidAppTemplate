package com.pine.tool.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pine.tool.access.UiAccessManager;
import com.pine.tool.permission.IPermissionCallback;
import com.pine.tool.permission.PermissionBean;
import com.pine.tool.permission.PermissionManager;
import com.pine.tool.permission.easy.AppSettingsDialog;
import com.pine.tool.permission.easy.AppSettingsDialogHolderActivity;
import com.pine.tool.permission.easy.EasyPermissions;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class Fragment extends android.support.v4.app.Fragment
        implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());
    private boolean mUiAccessReady;
    private HashMap<Integer, PermissionBean> mPermissionRequestMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        beforeInitOnCreateView(savedInstanceState);
        View layout = setContentView(inflater, container, savedInstanceState);

        findViewOnCreateView(layout);

        mUiAccessReady = true;
        if (!UiAccessManager.getInstance().checkCanAccess(this, false)) {
            mUiAccessReady = false;
        }

        tryOnAllRestrictionReleased();

        return layout;
    }

    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutResId(), container, false);
    }

    protected abstract int getFragmentLayoutResId();

    /**
     * onCreateView中前置初始化
     */
    protected void beforeInitOnCreateView(@Nullable Bundle savedInstanceState) {

    }

    /**
     * onCreateView中初始化View
     */
    protected abstract void findViewOnCreateView(View layout);

    private void tryOnAllRestrictionReleased() {
        if (mUiAccessReady) {
            if (!parseArguments()) {
                init();
                afterInit();
            }
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
    protected abstract void init();

    /**
     * onCreate中结束初始化
     */
    protected abstract void afterInit();

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

    public void requestPermission(int requestCode, IPermissionCallback callback,
                                  @Size(min = 1) @NonNull String... perms) {
        PermissionManager.requestPermission(this, requestCode, callback, perms);
    }

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

    public void showShortToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showShortToast(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showLongToast(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_LONG).show();
    }
}
