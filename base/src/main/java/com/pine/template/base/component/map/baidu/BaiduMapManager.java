package com.pine.template.base.component.map.baidu;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.IMapManager;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.base.component.map.baidu.location.BaiduLocationManager;
import com.pine.template.base.component.map.baidu.ui.BaiduMapActivity;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public class BaiduMapManager implements IMapManager {
    private final static String TAG = LogUtils.makeLogTag(BaiduMapManager.class);

    private static BaiduMapManager mInstance;

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
        BaiduLocationManager.getInstance().start();
    }

    @Override
    public void stopLocation() {
        BaiduLocationManager.getInstance().stop();
    }

    @Override
    public void registerLocationListener(ILocationListener locationListener, LocationActionType locationActionType) {
        BaiduLocationManager.getInstance().registerListener(locationListener, locationActionType);
        startLocation();
    }

    @Override
    public void unregisterLocationListener(ILocationListener locationListener) {
        BaiduLocationManager.getInstance().unregisterListener(locationListener);
        stopLocation();
    }

    @Override
    public LocationInfo getLocation() {
        return BaiduLocationManager.getInstance().getLocation();
    }

    @Override
    public Intent getMapActivityIntent(Context context, MapSdkManager.MapType mapType) {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("mapTypeOrdinal", mapType.ordinal());
        return intent;
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
