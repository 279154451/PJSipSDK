package com.cloudrtc.sipsdk.api.callback;

/**
 * 创建时间：2020/5/8
 * 创建人：singleCode
 * 功能描述：
 **/
public interface SipCommandCallBack {
    void onRemoteCommand(String sdpDescription);
}
