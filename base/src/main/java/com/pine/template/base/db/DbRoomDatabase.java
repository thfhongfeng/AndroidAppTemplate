package com.pine.template.base.db;

import android.content.Context;

import com.pine.template.base.db.dao.AppTrackDao;
import com.pine.template.base.db.dao.DownloadInfoDao;
import com.pine.template.base.db.entity.AppTrack;
import com.pine.template.base.db.entity.DownloadInfo;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {AppTrack.class, DownloadInfo.class}, version = 2, exportSchema = false)
public abstract class DbRoomDatabase extends RoomDatabase {
    private static final String TAG = LogUtils.makeLogTag(DbRoomDatabase.class);

    public static final Object DB_SYNC_LOCK = new Object();

    public abstract AppTrackDao appTrackDao();

    public abstract DownloadInfoDao downloadInfoDao();

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
            database.execSQL("CREATE TABLE IF NOT EXISTS db_download_info " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,type INTEGER NOT NULL," +
                    "module_tag TEXT NOT NULL,relate_id TEXT NOT NULL,remote_url TEXT NOT NULL," +
                    "local_url TEXT NOT NULL,file_name TEXT NOT NULL,file_size INTEGER NOT NULL," +
                    "downloaded_size INTEGER NOT NULL,download_speed INTEGER NOT NULL," +
                    "download_state INTEGER NOT NULL,extra_info TEXT,remark TEXT," +
                    "invalid INTEGER NOT NULL,update_time INTEGER NOT NULL,create_time INTEGER NOT NULL)");
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
