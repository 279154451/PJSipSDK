package com.cloudrtc.sipsdk.api.sip;

import android.view.SurfaceView;

import com.cloudrtc.binder.SipCallRequest;
import com.cloudrtc.sipsdk.api.callback.SipCallCallBack;

import org.pjsip.pjsua2.SipHeaderVector;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 创建时间：2020/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public abstract class SipAbstractManager {
    public Set<SipCallCallBack> sipCallCallBackSet = new CopyOnWriteArraySet<>();

    public void setSipCallCallBack(SipCallCallBack callCallBack){
        sipCallCallBackSet.add(callCallBack);
    }
    public void removeSipCallCallBack(SipCallCallBack callCallBack){
        sipCallCallBackSet.remove(callCallBack);
    }
    public void clearSipCallCallBack(){
        sipCallCallBackSet.clear();
    }

    /**
     * 注册账号
     * @param user_id
     * @param password
     * @param server
     */
    protected abstract void register(boolean restart,String user_id, String password, String server);
    protected abstract void unregister();

    /**
     * 发起呼叫
     * @param request
     */
    protected abstract void makeCall(SipCallRequest request, SipHeaderVector headerVector);

    /**
     * 接听呼叫
     * @param videoCall
     */
    protected abstract void answerCall(boolean videoCall);

    /**
     * 挂断会话
     */
    protected abstract void hangUp(boolean callConnect);

    /**
     * 拒绝来电
     */
    protected abstract void declineCall();

    /**
     * 删除会话
     */
    protected abstract void deleteCall(boolean resetInfo);

    /**
     * 释放资源
     */
    public abstract void onDestroy();

    /**
     * 初始化视频通道
     */
    public abstract void configVideoChannel(SipVideoConfig defVideoSize);

    /**
     * 开启视频通道，接受与发送双向通道
     * @param camera_index
     * @param rotation
     * @param local_renderer
     * @param remote_renderer
     */
    public abstract void startVideoChannel(int camera_index, int rotation, SurfaceView local_renderer, SurfaceView remote_renderer);

    /**
     * 关闭视频通道
     */
    public abstract void stopVideoChannel();

    /**
     * 开启语音通道
     */
    public abstract void startVoiceChannel();

    /**
     * 开启视频发送
     */
    public abstract void restartVideoSending();

    /**
     * 开启视频接受
     */
    public abstract void restartVideoReceiving();

    /**
     * 关闭视频接受
     */
    public abstract void stopVideoReceiving();

    /**
     * 关闭视频发送
     */
    public abstract void stopVideoSending();

    /**
     * 设置镜头旋转角度
     * @param rotation
     */
    public abstract void  setCameraOutputRotation(int rotation);

    /**
     * 获取镜头旋转角度
     * @param cam_index
     */
    public abstract int getCameraOrientation(int cam_index);

    /**
     * 切换镜头
     * @param camera_index
     * @param local_renderer
     */
    public abstract  void swapCamera(int camera_index, int rotation_new,SurfaceView local_renderer);

}
