package com.pine.db_server.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.pine.base.request.database.DbRequestBean;
import com.pine.base.request.database.DbResponse;
import com.pine.base.request.database.IDbRequestManager;
import com.pine.db_server.IDbCommand;
import com.pine.db_server.sqlite.server.SQLiteLoginServer;
import com.pine.db_server.sqlite.server.SQLiteShopServer;
import com.pine.db_server.sqlite.server.SQLiteTravelNoteServer;
import com.pine.db_server.sqlite.server.SQLiteWelcomeServer;
import com.pine.tool.util.AppUtils;

import java.util.HashMap;

public class SQLiteDbRequestManager implements IDbRequestManager {

    private static volatile SQLiteDbRequestManager mInstance;

    private SQLiteDbRequestManager(@NonNull Context context) {
        new SQLiteDbHelper(context).getReadableDatabase();
    }

    public synchronized static SQLiteDbRequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new SQLiteDbRequestManager(AppUtils.getApplicationContext());
        }
        return mInstance;
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
                                  HashMap<String, HashMap<String, String>> header) {
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
            case IDbCommand.REQUEST_ADD_SHOP:
                return SQLiteShopServer.addShop(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_SHOP_DETAIL:
                return SQLiteShopServer.queryShopDetail(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_SHOP_LIST:
                return SQLiteShopServer.queryShopList(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_SHOP_AND_PRODUCT_LIST:
                return SQLiteShopServer.queryShopProductList(context, requestBean, header);
            case IDbCommand.REQUEST_ADD_TRAVEL_NOTE:
                return SQLiteTravelNoteServer.addTravelNote(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_DETAIL:
                return SQLiteTravelNoteServer.queryTravelNoteDetail(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_LIST:
                return SQLiteTravelNoteServer.queryTravelNoteList(context, requestBean, header);
            case IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST:
                return SQLiteTravelNoteServer.queryTravelNoteCommentList(context, requestBean, header);
        }
        return DbResponseGenerator.getNoSuchTableRep(requestBean, header);
    }
}
