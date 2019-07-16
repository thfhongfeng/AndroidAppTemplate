package com.pine.db_server.remote.server.remote;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.db_server.remote.server.DbDataRemoteService;
import com.pine.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/db/dataService")
public class DbARouterDataRemote extends ARouterBundleRemote<DbDataRemoteService> {

}
