package com.pine.template.main.mqtt.mode;

public class KeepAliveMode {
    private String code;
    private boolean register;
    private String gotoUrl;
    private boolean deactivate;
    // 单位：秒
    private int ttl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public String getGotoUrl() {
        return gotoUrl;
    }

    public void setGotoUrl(String gotoUrl) {
        this.gotoUrl = gotoUrl;
    }

    public boolean isDeactivate() {
        return deactivate;
    }

    public void setDeactivate(boolean deactivate) {
        this.deactivate = deactivate;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "KeepAliveMode{" +
                "code='" + code + '\'' +
                ", register=" + register +
                ", gotoUrl='" + gotoUrl + '\'' +
                ", deactivate=" + deactivate +
                ", ttl=" + ttl +
                '}';
    }
}