package com.cloudrtc.sipsdk.api.entity;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：
 **/
public class RegisterStatus {
    private int code;
    private String reason;

    public RegisterStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RegisterStatus{" +
                "code=" + code +
                ", reason='" + reason + '\'' +
                '}';
    }
}
