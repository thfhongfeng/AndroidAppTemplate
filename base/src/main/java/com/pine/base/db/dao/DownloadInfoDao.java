package com.pine.base.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.pine.base.db.entity.DownloadInfo;

@Dao
public interface DownloadInfoDao {
    @Insert
    long insert(DownloadInfo downloadInfo);

    @Update
    int update(DownloadInfo downloadInfo);
}
