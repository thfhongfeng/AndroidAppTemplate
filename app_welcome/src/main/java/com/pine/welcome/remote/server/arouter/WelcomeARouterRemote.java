package com.pine.welcome.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.router.impl.arouter.ARouterBundleRemote;
import com.pine.welcome.remote.server.WelcomeRemoteService;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/wel/service")
public class WelcomeARouterRemote extends ARouterBundleRemote<WelcomeRemoteService> {

}
