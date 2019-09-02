package com.pine.tool.request.impl.database;

import android.os.Bundle;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IDbRequestServer {
    String requestBeanKey = "requestBean";
    String cookiesKey = "cookies";

    DbResponse request(Bundle bundle);
}
