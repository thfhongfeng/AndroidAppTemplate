package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.FileInfo;

@Dao
public interface FileInfoDao {
    @Insert
    long insert(FileInfo fileInfo);

    @Update
    int update(FileInfo fileInfo);
}
