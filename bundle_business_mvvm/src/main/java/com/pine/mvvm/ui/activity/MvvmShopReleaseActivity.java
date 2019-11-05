package com.pine.mvvm.ui.activity;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.BaseConstants;
import com.pine.base.access.UiAccessType;
import com.pine.base.access.VipLevel;
import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarTextMenuActivity;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.RemoteUploadFileInfo;
import com.pine.base.util.DialogUtils;
import com.pine.base.widget.dialog.DateSelectDialog;
import com.pine.base.widget.dialog.InputTextDialog;
import com.pine.base.widget.dialog.ProvinceSelectDialog;
import com.pine.base.widget.dialog.SelectItemDialog;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.databinding.MvvmShopReleaseActivityBinding;
import com.pine.mvvm.vm.MvvmShopReleaseVm;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.util.DecimalUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.VIP_LEVEL},
        AccessArgs = {"", VipLevel.VIP1}, AccessActions = {""})
public class MvvmShopReleaseActivity extends
        BaseMvvmActionBarTextMenuActivity<MvvmShopReleaseActivityBinding, MvvmShopReleaseVm> {
    private final int REQUEST_CODE_BAIDU_MAP = 1;
    private InputTextDialog mContactInputDialog;
    private DateSelectDialog mOnLineDateSelectDialog;
    private SelectItemDialog mTypeSelectDialog;
    private ProvinceSelectDialog mProvinceSelectDialog;

    @Override
    public void observeInitLiveData() {
        mViewModel.getShopDetailData().observe(this, new Observer<MvvmShopDetailEntity>() {
            @Override
            public void onChanged(@Nullable MvvmShopDetailEntity mvvmShopDetailEntity) {
                mBinding.setShopDetail(mvvmShopDetailEntity);
            }
        });
        mViewModel.getShopTypeNameArrData().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] strings) {
                if (strings != null && strings.length > 0) {
                    if (mTypeSelectDialog != null) {
                        mTypeSelectDialog.dismiss();
                    }
                    mTypeSelectDialog = DialogUtils.createItemSelectDialog(MvvmShopReleaseActivity.this, "",
                            strings, 0, new SelectItemDialog.IDialogSelectListener() {
                                @Override
                                public void onSelect(String selectText, int position) {
                                    mBinding.typeTv.setText(selectText);
                                    mBinding.typeTv.setData(mViewModel.getShopTypeArrData().getValue()[position]);
                                }
                            });
                }
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_shop_release;
    }

    @Override
    protected void init() {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        mBinding.swipeRefreshLayout.setEnabled(false);

        mBinding.photoIuv.init(this, true, new FileUploadComponent.OneByOneUploadAdapter() {
            @Override
            public String getUploadUrl() {
                return MvvmUrlConstants.Upload_Single_File;
            }

            @Override
            public String getFileKey(FileUploadBean fileUploadBean) {
                // Test code begin
                return "file";
                // Test code end
            }

            @Override
            public Map<String, String> getUploadParam(FileUploadBean fileUploadBean) {
                return mViewModel.makeUploadDefaultParams();
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
        mBinding.photoIuv.setCropEnable(101);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvvm_shop_release_title);
        menuBtnTv.setText(R.string.mvvm_shop_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddShopBtnClicked();
            }
        });
    }

    private void onAddShopBtnClicked() {
        mViewModel.addShop();
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
                mBinding.addressMarkerTv.setText(latitude + "," + longitude);
                mBinding.addressMarkerTv.setData1(String.valueOf(latitude));
                mBinding.addressMarkerTv.setData2(String.valueOf(longitude));
            }
        }
    }

    public class Presenter {
        public void onTypeSelectorClick(View view) {
            if (mTypeSelectDialog != null) {
                mTypeSelectDialog.show();
            }
        }

        public void onOnlineDateSelectorClick(View view) {
            if (mOnLineDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mOnLineDateSelectDialog = DialogUtils.createDateSelectDialog(MvvmShopReleaseActivity.this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                mBinding.onlineDateTv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mOnLineDateSelectDialog.show();
        }

        public void onContactTvClick(View view) {
            if (mContactInputDialog == null) {
                mContactInputDialog = DialogUtils.createTextInputDialog(MvvmShopReleaseActivity.this,
                        getString(R.string.mvvm_shop_release_contact_hint),
                        "", 11,
                        EditorInfo.TYPE_CLASS_NUMBER, new InputTextDialog.IActionClickListener() {

                            @Override
                            public void onSubmitClick(Dialog dialog, List<String> textList) {
                                mBinding.contactTv.setText(textList.get(0));
                            }

                            @Override
                            public void onCancelClick(Dialog dialog) {

                            }
                        });
            }
            mContactInputDialog.show();
        }

        public void onAddressSelectorClick(View view) {
            if (mProvinceSelectDialog == null) {
                mProvinceSelectDialog = DialogUtils.createProvinceSelectDialog(MvvmShopReleaseActivity.this,
                        new ProvinceSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(String provinceName, String cityName,
                                                   String districtName, String zipCode) {
                                mBinding.addressDistrictTv.setText(provinceName + cityName + districtName);
                                mBinding.addressDistrictTv.setData(zipCode);
                            }
                        });
            }
            mProvinceSelectDialog.show();
        }

        public void onAddressMarkerTvClick(View view) {
            String marker = mBinding.addressMarkerTv.getText().toString();
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
            startActivityForResult(MapSdkManager.getInstance().getMarkMapActivityIntent(
                    MvvmShopReleaseActivity.this, latLng[0], latLng[1], true),
                    REQUEST_CODE_BAIDU_MAP);
        }
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (mBinding.swipeRefreshLayout == null) {
            return;
        }
        mBinding.swipeRefreshLayout.setRefreshing(processing);
    }
}
