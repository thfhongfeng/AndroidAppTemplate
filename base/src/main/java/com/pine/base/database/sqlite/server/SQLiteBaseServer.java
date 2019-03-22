package com.pine.base.database.sqlite.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pine.base.database.sqlite.SQLiteDbHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SQLiteBaseServer {
    public static long insert(@NonNull Context context, @NonNull String tableName,
                              @NonNull String nullColumnHack,
                              @NonNull ContentValues values) throws SQLException {
        return new SQLiteDbHelper(context).getWritableDatabase().insert(tableName, nullColumnHack, values);
    }

    public static long insert(@NonNull Context context, @NonNull String tableName,
                              @NonNull Map<String, String> params) throws SQLException {
        if (params != null && params.size() > 0) {
            SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
            ContentValues cv = new ContentValues();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            Map.Entry<String, String> first = iterator.next();
            String nullColumnHack = first.getKey();
            cv.put(first.getKey(), first.getValue());
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                cv.put(entry.getKey(), entry.getValue());
            }
            long id = db.insert(tableName, nullColumnHack, cv);
            db.close();
            return id;
        }
        return -1;
    }

    public static Cursor query(@NonNull Context context, @NonNull String tableName,
                               String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having,
                               String orderBy) throws SQLException {
        return new SQLiteDbHelper(context).getReadableDatabase().query(tableName,
                columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public static Cursor query(@NonNull Context context, @NonNull String tableName,
                               Map<String, String> params) throws SQLException {
        String filter = "";
        List<String> filterArgs = null;
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        if (params != null && params.size() > 0) {
            filterArgs = new ArrayList<>();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                filter += entry.getKey() + "=?" + (iterator.hasNext() ? " and " : "");
                filterArgs.add(entry.getValue());
            }
        }
        Cursor cursor = db.query(tableName,
                null, filter, filterArgs == null ? null : filterArgs.toArray(new String[0]),
                null, null, null);
        return cursor;
    }

    public static int update(@NonNull Context context, @NonNull String tableName,
                             ContentValues values, String whereCause, String[] whereArgs) throws SQLException {
        return new SQLiteDbHelper(context).getWritableDatabase().update(tableName,
                values, whereCause, whereArgs);
    }

    public static int update(@NonNull Context context, @NonNull String tableName,
                             @NonNull String idKey, @NonNull String id,
                             Map<String, String> params) throws SQLException {
        if (params != null && params.size() > 0) {
            SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
            ContentValues cv = new ContentValues();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            Map.Entry<String, String> first = iterator.next();
            cv.put(first.getKey(), first.getValue());
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                cv.put(entry.getKey(), entry.getValue());
            }
            int count = db.update(tableName, cv, idKey + "=?", new String[]{id});
            db.close();
            return count;
        }
        return -1;
    }
}
