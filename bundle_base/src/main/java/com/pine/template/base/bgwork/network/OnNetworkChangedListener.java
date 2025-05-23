package com.pine.template.base.bgwork.network;

public interface OnNetworkChangedListener {
    void onConnected(NetworkType networkType);

    void onWifiSignalChange(int level);

    void onMobileSignalChange(int level);

    void onDisconnected();
}
