package com.pine.mvp.bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopItemEntity {

    /**
     * id :
     * name :
     * distance :
     * mainImgUrl :
     */

    private String id;
    private String name;
    private String distance;
    private String mainImgUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMainImgUrl() {
        return mainImgUrl;
    }

    public void setMainImgUrl(String mainImgUrl) {
        this.mainImgUrl = mainImgUrl;
    }
}
