package com.pine.template.base.component.media_selector.bean;

import java.util.ArrayList;

public class MediaFolderBean {
    public ArrayList<MediaItemBean> medias = new ArrayList<MediaItemBean>();
    /**
     * media的文件夹路径
     */
    private String dir;
    /**
     * media的文件夹relative路径
     */
    private String relativeParent;
    /**
     * 第一张media的路径
     */
    private String firstMediaPath;
    /**
     * 文件夹的名称
     */
    private String name;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        if (lastIndexOf >= 0) {
            this.name = this.dir.substring(lastIndexOf);
        } else {
            this.name = dir;
        }
    }

    public String getRelativeParent() {
        return relativeParent;
    }

    public void setRelativeParent(String relativeParent) {
        this.relativeParent = relativeParent;
    }

    public ArrayList<MediaItemBean> getMedias() {
        return medias;
    }

    public void setMedias(ArrayList<MediaItemBean> medias) {
        this.medias = medias;
    }

    public String getFirstMediaPath() {
        return firstMediaPath;
    }

    public void setFirstMediaPath(String firstMediaPath) {
        this.firstMediaPath = firstMediaPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
