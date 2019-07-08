package com.pine.db_server.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pine.db_server.sqlite.DbResponseGenerator;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.pine.db_server.DbConstants.APP_VERSION_TABLE_NAME;
import static com.pine.db_server.DbConstants.SWITCHER_CONFIG_TABLE_NAME;

public class SQLiteWelcomeServer extends SQLiteBaseServer {
    public static DbResponse queryConfigSwitcher(@NonNull Context context,
                                                 @NonNull DbRequestBean requestBean,
                                                 @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Cursor cursor = query(db, SWITCHER_CONFIG_TABLE_NAME, requestBean.getParams());
            try {
                JSONArray jsonArray = new JSONArray();
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("configKey", cursor.getString(cursor.getColumnIndex("configKey")));
                    jsonObject.put("open", cursor.getString(cursor.getColumnIndex("open")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                    jsonArray.put(jsonObject);
                }
                cursor.close();
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse queryAppVersion(@NonNull Context context,
                                             @NonNull DbRequestBean requestBean,
                                             @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Cursor cursor = query(db, APP_VERSION_TABLE_NAME, requestBean.getParams());
            try {
                JSONObject jsonObject = null;
                if (cursor.moveToFirst()) {
                    jsonObject = new JSONObject();
                    jsonObject.put("packageName", cursor.getString(cursor.getColumnIndex("packageName")));
                    jsonObject.put("versionName", cursor.getString(cursor.getColumnIndex("versionName")));
                    jsonObject.put("versionCode", cursor.getInt(cursor.getColumnIndex("versionCode")));
                    jsonObject.put("minSupportedVersion", cursor.getInt(cursor.getColumnIndex("minSupportedVersion")));
                    jsonObject.put("force", cursor.getString(cursor.getColumnIndex("force")));
                    jsonObject.put("fileName", cursor.getString(cursor.getColumnIndex("fileName")));
                    jsonObject.put("path", cursor.getString(cursor.getColumnIndex("path")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                }
                cursor.close();
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject == null ? "" : jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }
}
