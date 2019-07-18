package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.AppVersion;

@Dao
public interface AppVersionDao {
    @Insert
    long insert(AppVersion appVersion);

    @Update
    int update(AppVersion appVersion);
}
