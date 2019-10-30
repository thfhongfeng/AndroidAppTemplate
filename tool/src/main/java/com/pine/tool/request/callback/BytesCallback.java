package com.pine.tool.request.callback;

import android.app.Application;
import android.widget.Toast;

import com.pine.tool.R;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.Response;
import com.pine.tool.util.AppUtils;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class BytesCallback extends DataResponseCallback {

    @Override
    public void onResponse(int what, Response response) {
        if (response.getData() instanceof byte[]) {
            byte[] res = (byte[]) response.getData();
            onResponse(what, res, response);
        } else {
            Application application = AppUtils.getApplication();
            String errMsg = application.getString(R.string.tool_data_type_err);
            if (!onFail(what, new MessageException(errMsg), response)) {
                Toast.makeText(application, errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public abstract void onResponse(int what, byte[] bytes, Response response);
}
