package com.pine.db_server.remote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.base.request.database.DbRequestBean;
import com.pine.base.request.database.DbResponse;
import com.pine.db_server.sqlite.SQLiteDbRequestManager;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.command.RouterDbCommand;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class DbDataRemoteService {
    @RouterAnnotation(CommandName = RouterDbCommand.callDbServerCommand)
    public DbResponse callDbServerCommand(@NonNull Context context, Bundle args) {
        return SQLiteDbRequestManager.getInstance().callCommand(context,
                (DbRequestBean) args.getSerializable("requestBean"),
                (HashMap<String, HashMap<String, String>>) args.getSerializable("requestHeader"));
    }
}
