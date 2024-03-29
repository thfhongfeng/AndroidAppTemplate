package com.pine.template.user.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.template.user.remote.server.UserRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/user/service")
public class UserARouterRemote extends ARouterBundleRemote<UserRemoteService> {

}
