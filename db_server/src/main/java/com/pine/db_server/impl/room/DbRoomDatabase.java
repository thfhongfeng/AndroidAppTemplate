package com.pine.db_server.impl.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.pine.db_server.impl.room.dao.AccountAccessLogDao;
import com.pine.db_server.impl.room.dao.AccountDao;
import com.pine.db_server.impl.room.dao.AppTrackDao;
import com.pine.db_server.impl.room.dao.AppVersionDao;
import com.pine.db_server.impl.room.dao.ConfigSwitcherDao;
import com.pine.db_server.impl.room.dao.FileInfoDao;
import com.pine.db_server.impl.room.dao.ProductDao;
import com.pine.db_server.impl.room.dao.ShopDao;
import com.pine.db_server.impl.room.dao.ShopTypeDao;
import com.pine.db_server.impl.room.dao.TravelNoteCommentDao;
import com.pine.db_server.impl.room.dao.TravelNoteDao;
import com.pine.db_server.impl.room.dao.TravelNoteShopDao;
import com.pine.db_server.impl.room.entity.Account;
import com.pine.db_server.impl.room.entity.AccountAccessLog;
import com.pine.db_server.impl.room.entity.AppTrack;
import com.pine.db_server.impl.room.entity.AppVersion;
import com.pine.db_server.impl.room.entity.ConfigSwitcher;
import com.pine.db_server.impl.room.entity.FileInfo;
import com.pine.db_server.impl.room.entity.Product;
import com.pine.db_server.impl.room.entity.Shop;
import com.pine.db_server.impl.room.entity.ShopType;
import com.pine.db_server.impl.room.entity.TravelNote;
import com.pine.db_server.impl.room.entity.TravelNoteComment;
import com.pine.db_server.impl.room.entity.TravelNoteShop;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

import java.io.File;

@Database(entities = {Account.class, AccountAccessLog.class, AppVersion.class, ConfigSwitcher.class,
        FileInfo.class, Product.class, Shop.class, ShopType.class, TravelNote.class,
        TravelNoteComment.class, TravelNoteShop.class, AppTrack.class}, version = 2, exportSchema = false)
public abstract class DbRoomDatabase extends RoomDatabase {
    private static final String TAG = LogUtils.makeLogTag(DbRoomDatabase.class);

    public static final Object DB_SYNC_LOCK = new Object();

    public abstract AccountDao accountDao();

    public abstract AccountAccessLogDao accountAccessLogDao();

    public abstract AppVersionDao appVersionDao();

    public abstract ConfigSwitcherDao configSwitcherDao();

    public abstract FileInfoDao fileInfoDao();

    public abstract ProductDao productDao();

    public abstract ShopDao shopDao();

    public abstract ShopTypeDao shopTypeDao();

    public abstract TravelNoteDao travelNoteDao();

    public abstract TravelNoteCommentDao travelNoteCommentDao();

    public abstract TravelNoteShopDao travelNoteShopDao();

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

    public static DbRoomDatabase getINSTANCE(Context context) {
        synchronized (DB_SYNC_LOCK) {
            if (INSTANCE == null) {
                String path = PathUtils.getAppFilePath("dbserver") + File.separator + "database.db";
                LogUtils.d(TAG, "open or create database with path:" + path);
                RoomDatabase.Builder<DbRoomDatabase> builder = Room.databaseBuilder(context.getApplicationContext(), DbRoomDatabase.class, path);
                if (!AppUtils.isApkDebuggable(context)) {
                    LogUtils.d(TAG, "on debug apk, disable encrypt database");
                    builder.openHelperFactory(factory);  // encrypt
                }
                builder.allowMainThreadQueries();   // 允许主线程操作
                builder.addMigrations(MIGRATION_1_2);
                INSTANCE = builder.build();
            }
            return INSTANCE;
        }
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS db_app_track" +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,moduleTag TEXT NOT NULL," +
                    "accountId TEXT,userName TEXT,trackType INTEGER NOT NULL,title TEXT,curClass TEXT,preClass TEXT,buttonName TEXT," +
                    "ip TEXT,timeInStamp INTEGER,timeOutStamp INTEGER)");
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
