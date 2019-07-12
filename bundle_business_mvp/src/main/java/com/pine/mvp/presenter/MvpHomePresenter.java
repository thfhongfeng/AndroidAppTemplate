package com.pine.mvp.presenter;

import android.content.Intent;

import com.pine.mvp.contract.IMvpHomeContract;
import com.pine.mvp.ui.activity.MvpShopReleaseActivity;
import com.pine.tool.architecture.mvp.presenter.Presenter;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpHomePresenter extends Presenter<IMvpHomeContract.Ui>
        implements IMvpHomeContract.Presenter {

    @Override
    public void goToAddShopActivity() {
        Intent intent = new Intent(getContext(), MvpShopReleaseActivity.class);
        getContext().startActivity(intent);
    }
}
