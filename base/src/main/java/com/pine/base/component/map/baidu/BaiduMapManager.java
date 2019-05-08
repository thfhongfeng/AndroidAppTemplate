package com.pine.base.component.map.baidu;

import android.content.Context;
import android.content.Intent;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.IMapManager;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.component.map.baidu.location.BdLocationManager;
import com.pine.base.component.map.baidu.ui.BaiduMapActivity;
import com.pine.tool.util.GPSUtils;
import com.pine.tool.util.LogUtils;

import java.util.LinkedList;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public class BaiduMapManager implements IMapManager {
    private final static String TAG = LogUtils.makeLogTag(BaiduMapManager.class);

    private static BaiduMapManager mInstance;
    private LinkedList<ILocationListener> mLocationCallbackList = new LinkedList<>();
    private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LogUtils.d(TAG, "onReceiveLocation bdLocation:" + bdLocation +
                    (bdLocation != null ? ",bdLocation.getLocType():" + bdLocation.getLocType() : ""));
            if (bdLocation != null && (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation
                    || bdLocation.getLocType() == BDLocation.TypeOffLineLocation)) {
                BdLocationManager.getInstance().unregisterListener(mLocationListener);
                BdLocationManager.getInstance().stop();
                BdLocationManager.getInstance().setLocation(bdLocation);
                synchronized (mLocationCallbackList) {
                    for (ILocationListener listener : mLocationCallbackList) {
                        LocationInfo locationInfo = new LocationInfo();
                        locationInfo.setLatitude(bdLocation.getLatitude());
                        locationInfo.setLongitude(bdLocation.getLongitude());
                        listener.onReceiveLocation(locationInfo);
                    }
                }
            } else {
                synchronized (mLocationCallbackList) {
                    for (ILocationListener listener : mLocationCallbackList) {
                        listener.onReceiveFail();
                    }
                }
            }
        }
    };

    public static IMapManager getInstance() {
        if (mInstance == null) {
            synchronized (BaiduMapManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use third map: baidu");
                    mInstance = new BaiduMapManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(Context context) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(context);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    @Override
    public void startLocation() {
        BdLocationManager.getInstance().start();
    }

    @Override
    public void stopLocation() {
        BdLocationManager.getInstance().stop();
    }

    @Override
    public void registerLocationListener(ILocationListener locationListener) {
        synchronized (mLocationCallbackList) {
            mLocationCallbackList.add(locationListener);
        }
        BdLocationManager.getInstance().registerListener(mLocationListener);
    }

    @Override
    public void unregisterLocationListener(ILocationListener locationListener) {
        synchronized (mLocationCallbackList) {
            mLocationCallbackList.remove(locationListener);
        }
        BdLocationManager.getInstance().unregisterListener(mLocationListener);
    }

    @Override
    public LocationInfo getLocation() {
        BDLocation location = BdLocationManager.getInstance().getLocation();
        if (location == null) {
            return null;
        }
        LocationInfo locationInfo = new LocationInfo();
        double[] latLon = GPSUtils.bd09_To_Gcj02(location.getLatitude(), location.getLongitude());
        locationInfo.setLatitude(latLon[0]);
        locationInfo.setLongitude(latLon[1]);
        return locationInfo;
    }

    @Override
    public Intent getMapActivityIntent(Context context) {
        return getMapActivityIntent(context, MapSdkManager.MapType.MAP_TYPE_NORMAL);
    }

    @Override
    public Intent getMapActivityIntent(Context context, MapSdkManager.MapType mapType) {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("mapTypeOrdinal", mapType.ordinal());
        return intent;
    }

    @Override
    public Intent getMarkMapActivityIntent(Context context, double latitude, double longitude,
                                           boolean canMark) {
        return getMarkMapActivityIntent(context, MapSdkManager.MapType.MAP_TYPE_NORMAL,
                latitude, longitude, canMark);
    }

    /**
     * @param context
     * @param mapType
     * @param latitude  Gcj_02坐标系
     * @param longitude Gcj_02坐标系
     * @return
     */
    @Override
    public Intent getMarkMapActivityIntent(Context context, MapSdkManager.MapType mapType,
                                           double latitude, double longitude, boolean canMark) {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("mapTypeOrdinal", mapType.ordinal());
        if (latitude != -1 && longitude != -1) {
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        intent.putExtra("canMark", canMark);
        return intent;
    }
}
