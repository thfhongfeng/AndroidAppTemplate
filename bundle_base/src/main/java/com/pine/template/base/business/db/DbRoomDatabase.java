package com.pine.template.base.business.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pine.template.base.business.track.dao.AppTrackDao;
import com.pine.template.base.business.track.entity.AppTrack;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

import java.io.File;

@Database(entities = {AppTrack.class}, version = 1, exportSchema = false)
public abstract class DbRoomDatabase extends RoomDatabase {
    private static final String TAG = LogUtils.makeLogTag(DbRoomDatabase.class);

    public static final Object DB_SYNC_LOCK = new Object();

    public abstract AppTrackDao appTrackDao();

    private static final String PASSPHRASE = "pine123";

    // volatile关键字，确保不会被编译器优化
    private static volatile DbRoomDatabase INSTANCE;

    private static SQLiteCipherSpec cipherSpec = new SQLiteCipherSpec()
            .setPageSize(4096)
            .setKDFIteration(64000);

    private static WCDBOpenHelperFactory factory = new WCDBOpenHelperFactory()
            .passphrase(PASSPHRASE.getBytes())  // passphrase to the database, remove this line for plain-text
            .cipherSpec(cipherSpec);               // cipher to use, remove for default settings

    public static DbRoomDatabase getINSTANCE(final Context context) {
        synchronized (DB_SYNC_LOCK) {
            if (INSTANCE == null) {
                String path = PathUtils.getAppFilePath("db") + File.separator + "database.db";
                LogUtils.d(TAG, "open or create database with path:" + path);
                Builder<DbRoomDatabase> builder = Room.databaseBuilder(context.getApplicationContext(), DbRoomDatabase.class, path);
//                if (!AppUtils.isApkDebuggable(context)) {
//                    LogUtils.d(TAG, "on debug apk, disable encrypt database");
//                    builder.openHelperFactory(factory);  // encrypt
//                }
                builder.openHelperFactory(factory);  // encrypt
                builder.allowMainThreadQueries();   // 允许主线程操作
                builder.addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        LogUtils.d(TAG, "onCreate");
                    }
                });
                INSTANCE = builder.build();
            }
            return INSTANCE;
        }
    }

    public static void resetDatabase() {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
            }
        }
    }
}
