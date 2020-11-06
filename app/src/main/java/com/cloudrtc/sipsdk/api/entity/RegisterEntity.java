package com.cloudrtc.sipsdk.api.entity;

/**
 * 创建时间：2020/11/3
 * 创建人：singleCode
 * 功能描述：
 **/
public class RegisterEntity {
    private String server;
    private String userId;
    private String sipPwd;
    private boolean restart;

    public RegisterEntity(String server, String userId, String sipPwd, boolean restart) {
        this.server = server;
        this.userId = userId;
        this.sipPwd = sipPwd;
        this.restart = restart;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSipPwd() {
        return sipPwd;
    }

    public void setSipPwd(String sipPwd) {
        this.sipPwd = sipPwd;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
