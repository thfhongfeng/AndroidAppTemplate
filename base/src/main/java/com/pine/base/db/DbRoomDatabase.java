package com.pine.base.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pine.base.db.dao.AppAttachCacheDao;
import com.pine.base.db.dao.AppTrackDao;
import com.pine.base.db.entity.AppAttachCache;
import com.pine.base.db.entity.AppTrack;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

import java.io.File;

@Database(entities = {AppTrack.class, AppAttachCache.class}, version = 2, exportSchema = false)
public abstract class DbRoomDatabase extends RoomDatabase {
    private static final String TAG = LogUtils.makeLogTag(DbRoomDatabase.class);

    public static final Object DB_SYNC_LOCK = new Object();

    public abstract AppTrackDao appTrackDao();

    public abstract AppAttachCacheDao appAttachCacheDao();

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
                RoomDatabase.Builder<DbRoomDatabase> builder = Room.databaseBuilder(context.getApplicationContext(), DbRoomDatabase.class, path);
                if (!AppUtils.isApkDebuggable(context)) {
                    LogUtils.d(TAG, "on debug apk, disable encrypt database");
                    builder.openHelperFactory(factory);  // encrypt
                }
                builder.allowMainThreadQueries();   // 允许主线程操作
                builder.addMigrations(MIGRATION_1_2);
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

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS app_attach_cache" +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,module TEXT NOT NULL," +
                    "file_name TEXT,file_size INTEGER NOT NULL,url TEXT NOT NULL," +
                    "cache_url TEXT NOT NULL,remark TEXT,invalid INTEGER NOT NULL)");
        }
    };

    public static void resetDatabase() {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
            }
        }
    }
}
