package com.pine.template.mvvm.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.app.template.biz_bundle_mvvm.BuildConfigKey;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarTextMenuActivity;
import com.pine.template.base.business.access.UiAccessAction;
import com.pine.template.base.business.access.UiAccessType;
import com.pine.template.base.business.access.VipLevel;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.DateSelectDialog;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmProductDetailEntity;
import com.pine.template.mvvm.databinding.MvvmProductReleaseActivityBinding;
import com.pine.template.mvvm.vm.MvvmProductReleaseVm;
import com.pine.tool.access.UiAccessAnnotation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.CONFIG_SWITCHER, UiAccessType.VIP_LEVEL},
        AccessArgs = {"", BuildConfigKey.FUN_ADD_SHOP, VipLevel.VIP1},
        AccessActions = {"", UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_CREATE_SHOW_TOAST})
public class MvvmProductReleaseActivity extends
        BaseMvvmActionBarTextMenuActivity<MvvmProductReleaseActivityBinding, MvvmProductReleaseVm> {
    private InputTextDialog mPriceInputDialog, mShelvePriceInputDialog;
    private DateSelectDialog mShelveDateSelectDialog;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_product_release;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        bindingData();
        initView();
    }

    private void bindingData() {
        mBinding.setPresenter(new Presenter());

        mViewModel.getProductDetailData().observe(this, new Observer<MvvmProductDetailEntity>() {
            @Override
            public void onChanged(@Nullable MvvmProductDetailEntity mvvmProductDetailEntity) {
                mBinding.setProductDetail(mvvmProductDetailEntity);
            }
        });
    }

    private void initView() {
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        mBinding.swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvvm_product_release_title);
        menuBtnTv.setText(R.string.mvvm_product_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddBtnClicked();
            }
        });
    }

    private void onAddBtnClicked() {
        mViewModel.addProduct();
    }

    @Override
    public void onDestroy() {
        if (mPriceInputDialog != null && mPriceInputDialog.isShowing()) {
            mPriceInputDialog.dismiss();
        }
        if (mShelvePriceInputDialog != null && mShelvePriceInputDialog.isShowing()) {
            mShelvePriceInputDialog.dismiss();
        }
        if (mShelveDateSelectDialog != null && mShelveDateSelectDialog.isShowing()) {
            mShelveDateSelectDialog.dismiss();
        }
        super.onDestroy();
    }

    public class Presenter {
        public void onPriceTvClick(View view) {
            if (mPriceInputDialog == null) {
                mPriceInputDialog = DialogUtils.createTextInputDialog(MvvmProductReleaseActivity.this,
                        getString(R.string.mvvm_product_release_price_hint),
                        "", 12,
                        EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL,
                        new InputTextDialog.IActionClickListener() {

                            @Override
                            public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                                mBinding.priceTv.setText(textList.get(0));
                                return false;
                            }

                            @Override
                            public boolean onCancelClick(Dialog dialog) {
                                return false;
                            }
                        });
            }
            mPriceInputDialog.show();
        }

        public void onShelvePriceTvClick(View view) {
            if (mShelvePriceInputDialog == null) {
                mShelvePriceInputDialog = DialogUtils.createTextInputDialog(MvvmProductReleaseActivity.this,
                        getString(R.string.mvvm_product_release_shelve_price_hint),
                        "", 12,
                        EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL,
                        new InputTextDialog.IActionClickListener() {

                            @Override
                            public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                                mBinding.shelvePriceTv.setText(textList.get(0));
                                return false;
                            }

                            @Override
                            public boolean onCancelClick(Dialog dialog) {
                                return false;
                            }
                        });
            }
            mShelvePriceInputDialog.show();
        }

        public void onShelveDateSelectorClick(View view) {
            if (mShelveDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mShelveDateSelectDialog = DialogUtils.createDateSelectDialog(MvvmProductReleaseActivity.this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                mBinding.shelveDateTv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mShelveDateSelectDialog.show();
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
