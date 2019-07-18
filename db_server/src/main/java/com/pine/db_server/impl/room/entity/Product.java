package com.pine.db_server.impl.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "db_product")
public class Product {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;

    @NonNull
    @ColumnInfo(name = "id")
    private String productId;

    @NonNull
    private String name;

    @NonNull
    private String price;

    @NonNull
    private String shelvePrice;

    @NonNull
    private String shelveDate;

    @NonNull
    private String shopId;

    private String description;

    private String remark;

    private String createTime;

    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getPrice() {
        return price;
    }

    public void setPrice(@NonNull String price) {
        this.price = price;
    }

    @NonNull
    public String getShelvePrice() {
        return shelvePrice;
    }

    public void setShelvePrice(@NonNull String shelvePrice) {
        this.shelvePrice = shelvePrice;
    }

    @NonNull
    public String getShelveDate() {
        return shelveDate;
    }

    public void setShelveDate(@NonNull String shelveDate) {
        this.shelveDate = shelveDate;
    }

    @NonNull
    public String getShopId() {
        return shopId;
    }

    public void setShopId(@NonNull String shopId) {
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
