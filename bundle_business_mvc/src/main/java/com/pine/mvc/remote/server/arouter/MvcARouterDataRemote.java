package com.pine.mvc.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvc.remote.server.MvcDataRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2018/9/12
 */

@Route(path = "/mvc/dataService")
public class MvcARouterDataRemote extends ARouterBundleRemote<MvcDataRemoteService> {

}
