package com.pine.template.welcome.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.template.welcome.remote.server.WelcomeRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/wel/service")
public class WelcomeARouterRemote extends ARouterBundleRemote<WelcomeRemoteService> {

}
