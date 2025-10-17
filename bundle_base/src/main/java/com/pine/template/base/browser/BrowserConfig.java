package com.pine.template.base.browser;

import java.util.List;

public class BrowserConfig {

    // 哪些额外的Url加载错误时使用自定义404页面
    private List<String> extraUrlNeed404List;

    // 屏保配置
    private ScreenSaverConfig screensaverConfig;

    public List<String> getExtraUrlNeed404List() {
        return extraUrlNeed404List;
    }

    public void setExtraUrlNeed404List(List<String> extraUrlNeed404List) {
        this.extraUrlNeed404List = extraUrlNeed404List;
    }

    public ScreenSaverConfig getScreensaverConfig() {
        return screensaverConfig;
    }

    public void setScreensaverConfig(ScreenSaverConfig screensaverConfig) {
        this.screensaverConfig = screensaverConfig;
    }

    @Override
    public String toString() {
        return "BrowserConfig{" +
                "extraUrlNeed404List=" + extraUrlNeed404List +
                ", screensaverConfig=" + screensaverConfig +
                '}';
    }
}
