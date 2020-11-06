package com.cloudrtc.sipsdk.api;


import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.sipsdk.api.callback.SipCallStatusListener;
import com.cloudrtc.sipsdk.api.manager.SipMediaManager;
import com.cloudrtc.sipsdk.api.manager.SipManager;
import com.cloudrtc.sipsdk.api.sip.SipVideoConfig;
import com.cloudrtc.sipsdk.event.SipEvent;

/**
 * 创建时间：2020/11/3
 * 创建人：singleCode
 * 功能描述：通话相关帮助类
 **/
public class SipCallHelper {
    private static String TAG = SipCallHelper.class.getSimpleName();
    private static SipCallRequest callRequest;
    /**
     * 拨打电话
     * @param request  通话请求
     */
    public static void makeCall(SipCallRequest request){
        Log.d(TAG, "makeCall:"+request.toString());
        callRequest = request;
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.MAKE_CALL,request));
    }

    /**
     * 拨打网络电话
     * @param number
     */
    public static void makePhoneCall(String number){
        if(callRequest != null){
            Log.d(TAG, "makePhoneCall");
            hangUp();
            callRequest.setToNum(number);
            callRequest.setOpenVideo(false);
            SipManager.getInstance().postEvent(new SipEvent(SipEvent.MAKE_CALL,callRequest));
        }else {
            Log.d(TAG, "makePhoneCall error: callRequest is null");
        }
    }

    /**
     * 视频通话切到语音通话
     */
    public static void  switchToAudio(){
        Log.d(TAG, "switchToAudio");
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.SWITCH_TO_AUDIO));
    }

    /**
     * 拒绝/挂断通话
     */
    public static void  hangUp(){
        Log.d(TAG, "hangUp");
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.HANG_UP_CALL));
    }

    /**
     * 接听来电，通话状态通过设置监听来获取
     */
    public static void answerCall(){
        Log.d(TAG, "answerCall");
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.ANSWER_CALL));
    }

    /**
     * 发送指令
     * @param msg
     */
    public static void sendIM(String msg){
        Log.d(TAG, "sendIM: "+msg);
        SipManager.getInstance().postEvent(new SipEvent(SipEvent.SEND_IM, msg));
    }

    /**
     * 设置通话监听
     * @param listener
     */
    public static void setListener(SipCallStatusListener listener){
        SipManager.getInstance().setListener(listener);
    }

    /**
     * 移除通话监听
     * @param listener
     */
    public static void removeListener(SipCallStatusListener listener){
        SipManager.getInstance().removeListener(listener);
    }

    /**
     * 开启视频流、音频流接收传输
     * @param context
     * @param usingFrontCamera  是否使用前置摄像头
     * @param rotation  摄像头旋转角度
     * @param videoConfig 视频大小、帧率、比特率配置
     * @param localRender
     * @param remoteRender
     */
    public static void startVideoChannel(Context context, boolean usingFrontCamera, int rotation, SipVideoConfig videoConfig, SurfaceView localRender, SurfaceView remoteRender){
        SipMediaManager.getManager().getAudio(context).setLoudspeakerStatus(true);
        SipMediaManager.getManager().getAudio(context).muteMic(false);
        SipManager.getInstance().configVideoChannel(videoConfig);
        SipManager.getInstance().startVideoChannel(usingFrontCamera ? 1 : 0, rotation, localRender, remoteRender);
    }

    /**
     * 获取摄像头默认角度
     * @param usingFrontCamera
     * @return
     */
    public static int getCameraOrientation(boolean usingFrontCamera){
       return SipManager.getInstance().getCameraOrientation(usingFrontCamera ? 1 : 0);
    }

    /**
     * 关闭视频流、音频流传输
     */
    public static void stopVideoChannel(){
        SipMediaManager.getManager().closeRtcAudio();
        SipManager.getInstance().stopVideoChannel();
    }
}
