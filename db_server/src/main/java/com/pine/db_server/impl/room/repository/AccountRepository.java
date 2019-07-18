package com.pine.db_server.impl.room.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pine.db_server.DbResponseGenerator;
import com.pine.db_server.DbSession;
import com.pine.db_server.impl.room.DbRoomDatabase;
import com.pine.db_server.impl.room.dao.AccountAccessLogDao;
import com.pine.db_server.impl.room.dao.AccountDao;
import com.pine.db_server.impl.room.entity.Account;
import com.pine.db_server.impl.room.entity.AccountAccessLog;
import com.pine.db_server.impl.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class AccountRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile AccountRepository mInstance = null;

    private AccountDao accountDao;

    private AccountAccessLogDao accountAccessLogDao;

    public static AccountRepository getInstance(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            if (mInstance == null) {
                mInstance = new AccountRepository(application);
            }
            return mInstance;
        }
    }

    private DbRoomDatabase roomDatabase;

    private AccountRepository(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "new");
            roomDatabase = roomDatabase.getINSTANCE(application);
            accountDao = roomDatabase.accountDao();
            accountAccessLogDao = roomDatabase.accountAccessLogDao();
        }
    }

    public DbResponse register(@NonNull DbRequestBean requestBean,
                               @NonNull HashMap<String, String> cookies) {
        Map<String, String> requestParams = requestBean.getParams();
        String accountStr = requestParams.get("mobile");
        if (isAccountExist(accountStr)) {
            return DbResponseGenerator.getExistAccountJsonRep(requestBean, cookies, "账号已存在");
        }
        Account account = new Account();
        account.setAccountId("1000" + new Date().getTime());
        account.setAccount(accountStr);
        account.setName(accountStr);
        account.setState(1);
        account.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        account.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        long id = accountDao.insert(account);
        if (id == -1) {
            return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
        } else {
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, new Gson().toJson(account));
        }
    }

    public DbResponse login(@NonNull DbRequestBean requestBean,
                            @NonNull HashMap<String, String> cookies) {
        Map<String, String> requestParams = requestBean.getParams();
        String accountStr = requestParams.get("mobile");
        String password = requestParams.get("password");
        Account account = accountDao.checkAccountByAccountPwd(accountStr, password);
        if (account == null) {
            return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "用户名密码错误");
        } else {
            account.setCurLoginTimeStamp(Calendar.getInstance().getTimeInMillis());
            accountDao.update(account);
            if (cookies == null) {
                cookies = new HashMap<>();
            }
            String sessionId = SQLiteDbServerManager.getInstance().generateSessionId(account.getAccountId());
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(sessionId);
            session.setAccountId(account.getAccountId());
            session.setLoginTimeStamp(account.getCurLoginTimeStamp());
            cookies.put(SESSION_ID, sessionId);
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, new Gson().toJson(account));
        }
    }

    public DbResponse logout(@NonNull DbRequestBean requestBean,
                             @NonNull HashMap<String, String> cookies) {
        DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
        String accountIdStr = session.getAccountId();
        if (TextUtils.isEmpty(accountIdStr)) {
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
        }
        Account account = accountDao.checkAccountByAccountId(accountIdStr);
        if (account == null) {
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
        }
        SQLiteDbServerManager.getInstance().removeSession(session.getSessionId());
        boolean isSuccess = true;
        roomDatabase.beginTransaction();
        AccountAccessLog accessLog = new AccountAccessLog();
        accessLog.setAccountId(account.getAccountId());
        accessLog.setLoginTimeStamp(account.getCurLoginTimeStamp());
        accessLog.setLogoutTimeStamp(Calendar.getInstance().getTimeInMillis());
        isSuccess = isSuccess && accountAccessLogDao.insert(accessLog) > 0;
        account.setCurLoginTimeStamp(0);
        isSuccess = isSuccess && accountDao.update(account) > 0;
        if (isSuccess) {
            roomDatabase.setTransactionSuccessful();
        }
        roomDatabase.endTransaction();
        if (isSuccess) {
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
        } else {
            return DbResponseGenerator.getServerDbOpFailJsonRep(requestBean, cookies, "");
        }
    }

    private boolean isAccountExist(@NonNull String account) {
        return accountDao.checkAccountByAccount(account) != null;
    }
}
