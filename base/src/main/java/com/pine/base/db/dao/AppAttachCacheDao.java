package com.pine.base.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.pine.base.db.entity.AppAttachCache;


/**
 */

@Dao
public interface AppAttachCacheDao {

    @Insert
    long insert(AppAttachCache appAttachCache);

    @Query("SELECT * from app_attach_cache WHERE invalid=0 AND module=:module AND url=:url")
    AppAttachCache checkByUrl(String module, String url);
}
