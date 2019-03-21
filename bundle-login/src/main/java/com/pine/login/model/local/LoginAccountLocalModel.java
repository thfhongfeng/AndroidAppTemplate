package com.pine.login.model.local;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.login.bean.AccountBean;
import com.pine.login.model.ILoginAccountModel;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

public class LoginAccountLocalModel implements ILoginAccountModel {
    @Override
    public void requestRegister(HashMap<String, String> params, @NonNull IModelAsyncResponse<AccountBean> callback) {
        Cursor cursor = new LoginDBHelper(AppUtils.getApplicationContext()).getReadableDatabase().query(LoginDBHelper.ACCOUNT_TABLE_NAME,
                new String[]{"_id,account,password,state,create_time,update_time"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String account = cursor.getString(1);
            String password = cursor.getString(2);
            int state = cursor.getInt(3);
            String create_time = cursor.getString(4);
            String update_time = cursor.getString(5);
            LogUtils.d("LoginDBHelper", "id:" + id + ", account:" + account + ", password:" + password + ", state:" + state + ", create_time:" + create_time + ", update_time:" + update_time);
        }
        cursor.close();
    }
}
