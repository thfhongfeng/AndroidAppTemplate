package com.pine.db_server.remote.server;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.base.router.command.RouterDbServerCommand;
import com.pine.db_server.IDbServerManager;
import com.pine.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.request.impl.database.IDbRequestServer;
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
    public DbResponse callDbServerCommand(@NonNull Context context, Bundle args) {
        LogUtils.d(TAG, "callDbServerCommand execute");
        return mDbServerManager.callCommand(context,
                (DbRequestBean) args.getSerializable(IDbRequestServer.requestBeanKey),
                (HashMap<String, String>) args.getSerializable(IDbRequestServer.cookiesKey));
    }
}
