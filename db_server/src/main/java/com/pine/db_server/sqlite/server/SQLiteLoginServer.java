package com.pine.db_server.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.base.request.impl.database.DbRequestBean;
import com.pine.base.request.impl.database.DbResponse;
import com.pine.db_server.sqlite.DbResponseGenerator;
import com.pine.db_server.sqlite.SQLiteDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.pine.base.request.IRequestManager.SESSION_ID;
import static com.pine.db_server.DbConstants.ACCOUNT_LOGIN_TABLE_NAME;
import static com.pine.db_server.DbConstants.ACCOUNT_TABLE_NAME;

public class SQLiteLoginServer extends SQLiteBaseServer {

    public static DbResponse register(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                      @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            long id = insert(db, ACCOUNT_TABLE_NAME, requestBean.getParams());
            if (id == -1) {
                return DbResponseGenerator.getBadArgsRep(requestBean, cookies);
            } else {
                return DbResponseGenerator.getSuccessRep(requestBean, cookies, "{'id':" + id + "}");
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse login(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                   @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            Cursor cursor = query(db, ACCOUNT_TABLE_NAME, requestBean.getParams());
            if (!cursor.moveToFirst()) {
                return DbResponseGenerator.getLoginFailRep(requestBean, cookies, "用户名密码错误");
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
                    cookies.put(SESSION_ID, String.valueOf(accountId));
                    loginCursor.close();
                    cursor.close();
                    return DbResponseGenerator.getSuccessRep(requestBean, cookies, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    loginCursor.close();
                    cursor.close();
                    return DbResponseGenerator.getExceptionRep(requestBean, cookies, e);
                }
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse logout(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                    @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            String accountIdStr = cookies.get(SESSION_ID);
            if (TextUtils.isEmpty(accountIdStr)) {
                return DbResponseGenerator.getSuccessRep(requestBean, cookies, "");
            }
            Cursor loginCursor = query(db, ACCOUNT_LOGIN_TABLE_NAME, null, "accountId=?",
                    new String[]{accountIdStr}, null, null, null);
            while (loginCursor.moveToNext() &&
                    TextUtils.isEmpty(loginCursor.getString(loginCursor.getColumnIndex("logoutTime")))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("logoutTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                update(db, ACCOUNT_LOGIN_TABLE_NAME, contentValues, "accountId=?", new String[]{accountIdStr});
            }
            loginCursor.close();
            return DbResponseGenerator.getSuccessRep(requestBean, cookies, "");
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }
}
