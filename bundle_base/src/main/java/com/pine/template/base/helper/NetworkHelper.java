package com.pine.template.base.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pine.template.base.bgwork.AppBgManager;
import com.pine.template.base.bgwork.network.NetworkType;
import com.pine.template.base.bgwork.network.OnNetworkChangedListener;
import com.pine.template.bundle_base.R;
import com.pine.tool.ui.Activity;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;

import java.util.HashMap;
import java.util.Set;

public class NetworkHelper implements OnNetworkChangedListener {
    private final String TAG = this.getClass().getSimpleName();

    private static NetworkHelper instance;

    public synchronized static NetworkHelper getInstance() {
        if (instance == null) {
            instance = new NetworkHelper();
        }
        return instance;
    }

    private NetworkHelper() {

    }

    public static boolean checkNetWork() {
        return NetWorkUtils.checkNetWork();
    }

    private HashMap<String, Activity> mActivityMap = new HashMap<>();
    private HashMap<String, View> mSignalIvMap = new HashMap<>();
    private HashMap<String, IOnNetworkListener> mListenerMap = new HashMap<>();

    private Object MAP_SYNC_LOCK = new Object();

    private String getTargetTag(@NonNull Activity activity) {
        return activity.getClass().getSimpleName();
    }

    public void listen(@NonNull Activity activity, ImageView signalLevel, IOnNetworkListener listener) {
        String tag = getTargetTag(activity);
        synchronized (MAP_SYNC_LOCK) {
            mListenerMap.put(tag, listener);
            mActivityMap.put(tag, activity);
            mSignalIvMap.put(tag, signalLevel);
            AppBgManager.unRegNetworkChangedListener(this);
            AppBgManager.regNetworkChangedListener(this);
        }
    }

    public void listen(@NonNull Activity activity, TextView signalLevel, IOnNetworkListener listener) {
        String tag = getTargetTag(activity);
        synchronized (MAP_SYNC_LOCK) {
            mListenerMap.put(tag, listener);
            mActivityMap.put(tag, activity);
            mSignalIvMap.put(tag, signalLevel);
            AppBgManager.unRegNetworkChangedListener(this);
            AppBgManager.regNetworkChangedListener(this);
        }
    }

    public void unListen(@NonNull Activity activity) {
        String tag = getTargetTag(activity);
        synchronized (MAP_SYNC_LOCK) {
            mActivityMap.remove(tag);
            mSignalIvMap.remove(tag);
            mListenerMap.remove(tag);
            if (mActivityMap.size() <= 0) {
                AppBgManager.unRegNetworkChangedListener(this);
            }
        }
    }

    private void setupSignalLevelUi(TextView signalLevel, int level) {
        int resId = -1;
        if (NetWorkUtils.checkNetWork()) {
            if (AppBgManager.isWifiType()) {
                resId = R.string.net_wifi;
            } else if (AppBgManager.isMobilType()) {
                resId = R.string.net_4g;
            } else if (AppBgManager.isEthernet()) {
                resId = R.string.net_eth;
            }
        }
        if (resId == -1) {
            signalLevel.setSelected(false);
            signalLevel.setText(R.string.net_null);
        } else {
            signalLevel.setSelected(true);
            signalLevel.setText(resId);
        }
    }

    private void setupSignalLevelUi(ImageView signalLevel, int level) {
        int resId = -1;
        switch (level) {
            case 1:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_1_white;
                } else {
                    resId = R.mipmap.base_signal_1_white;
                }
                break;
            case 2:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_2_white;
                } else {
                    resId = R.mipmap.base_signal_2_white;
                }
                break;
            case 3:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_3_white;
                } else {
                    resId = R.mipmap.base_signal_3_white;
                }
                break;
            case 4:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_4_white;
                } else {
                    resId = R.mipmap.base_signal_4_white;
                }
                break;
            case 5:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_4_white;
                } else {
                    resId = R.mipmap.base_signal_5_white;
                }
                break;
            default:
                if (AppBgManager.isWifiType()) {
                    resId = R.mipmap.base_wifi_signal_0_white;
                } else if (AppBgManager.isMobilType()) {
                    resId = R.mipmap.base_signal_0_white;
                } else if (AppBgManager.isEthernet()) {
                    resId = R.mipmap.base_ethernet_white;
                }
                break;
        }
        if (!NetWorkUtils.checkNetWork()) {
            resId = -1;
        }
        if (resId != -1) {
            signalLevel.setImageResource(resId);
        } else {
            signalLevel.setImageResource(R.mipmap.base_no_network_white);
        }
    }

    @Override
    public void onDisconnected() {
        setupSignalIv(-1);
        synchronized (MAP_SYNC_LOCK) {
            Set<String> keySet = mListenerMap.keySet();
            for (String key : keySet) {
                IOnNetworkListener listener = mListenerMap.get(key);
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        }
    }

    @Override
    public void onConnected(NetworkType networkType) {
        setupSignalIv(-1);
        synchronized (MAP_SYNC_LOCK) {
            Set<String> keySet = mListenerMap.keySet();
            for (String key : keySet) {
                IOnNetworkListener listener = mListenerMap.get(key);
                if (listener != null) {
                    listener.onConnected(networkType);
                }
            }
        }
    }

    @Override
    public void onWifiSignalChange(int level) {
        if (!AppBgManager.isWifiType()) {
            return;
        }
        LogUtils.d(TAG, "onWifiSignalChange level:" + level);
        setupSignalIv(level);
    }

    @Override
    public void onMobileSignalChange(int level) {
        if (!AppBgManager.isMobilType()) {
            return;
        }
        LogUtils.d(TAG, "onMobileSignalChange level:" + level);
        setupSignalIv(level);
    }

    private void setupSignalIv(int level) {
        synchronized (MAP_SYNC_LOCK) {
            Set<String> keySet = mActivityMap.keySet();
            for (String key : keySet) {
                Activity activity = mActivityMap.get(key);
                View signalLevel = mSignalIvMap.get(key);
                if (activity != null && signalLevel != null) {
                    if (!activity.isFinishing() && !activity.isDestroyed()) {
                        if (signalLevel instanceof ImageView) {
                            setupSignalLevelUi((ImageView) signalLevel, level);
                        } else if (signalLevel instanceof TextView) {
                            setupSignalLevelUi((TextView) signalLevel, level);
                        }
                    }
                }
            }
        }
    }

    public interface IOnNetworkListener {
        void onDisconnected();

        void onConnected(NetworkType networkType);
    }
}
