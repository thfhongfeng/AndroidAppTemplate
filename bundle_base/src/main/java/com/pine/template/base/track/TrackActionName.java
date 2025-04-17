package com.pine.template.base.track;

public class TrackActionName {
    private String actionName;
    private String actionDesc;

    public TrackActionName(String name, String desc) {
        actionName = name;
        actionDesc = desc;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    @Override
    public String toString() {
        return "TrackAction{" +
                "actionName='" + actionName + '\'' +
                ", actionDesc='" + actionDesc + '\'' +
                '}';
    }
}
