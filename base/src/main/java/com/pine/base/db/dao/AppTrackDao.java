package com.pine.base.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pine.base.db.entity.AppTrack;

import java.util.List;

@Dao
public interface AppTrackDao {
    @Insert
    long insert(AppTrack appTrack);

    @Update
    int update(AppTrack appTrack);

    @Query("SELECT * FROM db_app_track ORDER BY timeInStamp ASC")
    List<AppTrack> queryAllList();

    @Query("SELECT * FROM db_app_track WHERE timeInStamp >= :startTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByStartTime(long startTime);

    @Query("SELECT * FROM db_app_track WHERE timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByEndTime(long endTime);

    @Query("SELECT * FROM db_app_track WHERE timeInStamp >= :startTime AND timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByTime(long startTime, long endTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag=:moduleTag ORDER BY timeInStamp ASC")
    List<AppTrack> queryAllListByModule(String moduleTag);

    @Query("SELECT * FROM db_app_track WHERE moduleTag=:moduleTag AND timeInStamp >= :startTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModuleAndStartTime(String moduleTag, long startTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag=:moduleTag AND timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModuleAndEndTime(String moduleTag, long endTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag=:moduleTag AND timeInStamp >= :startTime AND timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModuleAndTime(String moduleTag, long startTime, long endTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag IN (:moduleTags) ORDER BY timeInStamp ASC")
    List<AppTrack> queryAllListByModules(List<String> moduleTags);

    @Query("SELECT * FROM db_app_track WHERE moduleTag IN (:moduleTags) AND timeInStamp >= :startTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModulesAndStartTime(List<String> moduleTags, long startTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag IN (:moduleTags) AND timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModulesAndEndTime(List<String> moduleTags, long endTime);

    @Query("SELECT * FROM db_app_track WHERE moduleTag IN (:moduleTags) AND timeInStamp >= :startTime AND timeInStamp < :endTime ORDER BY timeInStamp ASC")
    List<AppTrack> queryListByModulesAndTime(List<String> moduleTags, long startTime, long endTime);

    @Delete
    int delete(List<AppTrack> list);
}
