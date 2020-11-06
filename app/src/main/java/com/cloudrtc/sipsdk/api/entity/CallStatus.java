package com.cloudrtc.sipsdk.api.entity;

/**
 * 创建时间：2020/5/8
 * 创建人：singleCode
 * 功能描述：sip通话状态
 **/
public enum CallStatus {
    CALL_CONNECTED,//通话建立成功
    CALL_FAILED,//通话失败
    CALL_END,//通话结束
    CALL_INCOMING,//来电中
    CALL_OUTGOING;//去电中
}
