package com.pine.mvvm.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvvm.remote.server.MvvmRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/mvvm/service")
public class MvvmARouterRemote extends ARouterBundleRemote<MvvmRemoteService> {

}
