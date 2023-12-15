package com.pine.tool.camera;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class CameraSurfaceParams {
    public static final String MAIN_TAG = "_mainTagForSurface";

    @NonNull
    public String cameraType;

    @NonNull
    public String tag;

    public int preWidth, preHeight;
    public int picWidth, picHeight;

    public int displayRotation = -1;

    public float frameZoomRatio = 1.0f;
    public boolean yuvDataRlMirror;
    public int yuvDataRotate;
    public int frameWidth, frameHeight;
    public int translationX, translationY;

    public CameraSurfaceParams(@NonNull String cameraType, @NonNull String tag) {
        this.cameraType = cameraType;
        this.tag = tag;
    }

    @NonNull
    public String getCameraType() {
        return cameraType;
    }

    public void setCameraType(@NonNull String cameraType) {
        this.cameraType = cameraType;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    public int getPreWidth() {
        return preWidth;
    }

    public void setPreWidth(int preWidth) {
        this.preWidth = preWidth;
    }

    public int getPreHeight() {
        return preHeight;
    }

    public void setPreHeight(int preHeight) {
        this.preHeight = preHeight;
    }

    public int getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public int getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public int getDisplayRotation() {
        return displayRotation;
    }

    public void setDisplayRotation(int displayRotation) {
        this.displayRotation = displayRotation;
    }

    public float getFrameZoomRatio() {
        return frameZoomRatio;
    }

    public void setFrameZoomRatio(float frameZoomRatio) {
        this.frameZoomRatio = frameZoomRatio;
    }

    public boolean isYuvDataRlMirror() {
        return yuvDataRlMirror;
    }

    public void setYuvDataRlMirror(boolean yuvDataRlMirror) {
        this.yuvDataRlMirror = yuvDataRlMirror;
    }

    public int getYuvDataRotate() {
        return yuvDataRotate;
    }

    public void setYuvDataRotate(int yuvDataRotate) {
        this.yuvDataRotate = yuvDataRotate;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public int getTranslationX() {
        return translationX;
    }

    public void setTranslationX(int translationX) {
        this.translationX = translationX;
    }

    public int getTranslationY() {
        return translationY;
    }

    public void setTranslationY(int translationY) {
        this.translationY = translationY;
    }

    public CameraSurfaceParams copyMain(@NonNull String tag) {
        CameraSurfaceParams cameraSurfaceParams = new CameraSurfaceParams(cameraType, tag);
        cameraSurfaceParams.preWidth = this.preWidth;
        cameraSurfaceParams.preHeight = this.preHeight;
        cameraSurfaceParams.picWidth = this.picWidth;
        cameraSurfaceParams.picHeight = this.picHeight;
        return cameraSurfaceParams;
    }

    public boolean isMainSurfaceParams() {
        return TextUtils.equals(tag, MAIN_TAG);
    }

    @Override
    public String toString() {
        return "CameraSurfaceParams{" +
                "cameraType='" + cameraType + '\'' +
                ", tag='" + tag + '\'' +
                ", preWidth=" + preWidth +
                ", preHeight=" + preHeight +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", displayRotation=" + displayRotation +
                ", frameZoomRatio=" + frameZoomRatio +
                ", yuvDataRlMirror=" + yuvDataRlMirror +
                ", yuvDataRotate=" + yuvDataRotate +
                ", frameWidth=" + frameWidth +
                ", frameHeight=" + frameHeight +
                ", translationX=" + translationX +
                ", translationY=" + translationY +
                '}';
    }
}
