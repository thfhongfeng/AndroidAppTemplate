package com.pine.template.base.bg.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BgNetWorker {
    private final static String TAG = BgNetWorker.class.getSimpleName();

    private static BgNetWorker instance;

    public synchronized static BgNetWorker getInstance() {
        if (instance == null) {
            instance = new BgNetWorker();
        }
        return instance;
    }

    private Context mContext;
    private Handler mMainHandler;

    private BgNetWorker() {
        mContext = AppUtils.getApplicationContext();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    private NetworkType mNetworkType = null;
    private int mWifiLevel = Integer.MIN_VALUE;
    private int mMobileLevel = Integer.MIN_VALUE;

    public void setNetworkType(NetworkType networkType) {
        LogUtils.d(TAG, "setNetworkType cur:" + networkType + ", last:" + mNetworkType);
        if (mNetworkType != networkType) {
            if (isWifiType()) {
                mWifiLevel = BgNetworkHelper.getWifiSignal(mContext);
            }
            mNetworkType = networkType;
            onNetworkType(null);
            if (BgNetworkHelper.isMobilType(mNetworkType)) {
                BgNetworkHelper.listenMobileSignal(mContext, mPhoneStateListener);
            } else {
                BgNetworkHelper.unListenMobileSignal(mContext, mPhoneStateListener);
            }
        }
    }

    private void onNetworkType(OnNetworkChangedListener newListener) {
        List<OnNetworkChangedListener> listeners = getListeners(newListener);
        boolean isWifiType = BgNetworkHelper.isWifiType(mNetworkType);
        boolean isMobileType = BgNetworkHelper.isMobilType(mNetworkType);
        for (OnNetworkChangedListener listener : listeners) {
            if (mNetworkType == NetworkType.NETWORK_NO) {
                listener.onDisconnected();
            } else {
                listener.onConnected(mNetworkType);
                if (isWifiType) {
                    listener.onWifiSignalChange(BgNetworkHelper.getWifiSignal(mContext));
                }
                if (isMobileType) {
                    listener.onMobileSignalChange(mMobileLevel);
                }
            }
        }
    }

    public void setWifiLevel(boolean checkEqual) {
        onWifiLevel(checkEqual, null);
    }

    public void onWifiLevel(boolean checkEqual, OnNetworkChangedListener newListener) {
        int wifiLevel = BgNetworkHelper.getWifiSignal(mContext);
        if (!checkEqual || mWifiLevel != wifiLevel) {
            LogUtils.d(TAG, "onWifiLevel cur:" + wifiLevel + ", last:" + mWifiLevel);
            mWifiLevel = wifiLevel;
            List<OnNetworkChangedListener> listeners = getListeners(newListener);
            for (OnNetworkChangedListener listener : listeners) {
                listener.onWifiSignalChange(mWifiLevel);
            }
        }
    }

    PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int mobileLevel = NetWorkUtils.getSignalLevel(signalStrength);
            if (mobileLevel != mMobileLevel) {
                mMobileLevel = mobileLevel;
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMobileLevel(null);
                    }
                });
            }
        }
    };

    public void onMobileLevel(OnNetworkChangedListener newListener) {
        List<OnNetworkChangedListener> listeners = getListeners(newListener);
        for (OnNetworkChangedListener listener : listeners) {
            listener.onMobileSignalChange(mMobileLevel);
        }
    }

    private Set<OnNetworkChangedListener> mNetworkChangedListeners = new HashSet<>();

    public void regNetworkChangedListener(final OnNetworkChangedListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mNetworkChangedListeners) {
            mNetworkChangedListeners.add(listener);
        }
        onNetworkType(listener);
        if (isWifiType()) {
            onWifiLevel(false, listener);
        }
        if (isMobilType()) {
            onMobileLevel(listener);
        }
    }

    public boolean isRegNetworkChangedListener(final OnNetworkChangedListener listener) {
        if (listener == null) {
            return false;
        }
        synchronized (mNetworkChangedListeners) {
            return mNetworkChangedListeners.contains(listener);
        }
    }

    public void unRegNetworkChangedListener(final OnNetworkChangedListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mNetworkChangedListeners) {
            mNetworkChangedListeners.remove(listener);
        }
    }

    public boolean isMobilType() {
        return BgNetworkHelper.isMobilType(mNetworkType);
    }

    public boolean isWifiType() {
        return BgNetworkHelper.isWifiType(mNetworkType);
    }

    public boolean isEthernet() {
        return BgNetworkHelper.isEthernet();
    }

    private List<OnNetworkChangedListener> getListeners(OnNetworkChangedListener newListener) {
        List<OnNetworkChangedListener> listeners = new ArrayList<>();
        if (newListener == null) {
            listeners = getSyncList(mNetworkChangedListeners);
        } else if (newListener != null) {
            listeners.add(newListener);
        }
        return listeners;
    }

    private <T> List<T> getSyncList(Set<T> listSet) {
        synchronized (listSet) {
            List<T> list = new ArrayList<>();
            for (T t : listSet) {
                list.add(t);
            }
            return list;
        }
    }
}
