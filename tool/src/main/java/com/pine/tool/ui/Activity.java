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
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pine.tool.access.UiAccessManager;
import com.pine.tool.permission.IPermissionCallback;
import com.pine.tool.permission.PermissionBean;
import com.pine.tool.permission.PermissionManager;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.permission.easy.AppSettingsDialog;
import com.pine.tool.permission.easy.AppSettingsDialogHolderActivity;
import com.pine.tool.permission.easy.EasyPermissions;
import com.pine.tool.util.LogUtils;
import com.pine.tool.widget.ILifeCircleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/28
 */

/**
 * 权限检查中关于onResume和init的执行顺序说明：因为EasyPermission是实质是另起了一个界面，
 * 所以当前Activity在进行权限检查时，会先onResume，再onPause，权限检查完后会先执行init，再执行onResume。
 * 也就是说init之后必定会执行onResume。综上有两种情况：
 * 1. 不执行权限检查时，onCreate（onNewIntent）-->init-->onResume;
 * 2. 执行权限检查时，onCreate（onNewIntent）-->onResume-->onPause-->init-->onResume;
 * 这就会出现在onResume、onPause的时候有可能init未执行，
 * 具体使用的时候如果onResume、onPause中有需要在init之后才能做的操作时，有以下两种方式：
 * 1.在onResume可以使用isInit方法来判断；
 * 2.不重写onResume，而通过重写onRealResume来做onResume操作。
 * 事实上，在执行权限检查流程中，没有init时的onResume的处理是可以忽略的，因为之后还会再次onResume，
 * 所以推荐通过重写onRealResume来解决以上问题。
 */
public abstract class Activity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    public final int REQUEST_ACCESS_PERMISSION = 33333;
    protected final String TAG = LogUtils.makeLogTag(this.getClass());
    // UiAccess（比如需要登陆）是否检查通过，没有则结束当前界面；
    public boolean mUiAccessReady;
    // 权限（比如需要登陆）是否检查通过，没有则弹出授权界面给用户授权；
    public boolean mPermissionReady;
    // onAllAccessRestrictionReleased方法是否被调用过（该方法在activity的生命周期中只会调用一次，onCreate，onNewIntent才会重置）；
    // 该参数保证一次生命周期中init方法只会执行一次，并且可以用来判断init是否已经执行。
    private boolean mOnAllAccessRestrictionReleasedMethodCalled;
    private boolean mPrePause;
    private HashMap<Integer, PermissionBean> mPermissionRequestMap = new HashMap<>();
    private Map<Integer, ILifeCircleView> mLifeCircleViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnAllAccessRestrictionReleasedMethodCalled = false;
        beforeInitOnCreate(savedInstanceState);
        setContentView(savedInstanceState);

        findViewOnCreate();

        mUiAccessReady = true;
        if (!UiAccessManager.getInstance().checkCanAccess(this)) {
            mUiAccessReady = false;
            if (!onUiAccessForbidden()) {
                finish();
                return;
            }
        }

        mPermissionReady = true;
        PermissionsAnnotation annotation = getClass().getAnnotation(PermissionsAnnotation.class);
        if (annotation != null) {
            String[] permissions = annotation.Permissions();
            if (permissions != null) {
                if (!hasPermissions(permissions)) {
                    mPermissionReady = false;
                    requestPermission(REQUEST_ACCESS_PERMISSION, null, permissions);
                }
            }
        }

        tryInitOnAllRestrictionReleased();
    }

    protected void setContentView(Bundle savedInstanceState) {
        setContentView(getActivityLayoutResId());
    }

    /**
     * onCreate中获取当前Activity的内容布局资源id
     *
     * @return Activity的内容布局资源id
     */
    protected abstract int getActivityLayoutResId();

    /**
     * onCreate中前置初始化
     */
    protected void beforeInitOnCreate(@Nullable Bundle savedInstanceState) {

    }

    /**
     * onCreate中初始化View
     */
    protected abstract void findViewOnCreate();

    /**
     * 当UiAccess准入条件不具备时的回调，重写该方法定制用户自己的处理方式
     *
     * @return false:默认处理方式（finish()）执行；true:默认处理方式（finish()）不执行
     */
    protected boolean onUiAccessForbidden() {
        return false;
    }

    protected boolean isInit() {
        return mOnAllAccessRestrictionReleasedMethodCalled;
    }

    private void onAllAccessRestrictionReleased() {
        if (!parseIntentData()) {
            init();
            afterInit();
        }
    }

    /**
     * 用于分析传入参数是否非法
     *
     * @return true表示非法， false表示合法
     */
    protected abstract boolean parseIntentData();

    /**
     * 所有准入条件(如：登陆限制，权限限制等)全部解除后回调（界面的数据业务初始化动作推荐在此进行）
     */
    protected abstract void init();

    /**
     * 初始化结束
     */
    protected abstract void afterInit();

    @CallSuper
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mOnAllAccessRestrictionReleasedMethodCalled = false;
        mPrePause = false;

        mUiAccessReady = true;
        if (!UiAccessManager.getInstance().checkCanAccess(this)) {
            mUiAccessReady = false;
            if (!onUiAccessForbidden()) {
                finish();
                return;
            }
        }

        mPermissionReady = true;
        PermissionsAnnotation annotation = getClass().getAnnotation(PermissionsAnnotation.class);
        if (annotation != null) {
            String[] permissions = annotation.Permissions();
            if (permissions != null) {
                if (!hasPermissions(permissions)) {
                    mPermissionReady = false;
                    requestPermission(REQUEST_ACCESS_PERMISSION, null, permissions);
                }
            }
        }

        tryInitOnAllRestrictionReleased();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPrePause) {
            mPrePause = false;
            mUiAccessReady = true;
            if (!UiAccessManager.getInstance().checkCanAccess(this)) {
                mUiAccessReady = false;
                if (!onUiAccessForbidden()) {
                    finish();
                    return;
                }
            } else {
                tryInitOnAllRestrictionReleased();
            }
        }
        if (isInit()) {
            onRealResume();
        }
    }

    protected void onRealResume() {

    }

    @Override
    protected void onPause() {
        hideSoftInputFromWindow();
        super.onPause();
        mPrePause = true;
    }

    @Override
    protected void onDestroy() {
        if (mLifeCircleViewMap != null && mLifeCircleViewMap.size() > 0) {
            mLifeCircleViewMap.clear();
        }
        if (mPermissionRequestMap != null && mPermissionRequestMap.size() > 0) {
            mPermissionRequestMap.clear();
        }
        super.onDestroy();
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "onActivityResult requestCode:" + requestCode +
                ", resultCode:" + resultCode);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE && data != null) {
            int permRequestCode = data.getIntExtra(AppSettingsDialogHolderActivity.REQUEST_CODE_KEY, -1);
            if (permRequestCode != -1) {
                String[] permissions = data.getStringArrayExtra(AppSettingsDialogHolderActivity.REQUEST_PERMISSIONS_KEY);
                if (!EasyPermissions.hasPermissions(this, permissions)) {
                    if (permRequestCode == REQUEST_ACCESS_PERMISSION) {
                        finish();
                    } else {
                        PermissionBean bean = mPermissionRequestMap.get(permRequestCode);
                        if (bean != null && bean.getCallback() != null) {
                            List<String> denied = new ArrayList<>();
                            for (int i = 0; i < permissions.length; i++) {
                                String perm = permissions[i];
                                if (ContextCompat.checkSelfPermission(this, perm)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    denied.add(perm);
                                }
                            }
                            bean.getCallback().onPermissionsDenied(permRequestCode, denied);
                        }
                    }
                } else {
                    onAllPermissionGranted(permRequestCode);
                }
            }
        }

        if (mLifeCircleViewMap != null) {
            Iterator<Map.Entry<Integer, ILifeCircleView>> iterator = mLifeCircleViewMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ILifeCircleView> entry = iterator.next();
                ILifeCircleView view = entry.getValue();
                if (view != null) {
                    view.onActivityResult(requestCode, resultCode, data);
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
            if (requestCode == REQUEST_ACCESS_PERMISSION) {
                finish();
            } else {
                if (bean != null && bean.getCallback() != null) {
                    bean.getCallback().onPermissionsDenied(requestCode, perms);
                }
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
        if (REQUEST_ACCESS_PERMISSION == requestCode) {
            finish();
        }
    }

    @Override
    public final void onAllPermissionGranted(int requestCode) {
        LogUtils.d(TAG, "onAllPermissionGranted: requestCode(" + requestCode + ")");
        PermissionBean bean = mPermissionRequestMap.get(requestCode);
        if (bean != null && bean.getCallback() != null) {
            bean.getCallback().onAllPermissionGranted(requestCode);
        }
        if (REQUEST_ACCESS_PERMISSION == requestCode) {
            mPermissionReady = true;
            tryInitOnAllRestrictionReleased();
        }
    }

    private void tryInitOnAllRestrictionReleased() {
        if (!mOnAllAccessRestrictionReleasedMethodCalled &&
                mUiAccessReady && mPermissionReady) {
            mOnAllAccessRestrictionReleasedMethodCalled = true;
            onAllAccessRestrictionReleased();
        }
    }

    public @NonNull
    HashMap<Integer, PermissionBean> getPermissionRequestMap() {
        return mPermissionRequestMap;
    }

    public boolean hasPermissions(@Size(min = 1) @NonNull String... perms) {
        return PermissionManager.hasPermissions(this, perms);
    }

    public void requestPermission(int requestCode, IPermissionCallback callback,
                                  @Size(min = 1) @NonNull String... perms) {
        PermissionManager.requestPermission(this, requestCode, callback, perms);
    }

    public void requestPermission(PermissionBean bean) {
        PermissionManager.requestPermission(this, bean);
    }

    public void attachCircleView(ILifeCircleView view) {
        mLifeCircleViewMap.put(view.hashCode(), view);
    }

    public void hideSoftInputFromWindow() {
        //如果软键盘已弹出，收回软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showShortToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showLongToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }
}
