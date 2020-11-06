package com.cloudrtc.sipsdk.api.entity;

/**
 * 创建时间：2020/11/2
 * 创建人：singleCode
 * 功能描述：
 **/
public enum  SipCallRxStatus {
    refuse_call,//拒绝通话
    no_people,//无人接听
    wait_call,//等待
    empty_number,//空号
    user_busy,//用户忙
    open_door,//开门
}
