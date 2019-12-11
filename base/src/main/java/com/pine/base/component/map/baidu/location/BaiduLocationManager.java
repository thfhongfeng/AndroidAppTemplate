package com.pine.base.component.map.baidu.location;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pine.base.BaseApplication;
import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.LocationActionType;
import com.pine.base.component.map.LocationInfo;
import com.pine.tool.util.GPSUtils;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class BaiduLocationManager {
    private final static String TAG = LogUtils.makeLogTag(BaiduLocationManager.class);
    private static volatile BaiduLocationManager mInstance;
    private LocationClient mClient = null;
    private LocationClientOption mOption, mDiyOption;
    private BDLocation mSdkLocation;
    private LocationInfo mLocation;
    private HashMap<ILocationListener, LocationActionType> mLocationCallbackMap = new HashMap<>();

    private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d(TAG, "onReceiveLocation location:" + location +
                    (location != null ? ",location.getLocType():" + location.getLocType() : ""));
            if (location != null && (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation
                    || location.getLocType() == BDLocation.TypeOffLineLocation)) {
                setLocation(location);
                dealLocationResult(true);
            } else {
                dealLocationResult(false);
            }
        }
    };

    private void dealLocationResult(boolean success) {
        if (mLocationCallbackMap == null || mLocationCallbackMap.size() < 1) {
            stop();
            return;
        }
        synchronized (mLocationCallbackMap) {
            Iterator<Map.Entry<ILocationListener, LocationActionType>> iterator = mLocationCallbackMap.entrySet().iterator();
            List<ILocationListener> noFlowingList = new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry<ILocationListener, LocationActionType> entry = iterator.next();
                if (success) {
                    entry.getKey().onReceiveLocation(mLocation);
                } else {
                    entry.getKey().onReceiveFail();
                }
                switch (entry.getValue()) {
                    case ONCE:
                        noFlowingList.add(entry.getKey());
                        break;
                    default:
                        break;
                }
            }
            for (ILocationListener listener : noFlowingList) {
                mLocationCallbackMap.remove(listener);
            }
        }
    }

    private BaiduLocationManager() {
        initLocation();
    }

    public static BaiduLocationManager getInstance() {
        if (mInstance == null) {
            synchronized (BaiduLocationManager.class) {
                if (mInstance == null) {
                    mInstance = new BaiduLocationManager();
                }
            }
        }
        return mInstance;
    }

    private void initLocation() {
        if (mClient == null) {
            mClient = new LocationClient(BaseApplication.mApplication);
            mClient.setLocOption(getDefaultLocationClientOption());
        }
        mClient.registerLocationListener(mLocationListener);
        stop();
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    private LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setCoorType("bd09ll");
            // 可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setScanSpan(1000);
            // 可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedAddress(true);
            // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationDescribe(true);
            // 可选，设置是否需要设备方向结果
            mOption.setNeedDeviceDirect(true);
            // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setLocationNotify(false);
            // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIgnoreKillProcess(true);
            // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.setIsNeedLocationPoiList(true);
            // 可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.SetIgnoreCacheException(false);
            // 可选，默认false，设置是否开启Gps定位
            mOption.setOpenGps(false);
            // 可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            mOption.setIsNeedAltitude(false);

        }
        return mOption;
    }

    public void registerListener(ILocationListener listener, LocationActionType locationActionType) {
        synchronized (mLocationCallbackMap) {
            mLocationCallbackMap.remove(listener);
            mLocationCallbackMap.put(listener, locationActionType);
        }
    }

    public void unregisterListener(ILocationListener listener) {
        synchronized (mLocationCallbackMap) {
            mLocationCallbackMap.remove(listener);
        }
    }

    public void setLocationOption(LocationClientOption option) {
        if (option != null) {
            stop();
            mDiyOption = option;
            mClient.setLocOption(option);
        }
    }

    public synchronized boolean start() {
        if (mClient != null) {
            LogUtils.d(TAG, "start location");
            mClient.start();
            return true;
        }
        return false;
    }

    public synchronized void stop() {
        if (mClient != null) {
            LogUtils.d(TAG, "stop location");
            mClient.stop();
        }
    }

    public boolean isStart() {
        return mClient.isStarted();
    }

    public boolean requestHotSpotState() {
        return mClient.requestHotSpotState();
    }

    public BDLocation getSdkLocation() {
        return mSdkLocation;
    }

    public LocationInfo getLocation() {
        return mLocation;
    }

    public void setLocation(BDLocation location) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLocationType(location.getLocType());
        locationInfo.setRadius(location.getRadius());
        locationInfo.setDirection(location.getDirection());
        double[] latLon = GPSUtils.bd09_To_Gcj02(location.getLatitude(), location.getLongitude());
        locationInfo.setLatitude(latLon[0]);
        locationInfo.setLongitude(latLon[1]);
        mSdkLocation = location;
        mLocation = locationInfo;
    }
}
