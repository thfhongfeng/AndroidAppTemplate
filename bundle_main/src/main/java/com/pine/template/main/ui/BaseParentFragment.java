package com.pine.template.main.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class BaseParentFragment<T extends ViewDataBinding, VM extends ViewModel> extends BaseFragment<T, VM> {
    // 当前显示的子fragment的index
    protected volatile int _mShowFragment = -1;
    // 子fragment列表
    private BaseFragment[] _mFragmentArr = null;
    //默认是否replace方式添加fragment
    private volatile boolean _defaultReplaceMode;

    protected void setDefaultFragmentAddMode(boolean replace) {
        _defaultReplaceMode = replace;
    }

    public void showHome() {
        goFragment(getSubHomeIndex());
    }

    public void showHome(boolean replace) {
        goFragment(getSubHomeIndex(), replace);
    }

    public BaseFragment goFragment(int index) {
        return goFragment(index, _defaultReplaceMode);
    }

    public BaseFragment goFragment(int index, Bundle args) {
        return goFragment(index, null, _defaultReplaceMode, args);
    }

    public BaseFragment goFragment(int index, boolean replace) {
        return goFragment(index, null, replace);
    }

    public BaseFragment goFragment(int index, boolean replace, Bundle args) {
        return goFragment(index, null, replace, args);
    }

    public BaseFragment goFragment(int index, String onceOnlyGoBackTag) {
        return goFragment(index, onceOnlyGoBackTag, _defaultReplaceMode);
    }

    public BaseFragment goFragment(int index, String onceOnlyGoBackTag, Bundle args) {
        return goFragment(index, onceOnlyGoBackTag, _defaultReplaceMode, args);
    }

    public BaseFragment goFragment(int index, String onceOnlyGoBackTag, boolean replace) {
        return goFragment(index, onceOnlyGoBackTag, replace, null);
    }

    private Handler mGoFragmentHandler = new Handler(Looper.getMainLooper());

    /**
     * 显示指定index的子fragment
     *
     * @param index             要显示的子fragment的index
     * @param onceOnlyGoBackTag 之后从子fragment返回时（调用子fragment（BaseFragment）的goBack方法），
     *                          用于判断返回的层级的临时标识
     *                          （默认为mGoBackTag指定的标识，但可以通过该参数指定临时的返回标识，
     *                          子fragment返回或者隐藏后，会恢复为mGoBackTag指定的标识）
     *                          参考BaseFragment的goBack方法
     *                          topHome:返回到最上层的Home界面；
     *                          parentHome：返回父级fragment的Home;
     *                          broHome：返回同级fragment的Home;
     * @param replace           是replace方式还是add方式添加fragment
     * @param args              携带的数据
     * @return
     */
    public BaseFragment goFragment(int index, String onceOnlyGoBackTag, boolean replace, Bundle args) {
        LogUtils.d(TAG, "goFragment " + index + ", show:" + _mShowFragment
                + ", onceOnlyGoBackTag:" + onceOnlyGoBackTag + ", replace:" + replace
                + ", _defaultReplaceMode:" + _defaultReplaceMode + ", bundle:" + args + ", this:" + this);
        if (_mFragmentArr == null) {
            _mFragmentArr = new BaseFragment[getSubFragmentCount()];
        }
        // 要显示的就是当前显示的子fragment， 直接返回
        if (_mShowFragment == index) {
            if (args != null) {
                _mFragmentArr[_mShowFragment].setArgs(args);
            }
            return _mFragmentArr[_mShowFragment];
        }
        boolean newAdd = false;
        // 要显示的子fragment不存在，则创建
        if (_mFragmentArr[index] == null) {
            _mFragmentArr[index] = getSubFragment(index);
            newAdd = true;
        }
        if (newAdd || replace) {
            final boolean finalNewAdd = newAdd;
            mGoFragmentHandler.removeCallbacksAndMessages(null);
            mGoFragmentHandler.post(new Runnable() {
                @Override
                public void run() {
                    goFragmentImpl(index, onceOnlyGoBackTag, replace, finalNewAdd, args);
                }
            });
        } else {
            goFragmentImpl(index, onceOnlyGoBackTag, false, false, args);
        }

        return _mFragmentArr[index] == null ? _mFragmentArr[_mShowFragment] : _mFragmentArr[index];
    }

    private void goFragmentImpl(int index, String onceOnlyGoBackTag, boolean replace, boolean newAdd, Bundle args) {
        if (_mFragmentArr[index] != null) {
            // 子fragment的返回层级的临时标识不为空，则设置子fragment的临时返回标识
            if (!TextUtils.isEmpty(onceOnlyGoBackTag)) {
                _mFragmentArr[index].setOnceOnlyGoBackTag(onceOnlyGoBackTag);
            }
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (replace) {
                _mFragmentArr[index].setArgs(args);
                _mShowFragment = index;
                // replace方式下，清理掉之前非当前显示的子fragment
                transaction.replace(getFragmentContainerId(), _mFragmentArr[index], String.valueOf(index));
                for (int i = 0; i < _mFragmentArr.length; i++) {
                    if (index != i) {
                        _mFragmentArr[i] = null;
                    }
                }
            } else {
                if (newAdd) {
                    transaction.add(getFragmentContainerId(), _mFragmentArr[index], String.valueOf(index));
                }
                for (int i = 0; i < _mFragmentArr.length; i++) {
                    if (_mFragmentArr[i] == null) {
                        continue;
                    }
                    if (i == index) {
                        _mShowFragment = index;
                        _mFragmentArr[i].setArgs(args);
                        transaction.show(_mFragmentArr[i]);
                    } else {
                        transaction.hide(_mFragmentArr[i]);
                    }
                }
            }
            transaction.commitNow();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //不为null，说明是死而复活，移除已经存在的fragment，解决fragment残影问题。
        if (savedInstanceState != null && _mFragmentArr != null) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            for (int i = 0; i < _mFragmentArr.length; i++) {
                Fragment fragment = manager.findFragmentByTag(String.valueOf(i));
                if (fragment != null) {
                    transaction.remove(fragment);
                }
            }
            transaction.commitNowAllowingStateLoss();
            _mFragmentArr = null;
            _mShowFragment = -1;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mGoFragmentHandler.removeCallbacksAndMessages(null);
        _defaultReplaceMode = false;
        _mFragmentArr = null;
        _mShowFragment = -1;
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onFragmentVisible(boolean first) {
        super.onFragmentVisible(first);
        BaseFragment subShowFragment = getSubShowFragment();
        if (subShowFragment != null) {
            subShowFragment.onFragmentVisible(subShowFragment._isFirstTime);
        }
    }

    @Override
    @CallSuper
    public void onFragmentHide() {
        super.onFragmentHide();
        BaseFragment fragment = getSubShowFragment();
        if (fragment != null) {
            fragment.onFragmentHide();
        }
    }

    public void onSubFragmentVisible(int index) {
        LogUtils.d(TAG, "onSubFragmentVisible index:" + index);
    }

    /**
     * 获取当前显示的子fragment
     *
     * @return
     */
    public BaseFragment getSubShowFragment() {
        if (_mFragmentArr != null && _mShowFragment >= 0 && _mShowFragment < _mFragmentArr.length) {
            BaseFragment fragment = _mFragmentArr[_mShowFragment];
            return fragment;
        }
        return null;
    }

    /**
     * 获取当前显示的子fragment的index
     *
     * @return
     */
    public int getSubShowFragmentIndex() {
        return _mShowFragment;
    }

    /**
     * 递归获取当前显示的子fragment的最底层的处于显示状态的子fragment
     *
     * @return
     */
    public BaseFragment getBottomLayerFragment() {
        LogUtils.d(TAG, "getBottomLayerFragment _mShowFragment:" + _mShowFragment
                + ", sub fragment length:" + (_mFragmentArr == null ? 0 : _mFragmentArr.length) + ", this:" + this);
        if (_mFragmentArr != null && _mShowFragment >= 0 && _mShowFragment < _mFragmentArr.length) {
            BaseFragment fragment = _mFragmentArr[_mShowFragment];
            if (fragment instanceof BaseParentFragment) {
                return ((BaseParentFragment) fragment).getBottomLayerFragment();
            } else {
                return fragment;
            }
        }
        return this;
    }

    /**
     * 从当前显示的子fragment的最底层的处于显示状态的子fragment返回
     */
    public void goBackToPre(Bundle args) {
        BaseFragment fragment = getBottomLayerFragment();
        if (fragment != null) {
            fragment.goBack(args);
        }
    }

    // 用于父fragment返回事件需要询问子fragment的情况：
    // 比如：当前子fragment可能在父fragment返回前要先返回到自己的其它UI才能允许父fragment返回
    public static final String EVENT_PARENT_UI_GO_BACK = "parentUiGoBack";


    /**
     * fragment的业务事件传递，类似motionEvent的传递与冒泡机制
     *
     * @param event
     * @param bundle
     */
    public boolean dispatchBizEvent(String event, Bundle bundle) {
        if (_mFragmentArr == null) {
            return false;
        }
        for (BaseFragment fragment : _mFragmentArr) {
            if (fragment != null) {
                if (fragment instanceof BaseParentFragment) {
                    if (!((BaseParentFragment) fragment).dispatchBizEvent(event, bundle)) {
                        return onBizEvent(event, bundle);
                    }
                } else {
                    return onBizEvent(event, bundle);
                }
            }
        }
        return true;
    }

    /**
     * fragment的业务事件传递，仅传递给正在显示的子Fragment，类似motionEvent的传递与冒泡机制
     *
     * @param event
     * @param bundle
     */
    public boolean dispatchBizEventToShownFragment(String event, Bundle bundle) {
        BaseFragment fragment = getSubShowFragment();
        if (fragment == null) {
            return false;
        }
        if (fragment instanceof BaseParentFragment) {
            if (!((BaseParentFragment) fragment).dispatchBizEventToShownFragment(event, bundle)) {
                return onBizEvent(event, bundle);
            }
        } else {
            return fragment.onBizEvent(event, bundle);
        }
        return false;
    }

    /**
     * 获取或者创建对应index的子fragment
     *
     * @param index
     * @return
     */
    public abstract BaseFragment getSubFragment(int index);

    /**
     * 获取子fragment的容器id
     *
     * @return
     */
    public abstract int getFragmentContainerId();

    /**
     * 获取子fragment的数量
     *
     * @return
     */
    public abstract int getSubFragmentCount();

    /**
     * 获取是本fragment的首页子fragment的index。
     *
     * @return
     */
    public int getSubHomeIndex() {
        return 0;
    }
}
