package com.pine.app.jni.listener;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class RequestListener implements IRequestListener {

    public abstract void onResponse(String action, @NonNull String data);

    public abstract void onFail(String action, int errCode);

    @Override
    public void onJniResponse(String action, String msg) {
        int errCode = IRequestListener.ERR_CODE_DEFAULT;
        boolean success = false;
        String data = "";
        if (msg != null) {
            try {
                JSONObject msgObj = new JSONObject(msg);
                success = msgObj.optBoolean(IRequestListener.KEY_SUCCESS);
                errCode = msgObj.optInt(IRequestListener.KEY_CODE);
                data = msgObj.optString(IRequestListener.KEY_DATA);
            } catch (JSONException e) {
                errCode = IRequestListener.ERR_CODE_DATA_FORMAT_EXCEPTION;
            }
        }
        if (success) {
            onResponse(action, data);
        } else {
            onFail(action, errCode);
        }
    }

    @Override
    public void onJniFail(String action, int errCode) {
        onFail(action, errCode);
    }
}
