package com.pine.base.component.map.gaode.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.pine.base.R;
import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.LocationActionType;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager.MapType;
import com.pine.base.component.map.gaode.GaodeMapManager;
import com.pine.base.ui.BaseActionBarTextMenuActivity;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.util.GPSUtils;

/**
 * Created by tanghongfeng on 2018/10/31
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
public class GaodeMapActivity extends BaseActionBarTextMenuActivity implements View.OnClickListener {
    private MapView map_view;
    private ImageView location_iv;

    private AMap mMap;
    private MyLocationStyle mLocationConfig;
    private boolean mMapSetup;
    private LatLng mMarkerLatLng, mInitLatLng;
    private int mMapType = AMap.MAP_TYPE_NORMAL;
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
        return R.layout.base_activity_gaode_map;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        location_iv = findViewById(R.id.location_iv);
        map_view = findViewById(R.id.map_view);
        mMap = map_view.getMap();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map_view.onCreate(savedInstanceState);
    }

    @Override
    protected boolean parseIntentData() {
        int mapTypeOrdinal = getIntent().getIntExtra("mapTypeOrdinal", 0);
        switch (MapType.values()[mapTypeOrdinal]) {
            case MAP_TYPE_NORMAL:
                mMapType = mMap.MAP_TYPE_NORMAL;
                break;
            case MAP_TYPE_SATELLITE:
                mMapType = mMap.MAP_TYPE_SATELLITE;
                break;
            case MAP_TYPE_NONE:
                mMapType = mMap.MAP_TYPE_NORMAL;
                break;
        }
        mCanMark = getIntent().getBooleanExtra("canMark", false);
        double latitude = getIntent().getDoubleExtra("latitude", -1);
        double longitude = getIntent().getDoubleExtra("longitude", -1);
        if (latitude != -1 && latitude != -1) {
            mInitLatLng = new LatLng(latitude, longitude);
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
        titleTv.setText(R.string.base_gaode_map_title);
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
                        Toast.makeText(GaodeMapActivity.this, R.string.base_map_marker_need,
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
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        if (mCanMark) {
            //地图点击事件响应
            mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    addMarker(latLng);
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
        MarkerOptions option = new MarkerOptions()
                .position(mMarkerLatLng)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mMap.addMarker(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map_view.onResume();
        GaodeMapManager.getInstance().registerLocationListener(mLocationListener, LocationActionType.FLOWING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map_view.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map_view.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        GaodeMapManager.getInstance().unregisterLocationListener(mLocationListener);
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
            if (mLocationConfig == null || mLocationConfig.getMyLocationType() != MyLocationStyle.LOCATION_TYPE_LOCATE) {
                // 定位蓝点配置
                mLocationConfig = new MyLocationStyle();
                // 设置我的位置展示模式，默认为LOCATION_TYPE_LOCATE。
                // LOCATION_TYPE_SHOW：只定位
                // LOCATION_TYPE_LOCATE：定位、且将视角移动到地图中心点
                // LOCATION_TYPE_FOLLOW：定位、且将视角移动到地图中心点，定位点跟随设备移动
                // LOCATION_TYPE_FOLLOW_NO_CENTER：定位、但不会移动到地图中心点，并且会跟随设备移动
                mLocationConfig.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                // 设置定位蓝点的Style
                mMap.setMyLocationStyle(mLocationConfig);
            }
        } else {
            if (mLocationConfig == null || mLocationConfig.getMyLocationType() != MyLocationStyle.LOCATION_TYPE_SHOW) {
                // 定位蓝点配置
                mLocationConfig = new MyLocationStyle();
                mLocationConfig.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
                // 设置定位蓝点的Style
                mMap.setMyLocationStyle(mLocationConfig);
            }
        }
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.location_iv) {
            locationInMap(true);
        }
    }
}
