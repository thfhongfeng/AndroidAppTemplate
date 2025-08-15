package com.pine.template.base.business.track;

import java.util.List;

public class TrackModuleInfo {
    private String moduleName;
    private String moduleDesc;
    private List<TrackActionInfo> actions;
    private boolean canUpload;

    public TrackModuleInfo(String moduleName, String moduleDesc, boolean canUpload) {
        this.moduleName = moduleName;
        this.moduleDesc = moduleDesc;
        this.canUpload = canUpload;
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

    public List<TrackActionInfo> getActions() {
        return actions;
    }

    public void setActions(List<TrackActionInfo> actions) {
        this.actions = actions;
    }

    public boolean isCanUpload() {
        return canUpload;
    }

    public void setCanUpload(boolean canUpload) {
        this.canUpload = canUpload;
    }
}
