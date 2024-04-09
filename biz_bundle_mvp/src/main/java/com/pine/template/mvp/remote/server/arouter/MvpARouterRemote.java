package com.pine.template.mvp.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.template.mvp.remote.server.MvpRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/mvp/service")
public class MvpARouterRemote extends ARouterBundleRemote<MvpRemoteService> {

}
