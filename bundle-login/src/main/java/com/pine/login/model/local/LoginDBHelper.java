package com.pine.login.model.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginDBHelper extends SQLiteOpenHelper {
    // 数据库名
    private static final String DATABASE_NAME = "pine.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;
    // 表名
    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String REG_VERIFY_CODE_TABLE_NAME = "reg_verify_code";
    public static final String BUNDLE_SWITCHER_TABLE_NAME = "bundle_switcher";
    public static final String APP_VERSION_TABLE_NAME = "app_version";
    public static final String SHOP_TABLE_NAME = "shop";
    public static final String SHOP_TYPE_TABLE_NAME = "shop_type";
    public static final String TRAVEL_NOTE_TABLE_NAME = "travel_note";

    public LoginDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + ACCOUNT_TABLE_NAME +
                "(_id integer primary key autoincrement,account text not null, password text not null, state integer not null, create_time datetime, update_time datetime)");
        insertAccount("admin", "111aaa");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertAccount(String account, String pwd) {
        ContentValues cv = new ContentValues();
        cv.put("account", account);
        cv.put("pwd", pwd);
        cv.put("state", 1);
        cv.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        cv.put("update_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        getWritableDatabase().insert(ACCOUNT_TABLE_NAME, "account", cv);
    }
}
