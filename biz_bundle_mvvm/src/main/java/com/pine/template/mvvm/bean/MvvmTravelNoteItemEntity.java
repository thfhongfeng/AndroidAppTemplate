package com.pine.template.mvvm.bean;

import com.pine.tool.bean.Bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteItemEntity extends Bean {

    /**
     * id :
     * title :
     * createTime :
     * updateTime :
     */

    private String id;
    private String title;
    private String createTime;
    private String updateTime;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
