package com.pine.template.mvvm.bean;

import com.pine.template.base.component.editor.bean.TextImageItemEntity;
import com.pine.tool.bean.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmTravelNoteDetailEntity extends Bean {
    /**
     * id : 1
     * title : Travel Note Title
     * setOutDate : 2018-10-10 10:10:10
     * headImgUrl :
     * author : 作者
     * belongShops :
     * likeCount : 100
     * hot : 0
     * readCount : 10000
     * preface :
     * dayCount :
     * days : []
     * createTime : 2018-10-10 10:10:10
     * updateTime :
     */

    private String id;
    private String title;
    private String setOutDate;
    private String headImgUrl;
    private String author;
    private List<MvvmShopItemEntity> belongShops;
    private int likeCount;
    private int hot;
    private int readCount;
    private String preface;
    private Integer dayCount = new Integer(0);
    private List<DayBean> days;
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

    public String getSetOutDate() {
        return setOutDate;
    }

    public void setSetOutDate(String setOutDate) {
        this.setOutDate = setOutDate;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<MvvmShopItemEntity> getBelongShops() {
        return belongShops;
    }

    public void setBelongShops(List<MvvmShopItemEntity> belongShops) {
        this.belongShops = belongShops;
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

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    public List<DayBean> getDays() {
        return days;
    }

    public void setDays(List<DayBean> days) {
        this.days = days;
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

    public static class DayBean {
        /**
         * id : 1
         * day : 第1天
         * contentList : 第1天的内容列表
         */

        private String id;
        private String day;
        private List<Content> contentList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public List<Content> getContentList() {
            return contentList;
        }

        public void setContentList(List<Content> contentList) {
            this.contentList = contentList;
        }

        public List<TextImageItemEntity> getTextImageContentList() {
            if (contentList == null) {
                return null;
            }
            List<TextImageItemEntity> list = new ArrayList<>();
            for (int i = 0; i < contentList.size(); i++) {
                Content content = contentList.get(i);
                if (content == null) {
                    continue;
                }
                TextImageItemEntity entity = new TextImageItemEntity(content.getType());
                entity.setIndex(content.getIndex());
                entity.setLocalFilePath(content.getLocalFilePath());
                entity.setRemoteFilePath(content.getRemoteFilePath());
                entity.setText(content.getText());
                entity.setOrderNum(content.getOrderNum());
                list.add(entity);
            }
            return list;
        }

        public static class Content {
            private String index;
            private String type;
            private String text;
            private String localFilePath;
            private String remoteFilePath;
            private int orderNum;

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getLocalFilePath() {
                return localFilePath;
            }

            public void setLocalFilePath(String localFilePath) {
                this.localFilePath = localFilePath;
            }

            public String getRemoteFilePath() {
                return remoteFilePath;
            }

            public void setRemoteFilePath(String remoteFilePath) {
                this.remoteFilePath = remoteFilePath;
            }

            public int getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(int orderNum) {
                this.orderNum = orderNum;
            }
        }
    }
}