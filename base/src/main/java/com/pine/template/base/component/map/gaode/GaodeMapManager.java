package com.pine.template.base.component.map.gaode;

import android.content.Context;
import android.content.Intent;

import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.IMapManager;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.base.component.map.gaode.loaction.GaodeLocationManager;
import com.pine.template.base.component.map.gaode.ui.GaodeMapActivity;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public class GaodeMapManager implements IMapManager {
    private final static String TAG = LogUtils.makeLogTag(GaodeMapManager.class);

    private static GaodeMapManager mInstance;

    public static IMapManager getInstance() {
        if (mInstance == null) {
            synchronized (GaodeMapManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use third map: gaode");
                    mInstance = new GaodeMapManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void startLocation() {
        GaodeLocationManager.getInstance().start();
    }

    @Override
    public void stopLocation() {
        GaodeLocationManager.getInstance().stop();
    }

    @Override
    public void registerLocationListener(ILocationListener locationListener, LocationActionType locationActionType) {
        GaodeLocationManager.getInstance().registerListener(locationListener, locationActionType);
        startLocation();
    }

    @Override
    public void unregisterLocationListener(ILocationListener locationListener) {
        GaodeLocationManager.getInstance().unregisterListener(locationListener);
    }

    @Override
    public LocationInfo getLocation() {
        return GaodeLocationManager.getInstance().getLocation();
    }

    @Override
    public Intent getMapActivityIntent(Context context, MapSdkManager.MapType mapType) {
        Intent intent = new Intent(context, GaodeMapActivity.class);
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
        Intent intent = new Intent(context, GaodeMapActivity.class);
        intent.putExtra("mapTypeOrdinal", mapType.ordinal());
        if (latitude != -1 && longitude != -1) {
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        intent.putExtra("canMark", canMark);
        return intent;
    }
}
