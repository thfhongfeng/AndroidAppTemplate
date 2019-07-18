package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.ConfigSwitcher;

import java.util.List;

@Dao
public interface ConfigSwitcherDao {
    @Insert
    long insert(ConfigSwitcher configSwitcher);

    @Update
    int update(ConfigSwitcher configSwitcher);

    @Query("SELECT * FROM db_switcher_config")
    List<ConfigSwitcher> checkListByAccountId();
}
