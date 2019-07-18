package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.TravelNoteShop;

@Dao
public interface TravelNoteShopDao {
    @Insert
    long insert(TravelNoteShop travelNoteShop);

    @Update
    int update(TravelNoteShop travelNoteShop);
}
