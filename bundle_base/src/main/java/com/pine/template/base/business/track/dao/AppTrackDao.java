package com.pine.template.base.business.track.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pine.template.base.business.track.entity.AppTrack;

import java.util.List;

@Dao
public interface AppTrackDao {
    @Insert
    long insert(AppTrack appTrack);

    @Update
    int update(AppTrack appTrack);

    @Query("SELECT COUNT(*) FROM db_app_track")
    int getCount();

    @Query("SELECT COUNT(*) FROM db_app_track WHERE module_tag = :moduleTag")
    int getCountByModuleTag(String moduleTag);

    @Query("DELETE FROM db_app_track WHERE _id IN (SELECT _id FROM db_app_track ORDER BY _id ASC LIMIT :deleteCount)")
    void deleteOldData(int deleteCount);

    @Query("DELETE FROM db_app_track WHERE _id IN (SELECT _id FROM db_app_track WHERE module_tag = :moduleTag ORDER BY _id ASC LIMIT :deleteCount)")
    void deleteOldDataByModuleTag(String moduleTag, int deleteCount);

    @Query("SELECT * FROM db_app_track ORDER BY time_in_stamp ASC")
    List<AppTrack> queryAllList();

    @Query("SELECT * FROM db_app_track WHERE time_in_stamp >= :startTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByStartTime(long startTime);

    @Query("SELECT * FROM db_app_track WHERE time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByEndTime(long endTime);

    @Query("SELECT * FROM db_app_track WHERE time_in_stamp >= :startTime AND time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByTime(long startTime, long endTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag ORDER BY time_in_stamp ASC")
    List<AppTrack> queryAllListByModule(String moduleTag);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND action_name = :actionName ORDER BY time_in_stamp ASC")
    List<AppTrack> queryAllListByModule(String moduleTag, String actionName);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND action_name IN (:actionNames) ORDER BY time_in_stamp ASC")
    List<AppTrack> queryAllListByModule(String moduleTag, List<String> actionNames);

    @Transaction
    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag ORDER BY time_in_stamp DESC LIMIT :startIndex,:pageSize")
    List<AppTrack> queryListByPage(String moduleTag, int startIndex, int pageSize);

    @Transaction
    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND action_name = :actionName ORDER BY time_in_stamp DESC LIMIT :startIndex,:pageSize")
    List<AppTrack> queryListByPage(String moduleTag, String actionName, int startIndex, int pageSize);

    @Transaction
    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND action_name IN (:actionNames) ORDER BY time_in_stamp DESC LIMIT :startIndex,:pageSize")
    List<AppTrack> queryListByPage(String moduleTag, List<String> actionNames, int startIndex, int pageSize);

    @Transaction
    @Query("SELECT * FROM db_app_track WHERE module_tag IN (:moduleTags) ORDER BY time_in_stamp DESC LIMIT :startIndex,:pageSize")
    List<AppTrack> queryListByPage(List<String> moduleTags, int startIndex, int pageSize);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND time_in_stamp >= :startTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModuleAndStartTime(String moduleTag, long startTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModuleAndEndTime(String moduleTag, long endTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag = :moduleTag AND time_in_stamp >= :startTime AND time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModuleAndTime(String moduleTag, long startTime, long endTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag IN (:moduleTags) ORDER BY time_in_stamp ASC")
    List<AppTrack> queryAllListByModules(List<String> moduleTags);

    @Query("SELECT * FROM db_app_track WHERE module_tag IN (:moduleTags) AND time_in_stamp >= :startTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModulesAndStartTime(List<String> moduleTags, long startTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag IN (:moduleTags) AND time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModulesAndEndTime(List<String> moduleTags, long endTime);

    @Query("SELECT * FROM db_app_track WHERE module_tag IN (:moduleTags) AND time_in_stamp >= :startTime AND time_in_stamp < :endTime ORDER BY time_in_stamp ASC")
    List<AppTrack> queryListByModulesAndTime(List<String> moduleTags, long startTime, long endTime);

    @Delete
    int delete(List<AppTrack> list);
}
