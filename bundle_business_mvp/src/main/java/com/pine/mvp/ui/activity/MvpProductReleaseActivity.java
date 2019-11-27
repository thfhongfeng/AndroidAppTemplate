package com.pine.mvp.ui.activity;

import android.app.Dialog;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.access.UiAccessType;
import com.pine.base.access.VipLevel;
import com.pine.base.architecture.mvp.ui.activity.BaseMvpActionBarTextMenuActivity;
import com.pine.base.util.DialogUtils;
import com.pine.base.widget.dialog.DateSelectDialog;
import com.pine.base.widget.dialog.InputTextDialog;
import com.pine.mvp.R;
import com.pine.mvp.contract.IMvpProductReleaseContract;
import com.pine.mvp.presenter.MvpProductReleasePresenter;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.bean.InputParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.VIP_LEVEL},
        AccessArgs = {"", VipLevel.VIP1}, AccessActions = {""})
public class MvpProductReleaseActivity extends
        BaseMvpActionBarTextMenuActivity<IMvpProductReleaseContract.Ui, MvpProductReleasePresenter>
        implements IMvpProductReleaseContract.Ui, View.OnClickListener {
    private SwipeRefreshLayout swipe_refresh_layout;
    private NestedScrollView nested_scroll_view;
    private LinearLayout shelve_date_ll;
    private EditText name_et, description_et, remark_et;
    private TextView price_tv, shelve_price_tv, shelve_date_tv;
    private InputTextDialog mPriceInputDialog, mShelvePriceInputDialog;
    private DateSelectDialog mShelveDateSelectDialog;

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_product_release;
    }

    @Override
    protected void findViewOnCreate() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        shelve_date_ll = findViewById(R.id.shelve_date_ll);
        name_et = findViewById(R.id.name_et);
        price_tv = findViewById(R.id.price_tv);
        shelve_price_tv = findViewById(R.id.shelve_price_tv);
        shelve_date_tv = findViewById(R.id.shelve_date_tv);
        description_et = findViewById(R.id.description_et);
        remark_et = findViewById(R.id.remark_et);
    }

    @Override
    protected void init() {
        shelve_date_ll.setOnClickListener(this);
        price_tv.setOnClickListener(this);
        shelve_price_tv.setOnClickListener(this);
        shelve_date_tv.setOnClickListener(this);

        swipe_refresh_layout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        swipe_refresh_layout.setEnabled(false);
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvp_product_release_title);
        menuBtnTv.setText(R.string.mvp_product_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddProductBtnClicked();
            }
        });
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shelve_date_ll) {
            if (mShelveDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mShelveDateSelectDialog = DialogUtils.createDateSelectDialog(this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                shelve_date_tv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mShelveDateSelectDialog.show();
        } else if (v.getId() == R.id.price_tv) {
            if (mPriceInputDialog == null) {
                mPriceInputDialog = DialogUtils.createTextInputDialog(this, getString(R.string.mvp_product_release_price_hint),
                        "", 12,
                        EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL,
                        new InputTextDialog.IActionClickListener() {

                            @Override
                            public void onSubmitClick(Dialog dialog, List<String> textList) {
                                price_tv.setText(textList.get(0));
                            }

                            @Override
                            public void onCancelClick(Dialog dialog) {

                            }
                        });
            }
            mPriceInputDialog.show();
        } else if (v.getId() == R.id.shelve_price_tv) {
            if (mShelvePriceInputDialog == null) {
                mShelvePriceInputDialog = DialogUtils.createTextInputDialog(this, getString(R.string.mvp_product_release_shelve_price_hint),
                        "", 12,
                        EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL,
                        new InputTextDialog.IActionClickListener() {

                            @Override
                            public void onSubmitClick(Dialog dialog, List<String> textList) {
                                shelve_price_tv.setText(textList.get(0));
                            }

                            @Override
                            public void onCancelClick(Dialog dialog) {

                            }
                        });
            }
            mShelvePriceInputDialog.show();
        }
    }

    private void onAddProductBtnClicked() {
        mPresenter.addProduct();
    }

    @Override
    public InputParam getProductNameParam(String key) {
        return new InputParam(this, key, name_et.getText().toString(),
                nested_scroll_view, name_et);
    }

    @NonNull
    @Override
    public InputParam getProductPriceParam(String key) {
        return new InputParam(this, key, price_tv.getText().toString(),
                nested_scroll_view, price_tv);
    }

    @NonNull
    @Override
    public InputParam getProductShelvePriceParam(String key) {
        return new InputParam(this, key, shelve_price_tv.getText().toString(),
                nested_scroll_view, shelve_price_tv);
    }

    @NonNull
    @Override
    public InputParam getProductShelveDateParam(String key) {
        return new InputParam(this, key, shelve_date_tv.getText().toString(),
                nested_scroll_view, shelve_date_tv);
    }

    @NonNull
    @Override
    public InputParam getProductDescriptionParam(String key) {
        return new InputParam(this, key, description_et.getText().toString(),
                nested_scroll_view, description_et);
    }

    @NonNull
    @Override
    public InputParam getProductRemarkParam(String key) {
        return new InputParam(this, key, remark_et.getText().toString(),
                nested_scroll_view, remark_et);
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
