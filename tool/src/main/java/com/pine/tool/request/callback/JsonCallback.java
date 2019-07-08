package com.pine.tool.request.callback;

import android.app.Application;
import android.widget.Toast;

import com.pine.tool.R;
import com.pine.tool.request.Response;
import com.pine.tool.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class JsonCallback extends AbstractBaseCallback {

    public void onResponse(int what, Response response) {
        String res = (String) response.getData();
        try {
            JSONObject jsonObject = new JSONObject(res);
            onResponse(what, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            if (!onFail(what, e)) {
                Application application = AppUtils.getApplication();
                Toast.makeText(application, application.getString(R.string.tool_json_data_err),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public abstract void onResponse(int what, JSONObject jsonObject);

    public abstract boolean onFail(int what, Exception e);

    public abstract void onCancel(int what);
}
