package com.pine.main.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.main.remote.server.MainRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/main/service")
public class MainARouterRemote extends ARouterBundleRemote<MainRemoteService> {

}
