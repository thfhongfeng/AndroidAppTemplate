package com.pine.mvp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.BaseConstants;
import com.pine.base.access.UiAccessAction;
import com.pine.base.access.UiAccessType;
import com.pine.base.access.VipLevel;
import com.pine.base.architecture.mvp.ui.activity.BaseMvpActionBarTextMenuActivity;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.RemoteUploadFileInfo;
import com.pine.base.component.uploader.ui.BaseImageUploadRecycleView;
import com.pine.base.util.DialogUtils;
import com.pine.base.widget.dialog.DateSelectDialog;
import com.pine.base.widget.dialog.InputTextDialog;
import com.pine.base.widget.dialog.ProvinceSelectDialog;
import com.pine.base.widget.dialog.SelectItemDialog;
import com.pine.config.ConfigKey;
import com.pine.mvp.MvpUrlConstants;
import com.pine.mvp.R;
import com.pine.mvp.contract.IMvpShopReleaseContract;
import com.pine.mvp.presenter.MvpShopReleasePresenter;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.bean.InputParam;
import com.pine.tool.util.DecimalUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.CONFIG_SWITCHER, UiAccessType.VIP_LEVEL},
        AccessArgs = {"", ConfigKey.FUN_ADD_SHOP_KEY, VipLevel.VIP1},
        AccessActions = {"", UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_CREATE_SHOW_TOAST})
public class MvpShopReleaseActivity extends
        BaseMvpActionBarTextMenuActivity<IMvpShopReleaseContract.Ui, MvpShopReleasePresenter>
        implements IMvpShopReleaseContract.Ui, View.OnClickListener {
    private final int REQUEST_CODE_BAIDU_MAP = 1;
    private SwipeRefreshLayout swipe_refresh_layout;
    private NestedScrollView nested_scroll_view;
    private LinearLayout type_ll, online_date_ll;
    private EditText name_et, address_street_et, description_et, remark_et;
    private TextView type_tv, online_date_tv, contact_tv, address_district_tv, address_marker_tv;
    private BaseImageUploadRecycleView photo_iuv;
    private InputTextDialog mContactInputDialog;
    private DateSelectDialog mOnLineDateSelectDialog;
    private SelectItemDialog mTypeSelectDialog;
    private ProvinceSelectDialog mProvinceSelectDialog;

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_shop_release;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        type_ll = findViewById(R.id.type_ll);
        online_date_ll = findViewById(R.id.online_date_ll);
        name_et = findViewById(R.id.name_et);
        type_tv = findViewById(R.id.type_tv);
        online_date_tv = findViewById(R.id.online_date_tv);
        contact_tv = findViewById(R.id.contact_tv);
        address_district_tv = findViewById(R.id.address_district_tv);
        address_marker_tv = findViewById(R.id.address_marker_tv);
        address_street_et = findViewById(R.id.address_street_et);
        description_et = findViewById(R.id.description_et);
        remark_et = findViewById(R.id.remark_et);
        photo_iuv = findViewById(R.id.photo_iuv);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        type_ll.setOnClickListener(this);
        online_date_ll.setOnClickListener(this);
        contact_tv.setOnClickListener(this);
        address_district_tv.setOnClickListener(this);
        address_marker_tv.setOnClickListener(this);

        swipe_refresh_layout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        swipe_refresh_layout.setEnabled(false);

        photo_iuv.init(this, true, new FileUploadComponent.OneByOneUploadAdapter() {
            @Override
            public String getUploadUrl() {
                return MvpUrlConstants.Upload_Single_File;
            }

            @Override
            public String getFileKey(FileUploadBean fileUploadBean) {
                // Test code begin
                return "file";
                // Test code end
            }

            @Override
            public Map<String, String> getUploadParam(FileUploadBean fileUploadBean) {
                return mPresenter.makeUploadDefaultParams();
            }

            @Override
            public RemoteUploadFileInfo getRemoteFileInfoFromResponse(FileUploadBean fileUploadBean, JSONObject response) {
                // Test code begin
                if (response == null) {
                    return null;
                }
                if (!response.optBoolean(BaseConstants.SUCCESS)) {
                    return null;
                }
                JSONObject data = response.optJSONObject(BaseConstants.DATA);
                if (data == null) {
                    return null;
                }
                RemoteUploadFileInfo fileInfo = new RemoteUploadFileInfo();
                fileInfo.setUrl(data.optString("fileUrl"));
                return fileInfo;
                // Test code end
            }
        }, 100);
        photo_iuv.setCropEnable(101);
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvp_shop_release_title);
        menuBtnTv.setText(R.string.mvp_shop_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddShopBtnClicked();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mTypeSelectDialog != null && mTypeSelectDialog.isShowing()) {
            mTypeSelectDialog.dismiss();
        }
        if (mContactInputDialog != null && mContactInputDialog.isShowing()) {
            mContactInputDialog.dismiss();
        }
        if (mOnLineDateSelectDialog != null && mOnLineDateSelectDialog.isShowing()) {
            mOnLineDateSelectDialog.dismiss();
        }
        if (mProvinceSelectDialog != null && mProvinceSelectDialog.isShowing()) {
            mProvinceSelectDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BAIDU_MAP) {
            if (resultCode == RESULT_OK) {
                double latitude = DecimalUtils.format(data.getDoubleExtra("latitude", 0d), 6);
                double longitude = DecimalUtils.format(data.getDoubleExtra("longitude", 0d), 6);
                address_marker_tv.setText(latitude + "," + longitude);
                address_marker_tv.setTag(new double[]{latitude, longitude});
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.type_ll) {
            if (mTypeSelectDialog == null) {
                mTypeSelectDialog = DialogUtils.createItemSelectDialog(this, "",
                        mPresenter.getShopTypeNameArr(), 0, new SelectItemDialog.IDialogSelectListener() {
                            @Override
                            public void onSelect(String selectText, int position) {
                                type_tv.setText(selectText);
                                type_tv.setTag(mPresenter.getShopTypeValueArr()[position]);
                            }
                        });
            }
            mTypeSelectDialog.show();
        } else if (v.getId() == R.id.online_date_ll) {
            if (mOnLineDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mOnLineDateSelectDialog = DialogUtils.createDateSelectDialog(this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                online_date_tv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mOnLineDateSelectDialog.show();
        } else if (v.getId() == R.id.contact_tv) {
            if (mContactInputDialog == null) {
                mContactInputDialog = DialogUtils.createTextInputDialog(this, getString(R.string.mvp_shop_release_contact_hint),
                        "", 11,
                        EditorInfo.TYPE_CLASS_NUMBER, new InputTextDialog.IActionClickListener() {

                            @Override
                            public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                                contact_tv.setText(textList.get(0));
                                return false;
                            }

                            @Override
                            public boolean onCancelClick(Dialog dialog) {
                                return false;
                            }
                        });
            }
            mContactInputDialog.show();
        } else if (v.getId() == R.id.address_district_tv) {
            if (mProvinceSelectDialog == null) {
                mProvinceSelectDialog = DialogUtils.createProvinceSelectDialog(this,
                        new ProvinceSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(String provinceName, String cityName,
                                                   String districtName, String zipCode) {
                                address_district_tv.setText(provinceName + cityName + districtName);
                                address_district_tv.setTag(zipCode);
                            }
                        });
            }
            mProvinceSelectDialog.show();
        } else if (v.getId() == R.id.address_marker_tv) {
            String marker = address_marker_tv.getText().toString();
            double[] latLng = new double[2];
            latLng[0] = -1;
            latLng[1] = -1;
            if (!TextUtils.isEmpty(marker)) {
                String[] latLngStr = marker.split(",");
                if (latLngStr.length == 2) {
                    latLng[0] = DecimalUtils.format(latLngStr[0].trim(), 6);
                    latLng[1] = DecimalUtils.format(latLngStr[1].trim(), 6);
                }
            }
            startActivityForResult(MapSdkManager.getMarkMapActivityIntent(this,
                    MapSdkManager.MapType.MAP_TYPE_NORMAL, latLng[0], latLng[1], true),
                    REQUEST_CODE_BAIDU_MAP);
        }
    }

    private void onAddShopBtnClicked() {
        mPresenter.addShop();
    }

    @Override
    public InputParam getShopNameParam(String key) {
        return new InputParam(this, key, name_et.getText().toString(),
                nested_scroll_view, name_et);
    }

    @NonNull
    @Override
    public InputParam getShopTypeParam(String key) {
        return new InputParam(this, key,
                type_tv.getTag() == null ? "" : type_tv.getTag().toString(),
                nested_scroll_view, type_tv);
    }

    @NonNull
    @Override
    public InputParam getShopTypeNameParam(String key) {
        return new InputParam(this, key, type_tv.getText().toString(),
                nested_scroll_view, type_tv);
    }

    @NonNull
    @Override
    public InputParam getShopOnlineDateParam(String key) {
        return new InputParam(this, key, online_date_tv.getText().toString(),
                nested_scroll_view, online_date_tv);
    }

    @NonNull
    @Override
    public InputParam getShopContactMobileParam(String key) {
        return new InputParam(this, key, contact_tv.getText().toString(),
                nested_scroll_view, contact_tv);
    }

    @NonNull
    @Override
    public InputParam getShopAddressParam(String key) {
        return new InputParam(this, key, address_district_tv.getText().toString(),
                nested_scroll_view, address_district_tv);
    }

    @NonNull
    @Override
    public InputParam getShopAddressZipCodeParam(String key) {
        return new InputParam(this,
                key, address_district_tv.getTag() == null ? "" : address_district_tv.getTag().toString(),
                nested_scroll_view, address_district_tv);
    }

    @NonNull
    @Override
    public InputParam getShopLocationLonParam(String key) {
        double[] locationLonLat = (double[]) address_marker_tv.getTag();
        String lon = "";
        if (locationLonLat != null && locationLonLat.length == 2) {
            lon = String.valueOf(locationLonLat[1]);
        }
        return new InputParam(this, key, lon,
                nested_scroll_view, address_marker_tv);
    }

    @NonNull
    @Override
    public InputParam getShopLocationLatParam(String key) {
        double[] locationLonLat = (double[]) address_marker_tv.getTag();
        String lat = "";
        if (locationLonLat != null && locationLonLat.length == 2) {
            lat = String.valueOf(locationLonLat[0]);
        }
        return new InputParam(this, key, lat,
                nested_scroll_view, address_marker_tv);
    }

    @NonNull
    @Override
    public InputParam getShopDetailAddressParam(String key) {
        return new InputParam(this, key, address_street_et.getText().toString(),
                nested_scroll_view, address_street_et);
    }

    @NonNull
    @Override
    public InputParam getShopDescriptionParam(String key) {
        return new InputParam(this, key, description_et.getText().toString(),
                nested_scroll_view, description_et);
    }

    @NonNull
    @Override
    public InputParam getShopRemarkParam(String key) {
        return new InputParam(this, key, remark_et.getText().toString(),
                nested_scroll_view, remark_et);
    }

    @NonNull
    @Override
    public InputParam getShopImagesParam(String key) {
        return new InputParam(this,
                key, photo_iuv.getNewUploadImageRemoteString(","),
                nested_scroll_view, photo_iuv);
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
