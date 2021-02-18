package com.pine.template.base.component.map.gaode.loaction;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pine.template.base.BaseApplication;
import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class GaodeLocationManager {
    private final static String TAG = LogUtils.makeLogTag(GaodeLocationManager.class);
    private static volatile GaodeLocationManager mInstance;
    private AMapLocationClient mClient = null;
    private AMapLocationClientOption mOption, mDiyOption;
    private AMapLocation mSdkLocation;
    private LocationInfo mLocation;
    private HashMap<ILocationListener, LocationActionType> mLocationCallbackMap = new HashMap<>();

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            LogUtils.d(TAG, "onLocationChanged location:" + location +
                    (location != null ? ",location.getLocationType():" + location.getLocationType() : ""));
            if (location != null && (location.getLocationType() == AMapLocation.LOCATION_TYPE_GPS
                    || location.getLocationType() == AMapLocation.LOCATION_TYPE_CELL
                    || location.getLocationType() == AMapLocation.LOCATION_TYPE_OFFLINE
                    || location.getLocationType() == AMapLocation.LOCATION_TYPE_WIFI
                    || location.getLocationType() == AMapLocation.LOCATION_TYPE_FIX_CACHE)) {
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

    private GaodeLocationManager() {
        initLocation();
    }

    public static GaodeLocationManager getInstance() {
        if (mInstance == null) {
            synchronized (GaodeLocationManager.class) {
                if (mInstance == null) {
                    mInstance = new GaodeLocationManager();
                }
            }
        }
        return mInstance;
    }

    private void initLocation() {
        if (mClient == null) {
            mClient = new AMapLocationClient(BaseApplication.mApplication);
            mClient.setLocationOption(getDefaultLocationClientOption());
        }
        mClient.setLocationListener(mLocationListener);
        stop();
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    private AMapLocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new AMapLocationClientOption();
            // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 可选，默认false，设置是否单次定位
            mOption.setOnceLocation(false);
            // 可选，默认true，设置是否返回地址信息
            mOption.setNeedAddress(true);
            // 可选，默认为true，设置是否使用缓存策略
            mOption.setLocationCacheEnable(true);
            // 可选，默认为false，设置是否使用设备传感器
            mOption.setSensorEnable(true);
            // 可选，默认值false，设置退出时是否杀死进程。注意：如果设置为true，并且配置的service不是remote的则会杀死当前页面进程，请慎重使用
            mOption.setKillProcess(false);
            // 可选，设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
            mOption.setInterval(1000);
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

    public void setLocationOption(AMapLocationClientOption option) {
        if (option != null) {
            stop();
            mDiyOption = option;
            mClient.setLocationOption(option);
        }
    }

    public synchronized boolean start() {
        if (mClient != null) {
            LogUtils.d(TAG, "start location");
            mClient.startLocation();
            return true;
        }
        return false;
    }

    public synchronized void stop() {
        if (mClient != null) {
            LogUtils.d(TAG, "stop location");
            mClient.stopLocation();
        }
    }

    public boolean isStart() {
        return mClient.isStarted();
    }


    public AMapLocation getSdkLocation() {
        return mSdkLocation;
    }

    public LocationInfo getLocation() {
        return mLocation;
    }

    public void setLocation(AMapLocation location) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLocationType(location.getLocationType());
        locationInfo.setRadius(location.getAccuracy());
        locationInfo.setDirection(location.getBearing());
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setLongitude(location.getLongitude());
        mSdkLocation = location;
        mLocation = locationInfo;
    }
}
