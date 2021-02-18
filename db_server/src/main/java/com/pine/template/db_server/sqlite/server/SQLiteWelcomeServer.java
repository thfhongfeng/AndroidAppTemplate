package com.pine.template.db_server.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.db_server.DbResponseGenerator;
import com.pine.template.db_server.DbSession;
import com.pine.template.db_server.sqlite.SQLiteDbHelper;
import com.pine.template.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pine.template.db_server.DbConstants.ACCOUNT_TABLE_NAME;
import static com.pine.template.db_server.DbConstants.APP_VERSION_TABLE_NAME;
import static com.pine.template.db_server.DbConstants.SWITCHER_CONFIG_TABLE_NAME;
import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteWelcomeServer extends SQLiteBaseServer {
    public static DbResponse queryConfigSwitcher(@NonNull Context context,
                                                 @NonNull DbRequestBean requestBean,
                                                 @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> requestParams = requestBean.getParams();
            String versionCodeStr = requestParams.get("versionCode");
            String versionName = requestParams.get("versionName");
            int versionCode = 1;
            if (TextUtils.isEmpty(versionCodeStr)) {
                try {
                    versionCode = Integer.parseInt(versionCodeStr);
                } catch (NumberFormatException nfe) {
                }
            }
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            String accountType = "0";
            if (!TextUtils.isEmpty(session.getAccountId())) {
                HashMap<String, String> accountParams = new HashMap<>();
                accountParams.put("id", session.getAccountId());
                Cursor accountCursor = query(db, ACCOUNT_TABLE_NAME, accountParams);
                if (accountCursor.moveToFirst()) {
                    accountType = accountCursor.getInt(accountCursor.getColumnIndex("accountType")) + "";
                }
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("accountType", accountType);
            Cursor cursor = query(db, SWITCHER_CONFIG_TABLE_NAME, params);
            try {
                JSONArray jsonArray = new JSONArray();
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("configKey", cursor.getString(cursor.getColumnIndex("configKey")));
                    jsonObject.put("state", cursor.getString(cursor.getColumnIndex("state")));
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
            Map<String, String> requestParams = requestBean.getParams();
            String versionCodeStr = requestParams.get("versionCode");
            String versionName = requestParams.get("versionName");
            int userVersionCode = 1;
            if (TextUtils.isEmpty(versionCodeStr)) {
                try {
                    userVersionCode = Integer.parseInt(versionCodeStr);
                } catch (NumberFormatException nfe) {
                }
            }
            Cursor cursor = query(db, APP_VERSION_TABLE_NAME, new HashMap<String, String>());
            try {
                JSONObject jsonObject = null;
                boolean hasNewVersion = false;
                if (cursor.moveToFirst()) {
                    jsonObject = new JSONObject();
                    int curVersionCode = cursor.getInt(cursor.getColumnIndex("versionCode"));
                    if (curVersionCode > userVersionCode) {
                        hasNewVersion = true;
                    }
                    jsonObject.put("packageName", cursor.getString(cursor.getColumnIndex("packageName")));
                    jsonObject.put("versionName", cursor.getString(cursor.getColumnIndex("versionName")));
                    jsonObject.put("versionCode", curVersionCode);
                    jsonObject.put("minSupportedVersion", cursor.getInt(cursor.getColumnIndex("minSupportedVersion")));
                    jsonObject.put("force", cursor.getString(cursor.getColumnIndex("force")));
                    jsonObject.put("fileName", cursor.getString(cursor.getColumnIndex("fileName")));
                    jsonObject.put("path", cursor.getString(cursor.getColumnIndex("path")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                }
                cursor.close();
                if (hasNewVersion) {
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject == null ? "" : jsonObject.toString());
                } else {
                    return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "");
                }
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
