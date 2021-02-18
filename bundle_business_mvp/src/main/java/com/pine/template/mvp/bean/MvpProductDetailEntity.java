package com.pine.template.mvp.bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpProductDetailEntity {

    /**
     * id :
     * name :
     * price :
     * shelvePrice :
     * shelveDate :
     * shopId :
     * description :
     * remark :
     * createTime :
     * updateTime :
     */

    private String id;
    private String name;
    private String price;
    private String shelvePrice;
    private String shelveDate;
    private String shopId;
    private String description;
    private String remark;
    private String createTime;
    private String updateTime;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShelvePrice() {
        return shelvePrice;
    }

    public void setShelvePrice(String shelvePrice) {
        this.shelvePrice = shelvePrice;
    }

    public String getShelveDate() {
        return shelveDate;
    }

    public void setShelveDate(String shelveDate) {
        this.shelveDate = shelveDate;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
