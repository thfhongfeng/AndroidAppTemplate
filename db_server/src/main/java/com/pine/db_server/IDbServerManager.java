package com.pine.db_server;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import java.util.HashMap;

public interface IDbServerManager {
    DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                           HashMap<String, String> header);
}
