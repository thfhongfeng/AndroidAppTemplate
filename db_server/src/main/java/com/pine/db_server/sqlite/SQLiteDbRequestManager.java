package com.pine.db_server.sqlite;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.db_server.DbSession;
import com.pine.db_server.DbUrlConstants;
import com.pine.db_server.sqlite.server.SQLiteFileServer;
import com.pine.db_server.sqlite.server.SQLiteLoginServer;
import com.pine.db_server.sqlite.server.SQLiteShopServer;
import com.pine.db_server.sqlite.server.SQLiteTravelNoteServer;
import com.pine.db_server.sqlite.server.SQLiteWelcomeServer;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.AppUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteDbRequestManager {

    private static volatile SQLiteDbRequestManager mInstance;
    private static volatile HashMap<String, DbSession> mSessionMap = new HashMap<>();

    private SQLiteDbRequestManager(@NonNull Context context) {
        new SQLiteDbHelper(context).getReadableDatabase();
    }

    public synchronized static SQLiteDbRequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new SQLiteDbRequestManager(AppUtils.getApplicationContext());
        }
        return mInstance;
    }

    public DbSession getOrGenerateSession(String sessionId) {
        synchronized (mSessionMap) {
            DbSession session = mSessionMap.get(sessionId);
            if (session == null) {
                session = new DbSession(sessionId);
                mSessionMap.put(sessionId, session);
            }
            return session;
        }
    }

    public void removeSession(String sessionId) {
        synchronized (mSessionMap) {
            mSessionMap.remove(sessionId);
        }
    }

    public String generateSessionId() {
        return Calendar.getInstance().getTimeInMillis() + "" + new Random().nextInt(10000);
    }

    public String generateSessionId(String userId) {
        return Calendar.getInstance().getTimeInMillis() + userId;
    }

    @NonNull
    public synchronized DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                               HashMap<String, String> header) {
        if (TextUtils.isEmpty(header.get(SESSION_ID))) {
            String sessionId = generateSessionId();
            header.put(SESSION_ID, sessionId);
            getOrGenerateSession(sessionId);
        }
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
        } else if (DbUrlConstants.Add_Product.equals(requestBean.getUrl())) {
            return SQLiteShopServer.addProduct(context, requestBean, header);
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
