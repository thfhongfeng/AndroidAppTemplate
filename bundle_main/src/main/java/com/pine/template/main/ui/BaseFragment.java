package com.pine.template.main.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.pine.template.base.manager.tts.TtsManager;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.KeyboardUtils;
import com.pine.tool.util.LogUtils;

public abstract class BaseFragment<T extends ViewDataBinding, VM extends ViewModel>
        extends UniversalVisibleFragment<T, VM> {

    public final static String TOP_HOME = "topHome";
    public final static String PARENT_HOME = "parentHome";
    public final static String BRO_HOME = "broHome";
    public final static String CUSTOM_BRO = "customBro";

    /**
     * 返回标识
     * topHome:返回到最上层的Home界面；
     * parentHome：返回父级fragment的Home;
     * broHome：返回同级fragment的Home;
     * customBro：返回同级指定fragment;
     */
    private volatile String _mGoBackTag = BRO_HOME;
    // 临时返回标识，只用于一次返回。
    // 在父类型的fragment（BaseParentFragment）中跳转（goFragment）时进行设置。
    // 返回或者隐藏后，会恢复为null，即之后使用mGoBackTag进行判断。
    private volatile String _mOnceOnlyGoBackTag;

    private Bundle mArgs = new Bundle();

    public synchronized void setArgs(@NonNull Bundle args) {
        mArgs = args;
        if (mArgs == null) {
            mArgs = new Bundle();
        }
    }

    @NonNull
    public synchronized Bundle getArgs() {
        return mArgs;
    }

    /**
     * 设置默认返回标识
     *
     * @param goBackTag
     */
    public void setGoBackTag(String goBackTag) {
        LogUtils.d(TAG, "setGoBackTag goBackTag:" + goBackTag + ", this:" + this);
        this._mGoBackTag = goBackTag;
    }

    private int _customBroGoBackFragIndex;

    /**
     * 设置默认返回标识
     *
     * @param broFragIndex
     */
    public void setGoBackTagForCustomBro(int broFragIndex) {
        LogUtils.d(TAG, "setGoBackTagForCustomBro goBackTag:" + CUSTOM_BRO
                + ", broFragIndex:" + broFragIndex + ", this:" + this);
        this._mGoBackTag = CUSTOM_BRO;
        this._customBroGoBackFragIndex = broFragIndex;
    }

    /**
     * 设置临时返回标识
     *
     * @param goBackTag
     */
    public void setOnceOnlyGoBackTag(String goBackTag) {
        LogUtils.d(TAG, "setOnceOnlyGoBackTag goBackTag:" + goBackTag + ", this:" + this);
        this._mOnceOnlyGoBackTag = goBackTag;
    }

    /**
     * 根据返回标识进行界面返回
     */
    public void goBack(Bundle args) {
        String goBackTag = TextUtils.isEmpty(_mOnceOnlyGoBackTag) ? _mGoBackTag : _mOnceOnlyGoBackTag;
        LogUtils.d(TAG, "goBack _mOnceOnlyGoBackTag:" + _mOnceOnlyGoBackTag + ", _mGoBackTag:" + _mGoBackTag
                + ", _customBroGoBackFragIndex:" + _customBroGoBackFragIndex + ", final goBackTag:" + goBackTag + ", this:" + this);
        _mOnceOnlyGoBackTag = null;
        if (!_isLastVisible) {
            return;
        }
        switch (goBackTag) {
            case TOP_HOME:
                if (getActivity() != null) {
                    ((BaseFragmentActivity) getActivity()).switchHomeFragment(args);
                }
                break;
            case PARENT_HOME:
                if (getParentFragment().getParentFragment() instanceof BaseParentFragment) {
                    BaseParentFragment fragment = (BaseParentFragment) getParentFragment().getParentFragment();
                    fragment.goFragment(fragment.getSubHomeIndex(), args);
                }
                break;
            case BRO_HOME:
                if (getParentFragment() instanceof BaseParentFragment) {
                    BaseParentFragment fragment = (BaseParentFragment) getParentFragment();
                    fragment.goFragment(fragment.getSubHomeIndex(), args);
                }
                break;
            case CUSTOM_BRO:
                if (getParentFragment() instanceof BaseParentFragment) {
                    BaseParentFragment fragment = (BaseParentFragment) getParentFragment();
                    fragment.goFragment(_customBroGoBackFragIndex, args);
                }
                break;
        }
    }

    /**
     * 返回最上层的Home界面
     */
    public void goTopHome() {
        goTopHome(null);
    }

    /**
     * 返回最上层的Home界面
     */
    public void goTopHome(Bundle args) {
        setOnceOnlyGoBackTag(TOP_HOME);
        goBack(args);
    }

    /**
     * 返回当前层级的Home界面
     */
    public void goBroHome() {
        goBroHome(null);
    }

    /**
     * 返回当前层级的Home界面
     */
    public void goBroHome(Bundle args) {
        setOnceOnlyGoBackTag(BRO_HOME);
        goBack(args);
    }

    /**
     * 返回指定的兄弟fragment
     *
     * @param index
     */
    public void goBroFragment(int index) {
        if (getParentFragment() instanceof BaseParentFragment) {
            ((BaseParentFragment) getParentFragment()).goFragment(index);
        }
    }

    /**
     * 返回指定的兄弟fragment
     *
     * @param index
     * @param args
     */
    public void goBroFragment(int index, Bundle args) {
        if (getParentFragment() instanceof BaseParentFragment) {
            ((BaseParentFragment) getParentFragment()).goFragment(index, args);
        }
    }

    /**
     * 返回指定的父级fragment
     *
     * @param index
     */
    public void goParentFragment(int index) {
        if (getParentFragment().getParentFragment() instanceof BaseParentFragment) {
            ((BaseParentFragment) (getParentFragment().getParentFragment())).goFragment(index);
        }
    }

    /**
     * 返回指定的父级fragment
     *
     * @param index
     * @param args
     */
    public void goParentFragment(int index, Bundle args) {
        if (getParentFragment().getParentFragment() instanceof BaseParentFragment) {
            ((BaseParentFragment) (getParentFragment().getParentFragment())).goFragment(index, args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "fragment_life onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        LogUtils.d(TAG, "fragment_life onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtils.d(TAG, "fragment_life onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.d(TAG, "fragment_life onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.d(TAG, "fragment_life onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LogUtils.d(TAG, "fragment_life onDestroyView");
        super.onDestroyView();
        _mGoBackTag = BRO_HOME;
        _mOnceOnlyGoBackTag = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtils.d(TAG, "onHiddenChanged hidden:" + hidden);
        if (hidden) {
            _mOnceOnlyGoBackTag = null;
            KeyboardUtils.closeSoftKeyboard(getActivity());
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    @CallSuper
    public void onFragmentVisible(boolean first) {
        super.onFragmentVisible(first);
        LogUtils.d(TAG, "onFragmentVisible first:" + first + ", fragment:" + this);
        Fragment fragment = getParentFragment();
        if (fragment != null && (fragment instanceof BaseParentFragment)) {
            BaseParentFragment parentFragment = ((BaseParentFragment) fragment);
            parentFragment.onSubFragmentVisible(parentFragment.getSubShowFragmentIndex());
        }
    }

    @Override
    @CallSuper
    public void onFragmentHide() {
        super.onFragmentHide();
        LogUtils.d(TAG, "onFragmentHide fragment:" + this);
    }

    public void reloadUi() {
        Fragment fragment = getParentFragment();
        LogUtils.d(TAG, "reload parent fragment:" + fragment);
        if (fragment == null || !(fragment instanceof BaseParentFragment)) {
            getBaseFragmentActivity().reloadUi(getArguments());
        } else {
            ((BaseParentFragment) fragment).reloadUi();
        }
    }

    /**
     * fragment的业务事件
     *
     * @param event
     * @param bundle
     */
    public boolean onBizEvent(String event, Bundle bundle) {
        return false;
    }

    public void setLoadingUiVisibility(boolean visibility) {
        getBaseFragmentActivity().setLoadingUiVisibility(visibility, false);
    }

    public void setLoadingUiVisibility(boolean visibility, boolean enableClickGone) {
        getBaseFragmentActivity().setLoadingUiVisibility(visibility, enableClickGone);
    }

    public void tts(String msg, boolean immediately) {
        TtsManager.getInstance().play(msg, immediately);
    }

    public void tts(@StringRes int resId, boolean immediately) {
        TtsManager.getInstance().play(resId, immediately);
    }

    public void tts(@StringRes int resId, boolean immediately, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        tts(resId, immediately, args);
    }

    public void tts(@StringRes int resId, boolean immediately, Object... formatArgs) {
        TtsManager.getInstance().play(immediately, resId, formatArgs);
    }

    public BaseFragmentActivity getBaseFragmentActivity() {
        return (BaseFragmentActivity) getActivity();
    }
}
