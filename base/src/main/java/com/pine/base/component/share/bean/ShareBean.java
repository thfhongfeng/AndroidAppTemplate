package com.pine.base.component.share.bean;

import androidx.annotation.NonNull;

import com.pine.base.R;
import com.pine.tool.util.AppUtils;

/**
 * Created by tanghongfeng on 2018/10/11
 */

public abstract class ShareBean {
    public static final int SHARE_TARGET_QQ = 0;
    public static final int SHARE_TARGET_WX = 1;
    public static final int SHARE_TARGET_WX_FRIEND_CIRCLE = 2;
    public static final int SHARE_TARGET_WEI_BO = 3;

    public static final int SHARE_CONTENT_TYPE_TEXT_URL = 1;
    public static final int SHARE_CONTENT_TYPE_IMAGE = 2;
    public static final int SHARE_CONTENT_TYPE_MULTI_IMAGE = 3;
    public static final int SHARE_CONTENT_TYPE_VIDEO = 4;

    private static final int[] DEFAULT_ICON_IDS = {
            R.mipmap.base_ic_share_qq,
            R.mipmap.base_ic_share_weixin,
            R.mipmap.base_ic_share_weixin_friend_circle,
            R.mipmap.base_ic_share_weibo
    };

    private static final String[] DEFAULT_ICON_NAMES = AppUtils
            .getApplication()
            .getResources().getStringArray(R.array.base_share_icon_name);

    private int shareTarget;
    private int shareContentType;
    private String iconName;
    private int iconId;
    private String shareTitle;
    private String shareDescription;
    private int shareThumbId;

    protected ShareBean(@NonNull int shareTarget, @NonNull int shareContentType) {
        this(shareTarget, shareContentType,
                DEFAULT_ICON_NAMES[shareTarget % DEFAULT_ICON_NAMES.length],
                DEFAULT_ICON_IDS[shareTarget % DEFAULT_ICON_IDS.length],
                "", "");
    }

    protected ShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                        String shareTitle, String shareDescription) {
        this(shareTarget, shareContentType,
                DEFAULT_ICON_NAMES[shareTarget % DEFAULT_ICON_NAMES.length],
                DEFAULT_ICON_IDS[shareTarget % DEFAULT_ICON_IDS.length],
                shareTitle, shareDescription);
    }

    /**
     * @param shareTarget      通过什么工具进行分享：QQ，微信，微博等，对应SHARE_TARGET_XXX;
     * @param shareContentType 分享的类型：url，图片，视频等，对应SHARE_CONTENT_TYPE_XXX;
     * @param iconName         分享按键的名称描述
     * @param iconId           分享按键的图标资源
     * @param shareTitle       分享内容标题
     * @param shareDescription 分享内容描述
     */
    protected ShareBean(@NonNull int shareTarget, @NonNull int shareContentType,
                        @NonNull String iconName, @NonNull int iconId,
                        String shareTitle, String shareDescription) {
        this.shareTarget = shareTarget;
        this.shareContentType = shareContentType;
        this.iconName = iconName;
        this.iconId = iconId;
        this.shareTitle = shareTitle;
        this.shareDescription = shareDescription;
    }

    public int getShareTarget() {
        return shareTarget;
    }

    public void setShareTarget(int shareTarget) {
        this.shareTarget = shareTarget;
    }

    public int getShareContentType() {
        return shareContentType;
    }

    public void setShareContentType(int shareContentType) {
        this.shareContentType = shareContentType;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDescription() {
        return shareDescription;
    }

    public void setShareDescription(String shareDescription) {
        this.shareDescription = shareDescription;
    }

    public int getShareThumbId() {
        return shareThumbId;
    }

    public void setShareThumbId(int shareThumbId) {
        this.shareThumbId = shareThumbId;
    }
}
