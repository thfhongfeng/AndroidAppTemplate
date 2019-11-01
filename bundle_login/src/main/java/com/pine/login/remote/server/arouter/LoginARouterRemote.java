package com.pine.login.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.login.remote.server.LoginRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2018/9/12
 */

@Route(path = "/login/service")
public class LoginARouterRemote extends ARouterBundleRemote<LoginRemoteService> {

}
