package com.pine.template.face.remote.server.arouter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.template.face.remote.server.FaceRemoteService;
import com.pine.tool.router.impl.arouter.ARouterBundleRemote;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/face/service")
public class FaceARouterRemote extends ARouterBundleRemote<FaceRemoteService> {

}
