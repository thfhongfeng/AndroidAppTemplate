package com.pine.template.base.component.map;

import android.content.Context;
import android.content.Intent;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public class MapSdkManager {
    private final static String TAG = LogUtils.makeLogTag(MapSdkManager.class);
    private static volatile IMapManager mMapManagerImpl;

    private MapSdkManager() {

    }

    private static boolean isInit() {
        return mMapManagerImpl != null;
    }

    public static void init(Context context, IMapManagerFactory factory) {
        mMapManagerImpl = factory.makeMapManager(context);
        if (mMapManagerImpl != null) {
            mMapManagerImpl.init(context);
        }
    }

    public static void startLocation() {
        if (!isInit()) {
            return;
        }
        mMapManagerImpl.startLocation();
    }

    public static void stopLocation() {
        if (!isInit()) {
            return;
        }
        mMapManagerImpl.stopLocation();
    }

    public static void registerLocationListener(ILocationListener locationListener, LocationActionType locationActionType) {
        if (!isInit()) {
            return;
        }
        mMapManagerImpl.registerLocationListener(locationListener, locationActionType);
    }

    public static void unregisterLocationListener(ILocationListener locationListener) {
        if (!isInit()) {
            return;
        }
        mMapManagerImpl.unregisterLocationListener(locationListener);
    }

    public static LocationInfo getLocation() {
        if (!isInit()) {
            return null;
        }
        return mMapManagerImpl.getLocation();
    }

    public static Intent getMapActivityIntent(Context context) {
        if (!isInit()) {
            return null;
        }
        return mMapManagerImpl.getMapActivityIntent(context, MapType.MAP_TYPE_NORMAL);
    }

    public static Intent getMapActivityIntent(Context context, MapType type) {
        if (!isInit()) {
            return null;
        }
        return mMapManagerImpl.getMapActivityIntent(context, type);
    }

    public static Intent getMarkMapActivityIntent(Context context, double latitude, double longitude,
                                                  boolean canMark) {
        if (!isInit()) {
            return null;
        }
        return mMapManagerImpl.getMarkMapActivityIntent(context, MapType.MAP_TYPE_NORMAL, latitude, longitude, canMark);
    }


    /**
     * @param context
     * @param type
     * @param latitude  Gcj_02坐标系 标记点纬度
     * @param longitude Gcj_02坐标系 标记点经度
     * @param canMark   是否可以标记
     * @return
     */
    public static Intent getMarkMapActivityIntent(Context context, MapType type,
                                                  double latitude, double longitude, boolean canMark) {
        if (!isInit()) {
            return null;
        }
        return mMapManagerImpl.getMarkMapActivityIntent(context, type, latitude, longitude, canMark);
    }

    public enum MapType {
        MAP_TYPE_NORMAL,
        MAP_TYPE_SATELLITE,
        MAP_TYPE_NONE
    }
}
