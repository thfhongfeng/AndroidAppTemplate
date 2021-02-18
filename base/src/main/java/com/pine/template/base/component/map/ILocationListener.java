package com.pine.template.base.component.map;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public interface ILocationListener {
    void onReceiveLocation(LocationInfo locationInfo);

    void onReceiveFail();
}
