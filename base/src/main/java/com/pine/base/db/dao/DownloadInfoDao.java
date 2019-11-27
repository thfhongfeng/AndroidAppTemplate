package com.pine.base.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.pine.base.db.entity.DownloadInfo;

@Dao
public interface DownloadInfoDao {
    @Insert
    long insert(DownloadInfo downloadInfo);

    @Update
    int update(DownloadInfo downloadInfo);
}
