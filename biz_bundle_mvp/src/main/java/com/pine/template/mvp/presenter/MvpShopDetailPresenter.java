package com.pine.template.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvp.MvpUrlConstants;
import com.pine.template.mvp.bean.MvpShopDetailEntity;
import com.pine.template.mvp.contract.IMvpShopDetailContract;
import com.pine.template.mvp.model.MvpShopModel;
import com.pine.template.mvp.ui.activity.MvpProductReleaseActivity;
import com.pine.template.mvp.ui.activity.MvpTravelNoteListActivity;
import com.pine.template.mvp.ui.activity.MvpWebViewActivity;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;
import com.pine.tool.util.DecimalUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpShopDetailPresenter extends Presenter<IMvpShopDetailContract.Ui>
        implements IMvpShopDetailContract.Presenter {
    private String mId;
    private MvpShopModel mShopModel;
    private MvpShopDetailEntity mShopDetailEntity;

    public MvpShopDetailPresenter() {
        mShopModel = new MvpShopModel();
    }

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mId)) {
            finishUi();
            return true;
        }
        return false;
    }

    @Override
    public void loadShopDetailData() {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        setUiLoading(true);
        mShopModel.requestShopDetailData(params, new IAsyncResponse<MvpShopDetailEntity>() {
            @Override
            public void onResponse(MvpShopDetailEntity entity) {
                setUiLoading(false);
                if (isUiAlive()) {
                    mShopDetailEntity = entity;
                    getUi().setupShopDetail(entity);
                }
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        showShortToast(e.getMessage());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    @Override
    public void showMarkerInMap() {
        if (mShopDetailEntity == null ||
                TextUtils.isEmpty(mShopDetailEntity.getLatitude()) ||
                TextUtils.isEmpty(mShopDetailEntity.getLongitude())) {
            return;
        }
        Intent intent = MapSdkManager.getMarkMapActivityIntent(
                getContext(),
                DecimalUtils.format(mShopDetailEntity.getLatitude().trim(), 6),
                DecimalUtils.format(mShopDetailEntity.getLongitude().trim(), 6), false);
        if (intent != null) {
            getContext().startActivity(intent);
        }
    }

    @Override
    public void goToShopH5Activity() {
        Intent intent = new Intent(getContext(), MvpWebViewActivity.class);
        intent.putExtra("url", MvpUrlConstants.H5_DefaultUrl());
        getContext().startActivity(intent);
    }

    @Override
    public void goToTravelNoteListActivity() {
        Intent intent = new Intent(getContext(), MvpTravelNoteListActivity.class);
        intent.putExtra("id", mId);
        getContext().startActivity(intent);
    }

    @Override
    public void goAddProductActivity() {
        Intent intent = new Intent(getContext(), MvpProductReleaseActivity.class);
        intent.putExtra("id", mId);
        getContext().startActivity(intent);
    }
}
