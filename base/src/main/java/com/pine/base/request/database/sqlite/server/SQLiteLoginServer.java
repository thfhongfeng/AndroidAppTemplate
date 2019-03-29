package com.pine.base.request.database.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.base.request.database.DbRequestBean;
import com.pine.base.request.database.DbResponse;
import com.pine.base.request.database.DbResponseGenerator;
import com.pine.base.request.database.sqlite.SQLiteDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.base.request.database.IDbRequestManager.ACCOUNT_LOGIN_TABLE_NAME;
import static com.pine.base.request.database.IDbRequestManager.ACCOUNT_TABLE_NAME;
import static com.pine.base.request.database.IDbRequestManager.COOKIE_KEY;
import static com.pine.base.request.database.IDbRequestManager.SESSION_ID;

public class SQLiteLoginServer extends SQLiteBaseServer {

    public static DbResponse register(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                      @NonNull Map<String, Map<String, String>> header) {
        try {
            long id = insert(new SQLiteDbHelper(context).getWritableDatabase(), ACCOUNT_TABLE_NAME, requestBean.getParams());
            if (id == -1) {
                return DbResponseGenerator.getBadArgsRep(requestBean, header);
            } else {
                return DbResponseGenerator.getSuccessRep(requestBean, header, "{'id':" + id + "}");
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse login(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                   @NonNull Map<String, Map<String, String>> header) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            Cursor cursor = query(db, ACCOUNT_TABLE_NAME, requestBean.getParams());
            if (!cursor.moveToFirst()) {
                return DbResponseGenerator.getLoginFailRep(requestBean, header, "用户名密码错误");
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
                    Map<String, String> cookies = header.get(COOKIE_KEY);
                    if (cookies == null) {
                        cookies = new HashMap<>();
                    }
                    cookies.put(SESSION_ID, String.valueOf(accountId));
                    header.put(COOKIE_KEY, cookies);
                    return DbResponseGenerator.getSuccessRep(requestBean, header, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return DbResponseGenerator.getExceptionRep(requestBean, header, e);
                } finally {
                    loginCursor.close();
                    cursor.close();
                }
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse logout(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                    @NonNull Map<String, Map<String, String>> header) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            String accountIdStr = header.get(COOKIE_KEY).get(SESSION_ID);
            if (TextUtils.isEmpty(accountIdStr)) {
                return DbResponseGenerator.getSuccessRep(requestBean, header, "");
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
            return DbResponseGenerator.getSuccessRep(requestBean, header, "");
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }
}
