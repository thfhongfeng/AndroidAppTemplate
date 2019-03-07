package com.pine.mvvm.bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteItemEntity {

    /**
     * id :
     * title :
     * imgUrl :
     */

    private String id;
    private String title;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
