package com.pine.base.component.scan;

import android.content.Context;

import com.pine.base.component.map.IMapManager;

/**
 * Created by tanghongfeng on 2019/11/20.
 */

public interface IScanManagerFactory {
    IScanManager makeScanManager(Context context);
}
