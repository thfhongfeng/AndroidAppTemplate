package com.pine.db_server.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.db_server.DbSession;
import com.pine.db_server.sqlite.DbResponseGenerator;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.db_server.sqlite.SQLiteDbRequestManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.db_server.DbConstants.ACCOUNT_LOGIN_TABLE_NAME;
import static com.pine.db_server.DbConstants.ACCOUNT_TABLE_NAME;
import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteLoginServer extends SQLiteBaseServer {

    public static DbResponse register(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                      @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            Map<String, String> requestParams = requestBean.getParams();
            String account = requestParams.get("mobile");
            if (isAccountExist(db, account)) {
                return DbResponseGenerator.getExistAccountJsonRep(requestBean, cookies, "账号已存在");
            }
            requestParams.put("id", "1000" + new Date().getTime());
            requestParams.put("account", account);
            requestParams.put("name", account);
            requestParams.put("state", "1");
            requestParams.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            requestParams.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            long id = insert(db, ACCOUNT_TABLE_NAME, requestParams);
            if (id == -1) {
                return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", requestParams.get("id"));
                    jsonObject.put("account", requestParams.get("account"));
                    jsonObject.put("password", requestParams.get("password"));
                    jsonObject.put("state", requestParams.get("state"));
                    jsonObject.put("mobile", requestParams.get("mobile"));
                    jsonObject.put("createTime", requestParams.get("createTime"));
                    jsonObject.put("updateTime", requestParams.get("updateTime"));
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse login(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                   @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            Map<String, String> requestParams = requestBean.getParams();
            String account = requestParams.remove("mobile");
            requestParams.put("account", account);
            Cursor cursor = query(db, ACCOUNT_TABLE_NAME, requestParams);
            if (!cursor.moveToFirst()) {
                return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "用户名密码错误");
            } else {
                ContentValues contentValues = new ContentValues();
                String accountId = cursor.getString(cursor.getColumnIndex("id"));
                contentValues.put("accountId", accountId);
                Cursor loginCursor = query(db, ACCOUNT_LOGIN_TABLE_NAME, null, "accountId=?",
                        new String[]{String.valueOf(accountId)}, null, null, null);
                if (!loginCursor.moveToFirst() ||
                        !TextUtils.isEmpty(loginCursor.getString(loginCursor.getColumnIndex("logoutTime")))) {
                    contentValues.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    insert(db, ACCOUNT_LOGIN_TABLE_NAME, "accountId", contentValues);
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                    jsonObject.put("account", cursor.getString(cursor.getColumnIndex("account")));
                    jsonObject.put("state", cursor.getInt(cursor.getColumnIndex("state")));
                    jsonObject.put("mobile", cursor.getInt(cursor.getColumnIndex("mobile")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                    if (cookies == null) {
                        cookies = new HashMap<>();
                    }
                    String sessionId = SQLiteDbRequestManager.getInstance().generateSessionId(accountId);
                    DbSession session = SQLiteDbRequestManager.getInstance().getOrGenerateSession(sessionId);
                    session.setUserId(accountId);
                    session.setLoginTimeStamp(Calendar.getInstance().getTimeInMillis());
                    cookies.put(SESSION_ID, sessionId);
                    loginCursor.close();
                    cursor.close();
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    loginCursor.close();
                    cursor.close();
                    return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse logout(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                    @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            DbSession session = SQLiteDbRequestManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            String accountIdStr = session.getUserId();
            if (TextUtils.isEmpty(accountIdStr)) {
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
            }
            SQLiteDbRequestManager.getInstance().removeSession(session.getSessionId());
            Cursor loginCursor = query(db, ACCOUNT_LOGIN_TABLE_NAME, null, "accountId=?",
                    new String[]{accountIdStr}, null, null, null);
            while (loginCursor.moveToNext() &&
                    TextUtils.isEmpty(loginCursor.getString(loginCursor.getColumnIndex("logoutTime")))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("logoutTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                update(db, ACCOUNT_LOGIN_TABLE_NAME, contentValues, "accountId=?", new String[]{accountIdStr});
            }
            loginCursor.close();
            return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    private static boolean isAccountExist(SQLiteDatabase db, String account) {
        Cursor cursor = query(db, ACCOUNT_TABLE_NAME, null, "account=?",
                new String[]{account}, null, null, null);
        return cursor.moveToFirst();
    }
}
