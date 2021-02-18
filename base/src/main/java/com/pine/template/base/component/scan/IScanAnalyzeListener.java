package com.pine.template.base.component.scan;

import android.graphics.Bitmap;

/**
 * Created by tanghongfeng on 2019/11/28.
 */

/**
 * 解析二维码结果
 */
public interface IScanAnalyzeListener {
    void onAnalyzeSuccess(Bitmap bitmap, String result);

    boolean onAnalyzeFailed();
}
