package com.pine.mvvm.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvvm.remote.server.MvvmDataRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/mvvm/dataService")
public class MvvmARouterDataRemote extends ARouterBundleRemote<MvvmDataRemoteService> {

}
