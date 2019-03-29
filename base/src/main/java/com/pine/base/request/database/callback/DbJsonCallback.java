package com.pine.base.request.database.callback;

import android.app.Application;
import android.widget.Toast;

import com.pine.base.R;
import com.pine.base.request.database.DbResponse;
import com.pine.tool.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class DbJsonCallback {

    // 该callback对应的db请求标识code
    private int what;
    // 该callback对应的db请求的command
    private int command;
    //模块标识，默认common
    private String moduleTag = "common";

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public void onResponse(int what, DbResponse response) {
        String res = (String) response.getData();
        try {
            JSONObject jsonObject = new JSONObject(res);
            onResponse(what, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            if (!onFail(what, e)) {
                Application application = AppUtils.getApplication();
                Toast.makeText(application, application.getString(R.string.base_json_data_err),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public abstract void onResponse(int what, JSONObject jsonObject);

    public abstract boolean onFail(int what, Exception e);

    public abstract void onCancel(int what);
}
