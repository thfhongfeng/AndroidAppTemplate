package com.pine.user.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pine.base.access.UiAccessType;
import com.pine.base.architecture.mvp.ui.activity.BaseMvpNoActionBarActivity;
import com.pine.router.IRouterCallback;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.user.R;
import com.pine.user.contract.IUserHomeContract;
import com.pine.user.presenter.UserHomePresenter;
import com.pine.user.remote.UserClientManager;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.VIP_LEVEL}, Args = {"", "1"})
public class UserHomeActivity extends BaseMvpNoActionBarActivity<IUserHomeContract.Ui, UserHomePresenter>
        implements IUserHomeContract.Ui {

    private TextView logout_btn_tv;

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_home;
    }

    @Override
    protected void findViewOnCreate() {
        logout_btn_tv = findViewById(R.id.logout_btn_tv);
    }

    @Override
    protected void init() {
        logout_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClientManager.logout(UserHomeActivity.this, null,
                        new IRouterCallback() {
                            @Override
                            public void onSuccess(Bundle responseBundle) {
                                finish();
                            }

                            @Override
                            public boolean onFail(int failCode, String errorInfo) {
                                return false;
                            }
                        });
            }
        });
    }
}
