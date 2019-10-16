package com.pine.db_server.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.db_server.DbResponseGenerator;
import com.pine.db_server.DbSession;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.db_server.DbConstants.ACCOUNT_ACCESS_LOG_TABLE_NAME;
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
            String verifyCode = requestParams.remove("verify_code");
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "1000" + new Date().getTime());
            contentValues.put("account", account);
            contentValues.put("mobile", account);
            contentValues.put("password", requestParams.get("password"));
            contentValues.put("name", account);
            contentValues.put("state", "1");
            contentValues.put("accountType", "10");
            contentValues.put("curLoginTimeStamp", Calendar.getInstance().getTimeInMillis() + "");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            long id = insert(db, ACCOUNT_TABLE_NAME, "account", contentValues);
            if (id == -1) {
                return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", requestParams.get("id"));
                    jsonObject.put("account", requestParams.get("account"));
                    jsonObject.put("password", requestParams.get("password"));
                    jsonObject.put("accountType", requestParams.get("accountType"));
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
            Map<String, String> params = new HashMap<>();
            params.put("account", requestParams.get("mobile"));
            Cursor cursor = query(db, ACCOUNT_TABLE_NAME, params);
            if (!cursor.moveToFirst()) {
                return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "用户名密码错误");
            } else {
                String accountId = cursor.getString(cursor.getColumnIndex("id"));
                long login_stamp = Calendar.getInstance().getTimeInMillis();
                ContentValues contentValues = new ContentValues();
                contentValues.put("curLoginTimeStamp", login_stamp);
                update(db, ACCOUNT_TABLE_NAME, contentValues, "id=?", new String[]{accountId});
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("account", cursor.getString(cursor.getColumnIndex("account")));
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject.put("accountType", cursor.getString(cursor.getColumnIndex("accountType")));
                    jsonObject.put("state", cursor.getInt(cursor.getColumnIndex("state")));
                    jsonObject.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                    if (cookies == null) {
                        cookies = new HashMap<>();
                    }
                    String sessionId = SQLiteDbServerManager.getInstance().generateSessionId(accountId);
                    DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(sessionId);
                    session.setAccountId(accountId);
                    session.setLoginTimeStamp(login_stamp);
                    cookies.put(SESSION_ID, sessionId);
                    cursor.close();
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            String accountIdStr = session.getAccountId();
            if (TextUtils.isEmpty(accountIdStr)) {
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
            }
            SQLiteDbServerManager.getInstance().removeSession(session.getSessionId());
            Cursor accountCursor = query(db, ACCOUNT_TABLE_NAME, null, "accountId=?",
                    new String[]{accountIdStr}, null, null, null);
            if (!accountCursor.moveToNext()) {
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
            }
            boolean isSuccess = true;
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("accountId", accountIdStr);
            contentValues.put("loginTimeStamp", accountCursor.getLong(accountCursor.getColumnIndex("curLoginTimeStamp")));
            contentValues.put("logoutTimeStamp", Calendar.getInstance().getTimeInMillis());
            isSuccess = isSuccess && insert(db, ACCOUNT_ACCESS_LOG_TABLE_NAME, "accountId", contentValues) > 0;
            ContentValues accountValue = new ContentValues();
            accountValue.put("curLoginTimeStamp", 0);
            isSuccess = isSuccess && update(db, ACCOUNT_TABLE_NAME, accountValue, "id=?", new String[]{accountIdStr}) > 0;
            if (isSuccess) {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
            if (isSuccess) {
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
            } else {
                return DbResponseGenerator.getServerDbOpFailJsonRep(requestBean, cookies, "");
            }
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
