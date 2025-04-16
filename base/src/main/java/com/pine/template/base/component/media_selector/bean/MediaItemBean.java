package com.pine.template.base.component.media_selector.bean;

import android.graphics.Bitmap;

public class MediaItemBean {
    public static final int TYPE_MEDIA_ITEM = 0;
    public static final int TYPE_CAMERA_PIC = 1;
    public static final int TYPE_CAMERA_VIDEO = 2;

    private int type;
    private MediaBean mediaBean;
    private Bitmap thumbnail;
    private boolean selected;

    public static MediaItemBean buildItem() {
        MediaItemBean bean = new MediaItemBean();
        return bean;
    }

    public static MediaItemBean buildCameraPicTake() {
        MediaItemBean bean = new MediaItemBean();
        bean.setType(TYPE_CAMERA_PIC);
        return bean;
    }

    public static MediaItemBean buildCameraVideoTake() {
        MediaItemBean bean = new MediaItemBean();
        bean.setType(TYPE_CAMERA_VIDEO);
        return bean;
    }

    public boolean isCameraPicTake() {
        return type == TYPE_CAMERA_PIC;
    }

    public boolean isCameraVideoTake() {
        return type == TYPE_CAMERA_VIDEO;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MediaBean getMediaBean() {
        return mediaBean;
    }

    public void setMediaBean(MediaBean mediaBean) {
        this.mediaBean = mediaBean;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
