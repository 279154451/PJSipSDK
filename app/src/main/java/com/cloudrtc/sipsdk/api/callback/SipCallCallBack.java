package com.cloudrtc.sipsdk.api.callback;


/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public interface SipCallCallBack extends SipCommandCallBack {
    void onCallConnected(String uri);
    void onCallEnd();
    void onCallFailed(int code);
    void onCallDtmf();
}
