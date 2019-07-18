package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_travel_note_shop")
public class TravelNoteShop {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    private String shopId;

    @NonNull
    private String travelNoteId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getShopId() {
        return shopId;
    }

    public void setShopId(@NonNull String shopId) {
        this.shopId = shopId;
    }

    @NonNull
    public String getTravelNoteId() {
        return travelNoteId;
    }

    public void setTravelNoteId(@NonNull String travelNoteId) {
        this.travelNoteId = travelNoteId;
    }
}
