package com.pine.template.base.component.scan;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by tanghongfeng on 2019/11/28.
 */

public interface IScanManager {
    void init(Context context);

    void setAnalyzeListener(IScanAnalyzeListener listener, @IdRes int surfaceContainerId);

    void attachScanSurface(FragmentActivity activity,
                           final IScanAnalyzeListener listener, @IdRes int surfaceContainerId);

    void detachScanSurface(@IdRes int surfaceContainerId);
}
