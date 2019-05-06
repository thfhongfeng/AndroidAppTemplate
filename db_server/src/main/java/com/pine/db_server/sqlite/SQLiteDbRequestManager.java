package com.pine.db_server.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.pine.base.request.impl.database.DbRequestBean;
import com.pine.base.request.impl.database.DbResponse;
import com.pine.db_server.DbUrlConstants;
import com.pine.db_server.sqlite.server.SQLiteFileServer;
import com.pine.db_server.sqlite.server.SQLiteLoginServer;
import com.pine.db_server.sqlite.server.SQLiteShopServer;
import com.pine.db_server.sqlite.server.SQLiteTravelNoteServer;
import com.pine.db_server.sqlite.server.SQLiteWelcomeServer;
import com.pine.tool.util.AppUtils;

import java.util.HashMap;

public class SQLiteDbRequestManager {

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

    public boolean execSQL(@NonNull Context context, String sql) {
        try {
            new SQLiteDbHelper(context).getWritableDatabase().execSQL(sql);
            return true;
        } catch (SQLException e) {

        }
        return false;
    }

    @NonNull
    public DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                  HashMap<String, String> header) {
        if (DbUrlConstants.Query_BundleSwitcher_Data.equals(requestBean.getUrl())) {
            return SQLiteWelcomeServer.queryConfigSwitcher(context, requestBean, header);
        } else if (DbUrlConstants.Query_Version_Data.equals(requestBean.getUrl())) {
            return SQLiteWelcomeServer.queryAppVersion(context, requestBean, header);
        } else if (DbUrlConstants.Login.equals(requestBean.getUrl())) {
            return SQLiteLoginServer.login(context, requestBean, header);
        } else if (DbUrlConstants.Logout.equals(requestBean.getUrl())) {
            return SQLiteLoginServer.logout(context, requestBean, header);
        } else if (DbUrlConstants.Register_Account.equals(requestBean.getUrl())) {
            return SQLiteLoginServer.register(context, requestBean, header);
        } else if (DbUrlConstants.Add_Shop.equals(requestBean.getUrl())) {
            return SQLiteShopServer.addShop(context, requestBean, header);
        } else if (DbUrlConstants.Query_ShopDetail.equals(requestBean.getUrl())) {
            return SQLiteShopServer.queryShopDetail(context, requestBean, header);
        } else if (DbUrlConstants.Query_ShopList.equals(requestBean.getUrl())) {
            return SQLiteShopServer.queryShopList(context, requestBean, header);
        } else if (DbUrlConstants.Query_ShopAndProductList.equals(requestBean.getUrl())) {
            return SQLiteShopServer.queryShopProductList(context, requestBean, header);
        } else if (DbUrlConstants.Add_TravelNote.equals(requestBean.getUrl())) {
            return SQLiteTravelNoteServer.addTravelNote(context, requestBean, header);
        } else if (DbUrlConstants.Query_TravelNoteDetail.equals(requestBean.getUrl())) {
            return SQLiteTravelNoteServer.queryTravelNoteDetail(context, requestBean, header);
        } else if (DbUrlConstants.Query_TravelNoteList.equals(requestBean.getUrl())) {
            return SQLiteTravelNoteServer.queryTravelNoteList(context, requestBean, header);
        } else if (DbUrlConstants.Query_TravelNoteCommentList.equals(requestBean.getUrl())) {
            return SQLiteTravelNoteServer.queryTravelNoteCommentList(context, requestBean, header);
        } else if (DbUrlConstants.Upload_Single_File.equals(requestBean.getUrl())) {
            return SQLiteFileServer.uploadSingleFile(context, requestBean, header);
        } else if (DbUrlConstants.Upload_Multi_File.equals(requestBean.getUrl())) {
            return SQLiteFileServer.uploadMultiFile(context, requestBean, header);
        } else {
            return DbResponseGenerator.getNoSuchTableJsonRep(requestBean, header);
        }
    }
}
