package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.ShopType;

@Dao
public interface ShopTypeDao {
    @Insert
    long insert(ShopType shopType);

    @Update
    int update(ShopType shopType);
}
