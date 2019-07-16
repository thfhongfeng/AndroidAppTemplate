package com.pine.main.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.main.remote.server.MainDataRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/main/dataService")
public class MainARouterDataRemote extends ARouterBundleRemote<MainDataRemoteService> {

}
