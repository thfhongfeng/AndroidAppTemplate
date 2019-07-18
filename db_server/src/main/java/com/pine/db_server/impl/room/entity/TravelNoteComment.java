package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_travel_note_comment")
public class TravelNoteComment {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    @ColumnInfo(name = "id")
    private String commentId;

    @NonNull
    private String content;

    @NonNull
    private String travelNoteId;

    @NonNull
    private String authorId;

    @NonNull
    private String author;

    private String headImgUrl;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(@NonNull String commentId) {
        this.commentId = commentId;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public String getTravelNoteId() {
        return travelNoteId;
    }

    public void setTravelNoteId(@NonNull String travelNoteId) {
        this.travelNoteId = travelNoteId;
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

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
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
