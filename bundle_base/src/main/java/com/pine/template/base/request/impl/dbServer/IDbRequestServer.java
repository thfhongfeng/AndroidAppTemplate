package com.pine.template.base.request.impl.dbServer;

import android.os.Bundle;

import com.pine.tool.request.Response;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IDbRequestServer {
    String requestBeanKey = "requestBean";
    String cookiesKey = "cookies";

    Response request(Bundle bundle);
}
