package com.cloudrtc.sipsdk.api.manager;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.view.SurfaceView;

import com.cloudrtc.mediaengine.VideoCaptureObserver;
import com.cloudrtc.mediaengine.VideoStreamObserver;
import com.cloudrtc.mediaengine.VoiceStreamObserver;
import com.cloudrtc.sipsdk.api.manager.media.AudioMediaManager;
import com.cloudrtc.sipsdk.api.manager.media.VideoMediaManager;
import com.cloudrtc.sipsdk.api.sip.SipCall;


/**
 * 创建时间：2020/4/26
 * 创建人：singleCode
 * 功能描述：sip媒体：视频、音频管理类
 **/
public class SipMediaManager {
    private static SipMediaManager manager;
    private AudioMediaManager audioMediaManager;
    private VideoMediaManager videoMediaManager;
    private SipMediaManager(){
        audioMediaManager = new AudioMediaManager();
        videoMediaManager = new VideoMediaManager();
    }

    public static SipMediaManager getManager() {
        if(manager == null){
            synchronized (SipMediaManager.class){
                if(manager == null){
                    manager = new SipMediaManager();
                }
            }
        }
        return manager;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        videoMediaManager.init(context);
    }





    //=============================================麦克风设置、铃声播放相关=====================================

    /**
     * 获取audio
     * @param context
     * @return
     */
    public AudioMediaManager getAudio(Context context){
        audioMediaManager.getRtcAudio(context);
        return audioMediaManager;
    }

    /**
     * 关闭audio
     */
    public void closeRtcAudio(){
        if(audioMediaManager != null){
            audioMediaManager.closeRtcAudio();
        }
    }
    /**
     * 手机震动
     */
    private  Vibrator Vibrate(final Context context, boolean isRepeat) {

        Vibrator vib = (Vibrator) context
                .getSystemService(Service.VIBRATOR_SERVICE);

        vib.vibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }, isRepeat ? 1
                : -1);
        return vib;

    }


    //=========================================会话建立，打开关闭音视频流相关接口=======================================================

    /**
     * 会话建立，初始化视频参数
     * @param call
     * @param width
     * @param height
     * @param fps
     * @param bitrate
     */
    public void initVideoFormat(SipCall call, int width, int height, int fps, int bitrate){
        if(call != null && call.isActive()){
            videoMediaManager.configVideoFormat(width,height,fps,bitrate);
        }
    }


    /**
     * 打开视频和音频流
     * @param sipCall
     * @param camera_index
     * @param local_renderer
     * @param remote_renderer
     */
    public void startVideoChannel(SipCall sipCall,boolean startVideo,int camera_index, SurfaceView local_renderer, SurfaceView remote_renderer){
        if(sipCall != null && sipCall.isActive()){
            if(startVideo){
                videoMediaManager.startSendVideo(camera_index,videoMediaManager.getCameraOrientation(camera_index),local_renderer);
                videoMediaManager.startReceiveVideo(remote_renderer);
            }
            videoMediaManager.startSendVoice();
            videoMediaManager.startReceiveVoice();
        }
    }

    /**
     * 关闭视频和音频流
     */
    public void stopVideoChannel(){
        videoMediaManager.stopReceiveVideo();
        videoMediaManager.stopSendVideo();
        videoMediaManager.stopReceiveVoice();
        videoMediaManager.stopSendVoice();
        videoMediaManager.release();
    }

    /**
     * 重新打开视频发送流
     * @param sipCall
     */
    public void restartSendVideoWithCall(SipCall sipCall) {
        if(sipCall !=null && sipCall.isActive()){
            videoMediaManager.startSendVideo(videoMediaManager.getCamera_index(),0,videoMediaManager.getLocal_renderer());
        }
    }

    /**
     * 重新打开视频接收流
     * @param sipCall
     */
    public void restartReceiveVideoWithCall(SipCall sipCall){
        if(sipCall != null && sipCall.isActive()){
            videoMediaManager.startReceiveVideo(videoMediaManager.getRemote_renderer());
        }
    }

    /**
     * 关闭视频发送流
     */
    public void stopSendVideo(){
        videoMediaManager.stopSendVideo();
    }

    /**
     * 关闭视频接收流
     */
    public void stopReceiveVideo(){
        videoMediaManager.stopReceiveVideo();
    }

    /**
     * 重新打开音频发送流
     * @param call
     */
    public void restartSendVoiceWithCall(SipCall call){
        if(call != null && call.isActive()){
            videoMediaManager.startSendVoice();
        }
    }

    /**
     * 重新打开音频接收流
     * @param call
     */
    public void restartReceiveVoiceWithCall(SipCall call){
        if(call != null && call.isActive()){
            videoMediaManager.startReceiveVoice();
        }
    }

    /**
     * 关闭音频发送流
     */
    public void stopSendVoice(){
        videoMediaManager.stopSendVoice();
    }

    /**
     * 关闭音频接收流
     */
    public void stopReceiveVoice(){
        videoMediaManager.stopReceiveVoice();
    }

    /**
     * 会话中切换摄像头
     * @param call
     * @param camera_index
     * @param rotation_new
     * @param local_renderer
     */
    public void swapCameraWithCall(SipCall call,int camera_index, int rotation_new, SurfaceView local_renderer){
        if(call != null && call.isActive()){
            videoMediaManager.swapCamera(camera_index,rotation_new,local_renderer);
        }
    }

    /**
     * 修改摄像头旋转角度
     * @param call
     * @param rotation
     */
    public void changeCaptureRotation(SipCall call,int rotation){
        if(call != null && call.isActive()){
            videoMediaManager.changeCaptureRotation(rotation);
        }
    }

    /**
     * 获取当前摄像头的旋转角度
     * @param cam_index
     * @return
     */
    public int getCameraOrientation(int cam_index){
        return videoMediaManager.getCameraOrientation(cam_index);
    }

    /**
     * 设置码流、分辨率监听
     * @param videoStreamObserver
     * @param captureObserver
     * @param voiceStreamObserver
     */
    public void registerStreamObserver(VideoStreamObserver videoStreamObserver, VideoCaptureObserver captureObserver, VoiceStreamObserver voiceStreamObserver){
        videoMediaManager.registerStreamObserver(videoStreamObserver,captureObserver,voiceStreamObserver);
    }
}
