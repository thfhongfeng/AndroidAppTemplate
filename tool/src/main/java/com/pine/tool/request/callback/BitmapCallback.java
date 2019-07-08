package com.pine.tool.request.callback;

import android.graphics.Bitmap;

import com.pine.tool.request.Response;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public abstract class BitmapCallback extends AbstractBaseCallback {

    public void onResponse(int what, Response response) {
        Bitmap bitmap = (Bitmap) response.getData();
        onResponse(what, bitmap);
    }

    public abstract void onResponse(int what, Bitmap bitmap);

    public abstract boolean onError(int what, Exception e);

    public abstract void onCancel(int what);
}
