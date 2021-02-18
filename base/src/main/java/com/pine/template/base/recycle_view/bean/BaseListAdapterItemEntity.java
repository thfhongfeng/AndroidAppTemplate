package com.pine.template.base.recycle_view.bean;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class BaseListAdapterItemEntity<T> {
    // 列表元素数据
    private T data;
    // 列表元素属性
    private BaseListAdapterItemProperty propertyEntity = new BaseListAdapterItemProperty();

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseListAdapterItemProperty getPropertyEntity() {
        return propertyEntity;
    }
}
