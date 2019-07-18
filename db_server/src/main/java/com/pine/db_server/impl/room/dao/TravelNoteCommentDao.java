package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.TravelNoteComment;

@Dao
public interface TravelNoteCommentDao {
    @Insert
    long insert(TravelNoteComment travelNoteComment);

    @Update
    int update(TravelNoteComment travelNoteComment);
}
