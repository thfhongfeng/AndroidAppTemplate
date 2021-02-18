package com.pine.template.db_server.sqlite;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.db_server.DbResponseGenerator;
import com.pine.template.db_server.DbSession;
import com.pine.template.db_server.DbUrlConstants;
import com.pine.template.db_server.IDbServerManager;
import com.pine.template.db_server.sqlite.server.SQLiteFileServer;
import com.pine.template.db_server.sqlite.server.SQLiteLoginServer;
import com.pine.template.db_server.sqlite.server.SQLiteShopServer;
import com.pine.template.db_server.sqlite.server.SQLiteTravelNoteServer;
import com.pine.template.db_server.sqlite.server.SQLiteWelcomeServer;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteDbServerManager implements IDbServerManager {
    private static final String TAG = LogUtils.makeLogTag(SQLiteDbServerManager.class);

    private static volatile SQLiteDbServerManager mInstance;
    private static volatile HashMap<String, DbSession> mSessionMap = new HashMap<>();

    private SQLiteDbServerManager(@NonNull Context context) {
        new SQLiteDbHelper(context).getReadableDatabase();
    }

    public synchronized static SQLiteDbServerManager getInstance() {
        if (mInstance == null) {
            mInstance = new SQLiteDbServerManager(AppUtils.getApplicationContext());
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

    public String generateSessionId(String accountId) {
        return Calendar.getInstance().getTimeInMillis() + accountId;
    }

    @Override
    @NonNull
    public DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                  HashMap<String, String> header) {
        LogUtils.d(TAG, "callCommand url:" + requestBean.getUrl());
        if (DbUrlConstants.Query_BundleSwitcher_Data.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteWelcomeServer.queryConfigSwitcher(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_Version_Data.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteWelcomeServer.queryAppVersion(context, requestBean, header);
            }
        } else if (DbUrlConstants.Login.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteLoginServer.login(context, requestBean, header);
            }
        } else if (DbUrlConstants.Logout.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteLoginServer.logout(context, requestBean, header);
            }
        } else if (requestBean.getUrl() != null && requestBean.getUrl().startsWith(DbUrlConstants.Verify_Code_Image)) {
            synchronized (this) {
                setupSession(header);
                return SQLiteLoginServer.getVerifyCode(context, requestBean, header);
            }
        } else if (DbUrlConstants.Register_Account.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteLoginServer.register(context, requestBean, header);
            }
        } else if (DbUrlConstants.Add_Shop.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteShopServer.addShop(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_ShopDetail.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteShopServer.queryShopDetail(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_ShopList.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteShopServer.queryShopList(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_ShopAndProductList.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteShopServer.queryShopProductList(context, requestBean, header);
            }
        } else if (DbUrlConstants.Add_Product.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteShopServer.addProduct(context, requestBean, header);
            }
        } else if (DbUrlConstants.Add_TravelNote.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteTravelNoteServer.addTravelNote(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_TravelNoteDetail.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteTravelNoteServer.queryTravelNoteDetail(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_TravelNoteList.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteTravelNoteServer.queryTravelNoteList(context, requestBean, header);
            }
        } else if (DbUrlConstants.Query_TravelNoteCommentList.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteTravelNoteServer.queryTravelNoteCommentList(context, requestBean, header);
            }
        } else if (DbUrlConstants.Upload_Single_File.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteFileServer.uploadSingleFile(context, requestBean, header);
            }
        } else if (DbUrlConstants.Upload_Multi_File.equals(requestBean.getUrl())) {
            synchronized (this) {
                setupSession(header);
                return SQLiteFileServer.uploadMultiFile(context, requestBean, header);
            }
        } else if (requestBean.getRequestType() == IRequestManager.RequestType.BITMAP) {
            return DbResponseGenerator.getSuccessUrlBitmapBytesRep(requestBean, header, requestBean.getUrl());
        } else {
            synchronized (this) {
                setupSession(header);
                return DbResponseGenerator.getNoSuchTableJsonRep(requestBean, header);
            }
        }
    }

    private synchronized void setupSession(HashMap<String, String> header) {
        if (TextUtils.isEmpty(header.get(SESSION_ID))) {
            String sessionId = generateSessionId();
            header.put(SESSION_ID, sessionId);
            getOrGenerateSession(sessionId);
        }
    }
}
