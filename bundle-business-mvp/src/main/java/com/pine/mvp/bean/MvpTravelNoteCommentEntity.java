package com.pine.mvp.bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpTravelNoteCommentEntity {

    /**
     * id :
     * author :
     * headImgUrl :
     * createTime :
     * content :
     */

    private String id;
    private String author;
    private String headImgUrl;
    private String createTime;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
