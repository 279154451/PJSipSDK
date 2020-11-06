package com.cloudrtc.sipsdk.api.callback;

import com.cloudrtc.sipsdk.api.entity.CallStatus;
import com.cloudrtc.sipsdk.api.entity.RegisterStatus;
import com.cloudrtc.sipsdk.api.entity.SipCallRxStatus;
import com.cloudrtc.sipsdk.api.sip.SipAccount;
import com.cloudrtc.sipsdk.api.sip.SipCall;

/**
 * 创建时间：2020/5/8
 * 创建人：singleCode
 * 功能描述：通话监听
 **/
public interface SipCallStatusListener {
    /**
     * 注册状态
     * @param account
     * @param status
     */
    void onRegisterStatus(SipAccount account, RegisterStatus status);

    /**
     * 会话状态
     * @param account
     * @param call
     * @param status
     */
    void onCallStatus(SipAccount account, SipCall call, CallStatus status);

    /**
     * 远端服务器通过Account instantMessage返回的通话状态 如：refuse_call wait_call user_busy等
     * @param account
     * @param call
     * @param status
     */
    void onCallRxStatus(SipAccount account, SipCall call, SipCallRxStatus status);

    /**
     * 视频流状态
     * @param account
     * @param call
     * @param open 视频是否开启
     */
    void onVideoStatusChange(SipAccount account, SipCall call, boolean open);

    /**
     * 视频分辨率
     * @param video_channel
     * @param width
     * @param height
     */
    void  OnVideoFrameSizeChanged(int video_channel, int width, int height);

    /**
     * 音频size、以及dtmf
     * @param size
     */
    void OnAudioIncomingSize(int size);

}
