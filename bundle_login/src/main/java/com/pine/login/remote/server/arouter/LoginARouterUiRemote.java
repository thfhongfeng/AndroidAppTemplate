package com.pine.login.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.login.remote.server.LoginUiRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2018/9/12
 */

@Route(path = "/login/uiService")
public class LoginARouterUiRemote extends ARouterBundleRemote<LoginUiRemoteService> {

}
