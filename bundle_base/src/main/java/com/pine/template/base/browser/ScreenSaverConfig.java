package com.pine.template.base.browser;

public class ScreenSaverConfig {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int type;

    private String filePath;
    private int idleTime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    @Override
    public String toString() {
        return "ScreenSaverConfig{" + "type=" + type + ", filePath='" + filePath + '\'' + ", idleTime=" + idleTime + '}';
    }
}
