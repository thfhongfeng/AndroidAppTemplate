package com.pine.base.component.map;

import android.content.Context;
import android.content.Intent;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public interface IMapManager {
    void init(Context context);

    void startLocation();

    void stopLocation();

    /**
     * @param locationListener
     * @param locationActionType     定位行为类型
     */
    void registerLocationListener(ILocationListener locationListener, LocationActionType locationActionType);

    void unregisterLocationListener(ILocationListener locationListener);

    LocationInfo getLocation();

    /**
     * @param context
     * @param type
     * @return
     */
    Intent getMapActivityIntent(Context context, MapSdkManager.MapType type);

    /**
     * @param context
     * @param type
     * @param latitude  Gcj_02坐标系 标记点纬度
     * @param longitude Gcj_02坐标系 标记点经度
     * @param canMark   是否可以标记
     * @return
     */
    Intent getMarkMapActivityIntent(Context context, MapSdkManager.MapType type,
                                    double latitude, double longitude, boolean canMark);
}
