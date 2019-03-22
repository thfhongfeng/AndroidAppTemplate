package com.pine.base.database.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.pine.base.database.DbRequestBean;
import com.pine.base.database.DbResponse;
import com.pine.base.database.DbResponseGenerator;
import com.pine.base.database.IDbCommand;
import com.pine.base.database.IDbRequestManager;
import com.pine.base.database.sqlite.server.SQLiteLoginServer;
import com.pine.base.database.sqlite.server.SQLiteWelcomeServer;

import java.util.Map;

public class SQLiteDbRequestManager implements IDbRequestManager {

    public SQLiteDbRequestManager(@NonNull Context context) {
        new SQLiteDbHelper(context).getReadableDatabase();
    }

    @Override
    public boolean execSQL(@NonNull Context context, String sql) {
        try {
            new SQLiteDbHelper(context).getWritableDatabase().execSQL(sql);
            return true;
        } catch (SQLException e) {

        }
        return false;
    }

    @NonNull
    @Override
    public DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                  Map<String, Map<String, String>> header) {
        switch (requestBean.getCommand()) {
            case IDbCommand.REQUEST_CONFIG_SWITCHER:
                return SQLiteWelcomeServer.queryConfigSwitcher(context, requestBean, header);
            case IDbCommand.REQUEST_APP_VERSION:
                return SQLiteWelcomeServer.queryAppVersion(context, requestBean, header);
            case IDbCommand.REQUEST_LOGIN:
                return SQLiteLoginServer.login(context, requestBean, header);
            case IDbCommand.REQUEST_LOGOUT:
                return SQLiteLoginServer.logout(context, requestBean, header);
            case IDbCommand.REQUEST_REGISTER:
                return SQLiteLoginServer.register(context, requestBean, header);
        }
        return DbResponseGenerator.getNoSuchTableRep(requestBean, header);
    }
}
