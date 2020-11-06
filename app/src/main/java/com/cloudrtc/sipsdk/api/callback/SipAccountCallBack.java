package com.cloudrtc.sipsdk.api.callback;

import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.entity.SipCallInfoEntity;
import com.cloudrtc.sipsdk.api.sip.SipAccount;

import org.pjsip.pjsua2.OnInstantMessageParam;

/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：账户监听
 **/
public interface SipAccountCallBack extends SipCommandCallBack{
    void onRegisterStatus(RegisterStatus status);
    void onInstantMessage(OnInstantMessageParam prm);
    void onIncomingCall(SipAccount account, int callId, SipCallInfoEntity recordBean);
    boolean isCallActive();
}
