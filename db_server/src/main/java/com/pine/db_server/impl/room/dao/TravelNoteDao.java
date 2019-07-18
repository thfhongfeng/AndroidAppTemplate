package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.TravelNote;

@Dao
public interface TravelNoteDao {
    @Insert
    long insert(TravelNote travelNote);

    @Update
    int update(TravelNote travelNote);
}
