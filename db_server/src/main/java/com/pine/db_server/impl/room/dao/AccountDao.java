package com.pine.db_server.impl.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pine.db_server.impl.room.entity.Account;

@Dao
public interface AccountDao {
    @Insert
    long insert(Account account);

    @Update
    int update(Account account);

    @Query("SELECT * FROM db_account WHERE id=:accountId AND invalid=0")
    Account checkAccountByAccountId(String accountId);

    @Query("SELECT * FROM db_account WHERE account=:account AND invalid=0")
    Account checkAccountByAccount(String account);

    @Query("SELECT * FROM db_account WHERE account=:account AND password=:password AND invalid=0")
    Account checkAccountByAccountPwd(String account, String password);
}
