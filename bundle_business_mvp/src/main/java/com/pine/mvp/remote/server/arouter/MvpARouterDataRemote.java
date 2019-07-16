package com.pine.mvp.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvp.remote.server.MvpDataRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/mvp/dataService")
public class MvpARouterDataRemote extends ARouterBundleRemote<MvpDataRemoteService> {

}
