package com.pine.tool.request.impl.database;

import android.os.Bundle;

public interface IDbRequestServer {
    String requestBeanKey = "requestBean";
    String cookiesKey = "cookies";

    DbResponse request(Bundle bundle);
}
