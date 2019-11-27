package com.pine.base.component.share.bean;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/10/11
 */

public class UriListShareBean extends ShareBean {
    private ArrayList<Uri> shareUriList;

    public UriListShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            ArrayList<Uri> shareUriList) {
        super(shareTarget, shareContentType);
        this.shareUriList = shareUriList;
    }

    public UriListShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            String shareTitle, String shareDescription, ArrayList<Uri> shareUriList) {
        super(shareTarget, shareContentType, shareTitle, shareDescription);
        this.shareUriList = shareUriList;
    }

    /**
     * @param shareTarget      通过什么工具进行分享：QQ，微信，微博等，对应SHARE_TARGET_XXX;
     * @param shareContentType 分享的类型：url，图片，视频等，对应SHARE_CONTENT_TYPE_XXX;
     * @param iconName         分享按键的名称描述
     * @param iconId           分享按键的图标资源
     * @param shareTitle       分享内容标题
     * @param shareDescription 分享内容描述
     * @param shareUriList     分享的图片或视频资源列表（用于图片，视频类型的分享）
     */
    public UriListShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            @NonNull String iconName, @NonNull int iconId,
                            String shareTitle, String shareDescription,
                            ArrayList<Uri> shareUriList) {
        super(shareTarget, shareContentType, iconName, iconId, shareTitle, shareDescription);
        this.shareUriList = shareUriList;
    }

    public ArrayList<Uri> getShareUriList() {
        return shareUriList;
    }

    public void setShareUriList(ArrayList<Uri> shareUriList) {
        this.shareUriList = shareUriList;
    }
}
