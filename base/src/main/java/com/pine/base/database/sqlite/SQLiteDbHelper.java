package com.pine.base.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.pine.base.database.IDbRequestManager.ACCOUNT_LOGIN_TABLE_NAME;
import static com.pine.base.database.IDbRequestManager.ACCOUNT_TABLE_NAME;
import static com.pine.base.database.IDbRequestManager.APP_VERSION_TABLE_NAME;
import static com.pine.base.database.IDbRequestManager.DATABASE_NAME;
import static com.pine.base.database.IDbRequestManager.DATABASE_VERSION;
import static com.pine.base.database.IDbRequestManager.SWITCHER_CONFIG_TABLE_NAME;

public class SQLiteDbHelper extends SQLiteOpenHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.d(TAG, "onCreate");
        createConfigSwitcherTable(db);
        createAppVersionTable(db);
        createAccountTable(db);
        createAccountLoginTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createConfigSwitcherTable(SQLiteDatabase db) {
        try {
            boolean insertSuccess = true;
            db.execSQL("create table if not exists " + SWITCHER_CONFIG_TABLE_NAME +
                    "(id integer primary key autoincrement,configKey text not null," +
                    "open text not null," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> contentValuesList = new ArrayList<>();
            ContentValues contentValues = new ContentValues();
            contentValues.put("configKey", "login_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "main_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "user_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvc_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvp_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvvm_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_demo_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValuesList.add(contentValues);
            db.beginTransaction();
            for (ContentValues cv : contentValuesList) {
                long id = db.insert(SWITCHER_CONFIG_TABLE_NAME, "configKey", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createConfigSwitcherTable success");
            } else {
                LogUtils.d(TAG, "createConfigSwitcherTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createConfigSwitcherTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createAppVersionTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + APP_VERSION_TABLE_NAME +
                    "(id integer primary key autoincrement,packageName text not null," +
                    "versionName text not null,versionCode integer not null," +
                    "minSupportedVersion text,force boolean,fileName text,path text," +
                    "createTime datetime,updateTime datetime)");
            ContentValues contentValues = new ContentValues();
            contentValues.put("packageName", "com.pine.template");
            contentValues.put("versionName", "1.0.2");
            contentValues.put("versionCode", 2);
            contentValues.put("minSupportedVersion", 1);
            contentValues.put("force", "false");
            contentValues.put("fileName", "pine_app_template-V1.0.2-release.apk");
            contentValues.put("path", "http://yanyangtian.purang.com/download/bsd_purang.apk");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            long id = db.insert(APP_VERSION_TABLE_NAME, "package", contentValues);
            if (id != -1) {
                LogUtils.d(TAG, "createAppVersionTable success");
            } else {
                LogUtils.d(TAG, "createAppVersionTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAppVersionTable fail: " + e.toString());
        }
    }

    private void createAccountTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_TABLE_NAME +
                    "(id integer primary key autoincrement,account text not null," +
                    "password text not null,state integer not null,login integer not null," +
                    "mobile text not null,createTime datetime,updateTime datetime)");
            db.execSQL("create table if not exists " + ACCOUNT_LOGIN_TABLE_NAME +
                    "(id integer primary key autoincrement,accountId integer not null," +
                    "loginTime datetime,logoutTime datetime)");
            ContentValues contentValues = new ContentValues();
            contentValues.put("account", "admin");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("login", 2); // 账户状态:1-登录中，2-已下线
            contentValues.put("mobile", "15221464292");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            long id = db.insert(ACCOUNT_TABLE_NAME, "account", contentValues);
            if (id != -1) {
                LogUtils.d(TAG, "createAccountTable success");
            } else {
                LogUtils.d(TAG, "createAccountTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAccountTable fail: " + e.toString());
        }
    }

    private void createAccountLoginTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_LOGIN_TABLE_NAME +
                    "(id integer primary key autoincrement,accountId integer not null," +
                    "loginTime datetime,loginOut datetime)");
            LogUtils.d(TAG, "createAccountLoginTable success");
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAccountLoginTable fail: " + e.toString());
        }
    }
}
