package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.Product;

@Dao
public interface ProductDao {
    @Insert
    long insert(Product product);

    @Update
    int update(Product product);
}
