package com.cloudrtc.sipsdk.api.manager.media;

import android.content.Context;
import android.view.SurfaceView;

import com.cloudrtc.mediaengine.MediaEngine;
import com.cloudrtc.mediaengine.VideoCaptureObserver;
import com.cloudrtc.mediaengine.VideoStreamObserver;
import com.cloudrtc.mediaengine.VoiceStreamObserver;

/**
 * 创建时间：2020/5/8
 * 创建人：singleCode
 * 功能描述：统一管理视频和音频流
 **/
public class VideoMediaManager {
    private static VideoMediaManager manager;
    private MediaEngine mMediaEngine = null;
    private int camera_index =-1;
    private SurfaceView local_renderer;
    private SurfaceView remote_renderer;
    public VideoMediaManager(){
    }
//    public static VideoMediaManager getManager(){
//        if(manager == null){
//            synchronized (VideoMediaManager.class){
//                if(manager == null){
//                    manager = new VideoMediaManager();
//                }
//            }
//        }
//        return manager;
//    }
    public void init(Context context){
        mMediaEngine = new MediaEngine(context);
    }

    public void registerStreamObserver(VideoStreamObserver videoStreamObserver, VideoCaptureObserver captureObserver, VoiceStreamObserver voiceStreamObserver){
        if(mMediaEngine != null){
            if(videoStreamObserver != null){
                mMediaEngine.GetVideoEngine().RegisterVideoStreamObserver(videoStreamObserver);
            }
            if(captureObserver != null){
                mMediaEngine.GetVideoEngine().RegisterVideoCaptureObserver(captureObserver);
            }
            if(voiceStreamObserver != null){
                mMediaEngine.GetVoiceEngine().RegisterVoiceStreamObserver(voiceStreamObserver);
            }
        }else {
            new Throwable("need init MediaEngine");
        }
    }

    public void configVideoFormat(int width, int height, int fps, int bitrate){
        mMediaEngine.GetVideoEngine().SetupVideoChannel(
                width,
                height,
                fps,
                bitrate);
    }

    public void startSendVideo(int camera_index, int rotation, SurfaceView local_renderer){
        this.camera_index = camera_index;
        this.local_renderer = local_renderer;
        mMediaEngine.GetVideoEngine().StartSending(camera_index,rotation,local_renderer);
    }
    public void startReceiveVideo(SurfaceView remote_renderer){
        this.remote_renderer = remote_renderer;
        mMediaEngine.GetVideoEngine().StartReceiving(remote_renderer);
    }

    public void stopSendVideo(){
        mMediaEngine.GetVideoEngine().StopSending();
    }
    public void stopReceiveVideo(){
        mMediaEngine.GetVideoEngine().StopReceiving();
    }

    /**
     * 开始音频发送
     */
    public void startSendVoice(){
        mMediaEngine.GetVoiceEngine().StartSending();
    }

    /**
     * 开始音频接收
     */
    public void startReceiveVoice(){
        mMediaEngine.GetVoiceEngine().StartReceiving();
    }

    /**
     * 结束音频发送
     */
    public void stopSendVoice(){
        mMediaEngine.GetVoiceEngine().StopSending();
    }

    /**
     * 结束音频接收
     */
    public void stopReceiveVoice(){
        mMediaEngine.GetVoiceEngine().StopReceiving();
    }
    public int getCamera_index() {
        return camera_index;
    }

    public SurfaceView getLocal_renderer() {
        return local_renderer;
    }

    public SurfaceView getRemote_renderer() {
        return remote_renderer;
    }

    /**
     * 获取摄像头角度
     * @param cam_index
     * @return
     */
    public int getCameraOrientation(int cam_index) {
        if(this.mMediaEngine != null)
        {
            return mMediaEngine.GetVideoEngine().GetCameraOrientation(cam_index);
        }
        return -1;
    }

    /**
     * 切换摄像头
     * @param camera_index
     * @param rotation_new
     * @param local_renderer
     * @return
     */
    public void swapCamera(int camera_index, int rotation_new, SurfaceView local_renderer) {
        mMediaEngine.GetVideoEngine().StopSending();
        mMediaEngine.GetVideoEngine().StartSending(camera_index, rotation_new, local_renderer);
    }

    public void changeCaptureRotation(int rotation){
        mMediaEngine.GetVideoEngine().ChangeCaptureRotation(rotation);
    }

    public void release(){
        this.camera_index = -1;
        this.local_renderer = null;
        this.remote_renderer =null;
    }



}
