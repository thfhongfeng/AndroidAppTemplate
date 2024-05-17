package com.pine.template.db_server;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pine.template.base.request.impl.dbServer.DbRequestBean;
import com.pine.template.base.request.impl.dbServer.DbResponse;

import java.util.HashMap;

public interface IDbServerManager {
    DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                           HashMap<String, String> header);
}
