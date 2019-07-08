package com.pine.db_server.remote.server;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.db_server.sqlite.SQLiteDbRequestManager;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.command.RouterDbServerCommand;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.request.impl.database.IDbRequestServer;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class DbDataRemoteService {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    @RouterAnnotation(CommandName = RouterDbServerCommand.callDbServerCommand)
    public DbResponse callDbServerCommand(@NonNull Context context, Bundle args) {
        LogUtils.d(TAG, "callDbServerCommand execute");
        return SQLiteDbRequestManager.getInstance().callCommand(context,
                (DbRequestBean) args.getSerializable(IDbRequestServer.requestBeanKey),
                (HashMap<String, String>) args.getSerializable(IDbRequestServer.cookiesKey));
    }
}
