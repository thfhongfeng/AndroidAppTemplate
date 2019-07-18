package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.Shop;

@Dao
public interface ShopDao {
    @Insert
    long insert(Shop shop);

    @Update
    int update(Shop shop);
}
