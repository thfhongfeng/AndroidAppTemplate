package com.pine.template.base.bean;

public class AccountBean {
    private String id;
    private String account;
    private String password;
    private int accountType;
    private String name;
    private String headImgUrl;
    // 账户状态:0-删除，1-激活，2-未激活
    private int state;
    private String mobile;
    // 0表示当前非登陆状态
    private long curLoginTimeStamp;
    private String createTime;
    private String updateTime;

    private int vipLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
        if (accountType < 9000) {
            this.vipLevel = 0;
        } else if (accountType > 9999) {
            this.vipLevel = 999;
        } else {
            this.vipLevel = (accountType - 9000) / 10;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getCurLoginTimeStamp() {
        return curLoginTimeStamp;
    }

    public void setCurLoginTimeStamp(long curLoginTimeStamp) {
        this.curLoginTimeStamp = curLoginTimeStamp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getVipLevel() {
        return vipLevel;
    }
}
