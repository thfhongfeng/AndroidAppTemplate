package com.pine.db_server.impl.room.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pine.db_server.impl.room.DbRoomDatabase;
import com.pine.db_server.impl.room.dao.AppVersionDao;
import com.pine.db_server.impl.room.dao.ConfigSwitcherDao;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;

public class WelcomeRepository {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    private static volatile WelcomeRepository mInstance = null;

    private AppVersionDao appVersionDao;

    private ConfigSwitcherDao configSwitcherDao;

    public static WelcomeRepository getInstance(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            if (mInstance == null) {
                mInstance = new WelcomeRepository(application);
            }
            return mInstance;
        }
    }

    private DbRoomDatabase roomDatabase;

    private WelcomeRepository(Context application) {
        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
            LogUtils.d(TAG, "new");
            roomDatabase = roomDatabase.getINSTANCE(application);
            appVersionDao = roomDatabase.appVersionDao();
            configSwitcherDao = roomDatabase.configSwitcherDao();
        }
    }

//    public DbResponse queryConfigSwitcher(@NonNull DbRequestBean requestBean,
//                                          @NonNull HashMap<String, String> cookies) {
//        synchronized (DbRoomDatabase.DB_SYNC_LOCK) {
//
//        }
//    }
}
