package com.pine.mvvm.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvvm.remote.server.MvvmOpRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/mvvm/opService")
public class MvvmARouterOpRemote extends ARouterBundleRemote<MvvmOpRemoteService> {

}
