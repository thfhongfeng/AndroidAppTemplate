package com.pine.template.base.track;

import java.util.List;

public class TrackActionNameInfo {
    private String moduleName;
    private String moduleDesc;
    private List<TrackActionName> actions;

    public TrackActionNameInfo(String moduleName, String moduleDesc) {
        this.moduleName = moduleName;
        this.moduleDesc = moduleDesc;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    public List<TrackActionName> getActions() {
        return actions;
    }

    public void setActions(List<TrackActionName> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "TrackActionInfo{" +
                "moduleName='" + moduleName + '\'' +
                ", moduleDesc='" + moduleDesc + '\'' +
                ", actions=" + actions +
                '}';
    }
}
