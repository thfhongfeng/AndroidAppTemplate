package com.pine.base.component.editor.bean;

import java.util.List;

public class TextImageEntity {
    private String title;

    private List<TextImageItemEntity> itemList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TextImageItemEntity> getItemList() {
        return itemList;
    }

    public void setItemList(List<TextImageItemEntity> itemList) {
        this.itemList = itemList;
    }
}
