package com.pine.template.face.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pine.template.face.db.entity.PersonEntity;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    long insert(PersonEntity entity);

    @Transaction
    @Insert
    List<Long> insertList(List<PersonEntity> entities);

    @Update
    int update(PersonEntity entity);

    @Query("SELECT * FROM db_person WHERE _id IN (:idList)")
    List<PersonEntity> queryInList(List<Long> idList);

    @Query("SELECT * FROM db_person ORDER BY update_time DESC")
    List<PersonEntity> queryAllList();

    @Transaction
    @Query("SELECT * FROM db_person ORDER BY update_time DESC LIMIT :startIndex,:pageSize")
    List<PersonEntity> queryByPage(int startIndex, int pageSize);

    @Delete
    int delete(PersonEntity entity);

    @Delete
    int delete(List<PersonEntity> list);

    @Query("DELETE FROM db_person")
    int deleteAll();
}
