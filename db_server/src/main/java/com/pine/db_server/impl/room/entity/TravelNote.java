package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_travel_note")
public class TravelNote {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    @ColumnInfo(name = "id")
    private String travelNoteId;

    @NonNull
    private String title;

    @NonNull
    private String authorId;

    @NonNull
    private String author;

    @NonNull
    private int dayCount;

    @NonNull
    private int likeCount;

    // 是否热门文章:0-否；1-是
    @NonNull
    private int hot;

    private String headImgUrl;

    @NonNull
    private int readCount;

    @NonNull
    private String preface;

    @NonNull
    private String days;

    @NonNull
    private String setOutDate;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTravelNoteId() {
        return travelNoteId;
    }

    public void setTravelNoteId(@NonNull String travelNoteId) {
        this.travelNoteId = travelNoteId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@NonNull String authorId) {
        this.authorId = authorId;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    @NonNull
    public String getPreface() {
        return preface;
    }

    public void setPreface(@NonNull String preface) {
        this.preface = preface;
    }

    @NonNull
    public String getDays() {
        return days;
    }

    public void setDays(@NonNull String days) {
        this.days = days;
    }

    @NonNull
    public String getSetOutDate() {
        return setOutDate;
    }

    public void setSetOutDate(@NonNull String setOutDate) {
        this.setOutDate = setOutDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
