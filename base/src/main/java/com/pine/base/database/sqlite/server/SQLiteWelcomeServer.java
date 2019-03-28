package com.pine.base.database.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.pine.base.database.DbRequestBean;
import com.pine.base.database.DbResponse;
import com.pine.base.database.DbResponseGenerator;
import com.pine.base.database.sqlite.SQLiteDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.pine.base.database.IDbRequestManager.APP_VERSION_TABLE_NAME;
import static com.pine.base.database.IDbRequestManager.SWITCHER_CONFIG_TABLE_NAME;

public class SQLiteWelcomeServer extends SQLiteBaseServer {
    public static DbResponse queryConfigSwitcher(@NonNull Context context,
                                                 @NonNull DbRequestBean requestBean,
                                                 @NonNull Map<String, Map<String, String>> header) {
        try {
            Cursor cursor = query(new SQLiteDbHelper(context).getReadableDatabase(),
                    SWITCHER_CONFIG_TABLE_NAME, requestBean.getParams());
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
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryAppVersion(@NonNull Context context,
                                             @NonNull DbRequestBean requestBean,
                                             @NonNull Map<String, Map<String, String>> header) {
        try {
            Cursor cursor = query(new SQLiteDbHelper(context).getReadableDatabase(),
                    APP_VERSION_TABLE_NAME, requestBean.getParams());
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
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonObject == null ? "" : jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }
}
