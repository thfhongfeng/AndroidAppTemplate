package com.pine.user.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.router.impl.arouter.ARouterBundleRemote;
import com.pine.user.remote.server.UserUiRemoteService;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/user/uiService")
public class UserARouterUiRemote extends ARouterBundleRemote<UserUiRemoteService> {

}
