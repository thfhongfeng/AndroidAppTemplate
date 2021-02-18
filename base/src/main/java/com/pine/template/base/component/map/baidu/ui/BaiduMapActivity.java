package com.pine.template.base.component.map.baidu.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.pine.template.base.R;
import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager.MapType;
import com.pine.template.base.component.map.baidu.BaiduMapManager;
import com.pine.template.base.component.map.baidu.location.BaiduLocationManager;
import com.pine.template.base.ui.BaseActionBarTextMenuActivity;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.util.GPSUtils;

/**
 * Created by tanghongfeng on 2018/10/31
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
public class BaiduMapActivity extends BaseActionBarTextMenuActivity implements View.OnClickListener {
    private MapView map_view;
    private ImageView location_iv;

    private BaiduMap mMap;
    private MyLocationConfiguration mLocationConfig;
    private boolean mMapSetup;
    private LatLng mMarkerLatLng, mInitLatLng;
    private int mMapType = BaiduMap.MAP_TYPE_NORMAL;
    private boolean mCanMark;

    private ILocationListener mLocationListener = new ILocationListener() {
        @Override
        public void onReceiveLocation(LocationInfo locationInfo) {
            locationInMap(false);
        }

        @Override
        public void onReceiveFail() {

        }
    };

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.base_activity_baidu_map;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        location_iv = findViewById(R.id.location_iv);
        map_view = findViewById(R.id.map_view);
        mMap = map_view.getMap();
    }

    @Override
    protected boolean parseIntentData() {
        int mapTypeOrdinal = getIntent().getIntExtra("mapTypeOrdinal", 0);
        switch (MapType.values()[mapTypeOrdinal]) {
            case MAP_TYPE_NORMAL:
                mMapType = BaiduMap.MAP_TYPE_NORMAL;
                break;
            case MAP_TYPE_SATELLITE:
                mMapType = BaiduMap.MAP_TYPE_SATELLITE;
                break;
            case MAP_TYPE_NONE:
                mMapType = BaiduMap.MAP_TYPE_NONE;
                break;
        }
        mCanMark = getIntent().getBooleanExtra("canMark", false);
        double latitude = getIntent().getDoubleExtra("latitude", -1);
        double longitude = getIntent().getDoubleExtra("longitude", -1);
        if (latitude != -1 && latitude != -1) {
            double[] latLon = GPSUtils.gcj02_To_Bd09(latitude, longitude);
            mInitLatLng = new LatLng(latLon[0], latLon[1]);
        }
        return false;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        location_iv.setOnClickListener(this);

        setupMap();
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.base_baidu_map_title);
        goBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                setResult(RESULT_CANCELED);
                return;
            }
        });
        if (mCanMark) {
            menuBtnTv.setText(R.string.base_done);
            menuBtnTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMarkerLatLng != null) {
                        Intent intent = new Intent();
                        double[] latLon = GPSUtils.bd09_To_Gcj02(mMarkerLatLng.latitude, mMarkerLatLng.longitude);
                        intent.putExtra("latitude", latLon[0]);
                        intent.putExtra("longitude", latLon[1]);
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                    } else {
                        Toast.makeText(BaiduMapActivity.this, R.string.base_map_marker_need,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        } else {
            menuBtnTv.setVisibility(View.GONE);
        }
    }

    private void setupMap() {
        if (mMapSetup) {
            return;
        }
        mMap.setMapType(mMapType);
        mMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15));
        if (mCanMark) {
            //地图点击事件响应
            mMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    addMarker(latLng);
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    //点击地图上的poi图标获取描述信息：mapPoi.getName()，经纬度：mapPoi.getPosition()
                    addMarker(mapPoi.getPosition());
                    return false;
                }
            });
        }
        if (mInitLatLng != null) {
            addMarker(mInitLatLng);
        }
        mMapSetup = true;
        locationInMap(true);
    }

    private void addMarker(LatLng latLng) {
        //点击地图某个位置获取经纬度latLng.latitude、latLng.longitude
        mMarkerLatLng = latLng;
        mMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.base_ic_map_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(mMarkerLatLng)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mMap.addOverlay(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        map_view.onResume();
        BaiduMapManager.getInstance().registerLocationListener(mLocationListener, LocationActionType.FLOWING);
    }

    @Override
    public void onStop() {
        BaiduMapManager.getInstance().unregisterLocationListener(mLocationListener);
        mMap.setMyLocationEnabled(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        map_view.onDestroy();
        super.onDestroy();
    }

    private void locationInMap(boolean moveToCenter) {
        if (!mMapSetup) {
            setupMap();
        }
        if (moveToCenter) {
            if (mLocationConfig == null || mLocationConfig.locationMode != MyLocationConfiguration.LocationMode.FOLLOWING) {
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
                mMap.setMyLocationConfiguration(mLocationConfig);
            }
        } else {
            if (mLocationConfig == null || mLocationConfig.locationMode != MyLocationConfiguration.LocationMode.NORMAL) {
                mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mMap.setMyLocationConfiguration(mLocationConfig);
            }
        }
        // 开启定位图层
        mMap.setMyLocationEnabled(true);

        BDLocation location = BaiduLocationManager.getInstance().getSdkLocation();
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mMap.setMyLocationData(locData);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.location_iv) {
            locationInMap(true);
        }
    }
}
