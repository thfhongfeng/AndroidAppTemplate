package com.pine.template.mvp.bean;

import android.text.TextUtils;

import com.pine.template.mvp.R;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.DecimalUtils;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopAndProductEntity {

    /**
     * id :
     * name : Shop Item 1
     * distance :
     * mainImgUrl :
     * createTime :
     * updateTime :
     * products : [{"name":"Product Item 1"},{"name":"Product Item 1"}]
     */

    private String id;
    private String name;
    private String distance;
    private String mainImgUrl;
    private String createTime;
    private String updateTime;

    private String location;

    private List<ProductsBean> products;

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
        this.location = "";
        this.location = getFormatDistance();
    }

    public String getMainImgUrl() {
        return mainImgUrl;
    }

    public void setMainImgUrl(String mainImgUrl) {
        this.mainImgUrl = mainImgUrl;
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

    public String getFormatDistance() {
        if (!TextUtils.isEmpty(location)) {
            return location;
        }
        if (!TextUtils.isEmpty(distance)) {
            float distanceF = Float.parseFloat(distance);
            if (distanceF >= 1000.0f) {
                location = DecimalUtils.divide(distanceF, 1000.0f, 2) +
                        AppUtils.getApplication().getString(R.string.unit_kilometre);
            } else {
                location = distanceF + AppUtils.getApplication().getString(R.string.unit_metre);
            }
        }
        return location;
    }

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public static class ProductsBean {
        /**
         * id :
         * name : Product Item 1
         */

        private String id;
        private String name;

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
    }
}
