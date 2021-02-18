package com.pine.template.base.widget.dialog;

import androidx.annotation.IdRes;

public class SelectItemBean<T> {
    @IdRes
    private int imgResId;
    private String name;
    private String nameColor;
    private T itemData;

    public SelectItemBean(String name) {
        this(-1, name, "");
    }

    public SelectItemBean(String name, String nameColor) {
        this(-1, name, nameColor);
    }

    public SelectItemBean(@IdRes int imgResId, String name) {
        this(imgResId, name, "");
    }

    public SelectItemBean(@IdRes int imgResId, String name, String nameColor) {
        this.imgResId = imgResId;
        this.name = name;
        this.nameColor = nameColor;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public T getItemData() {
        return itemData;
    }

    public void setItemData(T itemData) {
        this.itemData = itemData;
    }
}
