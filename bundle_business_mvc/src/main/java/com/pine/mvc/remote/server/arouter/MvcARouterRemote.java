package com.pine.mvc.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvc.remote.server.MvcRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2018/9/12
 */

@Route(path = "/mvc/service")
public class MvcARouterRemote extends ARouterBundleRemote<MvcRemoteService> {

}
