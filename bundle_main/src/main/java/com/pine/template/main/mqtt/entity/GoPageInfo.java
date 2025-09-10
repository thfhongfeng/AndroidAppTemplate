package com.pine.template.main.mqtt.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class GoPageInfo implements Serializable {
    // 页面名称
    private String pageName;
    // 数据列表(页面可能的填充数据)
    private ArrayList<String> dataList;
    // 填充完数据后是否执行下一步操作（比如查询行程等）
    private boolean doAction;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public ArrayList<String> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public boolean isDoAction() {
        return doAction;
    }

    public void setDoAction(boolean doAction) {
        this.doAction = doAction;
    }

    @Override
    public String toString() {
        return "GoPageInfo{" +
                "pageName='" + pageName + '\'' +
                ", dataList=" + dataList +
                ", doAction=" + doAction +
                '}';
    }
}
