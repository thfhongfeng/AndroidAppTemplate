package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.AccountAccessLog;

import java.util.List;

@Dao
public interface AccountAccessLogDao {
    @Insert
    long insert(AccountAccessLog accountAccessLogin);

    @Update
    int update(AccountAccessLog accountAccessLogin);

    @Query("SELECT * FROM db_account_access_log WHERE accountId=:accountId")
    List<AccountAccessLog> checkAccountLoginByAccountId(String accountId);
}
