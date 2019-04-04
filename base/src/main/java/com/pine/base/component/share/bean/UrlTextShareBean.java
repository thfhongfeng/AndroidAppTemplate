package com.pine.base.component.share.bean;

import android.support.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/10/11
 */

public class UrlTextShareBean extends ShareBean {
    private String shareText;
    private String shareUrl;

    public UrlTextShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            String shareUrl) {
        super(shareTarget, shareContentType);
        this.shareUrl = shareUrl;
    }

    public UrlTextShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            String shareTitle, String shareDescription, String shareUrl) {
        super(shareTarget, shareContentType, shareTitle, shareDescription);
        this.shareUrl = shareUrl;
    }

    public UrlTextShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            String shareTitle, String shareDescription, String shareText,
                            String shareUrl) {
        super(shareTarget, shareContentType, shareTitle, shareDescription);
        this.shareText = shareText;
        this.shareUrl = shareUrl;
    }

    /**
     * @param shareTarget      通过什么工具进行分享：QQ，微信，微博等，对应SHARE_TARGET_XXX;
     * @param shareContentType 分享的类型：url，图片，视频等，对应SHARE_CONTENT_TYPE_XXX;
     * @param iconName         分享按键的名称描述
     * @param iconId           分享按键的图标资源
     * @param shareTitle       分享内容标题
     * @param shareText        用于微博分享，分享到微博的文本内容
     * @param shareDescription 分享内容描述
     * @param shareUrl         分享的链接（用于链接类型的分享）
     */
    public UrlTextShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                            @NonNull String iconName, @NonNull int iconId,
                            String shareTitle, String shareText, String shareDescription,
                            String shareUrl) {
        super(shareTarget, shareContentType, iconName, iconId, shareTitle, shareDescription);
        this.shareText = shareText;
        this.shareUrl = shareUrl;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
