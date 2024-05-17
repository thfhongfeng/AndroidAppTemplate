package com.pine.template.db_server.remote.server;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.db_server.router.RouterDbServerCommand;
import com.pine.template.base.request.impl.dbServer.IDbRequestServer;
import com.pine.template.db_server.IDbServerManager;
import com.pine.template.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.Response;
import com.pine.tool.router.annotation.RouterCommand;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class DbRemoteService {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private IDbServerManager mDbServerManager = SQLiteDbServerManager.getInstance();

    @RouterCommand(CommandName = RouterDbServerCommand.callDbServerCommand)
    public Response callDbServerCommand(@NonNull Context context, Bundle args) {
        LogUtils.d(TAG, "callDbServerCommand execute");
        return mDbServerManager.callCommand(context,
                (RequestBean) args.getSerializable(IDbRequestServer.requestBeanKey),
                (HashMap<String, String>) args.getSerializable(IDbRequestServer.cookiesKey));
    }
}
